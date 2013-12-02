package com.technicolor.android.ci;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

/**
 * Upgrade Descriptor
 * <p/>
 * User: erichan
 * Date: 11/22/13
 * Time: 3:12 PM
 */
public class PackageDescriptor extends BuildStepDescriptor<Builder> {
    public static final String PLUG_IN_NAME = "Tablet Android Build Package";

    public PackageDescriptor() {
        super(PackageBuilder.class);
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
