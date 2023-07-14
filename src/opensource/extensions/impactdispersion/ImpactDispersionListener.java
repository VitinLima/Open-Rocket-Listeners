/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package opensource.extensions.impactdispersion;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.util.Coordinate;

/**
 *
 * @author Vítor Lima Aguirra, Universidade de Brasília, 2 de jun. de 2022
 */
public class ImpactDispersionListener extends AbstractSimulationListener {

  private final ImpactDispersionConditions impactDispersionConditions;
  private FileWriter fw;

  public ImpactDispersionListener(ImpactDispersionConditions conditions) {
    this.impactDispersionConditions = conditions;
  }

  @Override
  public void startSimulation(final SimulationStatus simulationStatus) throws SimulationException {
    try {
      fw = new FileWriter(impactDispersionConditions.simulationExporter.outputFile);

      Simulation nominalSimulation = new Simulation(simulationStatus.getConfiguration().getRocket());
      
      nominalSimulation.setName(impactDispersionConditions.nominalSimulation.getName());
//      nominalSimulation.getOptions().setMotorConfigurationID(impactDispersionConditions.nominalSimulation.getOptions().getMotorConfigurationID());
      nominalSimulation.getOptions().setLaunchRodLength(impactDispersionConditions.nominalSimulation.getOptions().getLaunchRodLength());
      nominalSimulation.getOptions().setLaunchIntoWind(impactDispersionConditions.nominalSimulation.getOptions().getLaunchIntoWind());
      nominalSimulation.getOptions().setLaunchRodAngle(impactDispersionConditions.nominalSimulation.getOptions().getLaunchRodAngle());
      nominalSimulation.getOptions().setLaunchRodDirection(impactDispersionConditions.nominalSimulation.getOptions().getLaunchRodDirection());
      nominalSimulation.getOptions().setWindSpeedAverage(impactDispersionConditions.nominalSimulation.getOptions().getWindSpeedAverage());
      nominalSimulation.getOptions().setWindSpeedDeviation(impactDispersionConditions.nominalSimulation.getOptions().getWindSpeedDeviation());
      nominalSimulation.getOptions().setWindTurbulenceIntensity(impactDispersionConditions.nominalSimulation.getOptions().getWindTurbulenceIntensity());
      nominalSimulation.getOptions().setWindDirection(impactDispersionConditions.nominalSimulation.getOptions().getWindDirection());
      nominalSimulation.getOptions().setLaunchAltitude(impactDispersionConditions.nominalSimulation.getOptions().getLaunchAltitude());
      nominalSimulation.getOptions().setLaunchLatitude(impactDispersionConditions.nominalSimulation.getOptions().getLaunchLatitude());
      nominalSimulation.getOptions().setLaunchLongitude(impactDispersionConditions.nominalSimulation.getOptions().getLaunchLongitude());
      nominalSimulation.getOptions().setISAAtmosphere(impactDispersionConditions.nominalSimulation.getOptions().isISAAtmosphere());
      nominalSimulation.getOptions().setLaunchTemperature(impactDispersionConditions.nominalSimulation.getOptions().getLaunchTemperature());
      nominalSimulation.getOptions().setLaunchPressure(impactDispersionConditions.nominalSimulation.getOptions().getLaunchPressure());
      nominalSimulation.getOptions().setGeodeticComputation(impactDispersionConditions.nominalSimulation.getOptions().getGeodeticComputation());
      nominalSimulation.getOptions().setTimeStep(impactDispersionConditions.nominalSimulation.getOptions().getTimeStep());
      nominalSimulation.getOptions().setMaximumStepAngle(impactDispersionConditions.nominalSimulation.getOptions().getMaximumStepAngle());
      nominalSimulation.getOptions().setCalculateExtras(impactDispersionConditions.nominalSimulation.getOptions().getCalculateExtras());
      nominalSimulation.getOptions().setRandomSeed(impactDispersionConditions.nominalSimulation.getOptions().getRandomSeed());
      nominalSimulation.simulate();
      
      fw.append("<NominalTrajectory>");
      fw.append(System.lineSeparator());
      impactDispersionConditions.simulationExporter.exportCSV(fw, nominalSimulation);
      fw.append(System.lineSeparator());
      fw.append("<\\NominalTrajectory>");
      fw.append(System.lineSeparator());
//      simulationStatus.setFlightData(nominalSimulation.getSimulatedData().getBranch(0));

      Simulation simulation = new Simulation(simulationStatus.getConfiguration().getRocket());
      simulation.setName("Impact dispersion simulation");
      simulation.getOptions().setGeodeticComputation(impactDispersionConditions.nominalSimulation.getOptions().getGeodeticComputation());
      simulation.getOptions().setTimeStep(impactDispersionConditions.nominalSimulation.getOptions().getTimeStep());
      simulation.getOptions().setMaximumStepAngle(impactDispersionConditions.nominalSimulation.getOptions().getMaximumStepAngle());
      simulation.getOptions().setCalculateExtras(impactDispersionConditions.nominalSimulation.getOptions().getCalculateExtras());
      simulation.getOptions().setRandomSeed(impactDispersionConditions.nominalSimulation.getOptions().getRandomSeed());
      impactDispersionConditions.simulationExporter.appendSelection = 0;
      
      fw.append("<ImpactDispersion," + impactDispersionConditions.getSize() + ">");
      fw.append(System.lineSeparator());
      simulate(simulation, 0);
      fw.append(System.lineSeparator());
      fw.append("<\\ImpactDispersion>");
      
      fw.close();
    } catch (IOException ex) {
      Logger.getLogger(ImpactDispersionListener.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void simulate(Simulation simulation, int recursionDepth) throws SimulationException, IOException {
    switch(recursionDepth){
      case 0:
//        for (String motorConfigurationID : impactDispersionConditions.motorConfigurationIDList) {
////          simulation.getOptions().setMotorConfigurationID(motorConfigurationID);
//        }
        simulate(simulation, recursionDepth + 1);
        break;
      case 1:
        for (Double launchRodLength : impactDispersionConditions.launchRodLengthList) {
          simulation.getOptions().setLaunchRodLength(launchRodLength);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 2:
        simulation.getOptions().setLaunchIntoWind(impactDispersionConditions.launchIntoWind);
        simulate(simulation, recursionDepth + 1);
        break;
      case 3:
        for (Double launchRodAngle : impactDispersionConditions.launchRodAngleList) {
          simulation.getOptions().setLaunchRodAngle(Math.toRadians(launchRodAngle));
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 4:
        for (Double launchRodDirection : impactDispersionConditions.launchRodDirectionList) {
          simulation.getOptions().setLaunchRodDirection(Math.toRadians(launchRodDirection));
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 5:
        for (Double windSpeedAverage : impactDispersionConditions.windSpeedAverageList) {
          simulation.getOptions().setWindSpeedAverage(windSpeedAverage);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 6:
        for (Double windSpeedDeviation : impactDispersionConditions.windSpeedDeviationList){
          simulation.getOptions().setWindSpeedDeviation(windSpeedDeviation);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 7:
        for (Double TurbulenceIntensity : impactDispersionConditions.windTurbulenceIntensityList){
          simulation.getOptions().setWindSpeedDeviation(TurbulenceIntensity);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 8:
        for (Double windDirection : impactDispersionConditions.windDirectionList) {
          simulation.getOptions().setWindDirection(Math.toRadians(windDirection));
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 9:
        for (Coordinate launchCoordinates : impactDispersionConditions.launchRodCoordinatesList) {
          simulation.getOptions().setLaunchAltitude(launchCoordinates.x);
          simulation.getOptions().setLaunchLatitude(launchCoordinates.y);
          simulation.getOptions().setLaunchLongitude(launchCoordinates.z);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 10:
        simulation.getOptions().setISAAtmosphere(impactDispersionConditions.isISAAtmosphere);
        simulate(simulation, recursionDepth + 1);
        break;
      case 11:
        for (Double launchTemperature : impactDispersionConditions.launchTemperatureList){
          simulation.getOptions().setLaunchTemperature(launchTemperature);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      case 12:
        for (Double launchPressure : impactDispersionConditions.launchPressureList){
          simulation.getOptions().setLaunchPressure(launchPressure);
          simulate(simulation, recursionDepth + 1);
        }
        break;
      default:
        simulation.getOptions().randomizeSeed();
        simulation.simulate();
        fw.append(System.lineSeparator());
        impactDispersionConditions.simulationExporter.exportCSV(fw, simulation);
    }
  }
}
