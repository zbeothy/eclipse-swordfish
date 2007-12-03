/***************************************************************************************************
 * Copyright (c) 2007 Deutsche Post AG. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Apache Software Foundation, Deutsche Post AG
 **************************************************************************************************/
/*
 * Copyright 2004-2005 The Apache Software Foundation.
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.InvariantReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

/**
 * The Class AbstractFileConfiguration.
 * 
 * 
 * adaption of commons-configuration AbstractFileConfiguration
 * 
 * <p>
 * Partial implementation of the <code>FileConfiguration</code> interface. Developpers of file
 * based configuration may want to extend this class, the two methods left to implement are
 * {@see AbstractFileConfiguration#load(Reader)} and {@see AbstractFileConfiguration#save(Reader)}.
 * </p>
 * <p>
 * This base class already implements a couple of ways to specify the location of the file this
 * configuration is based on. The following possibilities exist:
 * <ul>
 * <li>URLs: With the method <code>setURL()</code> a full URL to the configuration source can be
 * specified. This is the most flexible way. Note that the <code>save()</code> methods support
 * only <em>file:</em> URLs.</li>
 * <li>Files: The <code>setFile()</code> method allows to specify the configuration source as a
 * file. This can be either a relative or an absolute file. In the former case the file is resolved
 * based on the current directory.</li>
 * <li>As file paths in string form: With the <code>setPath()</code> method a full path to a
 * configuration file can be provided as a string.</li>
 * <li>Separated as base path and file name: This is the native form in which the location is
 * stored. The base path is a string defining either a local directory or a URL. It can be set using
 * the <code>setBasePath()</code> method. The file name, non surprisingly, defines the name of the
 * configuration file.</li>
 * </ul>
 * </p>
 * <p>
 * Note that the <code>load()</code> methods do not wipe out the configuration's content before
 * the new configuration file is loaded. Thus it is very easy to construct a union configuration by
 * simply loading multiple configuration files, e.g.
 * </p>
 * <p>
 * 
 * <pre>
 * config.load(configFile1);
 * config.load(configFile2);
 * </pre>
 * 
 * </p>
 * <p>
 * After executing this code fragment, the resulting configuration will contain both the properties
 * of configFile1 and configFile2. On the other hand, if the current configuration file is to be
 * reloaded, <code>clear()</code> should be called first. Otherwise the properties are doubled.
 * This behavior is analogous to the behavior of the <code>load(InputStream)</code> method in
 * <code>java.util.Properties</code>.
 * </p>
 */
public abstract class AbstractFileConfiguration extends BaseConfiguration implements FileConfiguration {

    /** The file name. */
    private String fileName;

    /** The base path. */
    private String basePath;

    /** The auto save. */
    private boolean autoSave;

    /** The strategy. */
    private ReloadingStrategy strategy;

    /** The reload lock. */
    private Object reloadLock = new Object();

    /** The encoding. */
    private String encoding;

    /**
     * Default constructor.
     */
    public AbstractFileConfiguration() {
        this.setReloadingStrategy(new InvariantReloadingStrategy());
    }

    /**
     * Creates and loads the configuration from the specified file.
     * 
     * @param file
     *        The file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     */
    public AbstractFileConfiguration(final File file) throws ConfigurationException {
        this();

        // set the file and update the url, the base path and the file name
        this.setFile(file);

        // load the file
        if (file.exists()) {
            this.load();
        }
    }

    /**
     * Creates and loads the configuration from the specified file. The passed in string must be a
     * valid file name, either absolute or relativ.
     * 
     * @param fileName
     *        The name of the file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     * 
     */
    public AbstractFileConfiguration(final String fileName) throws ConfigurationException {
        this();

        // store the file name
        this.setPath(fileName);

        // load the file
        this.load();
    }

    /**
     * Creates and loads the configuration from the specified URL.
     * 
     * @param url
     *        The location of the file to load.
     * 
     * @throws ConfigurationException
     *         Error while loading the file
     */
    public AbstractFileConfiguration(final URL url) throws ConfigurationException {
        this();

        // set the URL and update the base path and the file name
        this.setURL(url);

        // load the file
        this.load();
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
        this.possiblySave();
    }

    /**
     * Contains key.
     * 
     * @param key
     *        the key
     * 
     * @return true, if successful
     */
    @Override
    public boolean containsKey(final String key) {
        this.reload();
        return super.containsKey(key);
    }

