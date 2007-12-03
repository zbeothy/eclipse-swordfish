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
package org.eclipse.swordfish.configrepos.util.patch;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.FileSequence;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.apache.commons.transaction.file.ResourceManagerSystemException;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.w3c.dom.Document;

/**
 * The Class LocalVsRemotefallbackController.
 * 
 */
public class LocalVsRemotefallbackController {

    /** Name of the remote data standin. */
    public static final String REMOTEFALLBACKCFG_XML_PATTERN = "remotefallbackcfg.xml";

    /** Name of the local data file. */
    public static final String SBBLOCALCFG_XML_PATTERN = "sbblocalcfg.xml";

    /**
     * The main method.
     * 
     * @param args
     *        the args
     */
    public static void main(final String[] args) {
        LocalVsRemotefallbackController controller = new LocalVsRemotefallbackController();

        if (args.length < 6)
            throw new IllegalStateException("Missing parameters or values. At least --base <dir>, "
                    + "--installdir <rel-dir> and --workspacedir <dir> must be set");

        try {
            // Look for delegate definitions to load
            for (int pos = 0; pos < args.length; pos++) {
                if (pos == args.length - 1) throw new IllegalStateException("Missing parameters or values");

                if (args[pos].equals("--base")) {
                    if (!(new File(args[++pos]).isDirectory()))
                        throw new IllegalStateException("Parameter --base '" + new File(args[pos]).getCanonicalPath()
                                + "' is not a directory");
                    controller.sbbBaseDirString = args[pos];
                } else if (args[pos].equals("--installdir")) {
                    if (!(new File(controller.sbbBaseDirString + File.separatorChar + args[++pos]).isDirectory()))
                        throw new IllegalStateException("Parameter --installdir '"
                                + new File(controller.sbbBaseDirString + File.separatorChar + args[pos]).getCanonicalPath()
                                + "' is not a directory");
                    controller.installDirString = args[pos];
                } else if (args[pos].equals("--workspacedir")) {
                    if (!(new File(controller.sbbBaseDirString + File.separatorChar + args[++pos]).isDirectory()))
                        throw new IllegalStateException("Parameter --workspace '"
                                + new File(controller.sbbBaseDirString + File.separatorChar + args[pos]).getCanonicalPath()
                                + "' is not a directory");
                    controller.workspaceDirString = args[pos];
                } else if (args[pos].equals("--delegate")) {
                    try {
                        Class clazz = Thread.currentThread().getContextClassLoader().loadClass(args[++pos]);
                        Constructor constructor = clazz.getConstructor(new Class[] {LocalVsRemotefallbackController.class});
                        controller.addDelegate((LocalVsRemotefallbackDelegate) constructor.newInstance(new Object[] {controller}));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Couldn't instantiate delegate:" + e.getMessage());
                    }
                } else if (args[pos].equals("--localpattern")) {
                    controller.setLocalpattern(args[++pos]);
                } else if (args[pos].equals("--remotepattern")) {
                    controller.setRemotepattern(args[++pos]);
                } else
                    throw new IllegalArgumentException("Unknown parameter:" + args[pos]);
            }

            controller.execute();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Error initializing controller: " + ioe.getMessage());
        }
    }

    /** Logger. */
    private Logger logger = Logger.getAnonymousLogger();

    /** Variable holding the name of the local filename. */
    private String localpattern = SBBLOCALCFG_XML_PATTERN;

    /** Variable holding the name of the remote filename. */
    private String remotepattern = REMOTEFALLBACKCFG_XML_PATTERN;

    /** Name of this patch. */
    private String patchname = "patch";

    /** Document builder factory used to deal with XML data. */
    private DocumentBuilderFactory docfactory = null;

    /** Document builder used to read XML files. */
    private DocumentBuilder docbuilder = null;

    /** List of sub-processors. */
    private List delegates = null;

    /** Base directory file. */
    private File sbbBaseDir = null;

    /** Base directory path string. */
    private String sbbBaseDirString = null;

    /** Relative path of the install directory. */
    private File installDir = null;

    /** Relative path string of the install directory. */
    private String installDirString = null;

    /** Relative path of the workspace directory. */
    private File workspaceDir = null;

    /** Relative path of the workspace directory path string. */
    private String workspaceDirString = null;

    /** Resource manager controlling transactional operations on the file system. */
    private FileResourceManager resourceManager = null;

    /** File sequence for transactional operations on the file system. */
    private FileSequence sequence = null;

    /** Hashmap of local files already loaded. */
    private HashMap localConfigFiles = null;

    /** Hashmap of remote data standin files already loaded. */
    private HashMap remotefallbackConfigFiles = null;

    /**
     * Empty constructor.
     */
    public LocalVsRemotefallbackController() {
        super();
    }

