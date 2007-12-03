/*******************************************************************************
 * Copyright (c) 2007 Deutsche Post AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Deutsche Post AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.swordfish.core.management.instrumentation;

import java.io.InputStream;
import java.util.Properties;
import javax.management.ObjectName;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.extension.instrumentation.InternalAlreadyRegisteredException;

/**
 * Interface to register and unregister managed components with the management system.<br/> This
 * redundant implementation is necessary so that the internal users of
 * InternalInstrumentationManager do not depend on the presence of the papi extension jars at
 * runtime
 * 
 * An instance implementing this interface can be obtained via the environment of an existing
 * InternalSBB by
 * 
 * <pre>
 * InternalInstrumentationManager myManager =  mySbb.getEnvironment().getComponent(
 * org.eclipse.swordfish.core.management.instrumentation.InstrumentationManager.class, null)
 * </pre>
 * 
 * This instance remains valid until the InternalSBB is released. As best practice it is recommended
 * to obtain such an instance only once and store it in some variable. <p/> The internal
 * implementation uses Apache Jakarta Commons Modeler to create ModelMBeans for each instrumentation
 * object that is registered.
 * 
 * @see <a
 *      href="http://jakarta.apache.org/commons/modeler/docs/api/org/apache/commons/modeler/package-summary.html">
 *      Sample of the description format</a>
 * @see <a href="http://jakarta.apache.org/commons/modeler/mbeans-descriptors.dtd">Formal definition</a>
 */
public interface InstrumentationManager {

    /**
     * Gets the object name.
     * 
     * @param instrumentation
     *        The instrumentation object for which the ObjectName is to be retrieved
     * 
     * @return the <code>javax.management.ObjectName</code> used to reference the
     *         <code>instrumentation</code> object, <code>null</code> if
     *         <code>instrumentation</code> is not currently registered
     */
    ObjectName getObjectName(Object instrumentation);

    /**
     * Registers an instrumentation object with the management system.<br/> The instrumentation
     * object will be accessible to operators through the InternalSBB management system until it is
     * unregistered again.<br/> Note that it is not legal to register the same instrumentation
     * object multiple times using this method.
     * 
     * @param instrumentation -
     *        the object that implements the management functionality. The types allowed for
     *        attributes, parameters and return values are <code>java.lang.String</code> and the
     *        wrappersfor simple types in <code>java.lang</code>.
     * @param description -
     *        <code>InputStream</code> describing the management interface of the instrumenation
     *        object.
     * 
     * @throws ParticipantHandlingException
     *         if registration had to be aborted due to an internal error
     * @throws InternalAlreadyRegisteredException
     * 
     * @see <a
     *      href="http://jakarta.apache.org/commons/modeler/docs/api/org/apache/commons/modeler/package-summary.html">
     *      Sample of the description format</a>
     * @see <a href="http://jakarta.apache.org/commons/modeler/mbeans-descriptors.dtd">Formal
     *      definition</a>
     */
    void registerInstrumentation(Object instrumentation, InputStream description) throws InternalInfrastructureException,
            InternalAlreadyRegisteredException;

    /**
     * Registers an instrumentation object with the management system.<br/> The instrumentation
     * object will be accessible to operators through the InternalSBB management system until it is
     * unregistered again.<br/> Note that using this method it is possible to register the same
     * instrumentation object multiple times with different name properties.
     * 
     * @param instrumentation -
     *        the object that implements the management functionality. The types allowed for
     *        attributes, parameters and return values are <code>java.lang.String</code> and the
     *        wrappersfor simple types in <code>java.lang</code>.
     * @param description -
     *        <code>InputStream</code> describing the management interface of the instrumenation
     *        object.
     * @param nameProperties -
     *        additional parameter/value pairs to add to <code>ObjectName</code> for the
     *        instrumentation object. <br/> The pairs have to specify valid properties for an
     *        <code>ObjectName</code>. The following parameters are reserved for internal use:
     *        <ul>
     *        <li>class</li>
     *        <li>id</li>
     *        </ul>
     * 
     * @throws ParticipantHandlingException
     *         if registration had to be aborted due to an internal error
     * @throws InternalAlreadyRegisteredException
     * @throws
     * 
     * @see <a
     *      href="http://jakarta.apache.org/commons/modeler/docs/api/org/apache/commons/modeler/package-summary.html">Sample
     *      of the description format</a>
     * @see <a href="http://jakarta.apache.org/commons/modeler/mbeans-descriptors.dtd">Formal
     *      definition</a>
     */
    void registerInstrumentation(Object instrumentation, InputStream description, Properties nameProperties)
            throws InternalInfrastructureException, InternalAlreadyRegisteredException;

