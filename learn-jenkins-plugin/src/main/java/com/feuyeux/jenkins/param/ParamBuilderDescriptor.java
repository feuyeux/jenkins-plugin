package com.feuyeux.jenkins.param;


import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

public class ParamBuilderDescriptor extends BuildStepDescriptor<Builder> {
    public static final String PLUG_IN_NAME = "Param Builder";

    public ParamBuilderDescriptor() {
        super(ParamBuilder.class);
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
