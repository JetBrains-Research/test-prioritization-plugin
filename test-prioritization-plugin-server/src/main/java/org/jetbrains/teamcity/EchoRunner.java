package org.jetbrains.teamcity;

import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EchoRunner extends RunType {

    private final PluginDescriptor descriptor;

    public EchoRunner(RunTypeRegistry registry, PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        registry.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return Constants.RUNNER_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Echo Runner";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "This is Echo Runner";
    }

    @Nullable
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return properties -> new ArrayList<>();
    }

    @Nullable
    @Override
    public String getEditRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("editEchoRunnerParameters.jsp");
    }

    @Nullable
    @Override
    public String getViewRunnerParamsJspFilePath() {
        return descriptor.getPluginResourcesPath("viewEchoRunnerParameters.jsp");
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        return new HashMap<>();
    }
}
