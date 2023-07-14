package opensource.extensions.exporter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.file.CSVExport;
import net.sf.openrocket.simulation.FlightDataBranch;
import net.sf.openrocket.simulation.FlightDataType;
import net.sf.openrocket.simulation.FlightEvent;
import net.sf.openrocket.simulation.SimulationStatus;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.listeners.AbstractSimulationListener;
import net.sf.openrocket.unit.Unit;

/**
 *
 * @author Vítor Lima Aguirra, Universidade de Brasília, 31 de mai. de 2022
 */
public class ExporterListener extends AbstractSimulationListener {

  public File outputFile;
  public String fieldSeparator;
  public int decimalPlaces;
  public boolean isExponentialNotation;
  public String commentCharacter;
  public boolean simulationComments;
  public boolean fieldComments;
  public boolean eventComments;
  public int appendSelection;
  public FlightDataType[] fields;
  public Unit[] units;

  ExporterListener(File outputFile, String fieldSeparator, int decimalPlaces, boolean isExponentialNotation, String commentCharacter, boolean simulationComments, boolean fieldComments, boolean eventComments, int appendSelection, boolean[] fieldSelection) {
    this.outputFile = outputFile;
    this.fieldSeparator = fieldSeparator;
    this.decimalPlaces = decimalPlaces;
    this.isExponentialNotation = isExponentialNotation;
    this.commentCharacter = commentCharacter;
    this.simulationComments = simulationComments;
    this.fieldComments = fieldComments;
    this.eventComments = eventComments;
    this.appendSelection = appendSelection;
    
    int k = 0;
    for(int i = 0; i < FlightDataType.ALL_TYPES.length; i++){
      if(fieldSelection[i]){
        k++;
      }
    }
    this.fields = new FlightDataType[k];
    this.units = new Unit[k];
    k = 0;
    for(int i = 0; i < FlightDataType.ALL_TYPES.length; i++){
      if(fieldSelection[i]){
        FlightDataType f = FlightDataType.ALL_TYPES[i];
        fields[k] = f;
        units[k++] = f.getUnitGroup().getDefaultUnit();
        System.out.print(f.toString());
      }
    }
  }
  
  @Override
  public void endSimulation(SimulationStatus status, SimulationException exception) {
    FileOutputStream fos;
    try {
      fos = new FileOutputStream(outputFile, this.appendSelection == 0);
      ArrayList<FlightDataType> _fields = new ArrayList<>();
      ArrayList<Unit> _units = new ArrayList<>();
      for (int i = 0; i < this.fields.length; i++) {
        if (status.getFlightData().get(this.fields[i]) != null) {
          _fields.add(this.fields[i]);
          _units.add(this.units[i]);
        }
      }
      try {
        CSVExport.exportCSV(fos,
                status.getSimulationConditions().getSimulation(),
                status.getFlightData(),
                _fields.toArray(new FlightDataType[]{}), _units.toArray(new Unit[]{}),
                fieldSeparator, decimalPlaces, isExponentialNotation, commentCharacter,
                simulationComments, fieldComments, eventComments);
      } catch (IOException ex) {
        Logger.getLogger(ExporterExtension.class.getName()).log(Level.SEVERE, null, ex);
      }
      fos.close();
    } catch (FileNotFoundException ex) {
      Logger.getLogger(ExporterListener.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(ExporterListener.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}