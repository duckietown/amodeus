package ch.ethz.idsc.amodeus.traveldata;

import java.util.Collection;
import java.util.LinkedList;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.router.MainModeIdentifierImpl;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.router.TripStructureUtils.Trip;
import org.matsim.pt.PtConstants;

public class TravelDataItem {
    public double time;
    public Link startLink;
    public Link endLink;

    static public Collection<TravelDataItem> createFromPopulation(Population population, Network network) {
        StageActivityTypes stageActivityTypes = new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE);
        MainModeIdentifier mainModeIdentifier = new MainModeIdentifierImpl();

        Collection<TravelDataItem> items = new LinkedList<>();

        for (Person person : population.getPersons().values()) {
            Plan plan = person.getSelectedPlan();

            for (Trip trip : TripStructureUtils.getTrips(plan, stageActivityTypes)) {
                String mode = mainModeIdentifier.identifyMainMode(trip.getTripElements());

                if (mode.equals("av")) {
                    TravelDataItem item = new TravelDataItem();
                    item.time = trip.getOriginActivity().getEndTime();
                    item.startLink = network.getLinks().get(trip.getOriginActivity().getLinkId());
                    item.endLink = network.getLinks().get(trip.getDestinationActivity().getLinkId());
                    items.add(item);
                }
            }
        }

        return items;
    }
}
