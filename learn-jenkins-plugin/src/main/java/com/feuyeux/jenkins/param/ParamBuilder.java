package com.feuyeux.jenkins.param;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class ParamBuilder extends hudson.tasks.Builder {
    public static final String LOG_PREFIX = "ParamBuilder::";
    @Extension
    public static final ParamBuilderDescriptor DESCRIPTOR = new ParamBuilderDescriptor();
    private final String BUILDING_TAG = "BUILDING_TAG";
    private final String buildingTag;

    @DataBoundConstructor
    public ParamBuilder(String buildingTag) {
        this.buildingTag = buildingTag;
    }

    public String getBuildingTag() {
        return buildingTag;
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return (ParamBuilderDescriptor) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().format("Building Tag current: %s", buildingTag);
        listener.getLogger().println();
        String tag = ParamTools.retrieve(build, BUILDING_TAG);
        if (tag != null) {
            listener.getLogger().format("Building Tag cached: %s", tag);
            listener.getLogger().println();
        } else {
            listener.getLogger().println("Saving building tag...");
            ParamTools.store(build, BUILDING_TAG, buildingTag);
        }
        return true;
    }
}
