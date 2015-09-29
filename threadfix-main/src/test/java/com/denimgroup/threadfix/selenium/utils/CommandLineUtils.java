package com.denimgroup.threadfix.selenium.utils;

import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static org.junit.Assume.assumeTrue;

import com.denimgroup.threadfix.importer.util.JsonUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by rtimmons on 8/17/2015.
 */
public class CommandLineUtils {
    protected final SanitizedLogger log = new SanitizedLogger(CommandLineUtils.class);

    private static final String DIRECTORY;
    private final String INCREASE_RESULTS_ARG = "numberVulnerabilities=100";

    public static String cliVersion;

    //===========================================================================================================
    // Startup
    //===========================================================================================================

    static {
        DIRECTORY = System.getProperty("CLI_JAR_DIRECTORY");
        if (DIRECTORY == null) {
            throw new IllegalStateException("CLI_JAR_DIRECTORY system property was not set properly.");
        }
        System.out.println("Setting directory as: " + DIRECTORY);
    }

    public List<String> buildStartArgs(String cliVersion) {
        List<String> startArgs = list();
        if (System.getProperty("os.name").startsWith("Windows")) {
            startArgs.addAll(list("CMD", "/C"));
        }
        startArgs.addAll(list("java", "-jar"));
        startArgs.add(cliVersion + ".jar");
        System.out.println("Built start args with version as: " + cliVersion + ".jar");
        return startArgs;
    }

