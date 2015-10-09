package com.denimgroup.threadfix.selenium.utils;

import com.denimgroup.threadfix.importer.util.JsonUtils;
import com.denimgroup.threadfix.properties.PropertiesManager;
import com.denimgroup.threadfix.remote.HttpRestUtils;
import com.denimgroup.threadfix.remote.response.RestResponse;
import com.denimgroup.threadfix.service.defects.utils.RestUtils;
import com.denimgroup.threadfix.service.defects.utils.RestUtilsImpl;
import com.sun.jna.platform.dnd.GhostedDragImage;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONObject;
import sun.plugin.dom.exception.InvalidStateException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by rtimmons on 10/9/2015.
 */
public class DefectTrackerUtils {

    final static String JIRA_USERNAME = System.getProperty("JIRA_USERNAME");
    final static String JIRA_PASSWORD = System.getProperty("JIRA_PASSWORD");
    static String JIRA_URL = System.getProperty("JIRA_URL");
    final static HttpRestUtils httpRestUtils = new HttpRestUtils(new PropertiesManager());

    static {
        if (JIRA_USERNAME.length() <= 0) {
            throw new InvalidStateException("Please set JIRA_USERNAME property.");
        }
        if (JIRA_PASSWORD.length() <= 0) {
            throw new InvalidStateException("Please set JIRA_USERNAME property.");
        }
        if (JIRA_URL.length() <= 0) {
            throw new InvalidStateException("Please set JIRA_USERNAME property.");
        }
        // Ensure url has trailing slash
        if (!JIRA_URL.substring(JIRA_URL.length()-1).equals("/")) {
            JIRA_URL = JIRA_URL + "/";
        }
    }

    public static JSONObject getLatestJiraDefect() {
        final String JIRA_QUERY = HttpRestUtils.encode("order by created");
        final String PATH = JIRA_URL + "rest/api/2/search?jql="
                + JIRA_QUERY
                + "&maxResults=1";
        return executeGetMethod(PATH);
    }

    private static JSONObject executeGetMethod(String url) {
        GetMethod get = new GetMethod(url);
        get.setRequestHeader("Content-type", "application/json; charset=UTF-8");
        HttpClient client = new HttpClient();
        try {
            client.executeMethod(get);
            InputStream stream = get.getResponseBodyAsStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder stringBuilder = new StringBuilder();
            String output;
            while ((output = bufferedReader.readLine()) != null) {
                stringBuilder.append(output);
            }
            return JsonUtils.getJSONObject(stringBuilder.toString());
        } catch (IOException ioEx) {
            throw new InvalidStateException("IO Exception");
        }
    }
}
