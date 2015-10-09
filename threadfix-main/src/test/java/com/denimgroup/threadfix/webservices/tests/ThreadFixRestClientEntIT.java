package com.denimgroup.threadfix.webservices.tests;

import com.denimgroup.threadfix.WebServiceTests;
import com.denimgroup.threadfix.data.entities.Application;
import com.denimgroup.threadfix.data.entities.Organization;
import com.denimgroup.threadfix.data.entities.ScanQueueTask;
import com.denimgroup.threadfix.data.entities.Task;
import com.denimgroup.threadfix.remote.QARestClient;
import com.denimgroup.threadfix.remote.QARestClientImpl;
import com.denimgroup.threadfix.remote.ThreadFixRestClient;
import com.denimgroup.threadfix.remote.ThreadFixRestClientImpl;
import com.denimgroup.threadfix.remote.response.RestResponse;
import com.denimgroup.threadfix.selenium.tests.ScanContents;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Category(WebServiceTests.class)
public class ThreadFixRestClientEntIT {
    String dummyUrl = "http://test.com";

    public static final String API_KEY, REST_URL;
    public static final ThreadFixRestClient CLIENT;
    public static final QARestClient QA_CLIENT;

    static {
        API_KEY = System.getProperty("API_KEY");
        REST_URL = System.getProperty("REST_URL");

        CLIENT = new ThreadFixRestClientImpl(REST_URL, API_KEY);
        QA_CLIENT = new QARestClientImpl(REST_URL, API_KEY);

        if (API_KEY == null) {
            throw new IllegalStateException("API_KEY system variable was null.");
        }
        if (REST_URL == null) {
            throw new IllegalStateException("REST_URL system variable was null.");
        }
    }

    private RestResponse<Organization> createTeam(String name) {
        return CLIENT.createTeam(name);
    }

    private Integer getTeamId(String name) {
        RestResponse<Organization> teamResponse = createTeam(name);

        assertTrue("Rest Response was a failure. message was: " + teamResponse.message,
                teamResponse.success);
        assertNotNull("The returned team object was null.", teamResponse.object);

        return teamResponse.object.getId();
    }

    private RestResponse<Application> createApplication(String teamId, String name, String url) {
        return CLIENT.createApplication(teamId, name, url);
    }

    private Integer getApplicationId(String teamName, String name, String url) {
        RestResponse<Application> teamResponse = createApplication(
                getTeamId(teamName).toString(), name, url);

        assertTrue("Rest Response was a failure. message was: " + teamResponse.message,
                teamResponse.success);
        assertNotNull("The returned application object was null.", teamResponse.object);

        return teamResponse.object.getId();
    }

    @Test
    public void testQueueScan() {
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        RestResponse<ScanQueueTask> queueResponse = CLIENT.queueScan(appId, "Acunetix WVS");

        assertTrue("Future scan should have been queued.", queueResponse.success);
    }

    @Test
    public void testRequestTask() {
        String scannerList = "Acunetix WVS";
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        RestResponse<ScanQueueTask> queueResponse = CLIENT.queueScan(appId, "Acunetix WVS");

        RestResponse<Task> response = QA_CLIENT.requestTask(scannerList, "");

        assertTrue(response != null && response.object != null);
    }

    @Test
    public void testTaskStatusUpdate() {
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        RestResponse<ScanQueueTask> queueResponse = CLIENT.queueScan(appId, "Acunetix WVS");

        String taskId = queueResponse.object.getId().toString();

        RestResponse<String> statusResponse = QA_CLIENT.taskStatusUpdate(taskId, "This is a test.");

        assertTrue("Status should have been changed.", statusResponse.success);
    }

    @Test
    public void testSetTaskConfig() {
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        RestResponse<ScanQueueTask> queueResponse = CLIENT.queueScan(appId, "Acunetix WVS");

        String taskId = queueResponse.object.getId().toString();

        RestResponse<String> setResponse = CLIENT.setTaskConfig(taskId, "Acunetix WVS", ScanContents.getScanFilePath());

        assertTrue("Configuration for the task should have been set.", setResponse.success);
    }

    @Test
    public void testCompleteTask() {
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        CLIENT.queueScan(appId, "Acunetix WVS");

        RestResponse<Task> requestTask = QA_CLIENT.requestTask("Acunetix WVS", "Test");
        String taskId = Integer.toString(requestTask.object.getTaskId());
        String taskKey = requestTask.object.getSecureTaskKey();

        RestResponse<ScanQueueTask> completionResponse = QA_CLIENT.completeTask(taskId, ScanContents.getScanFilePath(), taskKey);

        assertTrue("Task should be completed.", completionResponse.success);

    }

    @Test
    public void testFailTest() {
        String appName = TestUtils.getName(), teamName = TestUtils.getName();

        String appId = getApplicationId(teamName, appName, dummyUrl).toString();

        CLIENT.queueScan(appId, "Acunetix WVS");

        RestResponse<Task> requestTask = QA_CLIENT.requestTask("Acunetix WVS", "Test");
        String taskId = Integer.toString(requestTask.object.getTaskId());
        String taskKey = requestTask.object.getSecureTaskKey();

        RestResponse<String> failureResponse = QA_CLIENT.failTask(taskId, "Task Failed.", taskKey);

        assertTrue("Task should have failed.", failureResponse.success);
    }
}
