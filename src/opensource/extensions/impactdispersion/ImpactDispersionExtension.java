package opensource.extensions.impactdispersion;

import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.SimulationConditions;

public class ImpactDispersionExtension extends AbstractSimulationExtension {
  
  ImpactDispersionConditions impactDispersionConditions;

  public ImpactDispersionExtension() {
    this.impactDispersionConditions = new ImpactDispersionConditions();
  }

  @Override
  public void initialize(final SimulationConditions simulationConditions) throws SimulationException {
    simulationConditions.getSimulationListenerList().add(new ImpactDispersionListener(impactDispersionConditions));
  }

  /*public void set_listeners_list(List<String> listeners_list) throws Exception{
    listenersList = new ArrayList<>();
    for(String s:listeners_list){
      if (!StringUtil.isEmpty(s)) {
        final Class<?> clazz = Class.forName(s);
        if (!SimulationListener.class.isAssignableFrom(clazz)) {
          throw new Exception("Class " + s + " does not implement SimulationListener");
        }
        final SimulationListener listener;
          listener = (SimulationListener) clazz.newInstance();
        listenersList.add(listener);
      }
    }
  }
  
  public String get_listeners_list(){
    if(listenersList == null)
      return null;
    List<String> listeners_list = new ArrayList<>();
    for(SimulationListener l:listenersList)
      //if(!l.getClass().getSimpleName().equals("null_Listener"))
        listeners_list.add(l.getClass().getSimpleName());
    return String.join(";", listeners_list);
  }*/
  
}
