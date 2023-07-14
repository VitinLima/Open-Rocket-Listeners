package opensource.extensions.template;

//package net.sf.openrocket.simulation.extension.template;

import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.util.Coordinate;

/**
 * Simulation extension that launches a rocket from a specific altitude.
 */
public class ExtensionTemplate extends AbstractSimulationExtension {

    public void initialize(SimulationConditions conditions) throws SimulationException {
        conditions.getSimulationListenerList().add(new SimulationExtensionTemplateListener());
    }

    @Override
    public String getName() {
        return "Simulation Extension Template";
    }

    @Override
    public String getDescription() {
        return "Simple extension example for air-start";
    }

    public double getLaunchAltitude() {
        return config.getDouble("launchAltitude", 1000.0);
    }

    public void setLaunchAltitude(double launchAltitude) {
        config.put("launchAltitude", launchAltitude);
        fireChangeEvent();
    }
        
    private class SimulationExtensionTemplateListener extends AbstractSimulationListener {

        @Override
        public void startSimulation(SimulationStatus status) throws SimulationException {

            status.setRocketPosition(new Coordinate(0, 0, getLaunchAltitude()));
        }
    }
}
