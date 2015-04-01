package com.warcgenerator.core.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.warcgenerator.AbstractTestCase;
import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.exception.config.PathNotFoundSaveAppConfigException;
import com.warcgenerator.core.exception.logic.ConfigFilePathIsNullException;
import com.warcgenerator.core.exception.logic.OutCorpusCfgNotFoundException;
import com.warcgenerator.core.helper.ConfigHelper;
import com.warcgenerator.core.task.generateCorpus.state.GenerateCorpusState;

public class AppLogicTest extends AbstractTestCase {
	private IAppLogic logic;

	private final String CONFIG_FILE1 = "src/test/resources/config/config1.wpg";
	private final String CONFIG_FILE2 = "src/test/resources/config/config2.wpg";
	private final String CONFIG_FILE3 = "src/test/resources/config/config3.wpg";
	private final String CONFIG_FILE4 = "src/test/resources/config/config4.wpg";
	
	private final String DS_TEST_NAME = "Test DS";
	private final String DS_TEST_TYPE = "FileDS";
	private final String DS_TEST_CLASS_NAME = "com.warcgenerator.datasources.file.FileDS";
	private final boolean DS_IS_ENABLED = true;
	private final String DS_FILE_PATH = "src/test/resources/datasources/file";

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	// No-args constructor defaults to port 8080

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// TestUtils.createFakeContext();
		// TestUtils.clearTestDatabase(sessionFactory);
	}

	@Before
	public void setUp() throws Exception {
		// this.dao = new PeopleDAO();
	}

	@After
	public void tearDown() throws Exception {
		// TestUtils.clearTestDatabase();
		// this.dao = null;
	}

	@Test(expected = OutCorpusCfgNotFoundException.class)
	public void testCheckCorpusCfgNotFound() {
		// Check if there is an empty configuration in memory
		AppConfig config = new AppConfig();

		logic = new AppLogicImpl(config);
		logic.loadNewAppConfig();
	}

	@Test
	public void testLoadNewAppConfig() {
		// Check if there is an empty configuration in memory
		AppConfig config = new AppConfig();
		config.init();

		logic = new AppLogicImpl(config);
		logic.loadNewAppConfig();

		assertNull("Configuration file path not empty",
				logic.getConfigFilePath());
	}

	// Check if a configuration file is setted
	@Test(expected = ConfigFilePathIsNullException.class)
	public void testConfigFilePathSetted() {
		ConfigHelper.setConfigFilePath(null);
		AppConfig config = new AppConfig();
		config.init();

		logic = new AppLogicImpl(config);
		logic.saveAppConfig();
	}

	// Check if a configuration file is setted
	@Test
	public void testSaveAppConfig() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();

		logic = new AppLogicImpl(config);
		logic.saveAppConfig();
	}
	
	@Test
	public void testSaveAsAppConfig() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		logic.saveAsAppConfig("src/test/resources/tmp/config/config_tmp.wpg");
		
		logic.loadAppConfig("src/test/resources/tmp/config/config_tmp.wpg");
		assertEquals(logic.getDataSourceConfigList().size(), 9);
	}
	
	@Test(expected = PathNotFoundSaveAppConfigException.class)
	public void testSaveAsAppConfigWrongPath() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		// Using a wrong path
		logic.saveAsAppConfig("src/test/resources/tmp/config/tmp/config_tmp.wpg");
	}
	
	@Test
	public void testLoadAppConfig() {
		AppConfig config = new AppConfig();
		config.init();
		
		logic = new AppLogicImpl(config);
		logic.loadAppConfig(CONFIG_FILE1);
		
		assertEquals(logic.getDataSourceConfigList().size(), 9);
	}
	
	@Test
	public void testUpdateAppConfig() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE2, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		
		assertEquals(logic.getDataSourceConfigList().size(), 0);
		
		AppConfig newConfig = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, newConfig);
		logic.updateAppConfig(newConfig);
		
		assertEquals(logic.getDataSourceConfigList().size(), 9);
	}
	
	@Test
	public void testDataSourceById() {
		ConfigHelper.setConfigFilePath(null);
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE3, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		DataSourceConfig dsConfig = logic.getDataSourceById(1);
		
		assertEquals(dsConfig.getName(), DS_TEST_NAME);
		//assertEquals(dsConfig.getDsClassName(), DS_TEST_CLASS_NAME);
		assertEquals(dsConfig.getType(), DS_TEST_TYPE);
		assertEquals(dsConfig.getEnabled(), DS_IS_ENABLED);
		assertEquals(dsConfig.getFilePath(), DS_FILE_PATH);
	}
	
	@Test
	public void testDataSourceType() {
		ConfigHelper.setConfigFilePath(null);
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		DataSourceConfig dsConfig = logic.getDataSourceType("WarcDS");
		
		assertEquals(dsConfig.getName(), "WarcDS");
	}
	 	
	// AddDataSourceConfig
	@Test
	public void testAddDataSourceConfig() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE2, config);
		config.init();

		logic = new AppLogicImpl(config);
		DataSourceConfig dsConfig = new DataSourceConfig();
		dsConfig.setName(DS_TEST_NAME);
		dsConfig.setDsClassName(DS_TEST_CLASS_NAME);
		dsConfig.setEnabled(DS_IS_ENABLED);
		dsConfig.setFilePath(DS_FILE_PATH);
		logic.addDataSourceConfig(dsConfig);
		
		dsConfig = logic.getDataSourceById(1);
		
		assertEquals(dsConfig.getName(), DS_TEST_NAME);
		assertEquals(dsConfig.getDsClassName(), DS_TEST_CLASS_NAME);
		assertEquals(dsConfig.getEnabled(), DS_IS_ENABLED);
		assertEquals(dsConfig.getFilePath(), DS_FILE_PATH);
	}

	@Test
	public void testGetDataSourceTypesList() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		Collection<DataSourceConfig> dataSourceConfigTypeList = 
				logic.getDataSourceTypesList();
		
		assertEquals(6, dataSourceConfigTypeList.size());
	}
	
	@Test
	public void testGetDataSourceConfigList() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		Collection<DataSourceConfig> dataSourceConfigList = 
				logic.getDataSourceConfigList();
		
		assertEquals(9, dataSourceConfigList.size());
	}
	
	@Test
	public void testRemoveDataSourceConfig() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE1, config);
		config.init();
		
		logic = new AppLogicImpl(config);
		logic.removeDataSourceConfig(1);
		
		Collection<DataSourceConfig> dataSourceConfigList = 
				logic.getDataSourceConfigList();
		
		assertEquals(8, dataSourceConfigList.size());
	}
	
	// Generate corpus test
	@Test
	public void testGenerateCorpus() {
		AppConfig config = new AppConfig();
		ConfigHelper.configure(CONFIG_FILE4, config);
		config.init();
		
		GenerateCorpusState generateCorpusState = new 
				GenerateCorpusState();
		logic = new AppLogicImpl(config);
		logic.generateCorpus(generateCorpusState);
	}
	
	/*
	 * 
	 * String getConfigFilePath(); 
	 * AppConfig getAppConfig();
	 * void stopGenerateCorpus();  void
	 * addObserver(Observer obs); void deleteObserver(Observer obs);
	 * List<Country> listAvailableLanguagesFilter(); List<Country>
	 * listNotSelectedLanguages( List<Country> listSelectedCountries);
	 */
}
