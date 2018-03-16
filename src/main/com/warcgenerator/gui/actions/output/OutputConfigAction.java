package com.warcgenerator.gui.actions.output;

import java.awt.event.ActionEvent;
import java.util.Observable;

import javax.swing.Action;
import javax.swing.JOptionPane;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.CustomAction;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.output.OutputConfigPanel;

public class OutputConfigAction extends CustomAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WarcGeneratorGUI view;
	private IAppLogic logic;
	private OutputConfigPanel configPanel;

	public OutputConfigAction(IAppLogic logic, WarcGeneratorGUI view,
			OutputConfigPanel configPanel) {
		super(view, configPanel);
		this.logic = logic;
		this.view = view;
		this.configPanel = configPanel;
		// view.addObserver(this);
	}

	@Override
	public void action(ActionEvent e) {
		AppConfig config = logic.getAppConfig();

		configPanel.getOutputDirTField().setValue(
				config.getOutputConfig().getOutputDir());
		configPanel.getCorpusDirTField().setValue(config.getCorpusDirName());
		configPanel.getSpamDirTField().setValue(config.getSpamDirName());
		configPanel.getHamDirTField().setValue(config.getHamDirName());
		configPanel.getChckbxEliminarLaSalida().setSelected(
				config.getFlushOutputDir());

		view.loadMainPanel(configPanel);
	}

	@Override
	public void update(Observable obj, Object message) {
		if (obj == view) {
			if (this.isCurrentAction()
					&& ((Object[]) message)[0]
							.equals(WarcGeneratorGUI.TRYING_CHANGE_MAIN_PANEL)) {
				int userSelection = JOptionPane.showConfirmDialog(
						view.getMainFrame(),
						Messages.getString("OutputConfigAction.warning.text"));

				if (userSelection == JOptionPane.OK_OPTION) {
					configPanel.save();
					Action nextAction = (Action) ((Object[]) message)[1];
					nextAction.actionPerformed(null);
				} else if (userSelection == JOptionPane.NO_OPTION) {
					configPanel.rollback();
					Action nextAction = (Action) ((Object[]) message)[1];
					nextAction.actionPerformed(null);
				}
			}
		}
	}
}
