package com.warcgenerator.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Observable;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.common.*;
import com.warcgenerator.gui.actions.datasource.DSAsisstantCreateAction;
import com.warcgenerator.gui.actions.datasource.DSourcesAction;
import com.warcgenerator.gui.actions.file.CreateNewConfigAction;
import com.warcgenerator.gui.actions.file.LoadAppConfigAction;
import com.warcgenerator.gui.actions.file.LoadRecentConfigAction;
import com.warcgenerator.gui.actions.file.SaveAppConfigAction;
import com.warcgenerator.gui.actions.file.SaveAsAppConfigAction;
import com.warcgenerator.gui.actions.general.GeneralConfigAction;
import com.warcgenerator.gui.actions.generate.GenerateCorpusAction;
import com.warcgenerator.gui.actions.other.OtherConfigAction;
import com.warcgenerator.gui.actions.output.OutputConfigAction;
import com.warcgenerator.gui.actions.upload.UploadCorpusAction;
import com.warcgenerator.gui.common.Constants;
import com.warcgenerator.gui.common.Session;
import com.warcgenerator.gui.components.CustomButton;
import com.warcgenerator.gui.components.CustomCardLayout;
import com.warcgenerator.gui.components.CustomMenu;
import com.warcgenerator.gui.components.CustomMenuItem;
import com.warcgenerator.gui.components.CustomTreeCellRenderer;
import com.warcgenerator.gui.components.CustomTreeNode;
import com.warcgenerator.gui.config.GUIConfig;
import com.warcgenerator.gui.helper.GUIConfigHelper;
import com.warcgenerator.gui.helper.MenuHelper;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.util.locale.LocaleChangeEvent;
import com.warcgenerator.gui.util.locale.LocaleChangeHandler;
import com.warcgenerator.gui.util.locale.LocaleChangeListener;
import com.warcgenerator.gui.view.common.InitPanel;
import com.warcgenerator.gui.view.general.GeneralConfigPanel;
import com.warcgenerator.gui.view.other.OtherConfigPanel;
import com.warcgenerator.gui.view.output.OutputConfigPanel;

public class WarcGeneratorGUI extends Observable {
	private GUIConfig guiConfig;

	public static final String TRYING_CHANGE_MAIN_PANEL = "TRYING_CHANGE_MAIN_PANEL";

	private Action assistantCreateDSAction;
	private Action generateCorpusAction;
    private Action loginAction;
    private Action uploadCorpusAction;
	private Action openOutputFolderAction;
	private OutputConfigAction outputConfigAction;
	private OtherConfigAction otherConfigAction;
	private Action saveAsAppConfigAction;
	private Action saveAppConfigAction;
	private Action loadAppConfigAction;
	private Action createNewConfigAction;
	private Action dsourcesAction;
	private Action exitAction;
	private Action aboutOfAction;

	private JFrame frmWarcgenerator;
	private JPanel mainPanel;
	private JPanel cleanPanel;
	private JPanel assistantPanel;
	private JSplitPane splitPane;
	private DefaultMutableTreeNode m_rootNode;
	private JTree tree;
	private GeneralConfigAction generalConfigAction;
	private CustomMenu recentFilesMI;
	private CustomMenuItem mntmSaveCG;
	private CustomMenuItem mnSpanish;
	private CustomMenuItem mnEnglish;

	private IAppLogic logic;

	private LocaleChangeHandler localeChangeHandler;

	/**
	 * Create the application.
	 * @param guiConfig GUI Configuration
	 * @param logic Business logic
	 */
	public WarcGeneratorGUI(GUIConfig guiConfig, IAppLogic logic) {
		this.logic = logic;
		this.guiConfig = guiConfig;
		initialize();
	}

	public void updateUI() {
		updateTree();
		localeChangeHandler.fireLocaleChanged(new LocaleChangeEvent(this,
				Locale.getDefault()));

		// Update UI properties
		UIManager.put("OptionPane.noButtonText",
				Messages.getString("OptionPane.noButtonText.text"));
		UIManager.put("OptionPane.okButtonText",
				Messages.getString("OptionPane.okButtonText.text"));
		UIManager.put("OptionPane.cancelButtonText",
				Messages.getString("OptionPane.cancelButtonText.text"));
	}

