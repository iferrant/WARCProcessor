package com.warcgenerator.gui.actions.output;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.exception.logic.LogicException;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.core.logic.LogicCallback;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.common.ValidationDialog;
import com.warcgenerator.gui.view.output.OutputConfigPanel;

@SuppressWarnings("serial")
public class OutputSaveAction 
	extends AbstractAction implements Observer {	
	private WarcGeneratorGUI view;
	private OutputConfigPanel panel;
	private IAppLogic logic;
	
	public OutputSaveAction(IAppLogic logic,
			WarcGeneratorGUI view,
			OutputConfigPanel panel
			) {
		this.view = view;
		this.logic = logic;
		this.panel = panel;
		logic.addObserver(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		AppConfig appConfig = logic.getAppConfig();
		
		appConfig.getOutputConfig().setOutputDir(
				panel.getOutputDirTField().getText());
		appConfig.setCorpusDirPath(panel.getOutputDirTField().getText());
		appConfig.setCorpusDirName(panel.getCorpusDirTField().getText());
		appConfig.setSpamDirName(panel.getSpamDirTField().getText());
		appConfig.setHamDirName(panel.getHamDirTField().getText());
		appConfig.setFlushOutputDir(panel.getChckbxEliminarLaSalida().isSelected());
		
		if (validate(appConfig)) {
			try {
				logic.updateAppConfig(appConfig);
			} catch (LogicException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean validate(AppConfig config) {
		StringBuilder errors = new StringBuilder();
		
		if (errors.length() != 0) {
			ValidationDialog dialog =
					ValidationDialog.getInstance(view);
			dialog.setErroresLabel(errors.toString());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			view.updateUI();
			dialog.setVisible(true);
			
			return false;
		}
		return true;
	}
	
	@Override
	public void update(Observable obj, Object logicCallback) {
		if (obj == logic) {
			if (panel.isVisible()) {
				String message = ((LogicCallback)logicCallback).getMessage();
				if (message.equals(IAppLogic.APP_CONFIG_UPDATED_CALLBACK)) {
					JOptionPane.showMessageDialog(view.getMainFrame(), 
							Messages.getString("OutputSaveAction.save.text"),
							Messages.getString("GeneralDialog.info.title.text"),
							JOptionPane.INFORMATION_MESSAGE);
					panel.commit();
				}
			}
		}
	}
}