    /**
     * Constructor.
     * 
     * @param aSbbBaseDir
     *        directory base
     * @param aInstallDir
     *        relative install dir
     * @param aWorkspaceDir
     *        relative workspace dir
     */
    public LocalVsRemotefallbackController(final String aSbbBaseDir, final String aInstallDir, final String aWorkspaceDir) {
        super();
        try {
            this.sbbBaseDirString = aSbbBaseDir;
            this.installDirString = aInstallDir;
            this.workspaceDirString = aWorkspaceDir;
            this.initialize();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new IllegalArgumentException("Exception: " + e.getMessage());
        }
    }

    /**
     * Adds the delegate.
     * 
     * @param aDelegate
     *        the a delegate
     */
    public void addDelegate(final LocalVsRemotefallbackDelegate aDelegate) {
        if (null == this.delegates) {
            this.delegates = new ArrayList();
        }
        this.delegates.add(aDelegate);
    }

    /**
     * Adds the local configuration.
     * 
     * @param aFilename
     *        the a filename
     * @param aDocument
     *        the a document
     */
    public void addLocalConfiguration(final String aFilename, final Document aDocument) {
        if (null == this.localConfigFiles) {
            this.localConfigFiles = new HashMap();
        }

        this.localConfigFiles.put(aFilename, aDocument);
    }

    /**
     * Adds the remote configuration.
     * 
     * @param aFilename
     *        the a filename
     * @param aDocument
     *        the a document
     */
    public void addRemoteConfiguration(final String aFilename, final Document aDocument) {
        if (null == this.remotefallbackConfigFiles) {
            this.remotefallbackConfigFiles = new HashMap();
        }

        this.remotefallbackConfigFiles.put(aFilename, aDocument);
    }

