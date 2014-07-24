package com.warcgenerator.gui.actions.datasource;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.actions.common.Constants;
import com.warcgenerator.gui.common.Session;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.datasources.DSAssistantStep3Panel;

public class DSAsisstantStep2ContinueAction 
	extends AbstractAction implements Observer {	
	private WarcGeneratorGUI view;
	private IAppLogic logic;
	
	public DSAsisstantStep2ContinueAction(IAppLogic logic,
			WarcGeneratorGUI view
			) {
		this.view = view;
		this.logic = logic;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DataSourceConfig dsConfig  = 
				(DataSourceConfig)Session.get(
						Constants.DATASOURCE_FORM_SESSION_KEY);
		
		view.loadMainPanel(new DSAssistantStep3Panel(logic,
				view));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