    /**
     * Registers an instrumentation object with the management system.<br/> The instrumentation
     * object will be accessible to operators through the InternalSBB management system until it is
     * unregistered again.<br/> Note that using this method it is possible to register the same
     * instrumentation object multiple times with different name properties.
     * 
     * @param instrumentation -
     *        the object that implements the management functionality. The types allowed for
     *        attributes, parameters and return values are <code>java.lang.String</code> and the
     *        wrappersfor simple types in <code>java.lang</code>.
     * @param description -
     *        <code>InputStream</code> describing the management interface of the instrumenation
     *        object.
     * @param nameProperties -
     *        additional parameter/value pairs to add to <code>ObjectName</code> for the
     *        instrumentation object. <br/> The pairs have to specify valid properties for an
     *        <code>ObjectName</code>. The following parameters are reserved for internal use:
     *        <ul>
     *        <li>class</li>
     *        <li>id</li>
     *        </ul>
     * @param typeName
     *        the type attribute from the mbean description. Use this register method if you want to
     *        use an interface as type attribute or if you want to register an instance of a
     *        subclass of the class mentioned in the type attribute.
     * 
     * @throws ParticipantHandlingException
     *         if registration had to be aborted due to an internal error
     * @throws InternalAlreadyRegisteredException
     * 
     * @see <a
     *      href="http://jakarta.apache.org/commons/modeler/docs/api/org/apache/commons/modeler/package-summary.html">Sample
     *      of the description format</a>
     * @see <a href="http://jakarta.apache.org/commons/modeler/mbeans-descriptors.dtd">Formal
     *      definition</a>
     */
    void registerInstrumentation(Object instrumentation, InputStream description, Properties nameProperties, String typeName)
            throws InternalInfrastructureException, InternalAlreadyRegisteredException;

    /**
     * Registers an instrumentation object with the management system.<br/> The instrumentation
     * object will be accessible to operators through the InternalSBB management system until it is
     * unregistered again.<br/> Note that it is not legal to register the same instrumentation
     * object multiple times using this method.
     * 
     * @param instrumentation -
     *        the object that implements the management functionality. The types allowed for
     *        attributes, parameters and return values are <code>java.lang.String</code> and the
     *        wrappersfor simple types in <code>java.lang</code>.
     * @param description -
     *        <code>InputStream</code> describing the management interface of the instrumenation
     *        object.
     * @param typeName
     *        the type attribute from the mbean description. Use this register method if you want to
     *        use an interface as type attribute or if you want to register an instance of a
     *        subclass of the class mentioned in the type attribute.
     * 
     * @throws ParticipantHandlingException
     *         if registration had to be aborted due to an internal error
     * @throws InternalAlreadyRegisteredException
     * 
     * @see <a
     *      href="http://jakarta.apache.org/commons/modeler/docs/api/org/apache/commons/modeler/package-summary.html">
     *      Sample of the description format</a>
     * @see <a href="http://jakarta.apache.org/commons/modeler/mbeans-descriptors.dtd">Formal
     *      definition</a>
     */
    void registerInstrumentation(Object instrumentation, InputStream description, String typeName)
            throws InternalInfrastructureException, InternalAlreadyRegisteredException;

    /**
     * Unregisters all references to an instrumentation object previously registered with the
     * <code>registerInstrumentation</code> methods.<br/> After this method returns, the object
     * will no longer be accessible to operators through the InternalSBB management system<br/>
     * 
     * @param instrumentation -
     *        the object to unregister
     * 
     * @return <code>true</code> if the object was successfully unregistered <code>false</code>
     *         if the object was not registered as an instrumentation object
     * 
     * @throws ParticipantHandlingException
     *         if unregistration had to be aborted due to an internal error
     */
    boolean unregisterInstrumentation(Object instrumentation) throws InternalInfrastructureException;

}
