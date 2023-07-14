package opensource.listeners.template;

import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.util.Coordinate;

/*
 * Simulation listener that launcher a rocket from a specific altitude.
 */
public class SimulationListenerTemplate extends AbstractSimulationListener{

	/** Launch altitude in meters */
	private static final double ALTITUDE = 690.0;

	@Override
	public void startSimulation(SimulationStatus status) throws SimulationException {
		Coordinate position = status.getRocketPosition();
		position = position.add(0,0,ALTITUDE);
		status.setRocketPosition(position);
	}

}
