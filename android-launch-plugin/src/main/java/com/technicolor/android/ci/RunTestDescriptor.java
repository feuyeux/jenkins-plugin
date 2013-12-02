package com.technicolor.android.ci;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/11/13
 * Time: 3:31 PM
 */
public class RunTestDescriptor extends BuildStepDescriptor<Builder> {
    public static final String PLUG_IN_NAME = "Tablet Android Run Test";

    public RunTestDescriptor() {
        super(RunTestBuilder.class);
        load();
    }

    @Override
    public String getDisplayName() {
        return PLUG_IN_NAME;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        return Boolean.TRUE;
    }
}
