package com.warcgenerator.gui.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.common.Constants;
import com.warcgenerator.gui.actions.common.ExitAction;
import com.warcgenerator.gui.actions.datasource.DSAsisstantCreateAction;
import com.warcgenerator.gui.actions.datasource.DSDetailAction;
import com.warcgenerator.gui.actions.datasource.DSourcesAction;
import com.warcgenerator.gui.actions.file.LoadAppConfigAction;
import com.warcgenerator.gui.actions.file.LoadRecentConfigAction;
import com.warcgenerator.gui.actions.file.SaveAppConfigAction;
import com.warcgenerator.gui.actions.general.GeneralConfigAction;
import com.warcgenerator.gui.actions.generate.GenerateCorpusAction;
import com.warcgenerator.gui.actions.output.OutputConfigAction;
import com.warcgenerator.gui.common.Session;
import com.warcgenerator.gui.components.CustomTreeNode;
import com.warcgenerator.gui.config.GUIConfig;
import com.warcgenerator.gui.view.general.GeneralConfigPanel;

public class WarcGeneratorGUI {
	private GUIConfig guiConfig;
	
	private Action assistantCreateDSAction;
	private Action generateCorpusAction;
	private Action outputConfigAction;
	private Action saveAppConfigAction;
	private Action loadAppConfigAction;
	private Action dsourcesAction;
	private Action exitAction;
	
	private JFrame frmWarcgenerator;
	private JPanel mainPanel;
	private JSplitPane splitPane; 
	private JTree tree;
	private GeneralConfigAction generalConfigAction;
	
	private IAppLogic logic;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WarcGeneratorGUI window = new WarcGeneratorGUI(null);
					window.frmWarcgenerator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WarcGeneratorGUI(IAppLogic logic) {
		this.logic = logic;
		assistantCreateDSAction = new DSAsisstantCreateAction(logic, 
				this);
		generateCorpusAction = new GenerateCorpusAction(logic, this);
		outputConfigAction = new OutputConfigAction(logic, this);
		saveAppConfigAction = new SaveAppConfigAction(logic, this);
		loadAppConfigAction = new LoadAppConfigAction(logic, this);
		dsourcesAction = new DSourcesAction(logic, this);
		exitAction = new ExitAction(logic, this);
		guiConfig = (GUIConfig)Session.get(Constants.GUI_CONFIG_SESSION_KEY);
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
	        UIManager.setLookAndFeel(
	                "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (UnsupportedLookAndFeelException ex) {
	        System.err.println(
	                "Nimbus L&F does not support. Default L&F will be used.");
	    } catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		frmWarcgenerator = new JFrame();
		frmWarcgenerator.setTitle("WarcGenerator GUI");
		frmWarcgenerator.setBounds(100, 100, 630, 470);
		frmWarcgenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String label = "Nuevo origen";
		final JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItemPopup = new JMenuItem(label);
		menuItemPopup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				assistantCreateDSAction.actionPerformed(null);
			}
		});
		popup.add(menuItemPopup);
		
		JMenuBar menuBar = new JMenuBar();
		frmWarcgenerator.setJMenuBar(menuBar);
		
		JMenu mnInicio = new JMenu("Inicio");
		menuBar.add(mnInicio);
		
		JMenuItem mntmCargarConfiguracionGeneral = new JMenuItem("Cargar configuracion general");
		mntmCargarConfiguracionGeneral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadAppConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmCargarConfiguracionGeneral);
		
		JMenuItem mntmGuardarConfiguracionGeneral = new JMenuItem("Guardar configuracion general");
		mntmGuardarConfiguracionGeneral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAppConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmGuardarConfiguracionGeneral);
		
		JSeparator separator = new JSeparator();
		mnInicio.add(separator);
		
		for(String configFile:guiConfig.getRecentConfigFiles()) {
			final String configFileName = configFile; 
			JMenuItem recentConfig = new JMenuItem(configFile);
			recentConfig.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Action loadRecentConfigAction =
							new LoadRecentConfigAction(logic, WarcGeneratorGUI.this,
									configFileName, true);
					loadRecentConfigAction.actionPerformed(e);
				}
			});
			mnInicio.add(recentConfig);
		}
		
		mnInicio.add(new JSeparator());
		
		JMenuItem mntmSalir = new JMenuItem("Salir");
		mntmSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitAction.actionPerformed(null);
			}
		});
		mnInicio.add(mntmSalir);
		
		JMenu mnGenerarCorpus = new JMenu("Generar corpus");
		mnGenerarCorpus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				generateCorpusAction.actionPerformed(null);
			}
		});
		mnGenerarCorpus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("action listener");
			}
		});
		menuBar.add(mnGenerarCorpus);
		
		JMenu mnSalir = new JMenu("Acerca De ...");
		menuBar.add(mnSalir);
		
		splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		frmWarcgenerator.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		JPanel panel = new JPanel();
		scrollPane.setViewportView(panel);
		
		tree = new JTree();
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//create a class which implements the MouseListener interface and
				//implement the following in your overridden mouseClicked method

			    if (SwingUtilities.isRightMouseButton(e)) {
			        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
			        tree.setSelectionRow(row);
			        popup.show(e.getComponent(), e.getX(), e.getY());
			    } else {
			    	TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
			    	if (tp != null) {
			    		Object obj = tp.getLastPathComponent();
			    		if (obj instanceof CustomTreeNode) {
					    	CustomTreeNode itemSelected = (CustomTreeNode)obj;
					    	if (itemSelected.getAction() != null)
					    		itemSelected.getAction().actionPerformed(null);
			    		}
			    	}
			    }
			}
		});
		
		final GeneralConfigPanel configPanel = new GeneralConfigPanel(logic, this);
		generalConfigAction = new
				GeneralConfigAction(logic, this);
		
		buildTree();
		panel.add(tree);
		
		mainPanel = new JPanel();
		splitPane.setRightComponent(mainPanel);
			
		frmWarcgenerator.setLocationRelativeTo(null);
	}
	
	public void buildTree() {
		tree.setModel(new DefaultTreeModel(
			new DefaultMutableTreeNode("Configuracion") {
				{
					CustomTreeNode node_1;
					CustomTreeNode general = new CustomTreeNode("General");
					general.setAction(generalConfigAction);
					add(general);
					CustomTreeNode output = new CustomTreeNode("Salida");
					output.setAction(outputConfigAction);
					add(output);
					
					node_1 = new CustomTreeNode("Origenes");
					node_1.setAction(dsourcesAction);
					loadDS(node_1);
					add(node_1);
				}
			}
		));
	}
	
	private void loadDS(DefaultMutableTreeNode treeNode) {
		for (DataSourceConfig config:logic.getDataSourceConfigList()) {
			CustomTreeNode treeNodeDS = new CustomTreeNode(
					config.getName());
			treeNodeDS.setAction(new DSDetailAction(logic, this, config));
			treeNode.add(treeNodeDS);
		}
	}
	
	public void loadMainPanel(JPanel newPanel) {	
		splitPane.setRightComponent(newPanel);
		splitPane.revalidate();
		splitPane.updateUI();
		splitPane.repaint();
	}
	
	public void setVisible(boolean visible) {
		frmWarcgenerator.setVisible(visible);
	}
	
	public JFrame getMainFrame() {
		return frmWarcgenerator;
	}

}
