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
package org.eclipse.swordfish.papi.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.eclipse.swordfish.papi.internal.exception.InternalFatalException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InternalSBBException;

/**
 * <b>Generate an InternalSBB instance</b><br>
 * This factory is used to create InternalSBB instances. It is the very first class one interacts
 * with when developing InternalSBB applications.
 * <p>
 * Its key method is {@link #getSBB(InternalParticipantIdentity)}. This methods returns an
 * InternalSBB instance to work with.
 * </p>
 */
public abstract class InternalSBBFactory {

    /**
     * Contains the Property file name for the delegate implementation class.
     */
    private static final String FACTORY_PROPERTY_FILE = "META-INF/services/org.eclipse.swordfish.papi.SBBFactory";

    /**
     * Contains the Property name of the InternalSBB Factory delegate implementation class name.
     */
    private static final String IMPLEMENTATION_PROPERTY = "classname";

    /**
     * Contains the Delegate object for the creation of instances of a concrete InternalSBB
     * implementation.
     */
    private static InternalSBBFactory instance = null;

    /**
     * This method creates and returns a new instance of an InternalSBB.
     * <p>
     * This method takes the initialization information contained in the
     * <code>InternalParticipantIdentity aParticipantIdentity</code> parameter and creates a new
     * InternalSBB instance based on this information for each unique InternalParticipantIdentity.
     * This new InternalSBB instance is distinct from and does not share resources with other
     * InternalSBB instances. As a result each InternalSBB instance allocates resources of its own.
     * Be careful not to create to many InternalSBB instances to preserve possibly scarce resources
     * such as memory space or network connections.
     * </p>
     * <p>
     * The using application holds a reference to the created InternalSBB instance and reuses it for
     * all operations involving the InternalSBB.
     * </p>
     * <p>
     * Note: If <code>getSBB</code> is called a second time with a
     * <code>InternalParticipantIdentity</code> containing identical values, the method returns
     * the same (identical) InternalSBB instance.<br>
     * Note: This is the same (identical) instance, as longs as
     * {@link org.eclipse.swordfish.papi.InternalSBB#release()} has not been invoked, otherwise it
     * will be a logically equal instance.
     * </p>
     * <p>
     * 
     * @param aParticipantIdentity
     *        The participant identity information the new InternalSBB instance is created for.
     * 
     * @return The newly created InternalSBB instance.
     * 
     * @throws InternalSBBException
     *         when the InternalSBB is unable to start. This can, for example, be a result of:
     *         <ul>
     *         <li>One or more of the configured addresses for the technical providers being
     *         invalid.</li>
     *         <li>One or more of the required technical providers being unreachable.</li>
     *         </ul>
     */
    public static synchronized InternalSBB getSBB(final InternalParticipantIdentity aParticipantIdentity)
            throws InternalSBBException {
        if (null == instance) {
            instance = createInstance();
        }
        InternalSBB theSBB = null;
        theSBB = instance.internalCreateSBB(aParticipantIdentity);
        return theSBB;
    }

    /**
     * Creates a factory delegate configured in the corresponding property.
     * 
     * @return The newly created delegate.
     * @throws InternalFatalException
     *         when the delegate could not be created. This is considered to be an internal
     *         InternalSBB problem.
     */
    private static InternalSBBFactory createInstance() throws InternalSBBException {
        Properties props = new Properties();
        ClassLoader loader = InternalSBBFactory.class.getClassLoader();
        URL url = loader.getResource(FACTORY_PROPERTY_FILE);

        if (null == url)
            throw new InternalFatalException("error while fetching '" + FACTORY_PROPERTY_FILE + "' property file from library.");

        try {
            InputStream inStream = url.openStream();
            BufferedInputStream resourceStream = new BufferedInputStream(inStream);
            props.load(resourceStream);
            resourceStream.close();
        } catch (IOException e) {
            throw new InternalInfrastructureException(e);
        }
        String implementationClassname = props.getProperty(IMPLEMENTATION_PROPERTY);
        if (null == implementationClassname)
            throw new InternalFatalException("bad InternalSBB factory implementation class property");
        /*
         * try to instantiate the class and in the case of exceptions, wrap them into a
         * infrastructure exception and rethrow them.
         */
        try {
            Class implementationClass = loader.loadClass(implementationClassname);
            return (InternalSBBFactory) implementationClass.newInstance();
        } catch (ClassNotFoundException e) {
            throw new InternalFatalException(e);
        } catch (InstantiationException e) {
            throw new InternalFatalException(e);
        } catch (IllegalAccessException e) {
            throw new InternalFatalException(e);
        } catch (ClassCastException e) {
            throw new InternalFatalException(e);
        }
    }

    /**
     * Protected: there is no reason to instantiate this class because only static methods to create
     * new InternalSBB instances are offered.
     */
    protected InternalSBBFactory() {
    }

    /**
     * Internal use only: Worker method for InternalSBB creation. Only used InternalSBB internally.
     * 
     * The method "createSBB" is called from outside, but in reality only delegates the call to an
     * instance of an InternalSBBFactory subclass that implements the "internalCreateSBB" method to
     * actually create an InternalSBB.
     * 
     * @param bootstrapInformation
     *        The bootstrap information needed to create a new InternalSBB instance.
     * @return The newly created InternalSBB instance.
     * 
     * @throws InternalSBBException
     *         when the InternalSBB is unable to start.
     */
    protected abstract InternalSBB internalCreateSBB(final InternalParticipantIdentity bootstrapInformation)
            throws InternalSBBException;
}
