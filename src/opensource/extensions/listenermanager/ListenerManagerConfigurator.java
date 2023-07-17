package opensource.extensions.listenermanager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import javax.swing.JComponent;
import javax.swing.JPanel;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;

/**
 *
 * @author 160047412
 */
@Plugin
public class ListenerManagerConfigurator extends AbstractSwingSimulationExtensionConfigurator<ListenerManagerExtension>{

    public ListenerManagerConfigurator() {
        super(ListenerManagerExtension.class);
    }
    
    @Override
    protected JComponent getConfigurationComponent(ListenerManagerExtension e, Simulation simulation, JPanel panel) {
        panel.add(new ListenerManagerPanel(e, simulation));
        return panel;
    }
    
}
