/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.dispatcher.util;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.router.FastAStarEuclideanFactory;
import org.matsim.core.router.FastDijkstraFactory;

import ch.ethz.matsim.av.plcpc.DefaultParallelLeastCostPathCalculator;
import ch.ethz.matsim.av.plcpc.ParallelLeastCostPathCalculator;

public enum DistanceHeuristics {
    EUCLIDEAN {
        @Override
        public DistanceFunction getDistanceFunction(Network network) {
            return new EuclideanDistanceFunction();
        }
    },
    DIJKSTRA {
        @Override
        public DistanceFunction getDistanceFunction(Network network) {
            return new NetworkDistanceFunction(network, new FastDijkstraFactory());
        }
    },
    ASTAR {
        @Override
        public DistanceFunction getDistanceFunction(Network network) {
            return new NetworkDistanceFunction(network, new FastAStarEuclideanFactory());
        }
    },
    ASTARLANDMARKS {
        @Override
        public DistanceFunction getDistanceFunction(Network network) {
            return new NetworkDistanceFunction(network, new AStarLandmarksFactory(300, 1.0, Runtime.getRuntime().availableProcessors()));
        }
    };

    public abstract DistanceFunction getDistanceFunction(Network network);
}