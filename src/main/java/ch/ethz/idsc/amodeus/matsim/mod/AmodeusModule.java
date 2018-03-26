/* amodeus - Copyright (c) 2018, ETH Zurich, Institute for Dynamic Systems and Control */
package ch.ethz.idsc.amodeus.matsim.mod;

import java.util.Collection;
import java.util.Collections;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.dvrp.run.DvrpModule;
import org.matsim.core.config.Config;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.mobsim.qsim.AbstractQSimPlugin;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.TransportModeNetworkFilter;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import ch.ethz.idsc.amodeus.dispatcher.util.DistanceFunction;
import ch.ethz.idsc.amodeus.dispatcher.util.EuclideanDistanceFunction;
import ch.ethz.matsim.av.framework.AVModule;
import ch.ethz.matsim.av.framework.AVQSimPlugin;

public class AmodeusModule extends AbstractModule {
    @Override
    public void install() {
        // ---
    }

    @Provides
    @Singleton
    public Collection<AbstractQSimPlugin> provideQSimPlugins(Config config) {
        /* We construct the same QSim as for the AV package, but we replace the original
         * QSim plugin with an adjusted version. */

        Collection<AbstractQSimPlugin> plugins = new AVModule().provideQSimPlugins(config);
        plugins.removeIf(p -> p instanceof AVQSimPlugin);
        plugins.add(new AmodeusQSimPlugin(config));
        return plugins;
    }

    @Provides
    @Singleton
    @Named(DvrpModule.DVRP_ROUTING)
    public Network provideAVNetwork(Network fullNetwork) {
        TransportModeNetworkFilter filter = new TransportModeNetworkFilter(fullNetwork);

        Network filtered = NetworkUtils.createNetwork();
        filter.filter(filtered, Collections.singleton(TransportMode.car));

        return filtered;
    }

    @Provides
    @Singleton
    public DistanceFunction provideDistanceFunction() {
        return new EuclideanDistanceFunction();
    }
}
