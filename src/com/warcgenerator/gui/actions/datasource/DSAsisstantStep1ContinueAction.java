package com.warcgenerator.gui.actions.datasource;

import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;

import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.gui.actions.common.Constants;
import com.warcgenerator.gui.common.Session;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.datasources.DSAssistantStep2Panel;

public class DSAsisstantStep1ContinueAction 
	extends AbstractAction implements Observer {	
	private WarcGeneratorGUI view;
	
	public DSAsisstantStep1ContinueAction(WarcGeneratorGUI view
			) {
		this.view = view;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		DataSourceConfig dsConfig  = 
				(DataSourceConfig)Session.get(
						Constants.DATASOURCE_FORM_SESSION_KEY);
		
		view.loadMainPanel(new DSAssistantStep2Panel(view));
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
