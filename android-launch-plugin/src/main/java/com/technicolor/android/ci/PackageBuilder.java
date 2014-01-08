package com.technicolor.android.ci;

import com.technicolor.android.ci.util.ParamTools;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: erichan
 * Date: 11/22/13
 * Time: 11:21 AM
 */
public class PackageBuilder extends hudson.tasks.Builder {
    public static final String LOG_PREFIX = "PackageBuilder::";
    @Extension
    public static final PackageDescriptor DESCRIPTOR = new PackageDescriptor();
    /*Package*/
    private String BUILD_SHELL = "buildShell";

    @DataBoundConstructor
    public PackageBuilder() {
    }

    @Override
    public Descriptor<Builder> getDescriptor() {
        return (PackageDescriptor) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        handleUpgrade(build, listener);
        return true;
    }

    private void handleUpgrade(AbstractBuild build, BuildListener listener) throws IOException, InterruptedException {
        listener.getLogger().println(LOG_PREFIX + "START BUILDING...");
        Map<String, String> buildVariables = build.getBuildVariables();
        String shell = buildVariables.get(BUILD_SHELL);
        String result = CmdExec.executeAndGetLastLine(listener, shell);
        listener.getLogger().println(LOG_PREFIX + "BUILD PACKAGE URL=" + result);

        ParamTools.store(build, PluginConstant.CI_BUILD_FILE, result);
        listener.getLogger().println(LOG_PREFIX + "BUILD DONE.");
    }
}

