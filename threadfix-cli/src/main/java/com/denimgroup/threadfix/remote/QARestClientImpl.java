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

package com.denimgroup.threadfix.remote;

import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import com.denimgroup.threadfix.properties.PropertiesManager;
import com.denimgroup.threadfix.remote.response.RestResponse;

import java.io.File;

/**
 * Created by dharrison on 10/9/2015.
 */
public class QARestClientImpl extends ThreadFixRestClientImpl implements QARestClient {

    private static final SanitizedLogger LOGGER = new SanitizedLogger(ThreadFixRestClientImpl.class);

    public QARestClientImpl(String url, String apiKey) {
        super(url, apiKey);
        httpRestUtils.setUnsafeFlag(true);
    }



    public RestResponse<Task> requestTask(String scanners, String agentConfig) {
        return httpRestUtils.httpPost("/tasks/requestTask",
                new String[] {"scanners", "agentConfig" },
                new String[] { scanners, agentConfig }, Task.class);
    }

    /**
     * Determine if we want to pass the taskId as a parameter or if we want to REST it up
     * @param scanQueueTaskId
     * @param message
     * @return
     */
    public RestResponse<String> taskStatusUpdate(String scanQueueTaskId, String message) {
        return httpRestUtils.httpPost("/tasks/taskStatusUpdate",
                new String[]{"scanQueueTaskId", "message"},
                new String[]{ scanQueueTaskId, message}, String.class);
    }

    public RestResponse<ScanQueueTask> completeTask(String scanQueueTaskId, String filePath, String secureTaskKey) {
        String url = "/tasks/completeTask";
        String[] paramNames 	= {	"scanQueueTaskId", "secureTaskKey" };
        String[] paramValues 	= {  scanQueueTaskId,   secureTaskKey };
        return httpRestUtils.httpPostFile(url, new File(filePath), paramNames, paramValues, ScanQueueTask.class);
    }

    public RestResponse<String> failTask(String scanQueueTaskId, String message, String secureTaskKey) {
        return httpRestUtils.httpPost("/tasks/failTask",
                new String[] { "scanQueueTaskId", "message", "secureTaskKey" },
                new String[] { scanQueueTaskId, message, secureTaskKey }, String.class);
    }

    @Override
    public RestResponse<User> trap() {
        return httpRestUtils.httpPost("user/trap",
                new String[] {},
                new String[] {}, User.class);
    }

    @Override
    public RestResponse<User> createUser(String username, String globalRoleName) {
        return httpRestUtils.httpPost("/user/create",
                new String[] {"username", "globalRoleName" },
                new String[] { username, globalRoleName }, User.class);
    }

    @Override
    public RestResponse<User> createUser(String username) {
        return httpRestUtils.httpPost("/user/create",
                new String[] {"username"},
                new String[] { username}, User.class);
    }

    public RestResponse<User> deleteUser(String userId) {
        return httpRestUtils.httpPost("/user/delete/"+userId,
                new String[] {},
                new String[] {}, User.class);
    }

    public RestResponse<User[]> listUsers() {
        return httpRestUtils.httpPost("/user/list",
                new String[] {},
                new String[] {}, User[].class);
    }

    @Override
    public RestResponse<User> addUserTeamAppPermission(String userName, String roleName, String teamName, String appName) {
        return httpRestUtils.httpPost("/user/permission",
                new String[] {"username", "rolename", "teamname", "appname"},
                new String[] {userName, roleName, teamName, appName}, User.class);
    }

    @Override
    public RestResponse<Role> createRole(String roleName, Boolean allPermissions) {
        return httpRestUtils.httpPost("/role/create",
                new String[] {"roleName", "allPermissions"},
                new String[] {roleName, allPermissions.toString()}, Role.class);
    }

    @Override
    public RestResponse<Role> createSpecificPermissionRole(String roleName, String permission) {
        return httpRestUtils.httpPost("/role/create/specific",
                new String[] {"roleName", "permission"},
                new String[] {roleName, permission}, Role.class);
    }

    @Override
    public RestResponse<Role> removePermission(String roleName, String permission) {
        return httpRestUtils.httpPost("/role/edit",
                new String[] {"roleName", "permission"},
                new String[] {roleName, permission}, Role.class);
    }

    public RestResponse<Organization> deleteTeam(String teamId) {
        return httpRestUtils.httpPost("/teams/delete/"+teamId,
                new String[] {},
                new String[] {}, Organization.class);
    }

    public RestResponse<Group> createGroup(String groupName) {
        return httpRestUtils.httpPost("/groups/create",
                new String[] {"groupName"},
                new String[] {groupName}, Group.class);
    }

    public RestResponse<String> deletePolicies() {
        return httpRestUtils.httpPost("/policy/deleteAll",
                new String[] {},
                new String[] {}, String.class);
    }

    public RestResponse<String> deleteEmailReports(){
        return httpRestUtils.httpPost("/scheduledEmailReport/deleteAll",
                new String[] {},
                new String[] {}, String.class);
    }
}
