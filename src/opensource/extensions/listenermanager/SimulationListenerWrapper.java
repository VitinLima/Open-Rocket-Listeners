/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package opensource.extensions.listenermanager;

import com.google.inject.ConfigurationException;
import net.sf.openrocket.simulation.SimulationConditions;
import net.sf.openrocket.simulation.exception.SimulationException;
import net.sf.openrocket.simulation.extension.AbstractSimulationExtension;
import net.sf.openrocket.simulation.listeners.SimulationListener;

/**
 *
 * @author vitinho
 */
public class SimulationListenerWrapper extends AbstractSimulationExtension {

        private final SimulationListener listener;
        private final String listenerName;

        public SimulationListenerWrapper(SimulationListener listener, String listenerName) {
            this.listener = listener;
            this.listenerName = listenerName;
        }

        public SimulationListener getListener() {
            return listener;
        }

        @Override
        public String getName() {
            return listenerName;
        }

        @Override
        public void initialize(SimulationConditions conditions) throws SimulationException {
//		String className = getClassName();
//		try {
//			if (!StringUtil.isEmpty(className)) {
//				Class<?> clazz = Class.forName(className);
//				if (!SimulationListener.class.isAssignableFrom(clazz)) {
//					throw new SimulationException("Class " + className + " does not implement SimulationListener");
//				}
//			}
//		} catch (ClassNotFoundException e) {
//			throw new SimulationException(trans.get("SimulationExtension.javacode.classnotfound") + " " + className);
//		}
            try {
                conditions.getSimulationListenerList().add(this.listener);
            } catch (ConfigurationException e) {
                throw new SimulationException(String.format(trans.get("SimulationExtension.javacode.couldnotinstantiate"), this.listener.getClass().getName()), e);
            }
        }
    }