    /**
     * Execute.
     */
    public void execute() {
        String txid = null;
        // create overarching transaction
        try {

            // initialize object, if this has not been done yet
            if (null == this.sequence) {
                this.initialize();
            }
            // initialize the sequencer
            this.sequence.create(this.patchname, 0);
            txid = Long.toString(this.sequence.nextSequenceValueBottom(this.patchname, 1L));

            this.resourceManager.start();
            this.resourceManager.startTransaction(txid);

            this.localConfigFiles = this.findLocalConfigurationSources(txid, this.installDir);
            this.remotefallbackConfigFiles = this.findRemotefallbackConfigurationSources(txid, this.sbbBaseDir);

            if ((null != this.delegates) && !this.delegates.isEmpty()) {
                this.prepareSubExecutionOnDelegates(txid);
                this.doSubExecutionOnDelegates(txid);
                this.commitSubExecutionOnDelegates(txid);
            }

            this.persistAllReadConfiguration(txid);

            this.resourceManager.commitTransaction(txid);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            try {
                this.resourceManager.rollbackTransaction(txid);
            } catch (ResourceManagerException e1) {
                e1.printStackTrace(System.err);
                System.exit(-2);
            }
        } finally {
            try {
                if (null != this.sequence) {
                    this.sequence.delete(this.patchname);
                }

                if (null != this.resourceManager) {
                    this.resourceManager.stop(0);
                }
            } catch (ResourceManagerSystemException e) {
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }

    /**
     * Fetch a specific configuration from the local filesystem.
     * 
     * @param aTxid
     *        is the transaction identifier
     * @param aFilename
     *        is the filename that will be retrieved
     * 
     * @return will return the DOM containing the documentation
     */
    public Document getLocalConfiguration(final String aTxid, final String aFilename) {
        return this.getConfiguration(aTxid, this.localConfigFiles, aFilename);
    }

    /**
     * Gets the local configuration keys.
     * 
     * @return the local configuration keys
     */
    public Iterator getLocalConfigurationKeys() {
        if (null == this.localConfigFiles) return null;

        return this.localConfigFiles.keySet().iterator();
    }

    /**
     * Gets the localpattern.
     * 
     * @return the localpattern
     */
    public String getLocalpattern() {
        return this.localpattern;
    }

    /**
     * Fetch remote standin file.
     * 
     * @param aTxid
     *        transaction identifier
     * @param aFilename
     *        name of the file
     * 
     * @return the configuration document
     */
    public Document getRemotefallbackConfiguration(final String aTxid, final String aFilename) {
        return this.getConfiguration(aTxid, this.remotefallbackConfigFiles, aFilename);
    }

    /**
     * Gets the remotefallback configuration keys.
     * 
     * @return the remotefallback configuration keys
     */
    public Iterator getRemotefallbackConfigurationKeys() {
        if (null == this.remotefallbackConfigFiles) return null;

        return this.remotefallbackConfigFiles.keySet().iterator();
    }

    /**
     * Gets the remotepattern.
     * 
     * @return the remotepattern
     */
    public String getRemotepattern() {
        return this.remotepattern;
    }

    /**
     * Sets the install dir.
     * 
     * @param aInstallDir
     *        the a install dir
     */
    public void setInstallDir(final String aInstallDir) {
        this.installDir = new File(aInstallDir);
        if (!this.installDir.isDirectory())
            throw new IllegalArgumentException("'" + this.installDir.getAbsolutePath() + "' is not a directory");
    }

    /**
     * Sets the localpattern.
     * 
     * @param localpattern
     *        the new localpattern
     */
    public void setLocalpattern(final String localpattern) {
        this.localpattern = localpattern;
    }

    /**
     * Sets the remotepattern.
     * 
     * @param remotepattern
     *        the new remotepattern
     */
    public void setRemotepattern(final String remotepattern) {
        this.remotepattern = remotepattern;
    }

    /**
     * Sets the sbb base dir.
     * 
     * @param aSbbBaseDir
     *        the a sbb base dir
     */
    public void setSbbBaseDir(final String aSbbBaseDir) {
        this.sbbBaseDir = new File(aSbbBaseDir);
        if (!this.sbbBaseDir.isDirectory())
            throw new IllegalArgumentException("'" + this.sbbBaseDir.getAbsolutePath() + "' is not a directory");
    }

    /**
     * Sets the workspace dir.
     * 
     * @param aWorkspaceDir
     *        the a workspace dir
     */
    public void setWorkspaceDir(final String aWorkspaceDir) {
        this.workspaceDir = new File(aWorkspaceDir);
        if (!this.workspaceDir.isDirectory())
            throw new IllegalArgumentException("'" + this.workspaceDir.getAbsolutePath() + "' is not a directory");
    }

    protected Logger getLogger() {
        return this.logger;
    }

    /**
     * Commit sub execution on delegates.
     * 
     * @param txid
     *        the txid
     */
    private void commitSubExecutionOnDelegates(final String txid) {
        Iterator iter = this.delegates.iterator();
        while (iter.hasNext()) {
            LocalVsRemotefallbackDelegate delegate = (LocalVsRemotefallbackDelegate) iter.next();
            delegate.commit(txid);
        }
    }

    /**
     * Do sub execution on delegates.
     * 
     * @param txid
     *        the txid
     */
    private void doSubExecutionOnDelegates(final String txid) {
        Iterator iter = this.delegates.iterator();
        while (iter.hasNext()) {
            LocalVsRemotefallbackDelegate delegate = (LocalVsRemotefallbackDelegate) iter.next();
            delegate.execute(txid);
        }
    }

    /**
     * Find configuration sources.
     * 
     * @param txid
     *        the txid
     * @param aDirectory
     *        the a directory
     * @param aPattern
     *        the a pattern
     * 
     * @return the hash map
     * 
     * @throws ResourceManagerException
     *         the resource manager exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    private HashMap findConfigurationSources(final String txid, final File aDirectory, final String aPattern)
            throws ResourceManagerException, IOException {
        HashMap result = new HashMap();
        String[] files = aDirectory.list();
        for (int pos = files.length - 1; pos >= 0; pos--) {
            File posTarget = new File(aDirectory.getCanonicalPath() + File.separatorChar + files[pos]);
            if (posTarget.getCanonicalPath().endsWith(aPattern)) {
                this.logger.info("Found file with patter '" + aPattern + "': '" + posTarget.getCanonicalPath() + "'");
                // resourceManager.lockResource(posTarget.getCanonicalPath(),
                // txid);
                result.put(StringUtils.stripStart(posTarget.getCanonicalPath().substring(
                        this.sbbBaseDir.getCanonicalPath().length()), File.separator), null);
            } else if (posTarget.isDirectory()) {
                this.logger.finest("Found directory '" + posTarget.getCanonicalPath() + "'. Descending.");
                HashMap subresult = this.findConfigurationSources(txid, posTarget, aPattern);
                if ((null != subresult) && !subresult.isEmpty()) {
                    this.logger.finest("Adding sub-results to list found below '" + aDirectory.getCanonicalPath() + "'.");
                    result.putAll(subresult);
                }
            } else {
                this.logger.finest("File '" + posTarget.getCanonicalPath() + "' skipped.");
            }
        }
        if (result.isEmpty()) return null;
        return result;
    }

    /**
     * Find local configuration sources.
     * 
     * @param txid
     *        the txid
     * @param aInstallDir
     *        the a install dir
     * 
     * @return the hash map
     * 
     * @throws ResourceManagerException
     *         the resource manager exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    private HashMap findLocalConfigurationSources(final String txid, final File aInstallDir) throws ResourceManagerException,
            IOException {
        return this.findConfigurationSources(txid, aInstallDir, this.localpattern);
    }

    /**
     * Find remotefallback configuration sources.
     * 
     * @param txid
     *        the txid
     * @param aInstallDir
     *        the a install dir
     * 
     * @return the hash map
     * 
     * @throws ResourceManagerException
     *         the resource manager exception
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     */
    private HashMap findRemotefallbackConfigurationSources(final String txid, final File aInstallDir)
            throws ResourceManagerException, IOException {
        return this.findConfigurationSources(txid, aInstallDir, this.remotepattern);
    }

    /**
     * Gets the configuration.
     * 
     * @param aTxid
     *        the a txid
     * @param aMap
     *        the a map
     * @param aFilename
     *        the a filename
     * 
     * @return the configuration
     */
    private Document getConfiguration(final String aTxid, final HashMap aMap, final String aFilename) {
        Document doc = null;
        if (aMap.containsKey(aFilename)) {
            doc = (Document) aMap.get(aFilename);
            if (null != doc) return doc;
        }
        try {
            doc = this.docbuilder.parse(this.resourceManager.readResource(aTxid, aFilename));
        } catch (Exception e) {
            this.logger.throwing(this.getClass().getName(), "getConfiguration", e);
            throw new IllegalArgumentException("Unable to read configuration file '" + aFilename + "':" + e.getMessage());
        }
        aMap.put(aFilename, doc);
        return doc;
    }

    /**
     * Initialize.
     * 
     * @throws IOException
     *         Signals that an I/O exception has occurred.
     * @throws ResourceManagerException
     *         the resource manager exception
     * @throws ParserConfigurationException
     *         the parser configuration exception
     */
    private void initialize() throws IOException, ResourceManagerException, ParserConfigurationException {
        this.setSbbBaseDir(this.sbbBaseDirString);
        this.setInstallDir(this.sbbBaseDir.getCanonicalPath() + File.separatorChar + this.installDirString);
        this.setWorkspaceDir(this.sbbBaseDir.getCanonicalPath() + File.separatorChar + this.workspaceDirString);
        this.docfactory = DocumentBuilderFactory.newInstance();
        this.docbuilder = this.docfactory.newDocumentBuilder();
        this.resourceManager =
                new FileResourceManager(this.sbbBaseDir.getCanonicalPath(), this.workspaceDir.getCanonicalPath()
                        + File.separatorChar + "log", false, new Jdk14Logger(this.logger));
        this.sequence =
                new FileSequence(this.workspaceDir.getCanonicalPath() + File.separatorChar + "seq", new Jdk14Logger(this.logger));
    }

    /**
     * Persist all read configuration.
     * 
     * @param txid
     *        the txid
     * 
     * @throws PatchException
     *         the patch exception
     */
    private void persistAllReadConfiguration(final String txid) throws PatchException {
        this.persistConfiguration(this.localConfigFiles, txid);
        this.persistConfiguration(this.remotefallbackConfigFiles, txid);
    }

    /**
     * Persist configuration.
     * 
     * @param aMap
     *        the a map
     * @param aTxid
     *        the a txid
     * 
     * @throws PatchException
     *         the patch exception
     */
    private void persistConfiguration(final HashMap aMap, final String aTxid) throws PatchException {
        Iterator iter = aMap.keySet().iterator();
        while (iter.hasNext()) {
            String filename = (String) iter.next();

            Document doc = (Document) aMap.get(filename);
            if (null != doc) {
                try {
                    Transformer trans = TransformerFactory.newInstance().newTransformer();
                    Properties props = new Properties();
                    props.setProperty(OutputKeys.INDENT, "yes");
                    props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
                    props.setProperty(OutputKeys.ENCODING, "UTF-8");
                    props.setProperty(OutputKeys.MEDIA_TYPE, "text/xml");
                    props.setProperty(OutputKeys.METHOD, "xml");
                    props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
                    trans.setOutputProperties(props);

                    OutputStream out = this.resourceManager.writeResource(aTxid, filename);
                    this.logger.info("Writing back resource '" + this.sbbBaseDir.getCanonicalPath() + File.separatorChar + filename
                            + "'");
                    trans.transform(new DOMSource(doc), new StreamResult(out));
                } catch (Exception e) {
                    throw new PatchException("Error while persisting resource", e);
                }
            }
        }
    }

    /**
     * Prepare sub execution on delegates.
     * 
     * @param txid
     *        the txid
     */
    private void prepareSubExecutionOnDelegates(final String txid) {
        Iterator iter = this.delegates.iterator();
        while (iter.hasNext()) {
            LocalVsRemotefallbackDelegate delegate = (LocalVsRemotefallbackDelegate) iter.next();
            delegate.prepare(txid);
        }
    }
}
