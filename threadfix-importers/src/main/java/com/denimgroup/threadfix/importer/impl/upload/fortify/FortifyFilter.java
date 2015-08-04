////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.importer.impl.upload.fortify;

import com.denimgroup.threadfix.framework.util.RegexUtils;
import com.denimgroup.threadfix.util.Tuple;
import com.denimgroup.threadfix.util.TupleSet;

import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.map;
import static com.denimgroup.threadfix.EnumUtils.in;
import static com.denimgroup.threadfix.importer.impl.upload.fortify.FilterResult.*;
import static com.denimgroup.threadfix.util.TupleSet.tupleSet;

/**
 * Created by mcollins on 3/5/15.
 */
public class FortifyFilter {

    final String target, query;
    boolean invalid = false;

    public static final String HIDE = "Hide";

    public FortifyFilter(Map<FilterKey, String> map) {
        this(map.get(FilterKey.SEVERITY), map.get(FilterKey.QUERY));
    }

    public FortifyFilter(String target, String query) {
        this.target = target;
        this.query = query;
        parseFields(query);
    }

    TupleSet<VulnKey, String> myFields = tupleSet();
    TupleSet<VulnKey, String> myNegativeFields = tupleSet();

    // list of filters that were in an OR
    List<FortifyFilter> oredFilters = list();

    Threshold impact   = new Threshold("Impact"),
            likelihood = new Threshold("Likelihood"),
            confidence = new Threshold("Confidence"),
            severity   = new Threshold("Severity");

    private void parseFields(String query) {

        if (query.contains(" OR ")) {
            String[] subqueries = query.split(" OR ");
            for (String subquery : subqueries) {
                oredFilters.add(new FortifyFilter(target, subquery));
            }
            return;
        }

        if (query.contains(" AND ")) {
            String[] subqueries = query.split(" AND ");
            for (String subquery : subqueries) {
                parseFields(subquery);
            }
            return;
        }

        for (VulnKey key : VulnKey.values()) {
            String result = RegexUtils.getRegexResult(query, key.pattern);

            if (result != null) {

                // This is a shim to handle weird syntax in ANDed categories
                if (in(key, VulnKey.CATEGORY, VulnKey.FULL_CATEGORY) && result.contains("category:")) {
                    String[] parts = result.split("category:");
                    for (String part : parts) {
                        parseFields("category:" + part);
                    }
                } else {
                    processRegexResult(key, result);
                }
            }
        }

        if (!impact.isValid()) impact.initialize(query);
        if (!confidence.isValid()) confidence.initialize(query);
        if (!severity.isValid()) severity.initialize(query);
        if (!likelihood.isValid()) likelihood.initialize(query);
    }

    private void processRegexResult(VulnKey key, String result) {
        String replaced = result.replaceAll("\\\\:", ":");
        if (replaced.charAt(0) == '!') {
            myNegativeFields.add(key, replaced.substring(1));
        } else {
            myFields.add(key, replaced);
        }
    }

    public String getFinalSeverity(Map<VulnKey, String> vulnInfo, float likelihood, float impact) {
        Map<String, Float> numberMap = map(
                "Impact", impact,
                "Likelihood", likelihood
        );

        return getFinalSeverity(vulnInfo, numberMap);
    }

    public String getFinalSeverity(Map<VulnKey, String> vulnInfo, Map<String, Float> numberMap) {

        String result;

        if (oredFilters.isEmpty()) {
            result = passes(vulnInfo, numberMap) ? target : null;
        } else {
            boolean success = false;
            for (FortifyFilter filter : oredFilters) {
                if (filter.passes(vulnInfo, numberMap)) {
                    success = true;
                    break;
                }
            }
            result = success ? target : null;
        }

        return result;
    }

    public boolean passes(Map<VulnKey, String> vulnInfo, Map<String, Float> numberMap) {
        if (invalid) {
            return false;
        }

        // basic, positive matching
        FilterResult result = getStringResult(vulnInfo, myFields);

        // negative matching
        FilterResult negativeResult = getStringResult(vulnInfo, myNegativeFields);
        if (negativeResult == MISS) {
            negativeResult = MATCH;
        } else if (in(negativeResult, SOME, MATCH)) {
            negativeResult = MISS;
        }

        // threshold matching
        FilterResult thresholdResult = passesThresholds(numberMap);

        return getCombinedResult(result, negativeResult, thresholdResult) == MATCH;
    }

    private FilterResult getCombinedResult(FilterResult... results) {
        FilterResult finalResult = NO_MATCH;

        for (FilterResult result : results) {
            if (result == MISS) {
                finalResult = MISS;
                break;
            } else if (result == MATCH) {
                finalResult = MATCH;
            }
        }

        return finalResult;
    }


    private FilterResult getStringResult(Map<VulnKey, String> vulnInfo, TupleSet<VulnKey, String> myFields) {
        FilterResult result = NO_MATCH;

        for (Tuple<VulnKey, String> fieldTuple : myFields) {
            VulnKey key = fieldTuple.getFirst();
            String myValue = fieldTuple.getSecond();

            String theirValue = vulnInfo.get(key);

            if (myValue != null) { // we need to filter on this value
                if (in(key, VulnKey.TAINT, VulnKey.AUDIENCE) && theirValue != null) {

                    if (theirValue.toLowerCase().contains(myValue.toLowerCase())) {
                        result = MATCH;
                    } else {
                        result = result == MATCH ? SOME : MISS;
                        break;
                    }
                } else if (myValue.equalsIgnoreCase(theirValue)) {
                    // this means we've passed at least one condition
                    // we can't break here because there may be multiple conditions
                    result = MATCH;
                } else {
                    // if we miss any one filter, fail the test
                    result = result == MATCH ? SOME : MISS;
                    break;
                }
            }
        }
        return result;
    }

    private FilterResult passesThresholds(Map<String, Float> numberMap) {

        FilterResult[] individualResults = {
                impact.check(numberMap.get("Impact")),
                confidence.check(numberMap.get("Confidence")),
                likelihood.check(numberMap.get("Likelihood")),
                severity.check(numberMap.get("Severity"))
        };

        return getCombinedResult(individualResults);
    }
}
