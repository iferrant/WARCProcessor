package com.warcgenerator.gui.actions.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.core.logic.LogicCallback;
import com.warcgenerator.gui.view.WarcGeneratorGUI;

public class SaveAppConfigAction
	extends AbstractAction implements Observer {	
	private WarcGeneratorGUI view;
	private IAppLogic logic;
	
	public SaveAppConfigAction(IAppLogic logic,
			WarcGeneratorGUI view
			) {
		this.view = view;
		this.logic = logic;
		
		logic.addObserver(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		logic.saveAppConfig();
	}

	@Override
	public void update(Observable obj, Object logicCallback) {
		if (obj == logic) {
			String message = ((LogicCallback)logicCallback).getMessage();
			if (message.equals(IAppLogic.APP_CONFIG_SAVED_CALLBACK)) {
				JOptionPane.showMessageDialog(view.getMainFrame(), 
						"La configuracion se ha guardado con exito.");
			}
		}
	}
}
