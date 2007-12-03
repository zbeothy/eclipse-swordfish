/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Apache Software Foundation, Deutsche Post AG
 **************************************************************************************************/
/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.eclipse.swordfish.configrepos.shared;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

/**
 * The Class AbstractHierarchicalFileConfiguration.
 * 
 * 
 * adaption of commons-configuration AbstractHierarchicalFileConfiguration
 * 
 * <p>
 * Base class for implementing file based hierarchical configurations.
 * </p>
 * <p>
 * This class serves an analogous purpose as the <code>{@link AbstractFileConfiguration}</code>
 * class for non hierarchical configurations. It behaves in exactly the same way, so please refer to
 * the documentation of <code>AbstractFileConfiguration</code> for further details.
 * </p>
 */
public abstract class AbstractHierarchicalFileConfiguration extends HierarchicalConfiguration implements FileConfiguration {

    /**
     * Stores the delegate used for implementing functionality related to the
     * <code>FileConfiguration</code> interface.
     */
    private FileConfigurationDelegate delegate = this.createDelegate();

    /**
     * Creates and loads the configuration from the specified file.
     * 
     * @param file
     *        The configuration file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     */
    public AbstractHierarchicalFileConfiguration(final File file) throws ConfigurationException {
        // set the file and update the url, the base path and the file name
        this.setFile(file);

        // load the file
        if (file.exists()) {
            this.load();
        }
    }

    /**
     * Creates and loads the configuration from the specified file.
     * 
     * @param fileName
     *        The name of the plist file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     */
    public AbstractHierarchicalFileConfiguration(final String fileName) throws ConfigurationException {
        // store the file name
        this.delegate.setPath(fileName);

        // load the file
        this.load();
    }

    /**
     * Creates and loads the configuration from the specified URL.
     * 
     * @param url
     *        The location of the configuration file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     */
    public AbstractHierarchicalFileConfiguration(final URL url) throws ConfigurationException {
        // set the URL and update the base path and the file name
        this.setURL(url);

        // load the file
        this.load();
    }

    /**
     * Instantiates a new abstract hierarchical file configuration.
     */
    protected AbstractHierarchicalFileConfiguration() {
    }

    /**
     * Clear property.
     * 
     * @param key
     *        the key
     */
    @Override
    public void clearProperty(final String key) {
        super.clearProperty(key);
        this.delegate.possiblySave();
    }

    /**
     * Clear tree.
     * 
     * @param key
     *        the key
     */
    @Override
    public void clearTree(final String key) {
        super.clearTree(key);
        this.delegate.possiblySave();
    }

