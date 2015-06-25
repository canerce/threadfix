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
package com.denimgroup.threadfix.cli;

import com.denimgroup.threadfix.service.FixOldMergeBehaviorService;

import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.cli.SpringConfigurationJDBCProperties.getSpringBean;

/**
 * Created by mcollins on 6/24/15.
 */
public class RepairToolMain {

    public static void main(String[] args) {
        FixOldMergeBehaviorService fixItService = getSpringBean(FixOldMergeBehaviorService.class);

        List<Integer> ints = getInts(args);

        for (Integer id : ints) {
            fixItService.fixOldBehavior(id);
        }
    }

    private static List<Integer> getInts(String[] args) {
        List<Integer> returnList = list();

        for (String arg : args) {
            if (arg.matches("^[0-9]+$")) {
                returnList.add(Integer.parseInt(arg));
            } else {
                System.out.println("Got non-numeric ID: " + arg);
            }
        }

        return returnList;
    }
}
