package com.warcgenerator;

import java.io.File;
import java.io.IOException;

import com.warcgenerator.core.rest.models.User;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.archive.util.FileUtils;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.exception.WarcException;
import com.warcgenerator.core.exception.config.Log4JConfigNotFoundException;
import com.warcgenerator.core.helper.ConfigHelper;
import com.warcgenerator.core.helper.FileHelper;
import com.warcgenerator.core.logic.AppLogicImpl;
import com.warcgenerator.core.logic.IAppLogic;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;
import com.warcgenerator.gui.common.Constants;

/**
 * Start and handle a new instance of WARCProcessor 
 * 
 * @author Miguel Callon
 */
public class AppWarc {
    public static User userGlobal = new User(); // User loged in the app
	private static AppWarc singleton = null;
	private AppConfig config = null;
	private IAppLogic logic = null;

	private static Logger logger = Logger.getLogger(AppWarc.class);

	public static final String CUSTOM_GUI_CONFIG_XML_FULLPATH = Constants.DEFAULT_DIR_CUSTOM_GUI_CONFIG_XML
			+ Constants.LOG4J_CONFIG_XML;

	private AppWarc() {
		// Check if exist Log4j configuration file
		if (!FileHelper.checkIfExists(CUSTOM_GUI_CONFIG_XML_FULLPATH)) {
			try {
				FileUtils.readFullyToFile(
						this.getClass().getResourceAsStream(
								Constants.DEFAULT_LOG4J_CONFIG_XML), 
								new File(CUSTOM_GUI_CONFIG_XML_FULLPATH));
			} catch (IOException e) {
				throw new Log4JConfigNotFoundException(e);
			}
		}
	}

	/**
	 * Singleton
	 * 
	 * @return singleton of {@link AppWarc}
	 */
	public static AppWarc getInstance() {
		if (singleton == null) {
			singleton = new AppWarc();
		}
		return singleton;
	}

	/**
	 * Load a basic default configuration
	 */
	public void init() {
		// Configure Log4j.xml
		DOMConfigurator.configure(CUSTOM_GUI_CONFIG_XML_FULLPATH);
		logger.info("Loading default configuration...");
		config = new AppConfig();

		// Using XML config instead of properties
		config.init();

		logger.info("-- AppConfig --\n" + config);
		logger.info("Configuration loaded successfully");
		logic = new AppLogicImpl(config);
	}

	/**
	 * Load a configuration from a configuration file path given
	 * 
	 * @param pathConfig
	 *            path to Xml properties config
	 */
	public void init(String pathConfig) throws WarcException {
		// Configure Log4j.xml
		DOMConfigurator.configure(CUSTOM_GUI_CONFIG_XML_FULLPATH);

		// Properties properties = ConfigHelper.loadParams(pathConfig);
		logger.info("Loading configuration...");
		config = new AppConfig();
		// Using XML config instead of properties
		ConfigHelper.configure(pathConfig, config);
		config.init();

		logger.info("-- AppConfig --\n" + config);
		logger.info("Configuration loaded successfully");
		logic = new AppLogicImpl(config);
	}

	/**
	 * Execute generate corpus process
	 * @throws WarcException If it is unable to generate corpus 
	 */
	public void execute() throws WarcException {
		// Start
		logger.info("Generating corpus...");
		logic.generateCorpus(new GenerateCorpusState());
		logger.info("Corpus generated successfully ...");
	}

	/**
	 * Get AppLogic
	 * @return logic of {@link IAppLogic}
	 */
	public IAppLogic getAppLogic() {
		return logic;
	}
}