    /**
     * Return the base path.
     * 
     * @return the base path
     */
    public String getBasePath() {
        return this.basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#getEncoding()
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Return the file where the configuration is stored. If the base path is a URL with a protocol
     * different than &quot;file&quot;, the return value will not point to a valid file object.
     * 
     * @return the file where the configuration is stored
     */
    public File getFile() {
        return ConfigurationUtils.getFile(this.getBasePath(), this.getFileName());
    }

    /**
     * Return the name of the file.
     * 
     * @return the file name
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Gets the keys.
     * 
     * @return the keyss
     */
    @Override
    public Iterator getKeys() {
        this.reload();
        return super.getKeys();
    }

    /**
     * Returns the full path to the file this configuration is based on. The return value is valid
     * only if this configuration is based on a file on the local disk.
     * 
     * @return the full path to the configuration file
     */
    public String getPath() {
        return this.getFile().getAbsolutePath();
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
        return this.strategy;
    }

    public ReloadingStrategy getStrategy() {
        return this.strategy;
    }

    /**
     * Return the URL where the configuration is stored.
     * 
     * @return the configuration's location as URL
     */
    public URL getURL() {
        return ConfigurationUtils.locate(this.getBasePath(), this.getFileName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#isAutoSave()
     */
    public boolean isAutoSave() {
        return this.autoSave;
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
     * Load the configuration from the underlying location.
     * 
     * @throws ConfigurationException
     *         if loading of the configuration fails
     */
    public void load() throws ConfigurationException {
        this.load(this.getFileName());
    }

    /**
     * Load the configuration from the specified file.
     * 
     * @param file
     *        the loaded file
     * 
     * @throws ConfigurationException
     */
    public void load(final File file) throws ConfigurationException {
        try {
            this.load(file.toURL());
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Load the configuration from the specified stream, using the encoding returned by
     * {@link #getEncoding()}.
     * 
     * @param in
     *        the input stream
     * 
     * @throws ConfigurationException
     */
    public void load(final InputStream in) throws ConfigurationException {
        this.load(in, this.getEncoding());
    }

    /**
     * Load the configuration from the specified stream, using the specified encoding. If the
     * encoding is null the default encoding is used.
     * 
     * @param in
     *        the input stream
     * @param sEncoding
     *        the encoding used. <code>null</code> to use the default encoding
     * 
     * @throws ConfigurationException
     */
    public void load(final InputStream in, final String sEncoding) throws ConfigurationException {
        Reader reader = null;

        if (sEncoding != null) {
            try {
                reader = new InputStreamReader(in, sEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
            }
        }

        if (reader == null) {
            reader = new InputStreamReader(in);
        }

        this.load(reader);
    }

    /**
     * Locate the specified file and load the configuration.
     * 
     * @param sFileName
     *        the name of the file loaded
     * 
     * @throws ConfigurationException
     */
    public void load(final String sFileName) throws ConfigurationException {
        try {
            URL url = ConfigurationUtils.locate(this.basePath, sFileName);
            if (url == null) throw new ConfigurationException("Cannot locate configuration source " + sFileName);
            this.load(url);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Load the configuration from the specified URL.
     * 
     * @param url
     *        the URL of the file loaded
     * 
     * @throws ConfigurationException
     */
    public void load(final URL url) throws ConfigurationException {
        InputStream in = null;

        try {
            in = url.openStream();
            this.load(in);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        } finally {
            // close the input stream
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reload.
     */
    public void reload() {
        synchronized (this.reloadLock) {
            if (this.strategy.reloadingRequired()) {
                try {
                    this.clear();
                    this.load();

                    // notify the strategy
                    this.strategy.reloadingPerformed();
                } catch (Exception e) {
                    e.printStackTrace();
                    // todo rollback the changes if the file can't be reloaded
                }
            }
        }
    }

    /**
     * Save the configuration.
     * 
     * @throws ConfigurationException
     */
    public void save() throws ConfigurationException {
        this.save(this.fileName);
        this.strategy.init();
    }

    /**
     * Save the configuration to the specified file. The file is created automatically if it doesn't
     * exist. This doesn't change the source of the configuration, use {@link #setFile} if you need
     * it.
     * 
     * @param file
     *        the file
     * 
     * @throws ConfigurationException
     */
    public void save(final File file) throws ConfigurationException {
        OutputStream out = null;

        try {
            // create the file if necessary
            this.createPath(file);
            out = new FileOutputStream(file);
            this.save(out);
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage(), e);
        } finally {
            // close the output stream
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save the configuration to the specified stream, using the encoding returned by
     * {@link #getEncoding()}.
     * 
     * @param out
     *        the out
     * 
     * @throws ConfigurationException
     */
    public void save(final OutputStream out) throws ConfigurationException {
        this.save(out, this.getEncoding());
    }

    /**
     * Save the configuration to the specified stream, using the specified encoding. If the encoding
     * is null the default encoding is used.
     * 
     * @param out
     *        the out
     * @param sEncoding
     *        the encoding
     * 
     * @throws ConfigurationException
     */
    public void save(final OutputStream out, final String sEncoding) throws ConfigurationException {
        Writer writer = null;

        if (sEncoding != null) {
            try {
                writer = new OutputStreamWriter(out, sEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("The requested encoding is not supported, try the default encoding.", e);
            }
        }

        if (writer == null) {
            writer = new OutputStreamWriter(out);
        }

        this.save(writer);
    }

    /**
     * Save the configuration to the specified file. This doesn't change the source of the
     * configuration, use setFileName() if you need it.
     * 
     * @param sFileName
     *        the file name
     * 
     * @throws ConfigurationException
     */
    public void save(final String sFileName) throws ConfigurationException {
        try {
            File file = ConfigurationUtils.getFile(this.basePath, sFileName);
            if (file == null) throw new ConfigurationException("Invalid file name for save: " + sFileName);
            this.save(file);
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }
    }

    /**
     * Save the configuration to the specified URL if it's a file URL. This doesn't change the
     * source of the configuration, use setURL() if you need it.
     * 
     * @param url
     *        the url
     * 
     * @throws ConfigurationException
     */
    public void save(final URL url) throws ConfigurationException {
        File file = ConfigurationUtils.fileFromURL(url);
        if (file != null) {
            this.save(file);
        } else
            throw new ConfigurationException("Could not save to URL " + url);
    }

    /**
     * Sets the auto save.
     * 
     * @param autoSave
     *        the new auto savee
     */
    public void setAutoSave(final boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * Set the base path. Relative configurations are loaded from this path. The base path can be
     * either a path to a directory or a URL.
     * 
     * @param basePath
     *        the base path.
     */
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.configuration.FileConfiguration#setEncoding(java.lang.String)
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set the file where the configuration is stored. The passed in file is made absolute if it is
     * not yet. Then the file's path component becomes the base path and its name component becomes
     * the file name.
     * 
     * @param file
     *        the file where the configuration is stored
     */
    public void setFile(final File file) {
        this.setFileName(file.getName());
        this.setBasePath((file.getParentFile() != null) ? file.getParentFile().getAbsolutePath() : null);
    }

    /**
     * Set the name of the file. The passed in file name should not contain a path. Use
     * <code>{@link AbstractFileConfiguration#setPath(String)
     * setPath()}</code> to set a full
     * qualified file name.
     * 
     * @param fileName
     *        the name of the file
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the location of this configuration as a full path name. The passed in path should
     * represent a valid file name.
     * 
     * @param path
     *        the full path name of the configuration file
     */
    public void setPath(final String path) {
        this.setFile(new File(path));
    }

    /**
     * Sets the reloading strategy.
     * 
     * @param rlStrategy
     *        the new reloading strategyy
     */
    public void setReloadingStrategy(final ReloadingStrategy rlStrategy) {
        this.strategy = rlStrategy;
        rlStrategy.setConfiguration(this);
        rlStrategy.init();
    }

    public void setStrategy(final ReloadingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Set the location of this configuration as a URL. For loading this can be an arbitrary URL
     * with a supported protocol. If the configuration is to be saved, too, a URL with the
     * &quot;file&quot; protocol should be provided.
     * 
     * @param url
     *        the location of this configuration as URL
     */
    public void setURL(final URL url) {
        this.setBasePath(ConfigurationUtils.getBasePath(url));
        this.setFileName(ConfigurationUtils.getFileName(url));
    }

    /**
     * Adds the property direct.
     * 
     * @param key
     *        the key
     * @param obj
     *        the obj
     */
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        super.addPropertyDirect(key, obj);
        this.possiblySave();
    }

    /**
     * Save the configuration if the automatic persistence is enabled and if a file is specified.
     */
    protected void possiblySave() {
        if (this.autoSave && (this.fileName != null)) {
            try {
                this.save();
            } catch (ConfigurationException e) {
                throw new ConfigurationRuntimeException("Failed to auto-save", e);
            }
        }
    }

    /**
     * Create the path to the specified file.
     * 
     * @param file
     *        the file
     */
    private void createPath(final File file) {
        if (file != null) {
            // create the path to the file if the file doesn't exist
            if (!file.exists()) {
                File parent = file.getParentFile();
                if ((parent != null) && !parent.exists()) {
                    parent.mkdirs();
                }
            }
        }
    }
}