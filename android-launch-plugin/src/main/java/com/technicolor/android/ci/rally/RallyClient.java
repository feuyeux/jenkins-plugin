package com.technicolor.android.ci.rally;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.response.Response;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * author:feuyeux
 * <p/>
 * rest client: https://github.com/RallyTools/RallyRestToolkitForJava
 * rest client document: https://github.com/RallyTools/RallyRestToolkitForJava/wiki/User-Guide
 * rallydev document: https://rally1.rallydev.com/slm/doc/webservice
 */
public class RallyClient {
    public static final String WSAPI_VERSION = "v2.0";
    public static final String RALLY_URL = "https://rally1.rallydev.com";
    public static final String WORKSPACE_REF = "/workspace/11627233416";//Tablet
    public static final String PROJECT_REF = "/project/15180679176"; //Name=MCBB
    final RallyTestCase rallyTestCase;
    final RallyTestResult rallyTestResult;
    final RallyDefect rallyDefect;
    final RallyTestSet rallyTestSet;
    private final PrintStream log;
    private RallyRestApi restApi;
    private String userName;
    private String password;
    private String proxyURL;
    private String proxyUser;
    private String proxyPassword;

    public RallyClient(String userName, String password, String proxyURL, String proxyUser, String proxyPassword, PrintStream log) throws URISyntaxException {
        this.userName = userName;
        this.password = password;
        this.proxyURL = proxyURL;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        getRestApi();
        this.log = log;
        rallyTestCase = new RallyTestCase(restApi, log);
        rallyTestResult = new RallyTestResult(restApi, log);
        rallyDefect = new RallyDefect(restApi, log);
        rallyTestSet = new RallyTestSet(restApi, log);
    }

    private void getRestApi() throws URISyntaxException {
        restApi = new RallyRestApi(new URI(RALLY_URL), userName, password);
        restApi.setProxy(new URI(proxyURL), proxyUser, proxyPassword);
        restApi.setWsapiVersion(WSAPI_VERSION);
        restApi.setApplicationName("Tablet Jenkins plugin");
    }

    public void close() throws IOException {
        restApi.close();
    }

    /*TEST CASE*/
    public JsonObject getCaseByObjectId(String testCaseId) {
        return rallyTestCase.getCaseByObjectId(testCaseId);
    }

    public JsonObject getCaseByFormattedId(String testCaseId) throws IOException {
        return rallyTestCase.getCaseByFormattedId(testCaseId);
    }

    /*DEFECT*/
    public JsonObject getDefectByTestCase(String testCaseRef) throws IOException {
        return rallyDefect.getDefectByTestCase(testCaseRef);
    }

    public JsonObject createDefect(String testCaseRef, String testCaseResultRef) throws IOException {
        return rallyDefect.createDefect(testCaseRef, testCaseResultRef, getUserRef("lu.han@technicolor.com"));
    }

    /*TEST CASE RESULT*/
    public JsonObject createTestCaseResult(String testCaseRef) throws IOException, URISyntaxException {
        return rallyTestResult.createTestCaseResult(testCaseRef, getUserRef("lu.han@technicolor.com"));
    }

    public JsonObject updateTestCaseResult(String ref, JsonObject updateResult) throws IOException {
        return rallyTestResult.updateTestCaseResult(ref, updateResult);
    }

    /*TEST SET*/
    public JsonArray getTestSetByTestSetId(String testSetId) throws IOException {
        return rallyTestSet.getTestSetByTestSetId(testSetId);
    }

    public JsonArray getTestCasesByTestSetFormattedId(String testSetId) throws IOException {
        return rallyTestSet.getTestCasesByTestSetFormattedId(testSetId);

    }

    private String getUserRef(String user) throws IOException {
        QueryRequest userRequest = new QueryRequest("user");
        userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName"));
        userRequest.setQueryFilter(new QueryFilter("UserName", "=", user));
        QueryResponse userQueryResponse = restApi.query(userRequest);
        JsonArray userQueryResults = userQueryResponse.getResults();
        JsonElement userQueryElement = userQueryResults.get(0);
        JsonObject userQueryObject = userQueryElement.getAsJsonObject();
        return userQueryObject.get("_ref").getAsString();
    }

    private void printWarningsOrErrors(Response response, StringBuilder result) {
        if (response.wasSuccessful()) {
            result.append("\nSuccess.");
            String[] warningList;
            warningList = response.getWarnings();
            for (int i = 0; i < warningList.length; i++) {
                result.append("\twarning:\n" + warningList[i]);
            }
        } else {
            String[] errorList;
            errorList = response.getErrors();
            if (errorList.length > 0) {
                result.append("\nError.");

            }
            for (int i = 0; i < errorList.length; i++) {
                result.append("\terror:\n" + errorList[i]);
            }
        }
    }

}
