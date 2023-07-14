package opensource.extensions.template;

//package net.sf.openrocket.simulation.extension.template;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.DoubleModel;
import net.sf.openrocket.gui.components.BasicSlider;
import net.sf.openrocket.gui.components.UnitSelector;
import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;
import net.sf.openrocket.unit.UnitGroup;

@Plugin
public class ExtensionTemplateConfigurator extends AbstractSwingSimulationExtensionConfigurator<ExtensionTemplate> {
	
    public ExtensionTemplateConfigurator() {
        super(ExtensionTemplate.class);
    }
	
    @Override
    protected JComponent getConfigurationComponent(ExtensionTemplate extension, Simulation simulation, JPanel panel) {
        panel.add(new JLabel("Launch altitude:"));

        DoubleModel m = new DoubleModel(extension, "LaunchAltitude", UnitGroup.UNITS_DISTANCE, 0);

        JSpinner spin = new JSpinner(m.getSpinnerModel());
        spin.setEditor(new SpinnerEditor(spin));
        panel.add(spin, "w 65lp!");

        UnitSelector unit = new UnitSelector(m);
        panel.add(unit, "w 25");

        BasicSlider slider = new BasicSlider(m.getSliderModel(0, 5000));
        panel.add(slider, "w 75lp, wrap");
		
        return panel;
    }
}
