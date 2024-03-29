package opensource.extensions.impactdispersion;

import com.google.inject.Key;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.components.DescriptionArea;
import net.sf.openrocket.gui.components.StyledLabel;
import net.sf.openrocket.gui.util.GUIUtil;
import net.sf.openrocket.gui.util.Icons;
import net.sf.openrocket.gui.widgets.SelectColorButton;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.simulation.extension.SimulationExtension;
import net.sf.openrocket.simulation.extension.SimulationExtensionProvider;
import net.sf.openrocket.simulation.extension.SwingSimulationExtensionConfigurator;
import net.sf.openrocket.simulation.extension.impl.JavaCode;
import net.sf.openrocket.simulation.listeners.SimulationListener;
import net.sf.openrocket.startup.Application;
import net.sf.openrocket.util.Coordinate;
import opensource.extensions.listenermanager.SimulationListenerWrapper;
import opensource.listeners.loadable.SimulationListenerProvider;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author 160047412
 */
public class ImpactDispersionPanel extends javax.swing.JPanel {

    private static final Translator trans = Application.getTranslator();

    private OpenRocketDocument document;

    final JPopupMenu listenerMenu;
    final JPopupMenu extensionMenu;
    JMenu listenerMenuCopyListener;
    JMenu extensionMenuCopyExtension;

    private final ImpactDispersionConditions conditions;

    final Simulation simulation;

