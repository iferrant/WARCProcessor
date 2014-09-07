package com.warcgenerator.gui.task;

import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.warcgenerator.core.common.GenerateCorpusState;
import com.warcgenerator.core.common.GenerateCorpusStates;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.generate.GeneratingCorpusDialog;

public class GenerateCorpusTask extends SwingWorker<Void, Integer> implements
		Observer {
	private IAppLogic logic;
	private WarcGeneratorGUI view;
	private GeneratingCorpusDialog gcd;
	private GenerateCorpusState gcState;

	private static Logger logger = Logger.getLogger(GenerateCorpusTask.class);

	public GenerateCorpusTask(IAppLogic logic, WarcGeneratorGUI view,
			GeneratingCorpusDialog gcd) {
		this.logic = logic;
		this.view = view;
		this.gcd = gcd;
		this.gcState = new GenerateCorpusState();
		gcState.addObserver(this);
	}

	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
		setProgress(0);

		logic.generateCorpus(gcState);
		// Initialize progress property.

		/*
		 * while (progress < 100) { // Sleep for up to one second. try {
		 * //Thread.sleep(random.nextInt(1000)); } catch (InterruptedException
		 * ignore) { } // Make random progress. //progress +=
		 * random.nextInt(10); setProgress(Math.min(progress, 100)); }
		 */
		return null;
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {
		if (this.isCancelled()) {
			gcState.setState(GenerateCorpusStates.CANCELlING_PROCESS);
			logic.stopGenerateCorpus();
			logger.info("Tarea cancelled");
		} else {
			logger.info("Task completed");
		}
	}

	@Override
	public void update(Observable obs, Object arg1) {
		// TODO Auto-generated method stub
		if (obs == gcState) {
			// gcd.getStateLbl().setText(arg0);
			int NUM_PHASES = 5;
			int inc = Math.round(100 / NUM_PHASES - 1);
			int progress = getProgress();
			switch (gcState.getState()) {
			case GETTING_URLS_FROM_DS:
				gcd.getStateLbl().setText("Obteniendo urls de los datasources");
				progress += inc;
				break;
			case CRAWLING_URLS:
				gcd.getStateLbl().setText(
						StringUtils.abbreviate(
								"Leyendo: " + gcState.getCurrentUrlCrawled(),
								38));
				break;
			case READING_URLS:
				gcd.getStateLbl().setText("Leyendo urls...");
				progress += inc;
				break;
			case ENDING:
				gcd.getStateLbl().setText("Finalizando la ejecucion...");
				progress = 100;
				break;
			case CANCELlING_PROCESS:
				gcd.getStateLbl().setText("Cancelando el proceso");
				break;
			case PROCESS_CANCELLED:
				gcd.getStateLbl().setText("Proceso cancelado");
				break;
			}
			setProgress(progress);
			publish();
		}
	}
}
