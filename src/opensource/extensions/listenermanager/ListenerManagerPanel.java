/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package opensource.extensions.listenermanager;

import com.google.inject.Key;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.components.StyledLabel;
import net.sf.openrocket.gui.util.Icons;
import net.sf.openrocket.gui.widgets.SelectColorButton;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.simulation.extension.SimulationExtension;
import net.sf.openrocket.simulation.extension.impl.JavaCode;
import net.sf.openrocket.simulation.extension.impl.JavaCodeConfigurator;
import net.sf.openrocket.simulation.listeners.SimulationListener;
import net.sf.openrocket.startup.Application;
import opensource.listeners.loadable.SimulationListenerProvider;

/**
 *
 * @author vitinho
 */
public class ListenerManagerPanel extends javax.swing.JPanel {

    private static final Translator trans = Application.getTranslator();

    final Simulation simulation;

    final JPopupMenu listenerMenu;
    JMenu listenerMenuCopyListener;

    /**
     * Creates new form ListenerManagerPane
     *
     * @param listenerManager
     * @param simulation
     */
    public ListenerManagerPanel(ListenerManagerExtension listenerManager, Simulation simulation) {
        initComponents();
        
        this.simulation = simulation;
        this.listenerMenu = getListenerMenu();
        updateCurrentListeners();
    }

