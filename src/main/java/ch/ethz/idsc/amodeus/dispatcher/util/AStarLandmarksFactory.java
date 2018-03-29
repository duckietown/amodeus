package ch.ethz.idsc.amodeus.dispatcher.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.matsim.api.core.v01.network.Network;
import org.matsim.core.router.ArrayFastRouterDelegateFactory;
import org.matsim.core.router.FastAStarLandmarks;
import org.matsim.core.router.FastRouterDelegateFactory;
import org.matsim.core.router.util.ArrayRoutingNetworkFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.PreProcessLandmarks;
import org.matsim.core.router.util.RoutingNetwork;
import org.matsim.core.router.util.RoutingNetworkFactory;
import org.matsim.core.router.util.RoutingNetworkNode;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;

public class AStarLandmarksFactory implements LeastCostPathCalculatorFactory {
    final private RoutingNetworkFactory routingNetworkFactory = new ArrayRoutingNetworkFactory();
    final private int numberOfLandmarks;
    final private double overdoFactor;

    private Network network;

    private RoutingNetwork routingNetwork;
    private PreProcessLandmarks preProcessLandmarks;

    public AStarLandmarksFactory(int numberOfLandmarks, double overdofactor) {
        this.numberOfLandmarks = numberOfLandmarks;
        this.overdoFactor = overdofactor;
    }

    @Override
    public synchronized LeastCostPathCalculator createPathCalculator(Network network, TravelDisutility travelCosts, TravelTime travelTimes) {
        if (this.network == null) {
            routingNetwork = routingNetworkFactory.createRoutingNetwork(network);

            preProcessLandmarks = new PreProcessLandmarks(travelCosts);
            preProcessLandmarks.setNumberOfThreads(Runtime.getRuntime().availableProcessors());
            preProcessLandmarks.run(network);

            for (RoutingNetworkNode node : routingNetwork.getNodes().values()) {
                node.setDeadEndData(preProcessLandmarks.getNodeData(node.getNode()));
            }
        } else if (!this.network.equals(network)) {
            throw new IllegalStateException();
        }

        FastRouterDelegateFactory fastRouterFactory = new ArrayFastRouterDelegateFactory();

        try {
            Constructor<FastAStarLandmarks> constructor = FastAStarLandmarks.class.getDeclaredConstructor(RoutingNetwork.class, PreProcessLandmarks.class, TravelDisutility.class,
                    TravelTime.class, double.class, FastRouterDelegateFactory.class);
            
            constructor.setAccessible(true);
            return constructor.newInstance(routingNetwork, preProcessLandmarks, travelCosts, travelTimes, overdoFactor, fastRouterFactory);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
