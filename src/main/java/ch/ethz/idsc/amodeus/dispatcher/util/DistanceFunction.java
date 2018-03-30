/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.dispatcher.util;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.matsim.api.core.v01.network.Link;

import ch.ethz.idsc.amodeus.dispatcher.core.RoboTaxi;
import ch.ethz.matsim.av.passenger.AVRequest;

public interface DistanceFunction {
    double getDistance(RoboTaxi robotaxi, AVRequest avRequest);

    double getDistance(RoboTaxi robotaxi, Link link);

    List<Double> getDistances(List<Pair<RoboTaxi, Link>> pairs);

    double getDistance(Link from, Link to);
}
