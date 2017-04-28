package com.magicfan.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Configuration class.
 * This represents connection pool configuration parameters
 */

public class Config {
	private static Logger log = Logger.getLogger(Config.class.getName());
	private static final String CONFIG_FILENAME = "ConnectionPoolConfig.xml";
    private static final Config config = new Config();
    private Map<String, ConnectionConf> settings = new HashMap<>();
    
    private Config() {
    	loadConfig();
    }
    
    /**
     * Get config instance
     * @return config object
     */
    public static Config getInstance() {
    	return config;
    }
    
    /**
     * Get connection config by config name
     * which is defined in ConnectionPoolConfig.xml
     * @param cfgName config name
     * @return connection config object
     */
    public ConnectionConf getConfig(String cfgName) {
    	return settings.get(cfgName);
    }
    
    private void loadConfig() {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
    	File file = new File("config/" + CONFIG_FILENAME);
    	InputStream in = null;
    	try {
    		if (file.exists())
    			in = new FileInputStream(file);
    		else
    			in = this.getClass().getResourceAsStream("/" + CONFIG_FILENAME)	; 
    		
    		if (in == null) {
    			log.severe("Can't find " + CONFIG_FILENAME + " in lib or CLASSPATH, loading config failed!");
    			return;
    		}
    			
	        DocumentBuilder dBuilder = factory.newDocumentBuilder();  
	        Document doc = dBuilder.parse(in);   
	        NodeList configList = doc.getElementsByTagName("connectionpools");
	        for (int i = 0; i < configList.getLength(); i++) {  
                Node cNode = configList.item(i); 
                if (cNode.getNodeType() == Node.ELEMENT_NODE)  {
                	Element celm = (Element)cNode;
                	NodeList nList = celm.getElementsByTagName("connectionpool") ;
			        for ( i = 0; i < nList.getLength(); i++) {  
		                Node configNode = nList.item(i); 
		                if (configNode.getNodeType() == Node.ELEMENT_NODE)  {
			                Element elm = (Element)configNode;
			                String cfgName = elm.getAttribute("name");
			                NodeList configs = elm.getElementsByTagName("property");
			                ConnectionConf info = new ConnectionConf();
			                for (int j = 0; j < configs.getLength(); j++) {
			                	  Node subNode = configs.item(j); 
			                	  if (subNode.getNodeType() == Node.ELEMENT_NODE)  {
			      	                Element subelm = (Element)subNode;
			      	                String name = subelm.getAttribute("name");
			      	                String value = subelm.getFirstChild().getNodeValue();
			      	                setValue(name, value,info );
			      	             }
			                }
			                settings.put(cfgName, info);
		                }
		            }
	           }
           }
	        
    	} catch (IOException | ParserConfigurationException | SAXException e) {
    		log.severe(e.getMessage());
    	} finally {
    		if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
    	}
    }
    
    private void setValue(String name, String value, ConnectionConf info) {
    	String methodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1);
    	try {
	    	Method mtd = info.getClass().getMethod(methodName, new Class[] {String.class});
	    	mtd.invoke(info, value);
    	} catch (Exception e) {
    		log.severe("can't set method value : " + name + " , " + value);
    	}
    }
}
