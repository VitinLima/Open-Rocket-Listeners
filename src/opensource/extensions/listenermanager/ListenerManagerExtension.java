package opensource.extensions.listenermanager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sf.openrocket.aerodynamics.Warning;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.simulation.FlightDataBranch;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.listeners.SimulationListener;
import net.sf.openrocket.unit.Unit;

/**
 *
 * @author 160047412
 */
public class ListenerManagerExtension extends AbstractSimulationExtension {

  public ArrayList<SimulationListener> selectedListeners = new ArrayList();

  @Override
  public void initialize(SimulationConditions sc) throws SimulationException {
      for(SimulationListener l : this.selectedListeners){
            sc.getSimulationListenerList().add(l);
      }
  }
}
