package opensource.extensions.impactdispersion;

import net.sf.openrocket.plugin.Plugin;
import net.sf.openrocket.simulation.extension.AbstractSwingSimulationExtensionConfigurator;
import net.sf.openrocket.document.Simulation;

import javax.swing.*;

@Plugin
public class ImpactDispersionConfigurator extends AbstractSwingSimulationExtensionConfigurator<ImpactDispersionExtension>{
  
  public ImpactDispersionConfigurator() {
    super(ImpactDispersionExtension.class);
  }
  
  @Override
  protected JComponent getConfigurationComponent(final ImpactDispersionExtension impactDispersion, final Simulation simulation, final JPanel panel) {
        impactDispersion.impactDispersionConditions.initiate(simulation);
        panel.add(new ImpactDispersionPanel(impactDispersion.impactDispersionConditions));
        return panel;
  }
}