package opensource.extensions.listenermanager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import opensource.extensions.exporter.*;
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
public class ListenerManagerListener extends AbstractSimulationListener {

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

  ListenerManagerListener(File outputFile, String fieldSeparator, int decimalPlaces, boolean isExponentialNotation, String commentCharacter, boolean simulationComments, boolean fieldComments, boolean eventComments, int appendSelection, boolean[] fieldSelection) {
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
  public void startSimulation(final SimulationStatus simulationStatus) throws SimulationException {
      
  }
}