/**
 *
 */
package org.eclipse.swordfish.core.wsdl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.wsdl.Definition;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author dwolz
 *
 */
public class WSDLManagerImpl implements WSDLManager {

	private static final Logger LOG = LoggerFactory.getLogger(WSDLManagerImpl.class);
	private static final String WSDL_PATH = "hello_world.wsdl";

	final WSDLFactory factory;
	final Map<Object, Definition> definitionsMap;
	final Map<Object, Service> serviceMap;
	final Map<Object, ServiceDescriptionImpl> descriptionsMap;

	public WSDLManagerImpl() {
		try {
			factory = WSDLFactory.newInstance();
		} catch (WSDLException e) {
			throw new RuntimeException(e.getMessage());
		}
		definitionsMap = new HashMap<Object, Definition>();
		serviceMap = new HashMap<Object, Service>();
		descriptionsMap = new HashMap<Object, ServiceDescriptionImpl>();
	}

	public WSDLFactory getWSDLFactory() {
		return factory;
	}

    public void setupWSDLs(URL wsdlPath) throws WSDLException, IOException {
    	String wsdlUrl = wsdlPath.toString();
    	if (!wsdlUrl.endsWith(".zip")) {
         	LOG.info("the path of wsdl file is " + wsdlUrl);
        	getDefinition(wsdlUrl);
    	} else {
	    	InputStream wsdlIs = wsdlPath.openStream();
	    	try {
	    	ZipInputStream wsdlZipIs = new ZipInputStream(wsdlIs);
	    	ZipEntry entry;
	    	while ((entry = wsdlZipIs.getNextEntry()) != null) {
	    		if (!entry.isDirectory() &&
	    				!entry.getName().startsWith("_") &&
	    				!entry.getName().startsWith(".") &&
	    				 entry.getName().endsWith(".wsdl")) {
		    		String entryName = entry.getName();
		    		byte[] bytes = streamToByteArray(wsdlZipIs); // stream must be "closeable"
		     		InputSource src = new InputSource(new ByteArrayInputStream(bytes));
		    		loadDefinition(entryName, src);
	    		}
		    }} finally {
		    	wsdlIs.close();
		    }
    	}
        for (Object qname: definitionsMap.keySet()) {
        	if (qname instanceof QName) {
	            LOG.info("portType = " + qname);
//	            LOG.info("definition = " + definitionsMap.get(qname).toString());
//	            if (serviceMap.containsKey(qname))
//	            	LOG.info("service = " + serviceMap.get(qname).toString());
        	}
        }
    }

	public Map<Object, Definition> getDefinitions() {
		synchronized (definitionsMap) {
			return Collections.unmodifiableMap(definitionsMap);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.cxf.wsdl.WSDLManager#getDefinition(java.net.URL)
	 */
	public Definition getDefinition(QName portType) throws WSDLException {
		synchronized (definitionsMap) {
			if (definitionsMap.containsKey(portType)) {
				return definitionsMap.get(portType);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.cxf.wsdl.WSDLManager#getDefinition(java.net.URL)
	 */
	public Definition getDefinition(URL url) throws WSDLException {
		synchronized (definitionsMap) {
			if (definitionsMap.containsKey(url)) {
				return definitionsMap.get(url);
			}
		}
		Definition def = loadDefinition(url.toString());
		synchronized (definitionsMap) {
			definitionsMap.put(url, def);
		}
		return def;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.cxf.wsdl.WSDLManager#getDefinition(java.lang.String)
	 */
	public Definition getDefinition(String url) throws WSDLException {
		synchronized (definitionsMap) {
			if (definitionsMap.containsKey(url)) {
				return definitionsMap.get(url);
			}
		}
		return loadDefinition(url);
	}

	public ServiceDescription getServiceDescription(QName portType)
			throws WSDLException {
		synchronized (descriptionsMap) {
			if (descriptionsMap.containsKey(portType)) {
				return descriptionsMap.get(portType);
			}
		}
		Definition definition = getDefinition(portType);
		if (definition != null) {
			try {
				ServiceDescriptionImpl description = new ServiceDescriptionImpl(definition);
				descriptionsMap.put(portType, description);
				return description;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public ServiceDescription getServiceDescription(String url)
			throws WSDLException {
		synchronized (descriptionsMap) {
			if (descriptionsMap.containsKey(url)) {
				return descriptionsMap.get(url);
			}
		}
		Definition definition = getDefinition(url);
		if (definition != null) {
			try {
				ServiceDescriptionImpl description = new ServiceDescriptionImpl(definition);
				descriptionsMap.put(url, description);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void addDefinition(Object key, Definition wsdl) {
		synchronized (definitionsMap) {
			definitionsMap.put(key, wsdl);
		}
	}

	private Definition loadDefinition(String url) throws WSDLException {
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		reader.setFeature("javax.wsdl.importDocuments", true);
		Definition def = reader.readWSDL(url);
   		QName portType = (QName) def.getPortTypes().keySet().iterator().next();
   	 	synchronized (definitionsMap) {
			definitionsMap.put(url, def);
			definitionsMap.put(portType, def);
		}
		for (Service serv : (Iterable<Service>)def.getServices().values()) {
            if (serv != null) {
            	synchronized (serviceMap) {
            		serviceMap.put(url, serv);
            		serviceMap.put(portType, serv);
        		}
            	break;
            }
        }
		return def;
	}

	private Definition loadDefinition(String name, InputSource src) throws WSDLException {
		WSDLReader reader = factory.newWSDLReader();
		reader.setFeature("javax.wsdl.verbose", false);
		reader.setFeature("javax.wsdl.importDocuments", false);
		Definition def = reader.readWSDL("", src);
   		QName portType = (QName) def.getPortTypes().keySet().iterator().next();
   	 	synchronized (definitionsMap) {
			definitionsMap.put(name, def);
			definitionsMap.put(portType, def);
		}
		for (Service serv : (Iterable<Service>)def.getServices().values()) {
            if (serv != null) {
            	synchronized (serviceMap) {
            		serviceMap.put(name, serv);
            		serviceMap.put(portType, serv);
        		}
            	break;
            }
        }
		return def;
	}

	public void removeDefinition(Definition wsdl) {
		synchronized (definitionsMap) {
			List<Object> keys = new ArrayList<Object>();
			for (Map.Entry<Object, Definition> e : definitionsMap.entrySet()) {
				if (e.getValue() == wsdl) {
					keys.add(e.getKey());
				}
			}
			for (Object o : keys) {
				definitionsMap.remove(o);
				serviceMap.remove(o);
			}
		}
	}

	private static byte[] streamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
  		byte[] buffer=new byte[1024];//byte buffer
		for (;;){
			int bytesRead = is.read(buffer,0,1024);
			if (bytesRead == -1)
				break;
			bos.write(buffer,0,bytesRead);
		}
		return bos.toByteArray();
	}



}
