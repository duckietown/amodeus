/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.dispatcher.core;

import org.matsim.core.config.ReflectiveConfigGroup;

import ch.ethz.idsc.amodeus.dispatcher.util.DistanceHeuristics;
import ch.ethz.idsc.amodeus.matsim.SafeConfig;

public class DispatcherConfig extends SafeConfig {

    private static final String DISPATCH_PERIOD = "dispatchPeriod";
    private static final String REBALANCING_PERIOD = "rebalancingPeriod";
    private static final String DISTANCE_HEURISTICS = "distanceHeuristics";

    public static DispatcherConfig wrap(ReflectiveConfigGroup reflectiveConfigGroup) {
        return new DispatcherConfig(reflectiveConfigGroup);
    }

    // ---
    protected DispatcherConfig(ReflectiveConfigGroup reflectiveConfigGroup) {
        super(reflectiveConfigGroup);
    }

    public int getDispatchPeriod(int alt) {
        return getInteger(DISPATCH_PERIOD, alt);
    }

    public int getRebalancingPeriod(int alt) {
        return getInteger(REBALANCING_PERIOD, alt);
    }

    public DistanceHeuristics getDistanceHeuristics(DistanceHeuristics alt) {
        try {
            return DistanceHeuristics.valueOf(getString(DISTANCE_HEURISTICS, null).toUpperCase());
        } catch (Exception exception) {
            // ---
        }
        return alt;
    }

}
