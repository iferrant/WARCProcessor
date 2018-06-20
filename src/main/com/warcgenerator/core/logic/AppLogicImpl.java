package com.warcgenerator.core.logic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.config.OutputCorpusConfig;
import com.warcgenerator.core.datasource.DataSource;
import com.warcgenerator.core.datasource.IDataSource;
import com.warcgenerator.core.datasource.common.bean.Country;
import com.warcgenerator.core.datasource.common.bean.DataBean;
import com.warcgenerator.core.datasource.generic.GenericDS;
import com.warcgenerator.core.exception.config.DSConfigException;
import com.warcgenerator.core.exception.config.LoadDataSourceException;
import com.warcgenerator.core.exception.logic.AddDataSourceException;
import com.warcgenerator.core.exception.logic.ConfigFilePathIsNullException;
import com.warcgenerator.core.exception.logic.DataSourceNotFoundException;
import com.warcgenerator.core.exception.logic.LogicException;
import com.warcgenerator.core.exception.logic.OutCorpusCfgNotFoundException;
import com.warcgenerator.core.helper.ConfigHelper;
import com.warcgenerator.core.helper.FileHelper;
import com.warcgenerator.core.helper.LangFilterHelper;
import com.warcgenerator.core.helper.XMLConfigHelper;
import com.warcgenerator.core.task.ExecutionTaskBatch;
import com.warcgenerator.core.task.Task;
import com.warcgenerator.core.balancer.BalanceCorpus;
import com.warcgenerator.core.task.generateCorpus.CheckActiveSitesConfigTask;
import com.warcgenerator.core.task.generateCorpus.GetURLFromDSTask;
import com.warcgenerator.core.task.generateCorpus.ReadURLsTask;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusStates;

/**
 * Business logic layer
 * 
 * <p>
 * This class contains the implementation the WARCProcessor API
 * </p>
 * 
 * @author Miguel Callon
 * 
 */
public class AppLogicImpl extends AppLogic implements IAppLogic {
	private AppConfig config;
	private OutputCorpusConfig outputCorpusConfig;
	private Map<String, DataSourceConfig> dataSourcesTypes;
	private ExecutionTaskBatch executorTasks;

	private static Logger logger = Logger.getLogger(AppLogicImpl.class);

	/**
	 * Business logic implementation
	 * 
	 * @param config
	 *            AppConfig
	 * @throws LogicException
	 *             If error
	 */
	public AppLogicImpl(AppConfig config) throws LogicException {
		this.config = config;

		dataSourcesTypes = new LinkedHashMap<String, DataSourceConfig>();
		XMLConfigHelper.getDataSources(Constants.dataSourcesTypesXML,
				dataSourcesTypes);

		// Reset DataSourceConfig ID value
		DataSourceConfig.resetId();

		// Set init outputCorpusConfig
		resetOutputCorpusConfig();
	}

	/**
	 * Save configuration in a file
	 */
	public void saveAppConfig() {
		String configFilePath = ConfigHelper.getConfigFilePath();

		if (configFilePath != null) {
			ConfigHelper.persistConfig(configFilePath, config);

			// Notify observers
			setChanged();
			notifyObservers(new LogicCallback(APP_CONFIG_SAVED_CALLBACK));
		} else {
			throw new ConfigFilePathIsNullException();
		}
	}

	/**
	 * Save configuration in a file
	 * 
	 * @param path
	 *            Path for saving the config
	 */
	public void saveAsAppConfig(String path) {
		ConfigHelper.persistConfig(path, config);

		// Notify observers
		setChanged();
		notifyObservers(new LogicCallback(APP_CONFIG_SAVED_AS_CALLBACK));
	}

	/**
	 * Return Filepath of the current Config file.
	 * 
	 * @return String with path for saving the config
	 */
	public String getConfigFilePath() {
		return ConfigHelper.getConfigFilePath();
	}

	/**
	 * Load AppConfig from config file path.
	 * 
	 * @param path
	 *            Config file path
	 */
	public void loadAppConfig(String path) {
		ConfigHelper.configure(path, config);
		config.init();

		// Notify observers
		setChanged();
		notifyObservers(new LogicCallback(APP_CONFIG_LOADED_CALLBACK));
	}

	/**
	 * Start a new empty AppConfig.
	 */
	public void loadNewAppConfig() {
		config = new AppConfig();
		config.init();

		// The filePath of this configuracion is not set yet.
		ConfigHelper.setConfigFilePath(null);
	}

