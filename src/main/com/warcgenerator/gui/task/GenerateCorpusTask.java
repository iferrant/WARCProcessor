package com.warcgenerator.gui.task;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.helper.XMLConfigHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import com.warcgenerator.gui.util.FileUtil;
import com.warcgenerator.gui.util.Messages;
import com.warcgenerator.gui.view.WarcGeneratorGUI;
import com.warcgenerator.gui.view.generate.GeneratingCorpusDialog;

public class GenerateCorpusTask extends SwingWorker<Void, Integer> implements
		Observer {
	private IAppLogic logic;
	private WarcGeneratorGUI view;
	private GeneratingCorpusDialog gcd;
	private GenerateCorpusState gcState;

	private boolean error;
	private String errorMessage;

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
		try {
			get();

			generateCorpusSummary();

			StringBuilder sb = new StringBuilder();
			sb.append(Messages
					.getString("GenerateCorpusTask.corpusgenerated.text"));
			sb.append("\n\n");
			sb.append("Output Corpus Info. (URLs):\n");
			sb.append("- Total: ")
					.append(gcState.getNumDomainsCorrectlyLabeled())
					.append("\n");
			sb.append("- SPAM/HAM: ")
					.append(gcState.getNumUrlSpamCorrectlyLabeled())
					.append("/").append(gcState.getNumUrlHamCorrectlyLabeled());

			JOptionPane.showMessageDialog(view.getMainFrame(), sb.toString(),
					Messages.getString("GeneralDialog.info.title.text"),
					JOptionPane.INFORMATION_MESSAGE);
			logger.info("Task completed");

			if (gcd.getOpenOutputFolderCBox().isSelected()) {
				// Open the output dir
				AppConfig config = logic.getAppConfig();
				FileUtil.openInDefaultExplorer(config.getOutputConfig()
						.getOutputDir());
			}
		} catch (ExecutionException e) {
			String msg = String.format("Unexpected problem: %s", e.getCause()
					.toString());
			JOptionPane.showMessageDialog(view.getAssistantPanel(), msg,
					"Error", JOptionPane.ERROR_MESSAGE);
			gcd.dispose();
		} catch (InterruptedException e) {
			logger.info("Task interrupted");
		} catch (CancellationException e) {
			logic.stopGenerateCorpus();
		} finally {
			gcd.dispose();
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
				// gcd.getStateLbl().setText("Obteniendo urls de los datasources");
				int numSitesTotal = logic.getAppConfig().getNumSites();
				StringBuilder sb = new StringBuilder(
						Messages.getString("GenerateCorpusTask.progress.urls.text")
								+ "("
								+ gcState.getNumUrlReadedFromDS()
								+ Messages
										.getString("GenerateCorpusTask.progress2.urls.text")
								+ numSitesTotal + ")");
				gcd.getStateLbl().setText(
						StringUtils.abbreviate(sb.toString(), 38));
				break;
			case CRAWLING_URLS:
				gcd.getStateLbl()
						.setText(
								StringUtils.abbreviate(
										Messages.getString("GenerateCorpusTask.progress3.urls.text")
												+ gcState
														.getCurrentUrlCrawled(),
										38));
				break;
			case READING_URLS:
				gcd.getStateLbl()
						.setText(
								Messages.getString("GenerateCorpusTask.progress4.urls.text"));
				if ((progress + inc) <= 100)
					progress += inc;
				break;
			case END:
				gcd.getStateLbl()
						.setText(
								Messages.getString("GenerateCorpusTask.progress5.urls.text"));
				progress = 100;
				break;
				
			case CANCELLING_PROCESS:
				gcd.getStateLbl()
				.setText(
						Messages.getString("GenerateCorpusTask.progress6.urls.text"));
				break;
				
			case PROCESS_CANCELLED:
				gcd.getStateLbl()
						.setText(
								Messages.getString("GenerateCorpusTask.progress7.urls.text"));
				progress = 0;
				break;
			}

			setProgress(progress);
			publish();
		}
	}

    /**
     * Generate the summary corpus file necessary for the API
     */
	private void generateCorpusSummary() {
        // Save the corpus summary on the corpus folder
        XMLConfigHelper.saveXmlCorpusSummary(
                logic.getAppConfig().getOutputConfig().getOutputDir()
                        + File.separator
                        + logic.getAppConfig().getCorpusDirName()
                        + File.separator
                        + Constants.corpusSummaryFile,
                logic.getAppConfig(),
                gcState.getNumUrlSpamCorrectlyLabeled(),
                gcState.getNumUrlHamCorrectlyLabeled());
    }

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
