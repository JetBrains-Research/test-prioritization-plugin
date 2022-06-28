package org.jetbrains.teamcity.testPrioritization;

import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CustomOrder implements MethodOrderer, ClassOrderer {
    private static final String TEST_PRIORITIZATION_CONFIG = "/test-prioritization-config.txt";

    private double toRatioValue(String s) {
        String[] splitFraction = s.split("/");
        if (splitFraction.length == 2) {
            double successful = Integer.parseInt(splitFraction[0]);
            double all = Integer.parseInt(splitFraction[1]);
            return successful / all;
        } else {
            return 0.0;
        }
    }

    Map<String, Double> successProbability = new HashMap<>();

    {
        try {
            InputStream config = this.getClass().getResourceAsStream(TEST_PRIORITIZATION_CONFIG);
            if (config != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(config));
                while (reader.ready()) {
                    String line = reader.readLine();
                    int lastColon = line.lastIndexOf(":");
                    if (lastColon >= 0) {
                        String name = line.substring(0, lastColon);
                        String ratio = line.substring(lastColon + 1);
                        successProbability.put(name, toRatioValue(ratio));
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    private String qualifiedName(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    private double getMethodSuccessProbability(MethodDescriptor descriptor) {
        return successProbability.getOrDefault(qualifiedName(descriptor.getMethod()), 0.0);
    }

    private double getClassSuccessValue(ClassDescriptor descriptor) {
        int value = 0;
        for (Method method : descriptor.getTestClass().getDeclaredMethods()) {
            value += 1.0 - (successProbability.getOrDefault(qualifiedName(method), 0.0));
        }
        return -value;
    }

    @Override
    public void orderMethods(MethodOrdererContext context) {
        if (!successProbability.isEmpty()) {
            context.getMethodDescriptors().sort(Comparator.comparingDouble(this::getMethodSuccessProbability));
        }
    }

    @Override
    public void orderClasses(ClassOrdererContext context) {
        if (!successProbability.isEmpty()) {
            context.getClassDescriptors().sort(Comparator.comparingDouble(this::getClassSuccessValue));
        }
    }
}
