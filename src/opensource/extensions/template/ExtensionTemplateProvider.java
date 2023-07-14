package opensource.extensions.template;

//package net.sf.openrocket.simulation.extension.template;

import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtensionProvider;

@Plugin
public class ExtensionTemplateProvider extends AbstractSimulationExtensionProvider {
    public ExtensionTemplateProvider() {
        super(ExtensionTemplate.class, "Templates", "Simulation extension template");
    }
}