	/**
	 * Update current AppConfig with a new AppConfig given.
	 * 
	 * @param appConfig
	 *            AppConfig
	 * @throws LogicException
	 *             If it is unable to update AppConfig
	 */
	public void updateAppConfig(AppConfig appConfig) throws LogicException {
		appConfig.validate();
		try {
			BeanUtils.copyProperties(config, appConfig);
			// Reset outputCorpusConfig to the new values
			config.resetOutputConfig();
			resetOutputCorpusConfig();
		} catch (IllegalAccessException e) {
			throw new LogicException(e);
		} catch (InvocationTargetException e) {
			throw new LogicException(e);
		}

		// Notify observers
		setChanged();
		notifyObservers(new LogicCallback(APP_CONFIG_UPDATED_CALLBACK));
	}

	/**
	 * Get current AppConfig.
	 * 
	 * @throws LogicException
	 *             If it is unable to get current AppConfig.
	 */
	public AppConfig getAppConfig() throws LogicException {
		AppConfig appConfigCopy = new AppConfig();
		try {
			BeanUtils.copyProperties(appConfigCopy, config);
		} catch (IllegalAccessException e) {
			throw new LogicException(e);
		} catch (InvocationTargetException e) {
			throw new LogicException(e);
		}
		return appConfigCopy;
	}

	/**
	 * List all types of available DataSources
	 * 
	 * @return list of {@link DataSourceConfig}
	 */
	public List<DataSourceConfig> getDataSourceTypesList()
			throws LogicException {
		List<DataSourceConfig> dataSourceTypesList = new ArrayList<DataSourceConfig>();

		for (DataSourceConfig dsConfig : dataSourcesTypes.values()) {
			DataSourceConfig dsConfigCopy = new DataSourceConfig();
			DataSourceConfig.copy(dsConfigCopy, dsConfig);
			dataSourceTypesList.add(dsConfigCopy);
		}

		Collections.sort(dataSourceTypesList);
		return dataSourceTypesList;
	}

	/**
	 * Get DataSourceConfig by a type given.
	 * 
	 * @param type
	 *            Type of {@link DataSourceConfig}
	 * @return {@link DataSourceConfig}
	 */
	public DataSourceConfig getDataSourceType(String type) {
		DataSourceConfig dsConfigCopy = new DataSourceConfig();
		DataSourceConfig dsConfig = dataSourcesTypes.get(type);
		DataSourceConfig.copy(dsConfigCopy, dsConfig);
		return dsConfigCopy;
	}

	/**
	 * List all {@link DataSourceConfig} configured in {@link AppConfig}.
	 * 
	 * @return list of {@link DataSourceConfig}
	 */
	public List<DataSourceConfig> getDataSourceConfigList() {
		List<DataSourceConfig> dataSourceConfigsList = new ArrayList<DataSourceConfig>();

		for (DataSourceConfig dsConfig : config.getDataSourceConfigs().values()) {
			DataSourceConfig dsConfigCopy = new DataSourceConfig();
			DataSourceConfig.copy(dsConfigCopy, dsConfig);
			dataSourceConfigsList.add(dsConfigCopy);
		}

		Collections.sort(dataSourceConfigsList);
		return dataSourceConfigsList;
	}

	/**
	 * Get a DataSourceConfig by an id given.
	 * 
	 * @param id
	 *            {@link DataSourceConfig} identifier
	 * @throws DataSourceNotFoundException
	 *             If unable to get DataSourceConfig
	 */
	public DataSourceConfig getDataSourceById(int id)
			throws DataSourceNotFoundException {
		DataSourceConfig dsConfigCopy = new DataSourceConfig();
		DataSourceConfig dsConfig = config.getDataSourceConfigs().get(id);
		if (dsConfig == null) {
			throw new DataSourceNotFoundException();
		}
		DataSourceConfig.copy(dsConfigCopy, dsConfig);
		return dsConfigCopy;
	}

	/**
	 * Add a new DataSource
	 * 
	 * @param dsConfig
	 *            of {@link DataSourceConfig}
	 * @throws AddDataSourceException
	 *             If unable to add DataSourceConfig
	 */
	public void addDataSourceConfig(DataSourceConfig dsConfig)
			throws AddDataSourceException {
		// Test if the new DataSourceConfig is ok before add to
		try {
			dsConfig.validate();
		} catch (DSConfigException ex) {
			throw new AddDataSourceException(ex);
		}

		String callback_message = DATASOURCE_UPDATED_CALLBACK;
		if (dsConfig.getId() == null) {
			dsConfig.setId(DataSourceConfig.getNextId());
			callback_message = DATASOURCE_CREATED_CALLBACK;
		}

		// Expand changes to all children
		expandChanges(dsConfig);

		config.getDataSourceConfigs().put(dsConfig.getId(), dsConfig);
		// Notify observers
		setChanged();
		notifyObservers(new LogicCallback(callback_message,
				new Object[] { dsConfig.getId() }));
	}

