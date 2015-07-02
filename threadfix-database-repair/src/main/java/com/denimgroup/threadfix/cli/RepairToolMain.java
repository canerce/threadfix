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

import java.io.File;
import java.util.List;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.cli.SpringConfigurationJDBCProperties.getSpringBean;

/**
 * Created by mcollins on 6/24/15.
 */
public class RepairToolMain {

    public static void main(String[] args) {
        if (insanityCheck(args)) return;

        System.out.println("Starting offline Hibernate...");
        FixOldMergeBehaviorService fixItService = getSpringBean(FixOldMergeBehaviorService.class);
        System.out.println("Started");

        List<Integer> ints = getInts(args);

        for (Integer id : ints) {
            fixItService.fixOldBehavior(id);
        }
    }

    private static boolean insanityCheck(String[] args) {
        if (args.length == 0) {
            System.out.println("No arguments given, " +
                    "please supply the numeric id of the applications you'd like to fix.");
            return true;
        }

        System.out.println("Checking for jdbc.properties.");
        if (new File("jdbc.properties").exists()) {
            System.out.println("Found jdbc.properties.");
        } else {
            System.out.println("jdbc.properties not present.\n" +
                    "Please put jdbc.properties from your ThreadFix installation in this directory and try again.\n" +
                    "jdbc.properties is found in {tomcatBase}/webapps/threadfix/WEB-INF/classes/jdbc.properties.");
            return true;
        }
        return false;
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
