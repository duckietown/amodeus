/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.matsim;

import java.util.Objects;

import org.matsim.core.config.ReflectiveConfigGroup;

import ch.ethz.idsc.amodeus.util.math.GlobalAssert;

public class SafeConfig {
    public static SafeConfig wrap(ReflectiveConfigGroup reflectiveConfigGroup) {
        return new SafeConfig(reflectiveConfigGroup);
    }

    private final ReflectiveConfigGroup reflectiveConfigGroup;

    protected SafeConfig(ReflectiveConfigGroup reflectiveConfigGroup) {
        if (Objects.isNull(reflectiveConfigGroup))
            throw new NullPointerException("reflective group == null");
        this.reflectiveConfigGroup = reflectiveConfigGroup;
    }

    public int getInteger(String key, int alt) {
        try {
            String string = reflectiveConfigGroup.getParams().get(key);
            if (string != null)
                return Integer.parseInt(string);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return alt;
    }

    public double getDouble(String key, double alt) {
        try {
            String string = reflectiveConfigGroup.getParams().get(key);
            if (string != null)
                return Double.parseDouble(string);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return alt;
    }

    public String getString(String key, String alt) {
        try {
            String string = reflectiveConfigGroup.getParams().get(key);
            if (string != null)
                return string;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return alt;
    }

    public int getIntegerStrict(String key) {
        String string = reflectiveConfigGroup.getParams().get(key);
        GlobalAssert.that(string != null);
        return Integer.parseInt(string);
    }

    public double getDoubleStrict(String key) {
        String string = reflectiveConfigGroup.getParams().get(key);
        GlobalAssert.that(string != null);
        return Double.parseDouble(string);
    }

    public boolean getBoolStrict(String key) {
        String string = reflectiveConfigGroup.getParams().get(key);
        GlobalAssert.that(string != null);
        return Boolean.parseBoolean(string);
    }

    public String getStringStrict(String key) {
        String string = reflectiveConfigGroup.getParams().get(key);
        GlobalAssert.that(string != null);
        return string;
    }

}
