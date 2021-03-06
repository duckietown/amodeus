/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.dispatcher.core;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.matsim.api.core.v01.network.Link;

import ch.ethz.matsim.av.passenger.AVRequest;

public enum DispatcherUtils {
    ;

    /** function call leaves the state of the {@link UniversalDispatcher} unchanged. successive
     * calls to the function return the identical collection.
     * 
     * @return list of {@link AVRequest}s grouped by link */
    public static final Map<Link, List<AVRequest>> getAVRequestsAtLinks(Collection<AVRequest> avRequests) {
        return avRequests.stream() // <- intentionally not parallel to guarantee ordering of requests
                .collect(Collectors.groupingBy(AVRequest::getFromLink));
    }
}
