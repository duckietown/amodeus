package ch.ethz.idsc.amodeus.dispatcher.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

import ch.ethz.idsc.amodeus.dispatcher.core.RoboTaxi;
import ch.ethz.matsim.av.passenger.AVRequest;
import ch.ethz.matsim.av.plcpc.ParallelLeastCostPathCalculator;

public class ParallelNetworkDistanceFunction implements DistanceFunction {
    final private ParallelLeastCostPathCalculator router;

    public ParallelNetworkDistanceFunction(ParallelLeastCostPathCalculator router) {
        this.router = router;
    }

    final private static Logger log = Logger.getLogger(ParallelNetworkDistanceFunction.class);

    private double staticCache[][] = null;

    public void loadCache(Network network, String networkPath, String dataPath) {
        Network vNetwork = NetworkUtils.createNetwork();
        new MatsimNetworkReader(vNetwork).readFile(networkPath);

        log.info("Starting to read data ...");

        try {
            List<Id<Link>> linkIds = new LinkedList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("distance_links.txt")));

            String line = null;
            while ((line = reader.readLine()) != null) {
                String id = line.trim();

                if (id.length() > 0) {
                    linkIds.add(Id.createLinkId(id));
                }
            }

            reader.close();

            for (int i = 0; i < linkIds.size(); i++) {
                Link link = network.getLinks().get(linkIds.get(i));
                link.getAttributes().putAttribute("cacheid", i);
            }

            DataInputStream stream = new DataInputStream(new FileInputStream(dataPath));

            int numberOfLinks = vNetwork.getLinks().size();
            long processed = 0;

            staticCache = new double[numberOfLinks][numberOfLinks];

            for (int i = 0; i < numberOfLinks; i++) {
                for (int j = 0; j < numberOfLinks; j++) {
                    staticCache[i][j] = stream.readDouble();
                }

                processed += 1;

                double progress = 100.0 * (double) processed / (double) numberOfLinks;
                log.info(String.format("%d / %d (%.2f%%)", processed, numberOfLinks, progress));
            }

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private double calculateDistance(LeastCostPathCalculator.Path path) {
        double distance = 0.0;

        for (Link link : path.links) {
            distance += link.getLength();
        }

        return distance;
    }

    @Override
    public double getDistance(RoboTaxi robotaxi, AVRequest avRequest) {
        return getDistance(robotaxi, avRequest.getFromLink());
    }

    @Override
    public double getDistance(RoboTaxi robotaxi, Link link) {
        return getDistance(robotaxi.getDivertableLocation(), link);
    }

    private Map<Pair<Link, Link>, Double> cache = new HashMap<>();

    private boolean hasCache(Link from, Link to) {
        Integer fromIndex = (Integer) from.getAttributes().getAttribute("cacheid");
        Integer toIndex = (Integer) to.getAttributes().getAttribute("cacheid");

        if (fromIndex != null && toIndex != null && !Double.isNaN(staticCache[fromIndex][toIndex])) {
            return true;
        }

        Pair<Link, Link> pair = Pair.of(from, to);
        return cache.containsKey(pair);
    }

    private double setCache(Link from, Link to, Future<Path> future) {
        try {
            double distance = calculateDistance(future.get());
            cache.put(Pair.of(from, to), distance);
            return distance;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private double getCache(Link from, Link to) {
        Integer fromIndex = (Integer) from.getAttributes().getAttribute("cacheid");
        Integer toIndex = (Integer) to.getAttributes().getAttribute("cacheid");

        if (fromIndex != null && toIndex != null) {
            double value = staticCache[fromIndex][toIndex];

            if (!Double.isNaN(value)) {
                return value;
            }
        }

        return cache.get(Pair.of(from, to));
    }

    @Override
    public List<Double> getDistances(List<Pair<RoboTaxi, Link>> pairs) {
        List<Double> distances = new ArrayList<>(Collections.nCopies(pairs.size(), 0.0));

        List<Future<LeastCostPathCalculator.Path>> futures = pairs.stream().map(p -> {
            return getNullableFuture(p.getLeft().getDivertableLocation(), p.getRight());
        }).collect(Collectors.toList());

        for (int i = 0; i < distances.size(); i++) {
            Pair<RoboTaxi, Link> pair = pairs.get(i);

            if (futures.get(i) != null) {
                distances.set(i, setCache(pair.getLeft().getDivertableLocation(), pair.getRight(), futures.get(i)));
            } else {
                distances.set(i, getCache(pair.getLeft().getDivertableLocation(), pair.getRight()));
            }
        }

        return distances;
    }

    private Future<LeastCostPathCalculator.Path> getNullableFuture(Link from, Link to) {
        if (hasCache(from, to)) {
            return null;
        } else {
            return router.calcLeastCostPath(from.getFromNode(), to.getFromNode(), 0.0, null, null);
        }
    }

    @Override
    public double getDistance(Link from, Link to) {
        Future<LeastCostPathCalculator.Path> future = getNullableFuture(from, to);

        if (future == null) {
            return getCache(from, to);
        } else {
            return setCache(from, to, future);
        }
    }
}
