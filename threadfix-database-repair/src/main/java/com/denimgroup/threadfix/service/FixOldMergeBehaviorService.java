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
package com.denimgroup.threadfix.service;

import com.denimgroup.threadfix.data.dao.ApplicationDao;
import com.denimgroup.threadfix.data.dao.ScanDao;
import com.denimgroup.threadfix.data.entities.Application;
import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.data.entities.Scan;
import com.denimgroup.threadfix.data.entities.ScanRepeatFindingMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.*;
import static java.util.Collections.reverse;

/**
 * This class has a single purpose: fix databases that had vulnerability merging
 * turned off before 2.2.7 with historical data and duplicate vulnerabilities.
 */
@Component
public class FixOldMergeBehaviorService {

    @Autowired
    VulnerabilityService vulnerabilityDao;
    @Autowired
    FindingService findingService;
    @Autowired
    ScanDao scanDao;
    @Autowired
    ApplicationDao applicationDao;

    @Transactional(readOnly = false)
    public void fixOldBehavior(int appId) {
        fixOldBehavior(applicationDao.retrieveById(appId));
    }

    @Transactional(readOnly = false)
    public void fixOldBehavior(Application application) {

        System.out.println("Starting to fix application " + application.getName() + " (id " + application.getId() + ")");

        Map<String, Finding> nativeIdMap = map();
        Map<String, List<Scan>> nativeIdToScansMap = map();
        List<Finding> findingsToDelete = list();
        List<String> falsePositiveList = list();

        List<Scan> scans = listFrom(application.getScans());
        reverse(scans);

        for (Scan scan : scans) {
            for (Finding finding : scan) {
                if (!nativeIdMap.containsKey(finding.getNativeId())) {
                    nativeIdMap.put(finding.getNativeId(), finding);
                } else {
                    if (!nativeIdToScansMap.containsKey(finding.getNativeId())) {
                        nativeIdToScansMap.put(finding.getNativeId(), listOf(Scan.class));
                    }
                    nativeIdToScansMap.get(finding.getNativeId()).add(scan);
                    findingsToDelete.add(finding);
                    if (finding.getVulnerability().getIsFalsePositive()) {
                        falsePositiveList.add(finding.getNativeId());
                    }
                }
            }
        }

        for (Map.Entry<String, Finding> entry : nativeIdMap.entrySet()) {
            String nativeId = entry.getKey();
            Finding finding = entry.getValue();

            if (nativeIdToScansMap.containsKey(nativeId)) {
                for (Scan scan : nativeIdToScansMap.get(nativeId)) {
                    new ScanRepeatFindingMap(finding, scan);
                }
            }
        }

        for (Finding finding : findingsToDelete) {
            findingService.deleteFindingAndRelations(finding);
        }

        for (String nativeId : falsePositiveList) {
            Finding finding = nativeIdMap.get(nativeId);
            if (finding == null) {
                System.out.println("Got null Finding. This could indicate an error.");
            } else {
                finding.getVulnerability().setIsFalsePositive(true);
                findingService.storeFinding(finding);
            }
        }

        System.out.println("Finished application " + application.getName() + " (id " + application.getId() + ")");
    }

}
