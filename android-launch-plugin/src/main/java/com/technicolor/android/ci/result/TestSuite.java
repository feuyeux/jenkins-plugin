package com.technicolor.android.ci.result;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/13/13
 * Time: 3:16 PM
 */
@XmlRootElement(name = "testsuite")
public class TestSuite implements Serializable {
    private String name;
    private int failures;
    private int skipped;
    private int tests;
    private List<TestCase> testCaseList;

    public TestSuite(String name) {
        this.name = name;
    }

    public TestSuite() {
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public int getFailures() {
        return failures;
    }

    public void setFailures(int failures) {
        this.failures = failures;
    }

    @XmlAttribute
    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    @XmlAttribute
    public int getTests() {
        return tests;
    }

    public void setTests(int tests) {
        this.tests = tests;
    }

    @XmlElement(name = "testcase")
    public List<TestCase> getTestCaseList() {
        return testCaseList;
    }

    public void setTestCaseList(List<TestCase> testCaseList) {
        this.testCaseList = testCaseList;
    }
}
