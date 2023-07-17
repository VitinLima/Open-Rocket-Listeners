package opensource.extensions.listenermanager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtensionProvider;

/**
 *
 * @author Vítor Lima Aguirra, Universidade de Brasília
 */
@Plugin
public class ListenerManagerProvider extends AbstractSimulationExtensionProvider{
    public ListenerManagerProvider() {
        super(ListenerManagerExtension.class, new String[] { "Open Source", "Listeners manager" });
    }
}