	public void loadPanels() {
		cleanPanel = new InitPanel();
		mainPanel = new JPanel(new CustomCardLayout());
		assistantPanel = new JPanel(new CustomCardLayout());
		assistantPanel.setName("AssistantPanel");

		GeneralConfigPanel generalConfigPanel = new GeneralConfigPanel(logic,
				this);
		OutputConfigPanel outputconfigPanel = new OutputConfigPanel(logic, this);
		OtherConfigPanel otherConfigPanel = new OtherConfigPanel(logic, this);

		addMainPanel(cleanPanel);
		addMainPanel(generalConfigPanel);
		addMainPanel(outputconfigPanel);
		addMainPanel(otherConfigPanel);
		addMainPanel(assistantPanel);

		assistantCreateDSAction = new DSAsisstantCreateAction(logic, this);
		generateCorpusAction = new GenerateCorpusAction(logic, this);
        loginAction = new OpenLoginAction(this);
        uploadCorpusAction = new UploadCorpusAction(this);
		openOutputFolderAction = new OpenOutputFolderAction(logic, this);
		generalConfigAction = new GeneralConfigAction(logic, this,
				generalConfigPanel);
		outputConfigAction = new OutputConfigAction(logic, this,
				outputconfigPanel);
		otherConfigAction = new OtherConfigAction(logic, this, otherConfigPanel);
		saveAppConfigAction = new SaveAppConfigAction(logic, this);
		saveAsAppConfigAction = new SaveAsAppConfigAction(logic, this);
		loadAppConfigAction = new LoadAppConfigAction(logic, this);
		createNewConfigAction = new CreateNewConfigAction(logic, this);
		dsourcesAction = new DSourcesAction(logic, this,
				assistantCreateDSAction);
		exitAction = new ExitAction(logic, this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			System.err
					.println("Nimbus L&F does not support. Default L&F will be used.");
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

		// Handle changes in the locale
		localeChangeHandler = new LocaleChangeHandler();

		frmWarcgenerator = new JFrame();
		loadPanels();

		frmWarcgenerator.setResizable(false);
		frmWarcgenerator
				.setIconImage(Toolkit
						.getDefaultToolkit()
						.getImage(
								WarcGeneratorGUI.class
										.getResource("/com/warcgenerator/gui/resources/img/warc.png")));
		frmWarcgenerator.setTitle(Constants.APP_NAME);
		frmWarcgenerator.setBounds(100, 100, 630, 470);
		frmWarcgenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPopupMenu popup = new JPopupMenu();
		CustomMenuItem menuItemPopup = new CustomMenuItem();
		menuItemPopup.setName("WarcGeneratorGUI.menuItemPopup.text");
		addLocaleChangeListener(menuItemPopup);
		menuItemPopup
				.setIcon(new ImageIcon(
						WarcGeneratorGUI.class
								.getResource("/com/warcgenerator/gui/resources/img/database16x16.png")));
		menuItemPopup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				assistantCreateDSAction.actionPerformed(null);
			}
		});
		popup.add(menuItemPopup);

		JMenuBar menuBar = new JMenuBar();
		frmWarcgenerator.setJMenuBar(menuBar);

		CustomMenu mnInicio = new CustomMenu();
		mnInicio.setName("WarcGeneratorGUI.mnInicio.text");
		addLocaleChangeListener(mnInicio);
		menuBar.add(mnInicio);

		CustomMenuItem mntmCreateNewConfig = new CustomMenuItem();
		mntmCreateNewConfig
				.setName("WarcGeneratorGUI.mntmCreateNewConfig.text");
		addLocaleChangeListener(mntmCreateNewConfig);
		mntmCreateNewConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmCreateNewConfig);

		CustomMenuItem mntmCargarConfiguracionGeneral = new CustomMenuItem();
		mntmCargarConfiguracionGeneral
				.setName("WarcGeneratorGUI.mntmCargarConfiguracionGeneral.text");
		addLocaleChangeListener(mntmCargarConfiguracionGeneral);
		mntmCargarConfiguracionGeneral.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadAppConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmCargarConfiguracionGeneral);

		mnInicio.add(new JSeparator());

		mntmSaveCG = new CustomMenuItem();
		mntmSaveCG.setName("WarcGeneratorGUI.mntmSaveCG.text");
		addLocaleChangeListener(mntmSaveCG);
		mntmSaveCG.setEnabled(false);
		mntmSaveCG.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				InputEvent.CTRL_MASK));
		mntmSaveCG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAppConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmSaveCG);

		CustomMenuItem mntmSaveAsCG = new CustomMenuItem();
		mntmSaveAsCG.setName("WarcGeneratorGUI.mntmSaveAsCG.text");
		addLocaleChangeListener(mntmSaveAsCG);
		mntmSaveAsCG.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsAppConfigAction.actionPerformed(e);
			}
		});
		mnInicio.add(mntmSaveAsCG);

		JSeparator separator = new JSeparator();
		mnInicio.add(separator);

		recentFilesMI = new CustomMenu();
		recentFilesMI.setName("WarcGeneratorGUI.recentFilesMI.text");
		addLocaleChangeListener(recentFilesMI);
		loadRecentFiles();
		mnInicio.add(recentFilesMI);

		mnInicio.add(new JSeparator());

		CustomMenuItem mntmSalir = new CustomMenuItem();
		mntmSalir.setName("WarcGeneratorGUI.mntmSalir.text");
		addLocaleChangeListener(mntmSalir);
		mntmSalir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				InputEvent.CTRL_MASK));
		mntmSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitAction.actionPerformed(null);
			}
		});
		mnInicio.add(mntmSalir);

		CustomMenu mnDataSources = new CustomMenu();
		mnDataSources.setName("WarcGeneratorGUI.mnDataSources.text");
		addLocaleChangeListener(mnDataSources);
		menuBar.add(mnDataSources);

		CustomMenuItem mntmCreateNewDS = new CustomMenuItem();
		mntmCreateNewDS.setName("WarcGeneratorGUI.mntmCreateNewDS.text");
		addLocaleChangeListener(mntmCreateNewDS);
		mntmCreateNewDS
				.setIcon(new ImageIcon(
						WarcGeneratorGUI.class
								.getResource("/com/warcgenerator/gui/resources/img/database16x16.png")));
		mntmCreateNewDS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				assistantCreateDSAction.actionPerformed(null);
			}
		});
		mnDataSources.add(mntmCreateNewDS);

		final CustomMenu mnPreferences = new CustomMenu();
		mnPreferences.setName("WarcGeneratorGUI.mnPreferences.text");
		addLocaleChangeListener(mnPreferences);
		menuBar.add(mnPreferences);

		final CustomMenuItem mnLogin = new CustomMenuItem();
		mnLogin.setName("WarcGeneratorGUI.mnLogin.text");
		addLocaleChangeListener(mnLogin);
		mnLogin.setHorizontalAlignment(SwingConstants.LEFT);
		mnPreferences.add(mnLogin);
		mnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loginAction.actionPerformed(actionEvent);
            }
        });

        separator = new JSeparator();
        mnPreferences.add(separator);

		final CustomMenu mnLanguages = new CustomMenu();
		mnLanguages.setName("WarcGeneratorGUI.mnLanguages.text");
		addLocaleChangeListener(mnLanguages);
		mnLanguages.setHorizontalAlignment(SwingConstants.LEFT);
		mnPreferences.add(mnLanguages);

		mnSpanish = new CustomMenuItem();
		mnSpanish.setName("WarcGeneratorGUI.mnSpanish.text");
		addLocaleChangeListener(mnSpanish);
		mnSpanish.setHorizontalAlignment(SwingConstants.LEFT);

		mnSpanish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Locale newLocale = new Locale(Constants.SPANISH_LOCALE,
						Constants.SPAIN_COUNTRY_LOCALE);
				ChangeLanguageAction action = new ChangeLanguageAction(
						newLocale, mnSpanish, logic, WarcGeneratorGUI.this);
				action.actionPerformed(e);
			}
		});
		mnLanguages.add(mnSpanish);

		mnEnglish = new CustomMenuItem();
		mnEnglish.setName("WarcGeneratorGUI.mnEnglish.text");
		addLocaleChangeListener(mnEnglish);
		mnEnglish.setHorizontalAlignment(SwingConstants.LEFT);

		mnEnglish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Locale newLocale = new Locale(Constants.ENGLISH_LOCALE,
						Constants.ENGLAND_COUNTRY_LOCALE);
				ChangeLanguageAction action = new ChangeLanguageAction(
						newLocale, mnEnglish, logic, WarcGeneratorGUI.this);
				action.actionPerformed(e);
			}
		});
		mnLanguages.add(mnEnglish);
		mnPreferences.add(mnLanguages);

        final CustomMenu mnHelp = new CustomMenu();
        mnHelp.setName("WarcGeneratorGUI.mnHelp.text");
        addLocaleChangeListener(mnHelp);
        menuBar.add(mnHelp);

		final CustomMenuItem mnAboutOf = new CustomMenuItem();
		mnAboutOf.setName("WarcGeneratorGUI.mnAboutOf.text");
		addLocaleChangeListener(mnAboutOf);
		mnAboutOf.setHorizontalAlignment(SwingConstants.LEFT);

		mnAboutOf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aboutOfAction = new AboutOfAction(WarcGeneratorGUI.this);
				aboutOfAction.actionPerformed(null);
			}
		});
		mnHelp.add(mnAboutOf);

		menuBar.add(Box.createHorizontalGlue());

		CustomMenuItem mnOpenOutputFolder = new CustomMenuItem();
		mnOpenOutputFolder
				.setIcon(new ImageIcon(
						WarcGeneratorGUI.class
								.getResource("/com/warcgenerator/gui/resources/img/output.png")));
		mnOpenOutputFolder
				.setLocaleToolTipText("WarcGeneratorGUI.mnOpenOutputFolder.text");
		addLocaleChangeListener(mnOpenOutputFolder);
		mnOpenOutputFolder.setMaximumSize(new Dimension(100, 26));
		mnOpenOutputFolder.setAlignmentX(Component.LEFT_ALIGNMENT);

		mnOpenOutputFolder.setHorizontalAlignment(SwingConstants.LEFT);
		mnOpenOutputFolder.setMnemonic('G');
		mnOpenOutputFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openOutputFolderAction.actionPerformed(null);
			}
		});
		menuBar.add(mnOpenOutputFolder);

        CustomButton mnUploadCorpus = new CustomButton();
        mnUploadCorpus.setName("WarcGeneratorGUI.mnUploadCorpus.text");
        addLocaleChangeListener(mnUploadCorpus);
        mnUploadCorpus.setMinimumSize(new Dimension(90, 26));
        mnUploadCorpus.setIcon(new ImageIcon(WarcGeneratorGUI.class
                .getResource("/com/warcgenerator/gui/resources/img/upload.png")));
        mnUploadCorpus.setAlignmentX(Component.LEFT_ALIGNMENT);

        mnUploadCorpus.setHorizontalAlignment(SwingConstants.LEFT);
        mnUploadCorpus.setMnemonic('U');
        mnUploadCorpus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadCorpusAction.actionPerformed(e);
            }
        });
        menuBar.add(mnUploadCorpus);


		CustomButton mnGenerarCorpus = new CustomButton();
		mnGenerarCorpus.setName("WarcGeneratorGUI.mnGenerarCorpus.text");
		addLocaleChangeListener(mnGenerarCorpus);
		mnGenerarCorpus.setMinimumSize(new Dimension(114, 26));
		mnGenerarCorpus.setIcon(new ImageIcon(WarcGeneratorGUI.class
				.getResource("/com/warcgenerator/gui/resources/img/load.png")));
		mnGenerarCorpus.setAlignmentX(Component.LEFT_ALIGNMENT);

		mnGenerarCorpus.setHorizontalAlignment(SwingConstants.LEFT);
		mnGenerarCorpus.setMnemonic('G');
		mnGenerarCorpus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateCorpusAction.actionPerformed(null);
			}
		});
		menuBar.add(mnGenerarCorpus);

		splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		frmWarcgenerator.getContentPane().add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(panel);
		splitPane.setLeftComponent(scrollPane);

		tree = new JTree();
		ToolTipManager.sharedInstance().registerComponent(tree);
		tree.setCellRenderer(new CustomTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();
				// if nothing is selected
				if (node == null)
					return;
			}
		});

		tree.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					Object obj = tree.getSelectionPath().getLastPathComponent();
					CustomTreeNode itemSelected = (CustomTreeNode) obj;
					// itemSelected.getAction().

				} else {
					MenuHelper.selectAndExecuteLeftMenu(tree,
							tree.getSelectionPath());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// create a class which implements the MouseListener interface
				// and
				// implement the following in your overridden mouseClicked
				// method

				if (SwingUtilities.isRightMouseButton(e)) {
					int row = tree.getClosestRowForLocation(e.getX(), e.getY());
					tree.setSelectionRow(row);
					popup.show(e.getComponent(), e.getX(), e.getY());
				} else {
					TreePath tp = tree.getPathForLocation(e.getX(), e.getY());
					if (tp != null) {
						Object obj = tp.getLastPathComponent();
						if (obj instanceof CustomTreeNode) {
							CustomTreeNode itemSelected = (CustomTreeNode) obj;
							if (itemSelected.getAction() != null)
								itemSelected.getAction().actionPerformed(null);
						}
					}
				}
			}
		});

		buildTree();
		panel.add(tree);

		splitPane.setRightComponent(mainPanel);
		frmWarcgenerator.setLocationRelativeTo(null);
	}

	public void refreshTitle() {
		StringBuffer title = new StringBuffer(Constants.APP_NAME);
		String configFilePath = logic.getConfigFilePath();

		if (configFilePath != null) {
			File file = new File(logic.getConfigFilePath());
			title.append(" - ").append(file.getName());
		}
		frmWarcgenerator.setTitle(title.toString());

	}

	/**
	 * Update only the tree
	 */
	public void updateTree() {
		if (tree != null) {
			tree.updateUI();
			tree.repaint();
		}
	}

	@SuppressWarnings("serial")
	public void buildTree() {
		final CustomTreeNode general = new CustomTreeNode(
				"WarcGeneratorGUI.tree.general.text");

		m_rootNode = new CustomTreeNode("WarcGeneratorGUI.tree.m_rootNode.text") {
			{
				CustomTreeNode node_1;

				general.setAction(generalConfigAction);
				add(general);
				CustomTreeNode output = new CustomTreeNode(
						"WarcGeneratorGUI.tree.output.text");
				output.setAction(outputConfigAction);
				add(output);

				CustomTreeNode other = new CustomTreeNode(
						"WarcGeneratorGUI.tree.other.text");
				other.setAction(otherConfigAction);
				add(other);

				node_1 = new CustomTreeNode("WarcGeneratorGUI.tree.node_1.text");
				node_1.setAction(dsourcesAction);
				add(node_1);
				loadDS(node_1);
			}
		};

		tree.setModel(new DefaultTreeModel(m_rootNode));
	}

	public void loadRecentFiles() {
		// Add config file path
		String configFilePath = logic.getConfigFilePath();
		if (configFilePath != null) {
			GUIConfig guiConfig = (GUIConfig) Session
					.get(Constants.GUI_CONFIG_SESSION_KEY);
			guiConfig.addRecentConfigFile(configFilePath);
			GUIConfigHelper.persistConfig(guiConfig);
		}

		recentFilesMI.removeAll();
		List<RecentFileCBItem> recentFiles = guiConfig
				.getRecentConfigFilesReversed();
		if (recentFiles.size() == 0) {
			CustomMenuItem recentConfig = new CustomMenuItem();
			recentConfig.setName("WarcGeneratorGUI.tree.recentConfig.text");
			addLocaleChangeListener(recentConfig);
			recentConfig.setEnabled(false);
			recentFilesMI.add(recentConfig);
		} else {
			for (RecentFileCBItem configFile : recentFiles) {
				final String configFilePathRecent = configFile.getPath();
				JMenuItem recentConfig = new JMenuItem(configFile.toString());
				recentConfig.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Action loadRecentConfigAction = new LoadRecentConfigAction(
								logic, WarcGeneratorGUI.this,
								configFilePathRecent, true);
						loadRecentConfigAction.actionPerformed(e);
					}
				});
				recentFilesMI.add(recentConfig);
			}
		}

		refreshTitle();
		updateUI();
	}

	public void selectFirstSelectionableItem() {
		MenuHelper.selectAndExecuteLeftMenu(tree,
				Messages.getString("WarcGeneratorGUI.tree.general.text"));
	}

	public void updateDS(Integer id, DataSourceConfig config) {
		MenuHelper.updateDS(tree, id, config, this, logic);
		updateUI();
	}

	public void removeDS(Integer id) {
		MenuHelper.removeDS(tree, id);
		updateUI();
	}

	public void addDS(DataSourceConfig config) {
		DefaultMutableTreeNode node = MenuHelper.searchNode(tree,
				"WarcGeneratorGUI.tree.node_1.text");
		MenuHelper.addDS(tree, node, config, this, logic);
		updateUI();
	}

	public void selectLeftMenu(TreePath tp) {
		MenuHelper.selectLeftMenu(tree, tp);
	}

	public DefaultMutableTreeNode getSelectedNode() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		return node;
	}

	public TreePath getSelectedMenu(DefaultMutableTreeNode node) {
		TreePath treePath = null;
		if (node != null) {
			treePath = new TreePath(node.getPath());
		}
		return treePath;
	}

	public void selectAndExecuteLeftMenu(String search) {
		MenuHelper.selectAndExecuteLeftMenu(tree, search);
	}

	private void loadDS(DefaultMutableTreeNode treeNode) {
		MenuHelper.loadDS(treeNode, this, logic);
	}

	public void addMainPanel(JPanel panel) {
		mainPanel.add(panel, panel.getName());
	}

	public void tryChangeMainPanel(Action nextAction) {
		setChanged();
		notifyObservers(new Object[] {
				WarcGeneratorGUI.TRYING_CHANGE_MAIN_PANEL, nextAction });
	}

	public void loadMainPanel(JPanel newPanel) {
		CustomCardLayout cardLayout = ((CustomCardLayout) mainPanel.getLayout());
		cardLayout.show(mainPanel, newPanel.getName());
	}

	public void setVisible(boolean visible) {
		frmWarcgenerator.setVisible(visible);
		frmWarcgenerator.pack();
	}

	public JFrame getMainFrame() {
		return frmWarcgenerator;
	}

	public JPanel getAssistantPanel() {
		return assistantPanel;
	}

	public void setAssistantPanel(JPanel assistantPanel) {
		this.assistantPanel = assistantPanel;
	}

	public CustomMenuItem getMntmSaveCG() {
		return mntmSaveCG;
	}

	public void setMntmSaveCG(CustomMenuItem mntmSaveCG) {
		this.mntmSaveCG = mntmSaveCG;
	}

	public void addLocaleChangeListener(LocaleChangeListener l) {
		localeChangeHandler.addLocaleChangeListener(l);
	}

	public void setLanguage(String language) {
		if (language.toLowerCase().equals(
				Constants.SPANISH_COMPLETE_LOCALE.toLowerCase())) {
			mnSpanish.doClick();
		} else {
			mnEnglish.doClick();
		}
	}
}