	/**
	 * Expand changes to all children
	 * 
	 * @param dsConfigParent
	 *            of {@link DataSourceConfig}
	 */
	private void expandChanges(DataSourceConfig dsConfigParent) {
		for (DataSourceConfig specificDsConfig : dsConfigParent.getChildren()) {
			ConfigHelper.copyProperties(specificDsConfig, dsConfigParent);
			expandChanges(specificDsConfig);
		}
	}

	/**
	 * Remove a DataSource
	 * 
	 * @param id
	 *            {@link DataSourceConfig} identifier
	 */
	public void removeDataSourceConfig(int id) {
		if (!config.getDataSourceConfigs().containsKey(id)) {
			throw new DataSourceNotFoundException();
		}
		config.getDataSourceConfigs().remove(id);
		// Notify observers
		setChanged();
		notifyObservers(new LogicCallback(DATASOURCE_REMOVED_CALLBACK,
				new Object[] { id }));
	}

	/**
	 * Stop the generation of corpus
	 */
	public void stopGenerateCorpus() {
		// Check if can be stoped		
		stopWebCrawling();
	}

	/**
	 * Generate corpus process.
	 * 
	 * @param generateCorpusState
	 *            of {@link GenerateCorpusState}
	 * @exception LogicException
	 *                If any error happen
	 */
	public void generateCorpus(GenerateCorpusState generateCorpusState)
			throws LogicException {
		// Init data structures
		Map<String, DataBean> urlsSpam;
		Map<String, DataBean> urlsHam;
		Map<String, DataSource> outputDS = new LinkedHashMap<String, DataSource>();
		Set<String> urlsActive;
		Set<String> urlsNotActive;

		IDataSource labeledDS = null;
		IDataSource notFoundDS = null;

		try {
			// Corpus Path dirs
			String dirs[] = { outputCorpusConfig.getOutputDir(),
					outputCorpusConfig.getCorpusDir(),
					outputCorpusConfig.getSpamDir(),
					outputCorpusConfig.getHamDir() };
			// Delete directories
			if (config.getFlushOutputDir() != null
					&& config.getFlushOutputDir()) {
				FileHelper.removeDirsIfExist(dirs);
			}
			FileHelper.createDirs(dirs);

			// Get dsHandlers
			ConfigHelper.getDSHandlers(config, dataSourcesTypes);

			// Generate wars
			labeledDS = new GenericDS(new DataSourceConfig(
					outputCorpusConfig.getDomainsLabeledFilePath()));
			notFoundDS = new GenericDS(new DataSourceConfig(
					outputCorpusConfig.getDomainsNotFoundFilePath()));

			/*
			 * Si solo activas (descarta estas urls) -> Si descargar de nuevo
			 * Coger de internet -> No descargar de nuevo Coger del fichero Si
			 * no solo activas -> Si descargar de nuevo Coger de internet -> No
			 * descargar de nuevo Coger del fichero
			 */

			boolean stop = false;
			do {
				urlsSpam = new LinkedHashMap<String, DataBean>();
				urlsHam = new LinkedHashMap<String, DataBean>();
				urlsActive = new HashSet<String>();
				urlsNotActive = new HashSet<String>();

				// Init Task
				Task t1 = new GetURLFromDSTask(config, generateCorpusState,
						urlsSpam, urlsHam);

				// ////////// READING SPAM
				Task t2 = new ReadURLsTask(config, outputCorpusConfig,
						generateCorpusState, outputDS, labeledDS,
                        notFoundDS, urlsSpam, true, urlsActive, urlsNotActive);

				// ////////// READING HAM
				Task t3 = new ReadURLsTask(config, outputCorpusConfig,
						generateCorpusState, outputDS, labeledDS,
                        notFoundDS, urlsHam, false, urlsActive, urlsNotActive);

				// Read url that contains html
				Task t4 = new CheckActiveSitesConfigTask(config, urlsSpam,
						urlsNotActive, outputDS, outputCorpusConfig, labeledDS,
						generateCorpusState);

				Task t5 = new CheckActiveSitesConfigTask(config, urlsHam,
						urlsNotActive, outputDS, outputCorpusConfig, labeledDS,
						generateCorpusState);

				executorTasks = new ExecutionTaskBatch();
				executorTasks.addTask(t1);
				executorTasks.addTask(t2);
				executorTasks.addTask(t3);
				executorTasks.addTask(t4);
				executorTasks.addTask(t5);

				executorTasks.execution();

				// Test if the process managed the outcome configurated

				if (executorTasks.isTerminate()) {
					generateCorpusState
						.setState(GenerateCorpusStates.CANCELLING_PROCESS);
					generateCorpusState
							.setState(GenerateCorpusStates.PROCESS_CANCELLED);
				}

				// Update generate corpus state
				generateCorpusState
						.setNumUrlSpamReadedFromDS(generateCorpusState
								.getNumUrlSpamCorrectlyLabeled());
				generateCorpusState
						.setNumUrlHamReadedFromDS(generateCorpusState
								.getNumUrlHamCorrectlyLabeled());

				// DataSources are exausted
				if ((urlsSpam.isEmpty() && urlsHam.isEmpty())
						|| generateCorpusState.getNumDomainsCorrectlyLabeled() >= config
								.getNumSites())
					stop = true;
			} while (!executorTasks.isTerminate() && !stop);

            // Close all output datasources
            // We need to close them to be balanced
            for (DataSource ds : outputDS.values()) {
                ds.close();
            }

            // Balance corpus to respect the percentages sets on the configuration
            new BalanceCorpus(generateCorpusState, outputCorpusConfig.getSpamDir(),
                    outputCorpusConfig.getHamDir(), config)
                    .execute();

			// Show the finish overcome
			if (!executorTasks.isTerminate()
					&& generateCorpusState.getNumDomainsCorrectlyLabeled() < config
							.getNumSites()) {
				logger.info("The ration spam/ham could not be completed."
						+ "There is not enough data in input datasources.");
				generateCorpusState.setState(GenerateCorpusStates.END);
			} else if (!executorTasks.isTerminate()) {
				logger.info("The ratio spam/ham was succesfully completed.");
				logger.info("Sites: "
						+ generateCorpusState.getNumDomainsCorrectlyLabeled()
						+ " - Spam: "
						+ generateCorpusState.getNumUrlSpamCorrectlyLabeled()
						+ ", Ham: "
						+ generateCorpusState.getNumUrlHamCorrectlyLabeled());
				generateCorpusState.setState(GenerateCorpusStates.END);
			} else if (executorTasks.isTerminate()) {
				generateCorpusState.setState(GenerateCorpusStates.PROCESS_CANCELLED);
			}
		} catch (LoadDataSourceException ex) {
			throw new LogicException(ex);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new LogicException(ex);
		} finally {
			// Close all output datasources
			for (DataSource ds : outputDS.values()) {
				ds.close();
			}
			if (labeledDS != null)
				labeledDS.close();
			if (notFoundDS != null)
				notFoundDS.close();
		}
	}

