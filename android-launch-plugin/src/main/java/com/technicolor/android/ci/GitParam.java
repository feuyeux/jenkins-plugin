package com.technicolor.android.ci;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 10/30/13
 * Time: 1:37 PM
 */
public class GitParam implements Serializable {
    private String caseId;

    public GitParam() {

    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
}
