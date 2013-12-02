package com.technicolor.android.ci;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/20/13
 * Time: 2:36 PM
 */
public class JobParam implements Serializable {
    public String getStationIp() {
        return stationIp;
    }

    public void setStationIp(String stationIp) {
        this.stationIp = stationIp;
    }

    private String stationIp;
    private String projectName;
    private String folderName;
    private String className;
    private String testCaseName;

    public JobParam() {
    }

    public JobParam(String stationIp, String projectName, String folderName, String className, String testCaseName) {
        this.stationIp = stationIp;
        this.projectName = projectName;
        this.folderName = folderName;
        this.className = className;
        this.testCaseName = testCaseName;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }
}