	/**
	 * List languages that has not already been selected in the languages given
	 * 
	 * @param listSelectedCountries
	 *            List of {@link Country}
	 * @return list of {@link Country}
	 */
	@Override
	public List<Country> listNotSelectedLanguages(
			List<Country> listSelectedCountries) {
		List<Country> listCountries = null;
		try {
			listCountries = LangFilterHelper
					.listNotSelectedLanguages(listSelectedCountries);
		} catch (IOException e) {
			throw new LogicException(e);
		}
		return listCountries;
	}

	/**
	 * List all available languages
	 * 
	 * @return list of {@link Country}
	 */
	@Override
	public List<Country> listAvailableLanguagesFilter() {
		List<Country> availableLangList = new ArrayList<Country>();
		try {
			List<Country> availableLang = LangFilterHelper
					.listAvailableLanguagesFilter();

			for (Country langBean : availableLang) {
				Country langCopy = new Country();
				BeanUtils.copyProperties(langCopy, langBean);
				availableLangList.add(langCopy);
			}
		} catch (IOException e) {
			throw new LogicException(e);
		} catch (IllegalAccessException e) {
			throw new LogicException(e);
		} catch (InvocationTargetException e) {
			throw new LogicException(e);
		}

		return availableLangList;
	}

	/**
	 * Stop the process of web crawling
	 */
	private void stopWebCrawling() {
		if (executorTasks != null)
			executorTasks.terminate();
	}

	/**
	 * Used for reseting outputCorpusConfig if their values
	 * are updated
	 */
	private void resetOutputCorpusConfig() {
		// Create a output corpus with config
		if (config.getOutputConfig() instanceof OutputCorpusConfig) {
			outputCorpusConfig = (OutputCorpusConfig) config.getOutputConfig();
		} else {
			throw new OutCorpusCfgNotFoundException();
		}
	}
	
	/**
	 * Add an Observer object
	 * 
	 * @param obj
	 *            {@link Observer}
	 */
	public void addObserver(Observer obj) {
		deleteObserver(obj);
		super.addObserver(obj);
	}
}