    // Executes command and returns JSON object
    public JSONObject executeCommand(String workingDirectory, String... args) {
        JSONObject jsonResponse = null;

        List<String> finalArgs = list();
        finalArgs.addAll(buildStartArgs(cliVersion));
        Collections.addAll(finalArgs, args);
        ProcessBuilder processBuilder = new ProcessBuilder(finalArgs);
        processBuilder.directory(new File(workingDirectory));
        try {
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            System.out.println("Output of running command is:");

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("{")) {
                    System.out.println("SUCCESS: " + JsonUtils.getStringProperty(line, "success"));
                    jsonResponse = JsonUtils.getJSONObject(line);
                    return jsonResponse;
                }
            }
        } catch (IOException e) {
            log.error("Process Build command could not be executed gracefully.", e);
        }
        return jsonResponse;
    }

    public JSONObject executeJarCommand(String... args) {
        return executeCommand(DIRECTORY, args);
    }

    public static void checkVersion(int introductionVersion) {
        int currentVersion = Integer.valueOf(cliVersion);
        assumeTrue(introductionVersion <= currentVersion);
    }

    //===========================================================================================================
    // Configuration
    //===========================================================================================================

    public void configureAPIKey(String apiKey) {
        executeJarCommand("-set", "key", apiKey);
    }

    public void setUrl(String url) {
        executeJarCommand("-set", "url", url);
    }

    //===========================================================================================================
    // JSON Helper Methods
    //===========================================================================================================

    public int getObjectId(JSONObject object) {
        try {
            return object.getJSONObject("object").getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getObjectField(JSONObject object, String field) {
        try {
            return object.getJSONObject("object").getString(field);
        } catch (JSONException e) {
            e.printStackTrace();
            return "Field does not exist";
        }
    }

    public JSONArray getObjectArray(JSONObject object) {
        try {
            return object.getJSONArray("object");
        } catch (JSONException e) {
            e.printStackTrace();
            log.error("Problem trying to obtain JSON list.", e);
            return null;
        }
    }

    public int getNthObjectId(JSONObject object, int n) {
        int result = 0;
        try {
            result = object.getJSONArray("object").getJSONObject(0).getInt("id");
        } catch (JSONException e) {
            log.error("Error accessing JSON object at index " + n + ".");
        }
        assertTrue("Error accessing JSON object at index " + n + ".", result != 0);
        return result;
    }

    public boolean isCommandResponseSuccessful(JSONObject response) {
        try {
            return response.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    //===========================================================================================================
    // Test Helper Methods
    //===========================================================================================================

    public int getNumberOfVulnerabilities(JSONObject object) {
        return getObjectArray(object).length();
    }

    public boolean isTagIdPresentInObjectArray(JSONObject object, int id) {
        JSONArray array = null;
        try {
            array = object.getJSONObject("object").getJSONArray("Application Tag");
        } catch (JSONException e) {
            log.error("Couldn't access Application Tag array in JSON object.");
        }
        for (int i = 0; i < array.length(); i++) {
            int inspect = 0;
            try {
                inspect = array.getJSONObject(i).getInt("id");
            } catch (JSONException e) {
                log.error("Couldn't get ID from object #" + i + " in JSON array.");
            }
            if (inspect == id) {
                return true;
            }
        }
        return false;
    }

    //===========================================================================================================
    // REST Actions
    //===========================================================================================================

    public JSONObject createTeam(String teamName) {
        return executeJarCommand("-ct", teamName);
    }

    public JSONObject createApplication(int teamId, String appName, String appUrl) {
        return executeJarCommand("-ca", String.valueOf(teamId), appName, appUrl);
    }

    public JSONObject createWaf(String wafName, String wafType) {
        return executeJarCommand("-cw", wafName, wafType);
    }

    public JSONObject searchTagByName(String tagName) {
        return executeJarCommand("-stg", "name", tagName);
    }

    public JSONObject uploadScanFile(int applicationId, String filepath) {
        return executeJarCommand("-u", String.valueOf(applicationId), filepath);
    }

    public JSONObject createTag(String tagName) {
        return executeJarCommand("-ctg", tagName);
    }

    public JSONObject createTag(String tagName, String tagType) {
        return executeJarCommand("-ctg", tagName, tagType);
    }

    public JSONObject searchTeamById(String id) {
        return executeJarCommand("-st", "id", id);
    }

    public JSONObject searchTeamByName(String name) {
        return executeJarCommand("-st", "name", name);
    }

    public JSONObject searchAppById(String id) {
        return executeJarCommand("-sa", "id", id);
    }

    public JSONObject searchAppByName(String appName, String teamName) {
        return executeJarCommand("-sa", "name", appName, teamName);
    }

    public JSONObject searchAppByUniqueId(String id, String teamName) {
        return executeJarCommand("-sa", "uniqueId", id, teamName);
    }

    public JSONObject searchWafById(String id) {
        return executeJarCommand("-sw", "id", id);
    }

    public JSONObject searchWafByName(String name) {
        return executeJarCommand("-sw", "name", name);
    }

    public JSONObject getWafRules(int wafId) {
        return executeJarCommand("-r", String.valueOf(wafId));
    }

    public JSONObject queueScan(int appId, String scannerName) {
        return executeJarCommand("-q", String.valueOf(appId), scannerName);
    }

    public JSONObject addUrlToApp(int appId, String url) {
        return executeJarCommand("-au", String.valueOf(appId), url);
    }

    public JSONObject lookupTags() {
        return executeJarCommand("-tg");
    }

    public JSONObject searchTagById(int tagID) {
        return executeJarCommand("-stg", "id", String.valueOf(tagID));
    }

    public JSONObject setTaskConfigFile(int appId, String scanner, String filepath) {
        return executeJarCommand("-stc", String.valueOf(appId), scanner, filepath);
    }

    public JSONObject setParameters(int appId, String framework) {
        return executeJarCommand("-sp", String.valueOf(appId), framework);
    }

    public JSONObject setParameters(int appId, String framework, String repositoryUrl) {
        return executeJarCommand("-sp", String.valueOf(appId), framework, repositoryUrl);
    }

    public JSONObject addTagToApplication(int appId, int tagId) {
        return executeJarCommand("-aat", String.valueOf(appId), String.valueOf(tagId));
    }

    public JSONObject vulnSearchById(String id) {
        return executeJarCommand("--search", "genericVulnerabilityIds=" + id, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByTeamId(int teamId) {
        return executeJarCommand("--search", "teamIds=" + teamId, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByApplicationId(int appId) {
        return executeJarCommand("--search", "applicationIds=" + appId, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByScannerName(String scannerName) {
        return executeJarCommand("--search", "scannerNames=" + scannerName, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchBySeverity(String severity) {
        return executeJarCommand("--search", "genericSeverityValues=" + severity, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByNumberOfResults(int numberOfResults) {
        return executeJarCommand("--search", "numberVulnerabilities=" + numberOfResults);
    }

    public JSONObject removeTagFromApplication(int appId, int tagId) {
        return executeJarCommand("-rat", String.valueOf(appId), String.valueOf(tagId));
    }

    public JSONObject updateTag(int tagId, String newName) {
        return executeJarCommand("-utg", String.valueOf(tagId), newName);
    }

    public JSONObject removeTag(int tagId) {
        return executeJarCommand("-rtg", String.valueOf(tagId));
    }

    public JSONObject vulnSearchByParameter(String parameter) {
        return executeJarCommand("--search", "parameter=" + parameter, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByPath(String path) {
        return executeJarCommand("--search", "path=" + path, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByStartDate(String startDate) {
        return executeJarCommand("--search", "startDate=" + startDate, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByEndDate(String endDate) {
        return executeJarCommand("--search", "endDate=" + endDate, INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchShowOnlyHidden(String status) {
        return executeJarCommand("--search", "showOpen=false", "showClosed=true", INCREASE_RESULTS_ARG);
    }

    public JSONObject vulnSearchByNumberMerged(int numberMerged) {
        return executeJarCommand("--search", "numberMerged=" + String.valueOf(numberMerged), INCREASE_RESULTS_ARG);
    }
}
