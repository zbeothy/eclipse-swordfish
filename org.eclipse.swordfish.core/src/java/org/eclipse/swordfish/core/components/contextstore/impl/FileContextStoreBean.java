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
package org.eclipse.swordfish.core.components.contextstore.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swordfish.core.components.jbi.ComponentContextAccess;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;

/**
 * this class implements a file based context storeage. TODO the transient cache is not used
 * anymore!!! remove it
 */
public class FileContextStoreBean extends AbstractContextStore {

    /** component context. */
    private ComponentContextAccess componentContextAccess;

    /** The relative work path. */
    private String relativeWorkPath;

    /** The transient cache. */
    private Map transientCache;

    /** The ensure directory. */
    private boolean ensureDirectory;

    /**
     * Instantiates a new file context store bean.
     */
    public FileContextStoreBean() {
        this.transientCache = new HashMap();
        this.ensureDirectory = false;
    }

    /**
     * Destroy.
     */
    public void destroy() {
        // Auto-generated method stub

    }

    /**
     * Gets the component context access.
     * 
     * @return Returns the componentContextAccess.
     */
    public ComponentContextAccess getComponentContextAccess() {
        return this.componentContextAccess;
    }

    /**
     * Gets the relative work path.
     * 
     * @return Returns the relativeWorkPath.
     */
    public String getRelativeWorkPath() {
        return this.relativeWorkPath;
    }

    /**
     * Init.
     */
    public void init() {
        // Auto-generated method stub

    }

    /**
     * Removes the call context.
     * 
     * @param key
     *        the key
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#removeCallContext(java.lang.String)
     */
    @Override
    public void removeCallContext(final String key) {
        // if the key in kept in the transient memory than remove it from there
        if (this.transientCache.containsKey(key)) {
            this.transientCache.remove(key);
        } else {
            // anyway remove it from the file system
            String filePath = this.createFilePath(key);
            File file = new File(filePath);
            file.delete();
        }
    }

    /**
     * Restore call context.
     * 
     * @param key
     *        the key
     * 
     * @return the call context extension
     * 
     * @throws ContextNotRestoreableException
     * @throws ContextNotFoundException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#restoreCallContext(java.lang.String)
     */
    @Override
    public CallContextExtension restoreCallContext(final String key) throws InternalIllegalInputException,
            InternalInfrastructureException {
        if (this.transientCache.containsKey(key)) {
            CallContextExtension ctx = (CallContextExtension) this.transientCache.get(key);
            this.removeCallContext(key);
            return ctx;
        } else {
            String filePath = this.createFilePath(key);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                throw new InternalConfigurationException("cannot find context that was supposed to be stored in " + filePath, e);
            } catch (SecurityException e) {
                throw new InternalInfrastructureException("cannot read context from file " + filePath + " access denied", e);
            }
            try {
                ObjectInputStream ois = new ObjectInputStream(fis);
                CallContextExtension ctx = (CallContextExtension) ois.readObject();
                ois.close();
                this.removeCallContext(key);
                return ctx;
            } catch (IOException e) {
                throw new InternalInfrastructureException("cannot write context to file " + filePath, e);
            } catch (ClassNotFoundException e) {
                throw new InternalConfigurationException(
                        "cannot find the correct InternalCallContext class for unmarshaling the context " + filePath, e);
            }

        }
    }

    /**
     * Sets the component context access.
     * 
     * @param componentContextAccess
     *        The componentContextAccess to set.
     */
    public void setComponentContextAccess(final ComponentContextAccess componentContextAccess) {
        this.componentContextAccess = componentContextAccess;
    }

    /**
     * Sets the relative work path.
     * 
     * @param relativeWorkPath
     *        The relativeWorkPath to set.
     */
    public void setRelativeWorkPath(final String relativeWorkPath) {
        this.relativeWorkPath = relativeWorkPath;
    }

    /**
     * Store call context.
     * 
     * @param ctx
     *        the ctx
     * 
     * @return the string
     * 
     * @throws ContextNotStoreableException
     * 
     * @see org.eclipse.swordfish.core.components.contextstore.ContextStore#storeCallContext(org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension)
     */
    @Override
    public String storeCallContext(final CallContextExtension ctx) throws InternalIllegalInputException,
            InternalInfrastructureException {
        String key = this.buildKey(ctx);

        // now save the thing also to the filesystem
        String filePath = this.createFilePath(key);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new InternalConfigurationException("cannot write context to file " + filePath, e);
        } catch (SecurityException e) {
            throw new InternalInfrastructureException("cannot write context to file " + filePath + " access denied", e);
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(ctx);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new InternalInfrastructureException("cannot write context to file " + filePath, e);
        }

        return key;
    }

    /**
     * creates a path to the destination where the context should be serialized to, using the key of
     * the context as the file name.
     * 
     * @param key
     *        the key that should used as the file name
     * 
     * @return -- a string representing the fully qualified path to the file that either contains
     *         the context or should be used to write a context to
     */
    private String createFilePath(final String key) {
        String wks = this.getComponentContextAccess().getWorkspaceRoot();
        if (!this.ensureDirectory) {
            File file = new File(wks + File.separator + this.relativeWorkPath);
            file.mkdirs();
            this.ensureDirectory = true;
        }
        return wks + File.separator + this.relativeWorkPath + File.separator + key + ".ctx";
    }

}