    /**
     * Contains key.
     * 
     * @param key
     *        the key
     * 
     * @return true, if successful
     */
    public boolean containsKeyy(final String key) {
        this.reload();
        return super.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getBasePath()
     */
    public String getBasePath() {
        return this.delegate.getBasePath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getEncoding()
     */
    public String getEncoding() {
        return this.delegate.getEncoding();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getFile()
     */
    public File getFile() {
        return this.delegate.getFile();
    }

    /**
     * Gets the file name.
     * 
     * @return the file namee
     */
    public String getFileName() {
        return this.delegate.getFileName();
    }

    /**
     * Gets the keys.
     * 
     * @param prefix
     *        the prefix
     * 
     * @return the keyss
     */
    @Override
    public Iterator getKeys(final String prefix) {
        this.reload();
        return super.getKeys(prefix);
    }

    /**
     * Gets the property.
     * 
     * @param key
     *        the key
     * 
     * @return the propertyy
     */
    @Override
    public Object getProperty(final String key) {
        this.reload();
        return super.getProperty(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getReloadingStrategy()
     */
    public ReloadingStrategy getReloadingStrategy() {
        return this.delegate.getReloadingStrategy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getURL()
     */
    public URL getURL() {
        return this.delegate.getURL();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#isAutoSave()
     */
    public boolean isAutoSave() {
        return this.delegate.isAutoSave();
    }

    /**
     * Checks if is empty.
     * 
     * @return true, if is emptyy
     */
    @Override
    public boolean isEmpty() {
        this.reload();
        return super.isEmpty();
    }

    /**
     * Load.
     * 
     * @throws ConfigurationException
     */
    public void load() throws ConfigurationException {
        this.delegate.load();
    }

    /**
     * Load.
     * 
     * @param file
     *        the file
     * 
     * @throws ConfigurationException
     */
    public void load(final File file) throws ConfigurationException {
        this.delegate.load(file);
    }

    /**
     * Load.
     * 
     * @param in
     *        the in
     * 
     * @throws ConfigurationException
     */
    public void load(final InputStream in) throws ConfigurationException {
        this.delegate.load(in);
    }

    /**
     * Load.
     * 
     * @param in
     *        the in
     * @param encoding
     *        the encoding
     * 
     * @throws ConfigurationException
     */
    public void load(final InputStream in, final String encoding) throws ConfigurationException {
        this.delegate.load(in, encoding);
    }

    /**
     * Load.
     * 
     * @param fileName
     *        the file name
     * 
     * @throws ConfigurationException
     */
    public void load(final String fileName) throws ConfigurationException {
        this.delegate.load(fileName);
    }

    /**
     * Load.
     * 
     * @param url
     *        the url
     * 
     * @throws ConfigurationException
     */
    public void load(final URL url) throws ConfigurationException {
        this.delegate.load(url);
    }

    /**
     * Reload.
     */
    public void reload() {
        this.delegate.reload();
    }

    /**
     * Save.
     * 
     * @throws ConfigurationException
     */
    public void save() throws ConfigurationException {
        this.delegate.save();
    }

    /**
     * Save.
     * 
     * @param file
     *        the file
     * 
     * @throws ConfigurationException
     */
    public void save(final File file) throws ConfigurationException {
        this.delegate.save(file);
    }

    /**
     * Save.
     * 
     * @param out
     *        the out
     * 
     * @throws ConfigurationException
     */
    public void save(final OutputStream out) throws ConfigurationException {
        this.delegate.save(out);
    }

    /**
     * Save.
     * 
     * @param out
     *        the out
     * @param encoding
     *        the encoding
     * 
     * @throws ConfigurationException
     */
    public void save(final OutputStream out, final String encoding) throws ConfigurationException {
        this.delegate.save(out, encoding);
    }

    /**
     * Save.
     * 
     * @param fileName
     *        the file name
     * 
     * @throws ConfigurationException
     */
    public void save(final String fileName) throws ConfigurationException {
        this.delegate.save(fileName);
    }

    /**
     * Save.
     * 
     * @param url
     *        the url
     * 
     * @throws ConfigurationException
     */
    public void save(final URL url) throws ConfigurationException {
        this.delegate.save(url);
    }

    /**
     * Sets the auto save.
     * 
     * @param autoSave
     *        the new t auto save
     */
    public void setAutoSave(final boolean autoSave) {
        this.delegate.setAutoSave(autoSave);
    }

    /**
     * Sets the base path.
     * 
     * @param basePath
     *        the new base pathh
     */
    public void setBasePath(final String basePath) {
        this.delegate.setBasePath(basePath);
    }

    /**
     * Sets the encoding.
     * 
     * @param encoding
     *        the new encodingg
     */
    public void setEncoding(final String encoding) {
        this.delegate.setEncoding(encoding);
    }

    /**
     * Sets the file.
     * 
     * @param file
     *        the new filee
     */
    public void setFile(final File file) {
        this.delegate.setFile(file);
    }

    /**
     * Sets the file name.
     * 
     * @param fileName
     *        the new file namee
     */
    public void setFileName(final String fileName) {
        this.delegate.setFileName(fileName);
    }

    /**
     * Sets the property.
     * 
     * @param key
     *        the key
     * @param value
     *        the value
     */
    @Override
    public void setProperty(final String key, final Object value) {
        super.setProperty(key, value);
        this.delegate.possiblySave();
    }

    /**
     * Sets the reloading strategy.
     * 
     * @param strategy
     *        the new reloading strategyy
     */
    public void setReloadingStrategy(final ReloadingStrategy strategy) {
        this.delegate.setReloadingStrategy(strategy);
    }

    /**
     * Sets the URL.
     * 
     * @param url
     *        the new t URL
     */
    public void setURL(final URL url) {
        this.delegate.setURL(url);
    }

    /**
     * Add property direct.
     * 
     * @param key
     *        the key
     * @param obj
     *        the obj
     */
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        super.addPropertyDirect(key, obj);
        this.delegate.possiblySave();
    }

    /**
     * Creates the file configuration delegate, i.e. the object that implements functionality
     * required by the <code>FileConfiguration</code> interface. This base implementation will
     * return an instance of the <code>FileConfigurationDelegate</code> class. Derived classes may
     * override it to create a different delegate object.
     * 
     * @return the file configuration delegate
     */
    protected FileConfigurationDelegate createDelegate() {
        return new FileConfigurationDelegate();
    }

    /**
     * Returns the file configuration delegate.
     * 
     * @return the delegate
     */
    protected FileConfigurationDelegate getDelegate() {
        return this.delegate;
    }

    /**
     * Allows to set the file configuration delegate.
     * 
     * @param delegate
     *        the new delegate
     */
    protected void setDelegate(final FileConfigurationDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * A special implementation of the <code>FileConfiguration</code> interface that is used
     * internally to implement the <code>FileConfiguration</code> methods for hierarchical
     * configurations.
     */
    protected class FileConfigurationDelegate extends AbstractFileConfiguration {

        /**
         * Clear.
         */
        @Override
        public void clear() {
            AbstractHierarchicalFileConfiguration.this.clear();
        }

        /**
         * Load.
         * 
         * @param in
         *        the in
         * 
         * @throws ConfigurationException
         */
        public void load(final Reader in) throws ConfigurationException {
            AbstractHierarchicalFileConfiguration.this.load(in);
        }

        /**
         * Save.
         * 
         * @param out
         *        the out
         * 
         * @throws ConfigurationException
         */
        public void save(final Writer out) throws ConfigurationException {
            AbstractHierarchicalFileConfiguration.this.save(out);
        }
    }
}
