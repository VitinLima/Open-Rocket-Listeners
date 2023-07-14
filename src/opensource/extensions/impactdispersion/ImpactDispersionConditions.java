/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package opensource.extensions.impactdispersion;

import opensource.extensions.exporter.ExporterExtension;
import java.util.ArrayList;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.util.Coordinate;

/**
 *
 * @author Vítor Lima Aguirra, Universidade de Brasília, 1 de jun. de 2022
 */
public class ImpactDispersionConditions{
  
//  final ArrayList<String> motorConfigurationIDList;
  final ArrayList<Double> launchRodLengthList;
  boolean launchIntoWind;
  final ArrayList<Double> launchRodAngleList;
  final ArrayList<Double> launchRodDirectionList;
  final ArrayList<Double> windSpeedAverageList;
  final ArrayList<Double> windSpeedDeviationList;
  final ArrayList<Double> windTurbulenceIntensityList;
  final ArrayList<Double> windDirectionList;
  final ArrayList<Coordinate> launchRodCoordinatesList;
  boolean isISAAtmosphere;
  final ArrayList<Double> launchTemperatureList;
  final ArrayList<Double> launchPressureList;
  
  final ArrayList<? extends AbstractSimulationListener> listenersList;
  final ArrayList<? extends AbstractSimulationExtension> extensionsList;
  final ExporterExtension simulationExporter;
  Simulation nominalSimulation;

  public ImpactDispersionConditions() {
//    this.motorConfigurationIDList = new ArrayList<>();
    this.launchRodLengthList = new ArrayList<>();
    this.launchRodAngleList = new ArrayList<>();
    this.launchRodDirectionList = new ArrayList<>();
    this.windSpeedAverageList = new ArrayList<>();
    this.windSpeedDeviationList = new ArrayList<>();
    this.windTurbulenceIntensityList = new ArrayList<>();
    this.windDirectionList = new ArrayList<>();
    this.launchRodCoordinatesList = new ArrayList<>();
    this.launchTemperatureList = new ArrayList<>();
    this.launchPressureList = new ArrayList<>();
    
    this.listenersList = new ArrayList<>();
    this.extensionsList = new ArrayList<>();
    this.simulationExporter = new ExporterExtension();
  }

  void initiate(Simulation simulation) {
    if(nominalSimulation != null){
      return;
    }
    nominalSimulation = simulation;
    
//    motorConfigurationIDList.add(nominalSimulation.getOptions().getMotorConfigurationID());
    
    launchRodLengthList.add(nominalSimulation.getOptions().getLaunchRodLength());
    launchIntoWind = false;
    launchRodAngleList.add(Math.toDegrees(nominalSimulation.getOptions().getLaunchRodAngle()));
    launchRodDirectionList.add(Math.toDegrees(nominalSimulation.getOptions().getLaunchRodDirection()));
    
    windSpeedAverageList.add(nominalSimulation.getOptions().getWindSpeedAverage());
    windSpeedDeviationList.add(nominalSimulation.getOptions().getWindSpeedDeviation());
    windTurbulenceIntensityList.add(nominalSimulation.getOptions().getWindTurbulenceIntensity());
    windDirectionList.add(Math.toDegrees(nominalSimulation.getOptions().getWindDirection()));
    
    launchRodCoordinatesList.add(new Coordinate(
            nominalSimulation.getOptions().getLaunchLongitude(),
            nominalSimulation.getOptions().getLaunchLatitude(),
            nominalSimulation.getOptions().getLaunchAltitude()));
    
    isISAAtmosphere = nominalSimulation.getOptions().isISAAtmosphere();
    launchTemperatureList.add(nominalSimulation.getOptions().getLaunchTemperature());
    launchPressureList.add(nominalSimulation.getOptions().getLaunchPressure());
  }
  
  public int getSize(){
      int n = 1;
//      n *= motorConfigurationIDList.size();
      n *= launchRodLengthList.size();
      n *= launchRodAngleList.size();
      n *= launchRodDirectionList.size();
      n *= windSpeedAverageList.size();
      n *= windSpeedDeviationList.size();
      n *= windTurbulenceIntensityList.size();
      n *= windDirectionList.size();
      n *= launchRodCoordinatesList.size();
      n *= launchTemperatureList.size();
      n *= launchPressureList.size();
      return n;
  }
}