    /**
     * Creates new form configurationPanel
     *
     * @param conditions
     * @param simulation
     */
    public ImpactDispersionPanel(ImpactDispersionConditions conditions, Simulation simulation) {
        this.conditions = conditions;
        this.simulation = simulation;

        initComponents();

        DefaultTableModel model;
        model = (DefaultTableModel) windSpeedAverageTable.getModel();
        for (Object o : this.conditions.windSpeedAverageList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) windTurbulenceIntensityTable.getModel();
        for (Object o : this.conditions.windTurbulenceIntensityList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) windDeviationTable.getModel();
        for (Object o : this.conditions.windSpeedDeviationList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) windDirectionTable.getModel();
        for (Object o : this.conditions.windDirectionList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) launchRodDirectionTable.getModel();
        for (Object o : this.conditions.launchRodDirectionList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) launchRodAngleTable.getModel();
        for (Object o : this.conditions.launchRodAngleList.toArray()) {
            model.addRow(new Object[]{o});
        }
        model = (DefaultTableModel) launchRodCoordinatesTable.getModel();
        for (Object o : this.conditions.launchRodCoordinatesList.toArray()) {
            model.addRow(new Object[]{o});
        }

        this.listenerMenu = getListenerMenu();
        this.extensionMenu = getExtensionMenu();
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
                            conditions.simulationExtensions.add((SimulationExtension) e);
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

    private JPopupMenu getExtensionMenu() {
        Set<SimulationExtensionProvider> extensions = Application.getInjector().getInstance(new Key<Set<SimulationExtensionProvider>>() {
        });

        JPopupMenu basemenu = new JPopupMenu();

        //// Use code / Launch conditions
        for (final SimulationExtensionProvider provider : extensions) {
            List<String> ids = provider.getIds();
            for (final String id : ids) {
                List<String> menuItems = provider.getName(id);
                if (menuItems != null) {
                    JComponent menu = findMenu(basemenu, menuItems);
                    JMenuItem item = new JMenuItem(menuItems.get(menuItems.size() - 1));
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            SimulationExtension e = provider.getInstance(id);
                            conditions.simulationExtensions.add(e);
                            updateCurrentListeners();
                            SwingSimulationExtensionConfigurator configurator = findConfigurator(e);
                            if (configurator != null) {
                                configurator.configure(e, simulation, SwingUtilities.windowForComponent(ImpactDispersionPanel.this));
                                updateCurrentListeners();
                            }
                        }
                    });
                    menu.add(item);
                }
            }
        }

        //// Copy extension
        updateExtensionMenuCopyExtension(basemenu);

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

    /**
     * Updates the contents of the "Copy extension" menu item in the extension
     * menu.
     *
     * @param extensionMenu extension menu to add the "Copy extension" menu item
     * to
     */
    private void updateExtensionMenuCopyExtension(JPopupMenu extensionMenu) {
        if (extensionMenu == null) {
            return;
        }
        if (this.extensionMenuCopyExtension != null) {
            extensionMenu.remove(this.extensionMenuCopyExtension);
        }

        this.extensionMenuCopyExtension = null;
        JMenu menu = new JMenu(this.simulation.getName());
        for (final SimulationExtension ext : this.simulation.getSimulationExtensions()) {
            JMenuItem item = new JMenuItem(ext.getName());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    SimulationExtension e = ext.clone();
                    conditions.simulationExtensions.add(e);
                    updateCurrentListeners();
                    SwingSimulationExtensionConfigurator configurator = findConfigurator(e);
                    if (configurator != null) {
                        configurator.configure(e, simulation, SwingUtilities.windowForComponent(ImpactDispersionPanel.this));
                    }
                }
            });
            menu.add(item);
        }

        if (this.extensionMenuCopyExtension == null) {
            this.extensionMenuCopyExtension = new JMenu(trans.get("simedtdlg.SimExt.copyExtension"));
        }
        this.extensionMenuCopyExtension.add(menu);
        if (this.extensionMenuCopyExtension != null) {
            extensionMenu.add(this.extensionMenuCopyExtension);
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

        if (this.conditions.simulationExtensions.isEmpty()) {
            StyledLabel l = new StyledLabel(trans.get("simedtdlg.SimExt.noExtensions"), StyledLabel.Style.ITALIC);
            l.setForeground(Color.DARK_GRAY);
            currentListeners.add(l, "growx, pad 5 5 5 5, wrap");
        } else {
            for (SimulationExtension e : this.conditions.simulationExtensions) {
                if (e instanceof SimulationListenerWrapper) {
                    SimulationListenerWrapper simulationExtensionWrapper = (SimulationListenerWrapper) e;
                    currentListeners.add(new ImpactDispersionPanel.SimulationListenerPanel(simulationExtensionWrapper), "growx, wrap");
                } else {
                    currentListeners.add(new ImpactDispersionPanel.SimulationExtensionPanel((SimulationExtension) e), "growx, wrap");
                }
            }
        }
        updateListenerMenuCopyListener(this.listenerMenu);

        // Both needed:
        currentListeners.revalidate();
        currentListeners.repaint();
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
                    Iterator<SimulationExtension> iter = conditions.simulationExtensions.iterator();
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

    private class SimulationExtensionPanel extends JPanel {

        public SimulationExtensionPanel(final SimulationExtension extension) {
            super(new MigLayout("fillx, gapx 0"));

            String extensionName = extension.getName();

            this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            this.add(new JLabel(extensionName), "spanx, growx, wrap");

            JButton button;

            this.add(new JPanel(), "spanx, split, growx, right");

            button = new SelectColorButton(Icons.CONFIGURE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingSimulationExtensionConfigurator configurator = findConfigurator(extension);
                    configurator.configure(extension, simulation,
                            SwingUtilities.windowForComponent(ImpactDispersionPanel.this));
                    updateCurrentListeners();
                }
            });
            this.add(button, "right");

            // Help
            if (extension.getDescription() != null) {
                button = new SelectColorButton(Icons.HELP);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final JDialog dialog = new JDialog(SwingUtilities.windowForComponent(ImpactDispersionPanel.this),
                                extension.getName(), Dialog.ModalityType.APPLICATION_MODAL);
                        JPanel panel = new JPanel(new MigLayout("fill"));
                        DescriptionArea area = new DescriptionArea(extension.getDescription(), 10, 0);
                        panel.add(area, "width 400lp, wrap para");
                        JButton close = new SelectColorButton(trans.get("button.close"));
                        close.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                dialog.setVisible(false);
                            }
                        });
                        panel.add(close, "right");
                        dialog.add(panel);
                        GUIUtil.setDisposableDialogOptions(dialog, close);
                        dialog.setLocationRelativeTo(SwingUtilities.windowForComponent(ImpactDispersionPanel.this));
                        dialog.setVisible(true);
                    }
                });
                this.add(button, "right");
            }

            // Delete
            button = new SelectColorButton(Icons.EDIT_DELETE);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Iterator<SimulationExtension> iter = conditions.simulationExtensions.iterator();
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

    private SwingSimulationExtensionConfigurator findConfigurator(SimulationExtension extension) {
        Set<SwingSimulationExtensionConfigurator> configurators = Application.getInjector().getInstance(new Key<Set<SwingSimulationExtensionConfigurator>>() {
        });
        for (SwingSimulationExtensionConfigurator c : configurators) {
            if (c.support(extension)) {
                return c;
            }
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        extensionsPanel = new javax.swing.JPanel();
        ExtensionConfigurationPane = new javax.swing.JPanel();
        addListenerButton = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        currentListeners = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        windOptionsPanel = new javax.swing.JPanel();
        windSpeedAveragePanel = new javax.swing.JPanel();
        addWindSpeedAverageButton = new javax.swing.JButton();
        removeWindSpeedAverageButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        windSpeedAverageTable = new javax.swing.JTable();
        windDeviationPanel = new javax.swing.JPanel();
        addWindDeviationButton = new javax.swing.JButton();
        removeWindDeviationButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        windDeviationTable = new javax.swing.JTable();
        windTurbulenceIntensityPanel = new javax.swing.JPanel();
        addWindTurbulenceIntensityButton = new javax.swing.JButton();
        removeWindTurbulenceIntensityButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        windTurbulenceIntensityTable = new javax.swing.JTable();
        windDirectionPanel = new javax.swing.JPanel();
        addWindDirectionButton = new javax.swing.JButton();
        removeWindDirectionButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        windDirectionTable = new javax.swing.JTable();
        launchOptionsPanel = new javax.swing.JPanel();
        launchRodDirectionPanel = new javax.swing.JPanel();
        addLaunchRodDirectionButton = new javax.swing.JButton();
        removeLaunchRodDirectionButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        launchRodDirectionTable = new javax.swing.JTable();
        launchRodAnglePanel = new javax.swing.JPanel();
        removeLaunchRodAngleButton = new javax.swing.JButton();
        addLaunchRodAngleButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        launchRodAngleTable = new javax.swing.JTable();
        launchRodCoordinatesPanel = new javax.swing.JPanel();
        removeLaunchRodCoordinatesButton = new javax.swing.JButton();
        addLaunchRodCoordinatesButton = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        launchRodCoordinatesTable = new javax.swing.JTable();
        otherOptionsPanel = new javax.swing.JPanel();
        optionsPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        launchIntoWindCheckBox = new javax.swing.JCheckBox();
        simulationExporterPanel = new javax.swing.JPanel();
        promptPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        promptArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();

        ExtensionConfigurationPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout ExtensionConfigurationPaneLayout = new javax.swing.GroupLayout(ExtensionConfigurationPane);
        ExtensionConfigurationPane.setLayout(ExtensionConfigurationPaneLayout);
        ExtensionConfigurationPaneLayout.setHorizontalGroup(
            ExtensionConfigurationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 390, Short.MAX_VALUE)
        );
        ExtensionConfigurationPaneLayout.setVerticalGroup(
            ExtensionConfigurationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        addListenerButton.setText("Add listener");
        addListenerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addListenerButtonActionPerformed(evt);
            }
        });

        currentListeners.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        currentListeners.setLayout(new javax.swing.BoxLayout(currentListeners, javax.swing.BoxLayout.LINE_AXIS));
        jScrollPane8.setViewportView(currentListeners);

        jButton1.setText("Add Java Code");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout extensionsPanelLayout = new javax.swing.GroupLayout(extensionsPanel);
        extensionsPanel.setLayout(extensionsPanelLayout);
        extensionsPanelLayout.setHorizontalGroup(
            extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, extensionsPanelLayout.createSequentialGroup()
                .addGroup(extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(extensionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(addListenerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane8))
                .addGap(30, 30, 30)
                .addComponent(ExtensionConfigurationPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        extensionsPanelLayout.setVerticalGroup(
            extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(extensionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ExtensionConfigurationPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(extensionsPanelLayout.createSequentialGroup()
                        .addGroup(extensionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(addListenerButton)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Listeners", extensionsPanel);

        windOptionsPanel.setLayout(new java.awt.GridLayout(1, 0));

        windSpeedAveragePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        addWindSpeedAverageButton.setText("Add");
        addWindSpeedAverageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWindSpeedAverageButtonActionPerformed(evt);
            }
        });

        removeWindSpeedAverageButton.setText("Remove");
        removeWindSpeedAverageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeWindSpeedAverageButtonActionPerformed(evt);
            }
        });

        windSpeedAverageTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Average wind speed"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        windSpeedAverageTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                windSpeedAverageTableFocusLost(evt);
            }
        });
        jScrollPane3.setViewportView(windSpeedAverageTable);

        javax.swing.GroupLayout windSpeedAveragePanelLayout = new javax.swing.GroupLayout(windSpeedAveragePanel);
        windSpeedAveragePanel.setLayout(windSpeedAveragePanelLayout);
        windSpeedAveragePanelLayout.setHorizontalGroup(
            windSpeedAveragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeWindSpeedAverageButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(addWindSpeedAverageButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        windSpeedAveragePanelLayout.setVerticalGroup(
            windSpeedAveragePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windSpeedAveragePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addWindSpeedAverageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeWindSpeedAverageButton))
        );

        windOptionsPanel.add(windSpeedAveragePanel);

        windDeviationPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        addWindDeviationButton.setText("Add");
        addWindDeviationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWindDeviationButtonActionPerformed(evt);
            }
        });

        removeWindDeviationButton.setText("Remove");
        removeWindDeviationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeWindDeviationButtonActionPerformed(evt);
            }
        });

        windDeviationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Standard deviation"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        windDeviationTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                windDeviationTableFocusLost(evt);
            }
        });
        jScrollPane4.setViewportView(windDeviationTable);

        javax.swing.GroupLayout windDeviationPanelLayout = new javax.swing.GroupLayout(windDeviationPanel);
        windDeviationPanel.setLayout(windDeviationPanelLayout);
        windDeviationPanelLayout.setHorizontalGroup(
            windDeviationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeWindDeviationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(addWindDeviationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        windDeviationPanelLayout.setVerticalGroup(
            windDeviationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windDeviationPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addWindDeviationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeWindDeviationButton))
        );

        windOptionsPanel.add(windDeviationPanel);

        windTurbulenceIntensityPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        addWindTurbulenceIntensityButton.setText("Add");
        addWindTurbulenceIntensityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWindTurbulenceIntensityButtonActionPerformed(evt);
            }
        });

        removeWindTurbulenceIntensityButton.setText("Remove");
        removeWindTurbulenceIntensityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeWindTurbulenceIntensityButtonActionPerformed(evt);
            }
        });

        windTurbulenceIntensityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Turbulence intensity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        windTurbulenceIntensityTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                windTurbulenceIntensityTableFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(windTurbulenceIntensityTable);

        javax.swing.GroupLayout windTurbulenceIntensityPanelLayout = new javax.swing.GroupLayout(windTurbulenceIntensityPanel);
        windTurbulenceIntensityPanel.setLayout(windTurbulenceIntensityPanelLayout);
        windTurbulenceIntensityPanelLayout.setHorizontalGroup(
            windTurbulenceIntensityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeWindTurbulenceIntensityButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(addWindTurbulenceIntensityButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        windTurbulenceIntensityPanelLayout.setVerticalGroup(
            windTurbulenceIntensityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windTurbulenceIntensityPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addWindTurbulenceIntensityButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeWindTurbulenceIntensityButton))
        );

        windOptionsPanel.add(windTurbulenceIntensityPanel);

        windDirectionPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        addWindDirectionButton.setText("Add");
        addWindDirectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWindDirectionButtonActionPerformed(evt);
            }
        });

        removeWindDirectionButton.setText("Remove");
        removeWindDirectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeWindDirectionButtonActionPerformed(evt);
            }
        });

        windDirectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Wind direction"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        windDirectionTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                windDirectionTableFocusLost(evt);
            }
        });
        jScrollPane5.setViewportView(windDirectionTable);

        javax.swing.GroupLayout windDirectionPanelLayout = new javax.swing.GroupLayout(windDirectionPanel);
        windDirectionPanel.setLayout(windDirectionPanelLayout);
        windDirectionPanelLayout.setHorizontalGroup(
            windDirectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeWindDirectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(addWindDirectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        windDirectionPanelLayout.setVerticalGroup(
            windDirectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(windDirectionPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addWindDirectionButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeWindDirectionButton))
        );

        windOptionsPanel.add(windDirectionPanel);

        jTabbedPane1.addTab("Wind", windOptionsPanel);

        launchOptionsPanel.setLayout(new java.awt.GridLayout(1, 0));

        launchRodDirectionPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        addLaunchRodDirectionButton.setText("Add");
        addLaunchRodDirectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLaunchRodDirectionButtonActionPerformed(evt);
            }
        });

        removeLaunchRodDirectionButton.setText("Remove");
        removeLaunchRodDirectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLaunchRodDirectionButtonActionPerformed(evt);
            }
        });

        launchRodDirectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Launch direction"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        launchRodDirectionTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                launchRodDirectionTableFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(launchRodDirectionTable);

        javax.swing.GroupLayout launchRodDirectionPanelLayout = new javax.swing.GroupLayout(launchRodDirectionPanel);
        launchRodDirectionPanel.setLayout(launchRodDirectionPanelLayout);
        launchRodDirectionPanelLayout.setHorizontalGroup(
            launchRodDirectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeLaunchRodDirectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
            .addComponent(addLaunchRodDirectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        launchRodDirectionPanelLayout.setVerticalGroup(
            launchRodDirectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(launchRodDirectionPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLaunchRodDirectionButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeLaunchRodDirectionButton))
        );

        launchOptionsPanel.add(launchRodDirectionPanel);

        launchRodAnglePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        removeLaunchRodAngleButton.setText("Remove");
        removeLaunchRodAngleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLaunchRodAngleButtonActionPerformed(evt);
            }
        });

        addLaunchRodAngleButton.setText("Add");
        addLaunchRodAngleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLaunchRodAngleButtonActionPerformed(evt);
            }
        });

        launchRodAngleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Launch angle"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        launchRodAngleTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                launchRodAngleTableFocusLost(evt);
            }
        });
        jScrollPane6.setViewportView(launchRodAngleTable);

        javax.swing.GroupLayout launchRodAnglePanelLayout = new javax.swing.GroupLayout(launchRodAnglePanel);
        launchRodAnglePanel.setLayout(launchRodAnglePanelLayout);
        launchRodAnglePanelLayout.setHorizontalGroup(
            launchRodAnglePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeLaunchRodAngleButton, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
            .addComponent(addLaunchRodAngleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        launchRodAnglePanelLayout.setVerticalGroup(
            launchRodAnglePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(launchRodAnglePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLaunchRodAngleButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeLaunchRodAngleButton))
        );

        launchOptionsPanel.add(launchRodAnglePanel);

        launchRodCoordinatesPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        removeLaunchRodCoordinatesButton.setText("Remove");
        removeLaunchRodCoordinatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLaunchRodCoordinatesButtonActionPerformed(evt);
            }
        });

        addLaunchRodCoordinatesButton.setText("Add");
        addLaunchRodCoordinatesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLaunchRodCoordinatesButtonActionPerformed(evt);
            }
        });

        launchRodCoordinatesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Launch coordinates"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        launchRodCoordinatesTable.setToolTipText("");
        launchRodCoordinatesTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                launchRodCoordinatesTableFocusLost(evt);
            }
        });
        jScrollPane7.setViewportView(launchRodCoordinatesTable);

        javax.swing.GroupLayout launchRodCoordinatesPanelLayout = new javax.swing.GroupLayout(launchRodCoordinatesPanel);
        launchRodCoordinatesPanel.setLayout(launchRodCoordinatesPanelLayout);
        launchRodCoordinatesPanelLayout.setHorizontalGroup(
            launchRodCoordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(removeLaunchRodCoordinatesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
            .addComponent(addLaunchRodCoordinatesButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        launchRodCoordinatesPanelLayout.setVerticalGroup(
            launchRodCoordinatesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(launchRodCoordinatesPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addLaunchRodCoordinatesButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeLaunchRodCoordinatesButton))
        );

        launchOptionsPanel.add(launchRodCoordinatesPanel);

        jTabbedPane1.addTab("Launch Site", launchOptionsPanel);

        otherOptionsPanel.setLayout(new java.awt.GridLayout(1, 0));

        optionsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel10.setText("Repeat each simulation:");

        launchIntoWindCheckBox.setSelected(this.conditions.launchIntoWind);
        launchIntoWindCheckBox.setText("Always launch upwind");
        launchIntoWindCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                launchIntoWindCheckBoxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(launchIntoWindCheckBox))
                .addContainerGap(459, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(launchIntoWindCheckBox))
        );

        otherOptionsPanel.add(optionsPanel);

        jTabbedPane1.addTab("Other options", otherOptionsPanel);

        simulationExporterPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        simulationExporterPanel.setLayout(new javax.swing.BoxLayout(simulationExporterPanel, javax.swing.BoxLayout.LINE_AXIS));

        simulationExporterPanel.add(new opensource.extensions.exporter.ExporterPanel(conditions.simulationExporter));

        jTabbedPane1.addTab("Export Results", simulationExporterPanel);

        promptArea.setEditable(false);
        promptArea.setColumns(20);
        promptArea.setRows(5);
        jScrollPane10.setViewportView(promptArea);
        promptArea.append("Hello there!\nGeneral Kenobi!\n");

        jLabel1.setLabelFor(promptArea);
        jLabel1.setText("Info prompt");

        javax.swing.GroupLayout promptPanelLayout = new javax.swing.GroupLayout(promptPanel);
        promptPanel.setLayout(promptPanelLayout);
        promptPanelLayout.setHorizontalGroup(
            promptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, promptPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        promptPanelLayout.setVerticalGroup(
            promptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, promptPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Prompt", promptPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addWindDeviationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWindDeviationButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) windDeviationTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addWindDeviationButtonActionPerformed

    private void addWindTurbulenceIntensityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWindTurbulenceIntensityButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) windTurbulenceIntensityTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addWindTurbulenceIntensityButtonActionPerformed

    private void removeWindDeviationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeWindDeviationButtonActionPerformed
        if (windDeviationTable.getModel().getRowCount() < 2 || windDeviationTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) windDeviationTable.getModel();
        model.removeRow(windDeviationTable.getSelectedRow());
    }//GEN-LAST:event_removeWindDeviationButtonActionPerformed

    private void addWindSpeedAverageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWindSpeedAverageButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) windSpeedAverageTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addWindSpeedAverageButtonActionPerformed

    private void removeWindSpeedAverageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeWindSpeedAverageButtonActionPerformed
        if (windSpeedAverageTable.getModel().getRowCount() < 2 || windSpeedAverageTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) windSpeedAverageTable.getModel();
        model.removeRow(windSpeedAverageTable.getSelectedRow());
    }//GEN-LAST:event_removeWindSpeedAverageButtonActionPerformed

    private void removeWindTurbulenceIntensityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeWindTurbulenceIntensityButtonActionPerformed
        if (windTurbulenceIntensityTable.getModel().getRowCount() < 2 || windTurbulenceIntensityTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) windTurbulenceIntensityTable.getModel();
        model.removeRow(windTurbulenceIntensityTable.getSelectedRow());
    }//GEN-LAST:event_removeWindTurbulenceIntensityButtonActionPerformed

    private void removeWindDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeWindDirectionButtonActionPerformed
        if (windDirectionTable.getModel().getRowCount() < 2 || windDirectionTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) windDirectionTable.getModel();
        model.removeRow(windDirectionTable.getSelectedRow());
    }//GEN-LAST:event_removeWindDirectionButtonActionPerformed

    private void addWindDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addWindDirectionButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) windDirectionTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addWindDirectionButtonActionPerformed

    private void addLaunchRodDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLaunchRodDirectionButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) launchRodDirectionTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addLaunchRodDirectionButtonActionPerformed

    private void removeLaunchRodDirectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLaunchRodDirectionButtonActionPerformed
        if (launchRodDirectionTable.getModel().getRowCount() < 2 || launchRodDirectionTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) launchRodDirectionTable.getModel();
        model.removeRow(launchRodDirectionTable.getSelectedRow());
    }//GEN-LAST:event_removeLaunchRodDirectionButtonActionPerformed

    private void addLaunchRodAngleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLaunchRodAngleButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) launchRodAngleTable.getModel();
        model.addRow(new Double[]{0.0});
    }//GEN-LAST:event_addLaunchRodAngleButtonActionPerformed

    private void removeLaunchRodAngleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLaunchRodAngleButtonActionPerformed
        if (launchRodAngleTable.getModel().getRowCount() < 2 || launchRodAngleTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) launchRodAngleTable.getModel();
        model.removeRow(launchRodAngleTable.getSelectedRow());
    }//GEN-LAST:event_removeLaunchRodAngleButtonActionPerformed

    private void addLaunchRodCoordinatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLaunchRodCoordinatesButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) launchRodCoordinatesTable.getModel();
        model.addRow(new String[]{"0.0,0.0,0.0"});
    }//GEN-LAST:event_addLaunchRodCoordinatesButtonActionPerformed

    private void removeLaunchRodCoordinatesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLaunchRodCoordinatesButtonActionPerformed
        if (launchRodCoordinatesTable.getModel().getRowCount() < 2 || launchRodCoordinatesTable.getSelectedRow() == -1) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) launchRodCoordinatesTable.getModel();
        model.removeRow(launchRodCoordinatesTable.getSelectedRow());
    }//GEN-LAST:event_removeLaunchRodCoordinatesButtonActionPerformed

  private void launchIntoWindCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_launchIntoWindCheckBoxStateChanged
      this.conditions.launchIntoWind = this.launchIntoWindCheckBox.isSelected();
  }//GEN-LAST:event_launchIntoWindCheckBoxStateChanged

  private void windSpeedAverageTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_windSpeedAverageTableFocusLost
      conditions.windSpeedAverageList.clear();
      for (int i = 0; i < windSpeedAverageTable.getRowCount(); i++) {
          this.conditions.windSpeedAverageList.add((Double) windSpeedAverageTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_windSpeedAverageTableFocusLost

  private void windDeviationTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_windDeviationTableFocusLost
      this.conditions.windSpeedDeviationList.clear();
      for (int i = 0; i < windDeviationTable.getRowCount(); i++) {
          this.conditions.windSpeedDeviationList.add((Double) windDeviationTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_windDeviationTableFocusLost

  private void windTurbulenceIntensityTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_windTurbulenceIntensityTableFocusLost
      this.conditions.windTurbulenceIntensityList.clear();
      for (int i = 0; i < windTurbulenceIntensityTable.getRowCount(); i++) {
          this.conditions.windTurbulenceIntensityList.add((Double) windTurbulenceIntensityTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_windTurbulenceIntensityTableFocusLost

  private void windDirectionTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_windDirectionTableFocusLost
      this.conditions.windDirectionList.clear();
      for (int i = 0; i < windDirectionTable.getRowCount(); i++) {
          this.conditions.windDirectionList.add((Double) windDirectionTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_windDirectionTableFocusLost

  private void launchRodDirectionTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_launchRodDirectionTableFocusLost
      this.conditions.launchRodDirectionList.clear();
      for (int i = 0; i < launchRodDirectionTable.getRowCount(); i++) {
          this.conditions.launchRodDirectionList.add((Double) launchRodDirectionTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_launchRodDirectionTableFocusLost

  private void launchRodAngleTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_launchRodAngleTableFocusLost
      this.conditions.launchRodAngleList.clear();
      for (int i = 0; i < launchRodAngleTable.getRowCount(); i++) {
          this.conditions.launchRodAngleList.add((Double) launchRodAngleTable.getModel().getValueAt(i, 0));
      }
  }//GEN-LAST:event_launchRodAngleTableFocusLost

  private void launchRodCoordinatesTableFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_launchRodCoordinatesTableFocusLost
      this.conditions.launchRodCoordinatesList.clear();
      for (int i = 0; i < launchRodCoordinatesTable.getRowCount(); i++) {
          String s = (String) launchRodCoordinatesTable.getModel().getValueAt(i, 0);
          String[] ss = s.split(",");
          if (ss.length == 3) {
              this.conditions.launchRodCoordinatesList.add(new Coordinate(Double.parseDouble(ss[0]), Double.parseDouble(ss[1]), Double.parseDouble(ss[2])));
          }
      }
  }//GEN-LAST:event_launchRodCoordinatesTableFocusLost

    private void addListenerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addListenerButtonActionPerformed
        this.listenerMenu.show(addListenerButton, 5, addListenerButton.getBounds().height);
    }//GEN-LAST:event_addListenerButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.extensionMenu.show(jButton1, 5, jButton1.getBounds().height);
//        SimulationExtension javaCodeExtension = Application.getInjector().getInstance(JavaCode.class);
//        conditions.simulationExtensions.add(javaCodeExtension);
//        JavaCodeConfigurator configurator = Application.getInjector().getInstance(JavaCodeConfigurator.class);
//        configurator.configure(javaCodeExtension, simulation, SwingUtilities.windowForComponent(ImpactDispersionPanel.this));
//        updateCurrentListeners();
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ExtensionConfigurationPane;
    private javax.swing.JButton addLaunchRodAngleButton;
    private javax.swing.JButton addLaunchRodCoordinatesButton;
    private javax.swing.JButton addLaunchRodDirectionButton;
    private javax.swing.JButton addListenerButton;
    private javax.swing.JButton addWindDeviationButton;
    private javax.swing.JButton addWindDirectionButton;
    private javax.swing.JButton addWindSpeedAverageButton;
    private javax.swing.JButton addWindTurbulenceIntensityButton;
    private javax.swing.JPanel currentListeners;
    private javax.swing.JPanel extensionsPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JCheckBox launchIntoWindCheckBox;
    private javax.swing.JPanel launchOptionsPanel;
    private javax.swing.JPanel launchRodAnglePanel;
    private javax.swing.JTable launchRodAngleTable;
    private javax.swing.JPanel launchRodCoordinatesPanel;
    private javax.swing.JTable launchRodCoordinatesTable;
    private javax.swing.JPanel launchRodDirectionPanel;
    private javax.swing.JTable launchRodDirectionTable;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JPanel otherOptionsPanel;
    private javax.swing.JTextArea promptArea;
    private javax.swing.JPanel promptPanel;
    private javax.swing.JButton removeLaunchRodAngleButton;
    private javax.swing.JButton removeLaunchRodCoordinatesButton;
    private javax.swing.JButton removeLaunchRodDirectionButton;
    private javax.swing.JButton removeWindDeviationButton;
    private javax.swing.JButton removeWindDirectionButton;
    private javax.swing.JButton removeWindSpeedAverageButton;
    private javax.swing.JButton removeWindTurbulenceIntensityButton;
    private javax.swing.JPanel simulationExporterPanel;
    private javax.swing.JPanel windDeviationPanel;
    private javax.swing.JTable windDeviationTable;
    private javax.swing.JPanel windDirectionPanel;
    private javax.swing.JTable windDirectionTable;
    private javax.swing.JPanel windOptionsPanel;
    private javax.swing.JPanel windSpeedAveragePanel;
    private javax.swing.JTable windSpeedAverageTable;
    private javax.swing.JPanel windTurbulenceIntensityPanel;
    private javax.swing.JTable windTurbulenceIntensityTable;
    // End of variables declaration//GEN-END:variables
}