    private JPopupMenu getListenerMenu() {
        Set<SimulationListenerProvider> listeners = Application.getInjector().getInstance(new Key<Set<SimulationListenerProvider>>() {
        });

        JPopupMenu basemenu = new JPopupMenu();

        for (final SimulationListenerProvider provider : listeners) {
            List<String> ids = provider.getIds();
            for (final String id : ids) {
                List<String> menuItems = provider.getName(id);
                if (menuItems != null) {
                    JComponent menu = findMenu(basemenu, menuItems);
                    JMenuItem item = new JMenuItem(menuItems.get(menuItems.size() - 1));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            SimulationListener listener = provider.getListener();
                            String listenerName = provider.getListenerName();
                            SimulationListenerWrapper e = new SimulationListenerWrapper(listener, listenerName);
                            simulation.getSimulationExtensions().add(e);
                            updateCurrentListeners();
                        }
                    });
                    menu.add(item);
                }
            }
        }

        //// Copy extension
        updateListenerMenuCopyListener(basemenu);

        return basemenu;
    }

    /**
     * Updates the contents of the "Copy extension" menu item in the extension
     * menu.
     *
     * @param extensionMenu extension menu to add the "Copy extension" menu item
     * to
     */
    private void updateListenerMenuCopyListener(JPopupMenu listenerMenu) {
        if (listenerMenu == null) {
            return;
        }
        if (this.listenerMenuCopyListener != null) {
            listenerMenu.remove(this.listenerMenuCopyListener);
        }
    }

    private JComponent findMenu(MenuElement menu, List<String> menuItems) {
        for (int i = 0; i < menuItems.size() - 1; i++) {
            String menuItem = menuItems.get(i);

            MenuElement found = null;
            for (MenuElement e : menu.getSubElements()) {
                if (e instanceof JMenu && ((JMenu) e).getText().equals(menuItem)) {
                    found = e;
                    break;
                }
            }

            if (found != null) {
                menu = found;
            } else {
                JMenu m = new JMenu(menuItem);
                ((JComponent) menu).add(m);
                menu = m;
            }
        }
        return (JComponent) menu;
    }

    private void updateCurrentListeners() {
        currentListeners.removeAll();

        if (simulation.getSimulationExtensions().isEmpty()) {
            StyledLabel l = new StyledLabel(trans.get("simedtdlg.SimExt.noExtensions"), StyledLabel.Style.ITALIC);
            l.setForeground(Color.DARK_GRAY);
            currentListeners.add(l, "growx, pad 5 5 5 5, wrap");
        } else {
            for (SimulationExtension e : simulation.getSimulationExtensions()) {
                if (e instanceof SimulationListenerWrapper) {
                    SimulationListenerWrapper simulationExtensionWrapper = (SimulationListenerWrapper) e;
                    currentListeners.add(new ListenerManagerPanel.SimulationListenerPanel(simulationExtensionWrapper), "growx, wrap");
                } else if (e instanceof JavaCode) {
                    currentListeners.add(new ListenerManagerPanel.JavaCodePanel((JavaCode) e), "growx, wrap");
                }
            }
        }
        updateListenerMenuCopyListener(this.listenerMenu);

        // Both needed:
        this.revalidate();
        this.repaint();
    }

    private class JavaCodePanel extends JPanel {

        public JavaCodePanel(final JavaCode extension) {
            super(new MigLayout("fillx, gapx 0"));

            String listenerName = extension.getName();

            this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            this.add(new JLabel(listenerName), "spanx, growx, wrap");

            JButton button;

            this.add(new JPanel(), "spanx, split, growx, right");

            button = new SelectColorButton(Icons.CONFIGURE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JavaCodeConfigurator configurator = Application.getInjector().getInstance(JavaCodeConfigurator.class);
                    configurator.configure(extension, simulation,
                            SwingUtilities.windowForComponent(ListenerManagerPanel.this));
                    updateCurrentListeners();
                }
            });
            this.add(button, "right");
            // Delete
            button = new SelectColorButton(Icons.EDIT_DELETE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Iterator<SimulationExtension> iter = simulation.getSimulationExtensions().iterator();
                    while (iter.hasNext()) {
                        // Compare with identity
                        if (iter.next() == extension) {
                            iter.remove();
                            break;
                        }
                    }
                    updateCurrentListeners();
                }
            });
            this.add(button, "right");

        }
    }

    private class SimulationListenerPanel extends JPanel {

        /**
         *
         */
//        private static final long serialVersionUID = -3296795614810745035L;
        public SimulationListenerPanel(final SimulationListenerWrapper listener) {
            super(new MigLayout("fillx, gapx 0"));

            String listenerName = listener.getName();

            this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            this.add(new JLabel(listenerName), "spanx, growx, wrap");

            JButton button;

            this.add(new JPanel(), "spanx, split, growx, right");

            // Delete
            button = new SelectColorButton(Icons.EDIT_DELETE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Iterator<SimulationExtension> iter = simulation.getSimulationExtensions().iterator();
                    while (iter.hasNext()) {
                        // Compare with identity
                        if (iter.next() == listener) {
                            iter.remove();
                            break;
                        }
                    }
                    updateCurrentListeners();
                }
            });
            this.add(button, "right");

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        addListenerButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        currentListeners = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        addListenerButton.setText("Add listener");
        addListenerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addListenerButtonActionPerformed(evt);
            }
        });

        currentListeners.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        currentListeners.setLayout(new javax.swing.BoxLayout(currentListeners, javax.swing.BoxLayout.LINE_AXIS));
        currentListeners.setLayout(new javax.swing.BoxLayout(currentListeners, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(currentListeners);

        jButton1.setText("Add Java Code");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(addListenerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(0, 353, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addListenerButton)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addListenerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addListenerButtonActionPerformed
        this.listenerMenu.show(addListenerButton, 5, addListenerButton.getBounds().height);
    }//GEN-LAST:event_addListenerButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        SimulationExtension javaCodeExtension = Application.getInjector().getInstance(JavaCode.class);
        simulation.getSimulationExtensions().add(javaCodeExtension);
        JavaCodeConfigurator configurator = Application.getInjector().getInstance(JavaCodeConfigurator.class);
        configurator.configure(javaCodeExtension, simulation, SwingUtilities.windowForComponent(ListenerManagerPanel.this));
        updateCurrentListeners();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addListenerButton;
    private javax.swing.JPanel currentListeners;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
