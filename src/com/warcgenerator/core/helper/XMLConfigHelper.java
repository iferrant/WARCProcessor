package com.warcgenerator.core.helper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.warcgenerator.core.config.AppConfig;
import com.warcgenerator.core.config.Constants;
import com.warcgenerator.core.config.CustomParamConfig;
import com.warcgenerator.core.config.DataSourceConfig;
import com.warcgenerator.core.exception.config.ConfigException;
import com.warcgenerator.core.exception.config.ValidateXMLSchemaException;

/**
 * Read the app configuration from xml config
 * 
 * @author Miguel Callon
 * 
 */
public class XMLConfigHelper {
	private static Logger logger = Logger.getLogger(XMLConfigHelper.class);

	private static void validateSchema(Document document, String schemaFilePath)
			throws ValidateXMLSchemaException {
		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		Source schemaFile = new StreamSource(new File(schemaFilePath));
		Schema schema;
		try {
			schema = factory.newSchema(schemaFile);
		} catch (SAXException e) {
			throw new ValidateXMLSchemaException(e);
		}

		// create a Validator instance, which can be used to validate an
		// instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
			validator.validate(new DOMSource(document));
		} catch (SAXException e) {
			throw new ValidateXMLSchemaException(e);
		} catch (IOException e) {
			throw new ValidateXMLSchemaException(e);
		}
	}

	public static void getAppConfigFromXml(String path, AppConfig config) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(path));

			// At the moment we are not to validate Scheme
			validateSchema(doc, Constants.configSchemaFilePath);

			// normalize text representation
			doc.getDocumentElement().normalize();

			config.setNumSites(Integer.parseInt(getValueFromElement(doc,
					"numSites")));
			config.setOnlyActiveSites(Boolean.valueOf(getAttributeFromElement(
					doc, "numSites", "onlyActiveSites")));
			config.setDownloadAgain(Boolean.valueOf(getAttributeFromElement(
					doc, "numSites", "downloadAgain")));
			config.setRatioIsPercentage(Boolean
					.valueOf(getAttributeFromElement(doc, "ratio",
							"isPercentage")));
			NodeList listRatio = doc.getElementsByTagName("ratio").item(0)
					.getChildNodes();

			for (int s = 0; s < listRatio.getLength(); s++) {
				Node nodeAux = listRatio.item(s);
				if (nodeAux.getNodeType() == Node.ELEMENT_NODE) {
					System.out.println("nodename es " + nodeAux.getNodeName());
					if (nodeAux.getNodeName().equals("percentageSpam")) {
						config.setRatioPercentageSpam(Integer.parseInt(nodeAux
								.getTextContent().trim()));
					} else if (nodeAux.getNodeName().equals("quantitySpam")) {
						config.setRatioQuantitySpam(Integer.parseInt(nodeAux
								.getTextContent().trim()));
					}
				}
			}

			config.setCorpusDirPath(getValueFromElement(doc, "corpusDirPath"));
			config.setSpamDirName(getValueFromElement(doc, "spamDirName"));
			config.setHamDirName(getValueFromElement(doc, "hamDirName"));
			config.setDomainsLabeledFileName(getValueFromElement(doc,
					"domainsLabeledFileName"));
			config.setDomainsNotFoundFileName(getValueFromElement(doc,
					"domainsNotFoundFileName"));
			String flushOutputDir = getValueFromElement(doc, "flushOutputDir");
			if (flushOutputDir != null) {
				config.setFlushOutputDir(Boolean.valueOf(flushOutputDir));
			}
			config.setMaxDepthOfCrawling(Integer.parseInt(getValueFromElement(
					doc, "maxDepthOfCrawling")));
			config.setNumCrawlers(Integer.parseInt(getValueFromElement(doc,
					"numCrawlers")));
			config.setWebCrawlerTmpStorePath(getValueFromElement(doc,
					"webCrawlerDirTmpStorePath"));

			// Read datasources
			NodeList listOfDS = doc.getElementsByTagName("dataSource");
			int totalDS = listOfDS.getLength();
			logger.info("Total no of datasouces : " + totalDS);

			for (int s = 0; s < listOfDS.getLength(); s++) {
				Node dataSourceNode = listOfDS.item(s);
				DataSourceConfig ds = new DataSourceConfig();
				ds.setId(DataSourceConfig.getNextId());

				if (dataSourceNode.getNodeType() == Node.ELEMENT_NODE) {
					Element dataSourceElement = (Element) dataSourceNode;
					ds.setName(dataSourceElement.getAttribute("name"));
					ds.setDsClassName(dataSourceElement.getAttribute("class"));

					// Check if isSpam parameter exists
					
					System.out.println("is Spam is " + 
							dataSourceElement.getAttribute("isSpam"));
					if (dataSourceElement.getAttribute("isSpam") != null) {
						String isSpam = dataSourceElement
								.getAttribute("isSpam");
						if (isSpam.toLowerCase().equals("true")) {
							ds.setSpam(true);
						} else {
							ds.setSpam(false);
						}
					}

					if (dataSourceElement.getAttribute("maxElements") != null
							&& !dataSourceElement.getAttribute("maxElements")
									.equals("")) {
						String maxElements = dataSourceElement
								.getAttribute("maxElements");
						
						System.out.println("maxElements es " + maxElements);
						
						// TODO Test if maxElements is a int number
						ds.setMaxElements(Integer.valueOf(maxElements));
					}

					// NodeList nodeList = dataSourceElement.getChildNodes();
					NodeList dataSourceInfoNode = dataSourceNode
							.getChildNodes();

					for (int i = 0; i < dataSourceInfoNode.getLength(); i++) {
						Node nodeAux = (Node) dataSourceInfoNode.item(i);
						if (nodeAux.getNodeName().equals("customParams")) {
							NodeList customParamsInfoNode = nodeAux
									.getChildNodes();
							for (int j = 0; j < customParamsInfoNode
									.getLength(); j++) {
								Node nodeCustomParamAux = (Node) customParamsInfoNode
										.item(j);

								CustomParamConfig customParam = new CustomParamConfig();
								customParam.setName(nodeCustomParamAux
										.getNodeName());
								customParam.setValue(nodeCustomParamAux
										.getTextContent().trim());
								// Caution!! We are not getting the defaultValue
								// and type because already know it of
								// datasources.xml

								ds.getCustomParams().put(
										nodeCustomParamAux.getNodeName(),
										customParam);
							}
						} else if (nodeAux.getNodeName().equals("srcDirPath")) {
							ds.setFilePath(nodeAux.getTextContent().trim());
						} else if (nodeAux.getNodeName().equals("handler")) {
							ds.setHandlerClassName(nodeAux.getTextContent()
									.trim());
						}
					}

					logger.info(ds);

					config.getDataSourceConfigs().put(ds.getId(), ds);
				}
			}
		} catch (SAXParseException err) {
			logger.error("** Parsing error" + ", line " + err.getLineNumber()
					+ ", uri " + err.getSystemId());
			logger.error(" " + err.getMessage());
			throw new ConfigException(err);

		} catch (SAXException e) {
			throw new ConfigException(e);
		} catch (Throwable t) {
			throw new ConfigException(t);
		}
		// System.exit (0);

	}// end of main

	/**
	 * Get information about of datasources available
	 * 
	 * @param path
	 * @param config
	 */
	public static void getDataSources(String path,
			Map<String, DataSourceConfig> dataSourcesTypes)
			throws ConfigException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(path));

			// normalize text representation
			doc.getDocumentElement().normalize();

			NodeList listOfDS = doc.getElementsByTagName("dataSource");
			int totalDS = listOfDS.getLength();

			System.out.println("path es: " + path);

			System.out.println("total ds: " + totalDS);

			System.out.println("dataSourcesTypes es: " + dataSourcesTypes);

			for (int s = 0; s < listOfDS.getLength(); s++) {
				Node dataSourceNode = listOfDS.item(s);
				DataSourceConfig ds = new DataSourceConfig();

				if (dataSourceNode.getNodeType() == Node.ELEMENT_NODE) {
					Element dataSourceElement = (Element) dataSourceNode;
					ds.setName(dataSourceElement.getAttribute("name"));
					ds.setDsClassName(dataSourceElement.getAttribute("class"));

					System.out.println("name es " + ds.getName());
					System.out.println("class " + ds.getDsClassName());

					// NodeList nodeList = dataSourceElement.getChildNodes();
					NodeList dataSourceInfoNode = dataSourceNode
							.getChildNodes();

					for (int i = 0; i < dataSourceInfoNode.getLength(); i++) {
						Node nodeAux = (Node) dataSourceInfoNode.item(i);
						if (nodeAux.getNodeName().equals("customParams")) {
							NodeList customParamsInfoNode = nodeAux
									.getChildNodes();
							for (int j = 0; j < customParamsInfoNode
									.getLength(); j++) {
								Node nodeCustomParamAux = (Node) customParamsInfoNode
										.item(j);

								if (nodeCustomParamAux.getNodeType() == Node.ELEMENT_NODE) {
									// Extract the type of the parameter
									Element nodeElement = (Element) nodeCustomParamAux;

									CustomParamConfig customParam = new CustomParamConfig();
									customParam.setName(nodeCustomParamAux
											.getNodeName());
									customParam.setType(nodeElement
											.getAttribute("type"));
									customParam.setValue(nodeCustomParamAux
											.getTextContent().trim());
									customParam
											.setDefaultValue(nodeCustomParamAux
													.getTextContent().trim());

									ds.getCustomParams().put(
											nodeCustomParamAux.getNodeName(),
											customParam);
								}
							}
						} else if (nodeAux.getNodeName().equals("handler")) {
							ds.setHandlerClassName(nodeAux.getTextContent()
									.trim());
						}
					}
				}
				dataSourcesTypes.put(ds.getName(), ds);
				logger.info("Reading data source types");
				logger.info(ds);
			}

		} catch (SAXParseException err) {
			logger.error("** Parsing error" + ", line " + err.getLineNumber()
					+ ", uri " + err.getSystemId());
			logger.error(" " + err.getMessage());
			throw new ConfigException(err);

		} catch (SAXException e) {
			throw new ConfigException(e);
		} catch (Throwable t) {

			t.printStackTrace();
			throw new ConfigException(t);
		}
	}

	/**
	 * Return a XML with config
	 * 
	 * @param config
	 * @return
	 */
	public static void saveXMLFromAppConfig(String path, 
			AppConfig config) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("configuration");
			doc.appendChild(rootElement);

			Element numSites = doc.createElement("numSites");
			numSites.setTextContent(config.getNumSites().toString());
			rootElement.appendChild(numSites);

			// set attribute to staff element
			Attr attr = doc.createAttribute("onlyActiveSites");
			attr.setValue(config.getOnlyActiveSites().toString());
			numSites.setAttributeNode(attr);

			attr = doc.createAttribute("downloadAgain");
			attr.setValue(config.getDownloadAgain().toString());
			numSites.setAttributeNode(attr);

			Element ratio = doc.createElement("ratio");
			rootElement.appendChild(ratio);

			attr = doc.createAttribute("isPercentage");
			attr.setValue(config.getRatioIsPercentage().toString());
			ratio.setAttributeNode(attr);

			if (config.getRatioPercentageSpam() != null) {
				Element spam = doc.createElement("percentageSpam");
				spam.setTextContent(config.getRatioPercentageSpam().toString());
				ratio.appendChild(spam);
			}
			
			if (config.getRatioQuantitySpam() != null) {
				Element spam = doc.createElement("quantitySpam");
				spam.setTextContent(config.getRatioPercentageSpam().toString());
				ratio.appendChild(spam);
			}

			Element corpusDirPath = doc.createElement("corpusDirPath");
			corpusDirPath.setTextContent(config.getCorpusDirPath());
			rootElement.appendChild(corpusDirPath);

			Element spamDirName = doc.createElement("spamDirName");
			spamDirName.setTextContent(config.getSpamDirName());
			rootElement.appendChild(spamDirName);

			Element hamDirName = doc.createElement("hamDirName");
			hamDirName.setTextContent(config.getHamDirName());
			rootElement.appendChild(hamDirName);

			Element domainsLabeledFileName = doc
					.createElement("domainsLabeledFileName");
			domainsLabeledFileName.setTextContent(config
					.getDomainsLabeledFileName());
			rootElement.appendChild(domainsLabeledFileName);

			Element domainsNotFoundFileName = doc
					.createElement("domainsNotFoundFileName");
			domainsNotFoundFileName.setTextContent(config
					.getDomainsNotFoundFileName());
			rootElement.appendChild(domainsNotFoundFileName);

			Element flushOutputDir = doc.createElement("flushOutputDir");
			flushOutputDir
					.setTextContent(config.getFlushOutputDir().toString());
			rootElement.appendChild(flushOutputDir);

			Element maxDepthOfCrawling = doc
					.createElement("maxDepthOfCrawling");
			maxDepthOfCrawling.setTextContent(config.getMaxDepthOfCrawling()
					.toString());
			rootElement.appendChild(maxDepthOfCrawling);

			Element numCrawlers = doc.createElement("numCrawlers");
			numCrawlers.setTextContent(config.getNumCrawlers().toString());
			rootElement.appendChild(numCrawlers);

			Element webCrawlerDirTmpStorePath = doc
					.createElement("webCrawlerDirTmpStorePath");
			webCrawlerDirTmpStorePath.setTextContent(config
					.getWebCrawlerTmpStorePath());
			rootElement.appendChild(webCrawlerDirTmpStorePath);

			Element dataSources = doc.createElement("dataSources");
			rootElement.appendChild(dataSources);

			// Start create datasource
			for (DataSourceConfig dsConfig : config.getDataSourceConfigs().values()) {

				Element dataSource = doc.createElement("dataSource");
				dataSources.appendChild(dataSource);

				attr = doc.createAttribute("name");
				attr.setValue(dsConfig.getName());
				dataSource.setAttributeNode(attr);

				attr = doc.createAttribute("class");
				attr.setValue(dsConfig.getDsClassName());
				dataSource.setAttributeNode(attr);

				if (dsConfig.getMaxElements() != null) {
					attr = doc.createAttribute("maxElements");
					attr.setValue(dsConfig.getMaxElements().toString());
					dataSource.setAttributeNode(attr);
				}

				
				Element customParams = doc.createElement("customParams");
				dataSource.appendChild(customParams);

				// Here we put the custom params
				for (String paramName: dsConfig.getCustomParams().keySet()) {
					Element customParam = doc.createElement(paramName);
					customParam.setTextContent(
							dsConfig.getCustomParams().get(paramName).getValue());
					customParams.appendChild(customParam);
				}
				
				// End custom params

				Element srcDirPath = doc.createElement("srcDirPath");
				srcDirPath.setTextContent(dsConfig.getFilePath());
				dataSource.appendChild(srcDirPath);

				Element handler = doc.createElement("handler");
				handler.setTextContent(dsConfig.getHandlerClassName());
				dataSource.appendChild(handler);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			// At the moment we are not to validate Scheme
			validateSchema(doc, Constants.configSchemaFilePath);

			// normalize text representation
			doc.getDocumentElement().normalize();
			
			StreamResult result = new StreamResult(new File(path));

			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get value from first child node element
	 * 
	 * @param doc
	 *            XML document
	 * @param field
	 *            Field to read
	 * @return Value from the first child node element
	 */
	public static String getValueFromElement(Document doc, String field) {
		Element element = (Element) doc.getElementsByTagName(field).item(0);
		NodeList textLNList = element.getChildNodes();
		return ((Node) textLNList.item(0)).getNodeValue().trim();
	}

	/**
	 * Get value from an attribute of a field
	 * 
	 * @param doc
	 * @param field
	 * @param attrName
	 * @return
	 */
	public static String getAttributeFromElement(Document doc, String field,
			String attrName) {
		Element element = (Element) doc.getElementsByTagName(field).item(0);
		return element.getAttribute(attrName);
	}
}
