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
import com.denimgroup.threadfix.remote.response.RestResponse;

/**
 * Created by dharrison on 10/9/2015.
 */
public interface QARestClient {

    public RestResponse<Task> requestTask(String scanners, String agentConfig);

    public RestResponse<String> taskStatusUpdate(String scanQueueTaskId, String message);

    public RestResponse<ScanQueueTask> completeTask(String scanQueueTaskId, String filePath, String secureTaskKey);

    public RestResponse<String> failTask(String scanQueueTaskId, String message, String secureTaskKey);

    public RestResponse<User> trap();

    public RestResponse<User> createUser(String username, String globalRoleName);

    public RestResponse<User> createUser(String username);

    public RestResponse<User> deleteUser(String userId);

    public RestResponse<User[]> listUsers();

    public RestResponse<User> addUserTeamAppPermission(String userName, String roleName, String teamName, String appName);

    public RestResponse<Role> createRole(String roleName, Boolean allPermissions);

    public RestResponse<Role> createSpecificPermissionRole(String roleName, String permission);

    public RestResponse<Role> removePermission(String roleName, String permission);

    public RestResponse<Organization> deleteTeam(String teamId);

    public RestResponse<Group> createGroup(String groupName);

    public RestResponse<String> deletePolicies();
}
