/**
 *
 */
package org.eclipse.swordfish.core.wsdl;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;


/**
 * @author dwolz
 *
 */
public interface WSDLManager {

    /**
     * Read all WSDL definitions for a given path to a zip file as resource
     * using the class loader.
     * @param url - the location of the WSDL zip file to load
     */
	public void setupWSDLs(URL wsdlPath) throws WSDLException, IOException;

	/**
     * Get the WSDL definition for the given port type.  Implementations
     * return a copy from a local cache
     * @param portType - the portType defined in the wsdl
     * @return the wsdl definition
     */
	public Definition getDefinition(QName portType) throws WSDLException;

    /**
     * Get the WSDL definition for the given URL.  Implementations
     * may return a copy from a local cache or load a new copy
     * from the URL.
     * @param url - the location of the WSDL to load
     * @return the wsdl definition
     */
    Definition getDefinition(String url) throws WSDLException;

	/**
     * Get the service description for the given port type.
     * @param portType - the portType defined in the wsdl
     * @return the service description
     */
	ServiceDescription getServiceDescription(QName portType) throws WSDLException;

    /**
     * Get the service description for the given URL.  Implementations
     * may return a copy from a local cache or load a new copy
     * from the URL.
     * @param url - the location of the WSDL to load
     * @return the service description
     */
	ServiceDescription getServiceDescription(String url) throws WSDLException;

    /**
     * Adds a definition into the cache for lookup later
     * @param key
     * @param wsdl
     */
    void addDefinition(Object key, Definition wsdl);

    /**
     * Removes a definition from the cache
     * @param wsdl
     */
	void removeDefinition(Definition wsdl);

    /**
     *
     * @return all Definitions in the map
     */
    Map<Object, Definition> getDefinitions();

}
