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
package org.eclipse.swordfish.core.management.operations;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Convenience class as abstract implementation of interface InternalOperationalMessage. This class
 * gets all information but the message id for an operational message from a ressource bundle. By
 * using this class it's ensured that all static parts of a message can be localized and that the
 * information for a message is kept outside the code.
 * 
 * While instantiaiting this class a message id and an optional ressource bundle has to be given to
 * the constructor. If no resource bundle is specified, the bundle will be lookedup under the
 * classname with all dots '.' replaced by underscores '_' .
 * 
 * For each instance with a given message id all attributes are looked up in the bundle. Keys to
 * access the bundle are :
 * <ul>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.severity</code> - severity of the message</li>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.category</code> - category of the message</li>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.message</code> - raw text of the message</li>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.params</code> - expected number of params for the
 * message</li>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.description</code> - description for the cause of
 * the message</li>
 * <li><code>&lt;classname&gt;.&lt;msgID&gt;.instruction</code> - instruction for the operator,
 * how to hanlde this message</li>
 * </ul>
 * 
 * If any of this information is missing or invalid , the constructor will fail with a
 * <code>MissingResourceException</code> .
 * 
 * <h3>USAGE</h3>
 * 
 * For usage of this class you define subclass holding your specific messages in static final
 * fields. As Example
 * 
 * <pre>
 * 
 * package org.example.app;
 * 
 * import org.eclipse.swordfish.papi.adapter.extensions.operations.AbstractOperationalMessage
 * 
 * public class StandardOpMsg extends InternalAbstractOperationalMessage {
 * 
 * public static final StandardOpMsg STARTING_MSG   = new StandardOpMsg(1);
 * public static final StandardOpMsg STARTED_MSG    = new StandardOpMsg(2);
 * public static final StandardOpMsg STOPPING_MSG   = new StandardOpMsg(3);
 * public static final StandardOpMsg STOPPED_MSG    = new StandardOpMsg(4);
 * public static final StandardOpMsg PARAM_MISS_MSG = new StandardOpMsg(5);
 * 
 * // should not be instantiated outside this class
 * private StandardOpMsg(int aMsgID) {
 * super(aMsgID);
 * }
 * }
 * </pre>
 * 
 * Additional you will create a properties file with all attributes of the defined messages. In this
 * example the file must be named <code>org_example_app_StandardOpMessage.properties</code> and
 * could look like this
 * 
 * <pre>
 * # definition of all attributes for standard operational messages
 * 
 * org.example.app.StandardOpMsg.1.severity=INFO
 * org.example.app.StandardOpMsg.1.category=LIFECYCLE
 * org.example.app.StandardOpMsg.1.message=Starting component '{0}'
 * org.example.app.StandardOpMsg.1.params=1
 * org.example.app.StandardOpMsg.1.description=indicates that the application is about to start \
 * the named module.
 * org.example.app.StandardOpMsg.1.instruction=Information only, no action necessary.
 * 
 * org.example.app.StandardOpMsg.2.severity=INFO
 * org.example.app.StandardOpMsg.2.category=LIFECYCLE
 * org.example.app.StandardOpMsg.2.message=Component '{0}' started.
 * org.example.app.StandardOpMsg.2.params=1
 * org.example.app.StandardOpMsg.2.description=indicates that the application has started \
 * the named module.
 * org.example.app.StandardOpMsg.2.instruction=Information only, no action necessary.
 * 
 * org.example.app.StandardOpMsg.3.severity=INFO
 * org.example.app.StandardOpMsg.3.category=LIFECYCLE
 * org.example.app.StandardOpMsg.3.message=Stopping component '{0}'
 * org.example.app.StandardOpMsg.3.params=1
 * org.example.app.StandardOpMsg.3.description=indicates that the application is about to stop \
 * the named module.
 * org.example.app.StandardOpMsg.3.instruction=Information only, no action necessary.
 * 
 * org.example.app.StandardOpMsg.4.severity=INFO
 * org.example.app.StandardOpMsg.4.category=LIFECYCLE
 * org.example.app.StandardOpMsg.4.message=Component '{0}' stopped.
 * org.example.app.StandardOpMsg.4.params=1
 * org.example.app.StandardOpMsg.4.description=indicates that the application has stopped \
 * the named module.
 * org.example.app.StandardOpMsg.4.instruction=Information only, no action necessary.
 * 
 * org.example.app.StandardOpMsg.5.severity=WARN
 * org.example.app.StandardOpMsg.5.category=CONFIGURATION
 * org.example.app.StandardOpMsg.5.message=Component '{0}' is missing configuration parameter \
 * '{1}'. Using default value '{2}'
 * org.example.app.StandardOpMsg.5.params=3
 * org.example.app.StandardOpMsg.5.description=The named component tried to access the given \
 * configuration item but this had no assigned value. Component uses default value as \
 * replacement.
 * org.example.app.StandardOpMsg.5.instruction=Check whether the configuration parameter is \
 * omitted by intention. ensure that the default value is appropriate for the usage \
 * of this application.
 * 
 * </pre>
 * 
 * Within your application you will use these messages like
 * 
 * <pre>
 * public void startUp(Component compToStart) {
 *     myOperations.notify(StandardOpMsg.STARTING_MSG, compToStart.getName());
 *     compToStart.start();
 *     myOperations.notify(StandardOpMsg.STARTED_MSG, compToStart.getName());
 * }
 * </pre>
 * 
 */
public abstract class AbstractOperationalMessage implements OperationalMessage {

    /** key to get from bundle. */
    private static final String SEVERITY_KEY = "severity";

    /** key to get from bundle. */
    private static final String MESSAGE_KEY = "message";

    /** key to get from bundle. */
    private static final String INSTRUCTION_KEY = "instruction";

    /** key to get from bundle. */
    private static final String DESCRIPTION_KEY = "description";

    /** key to get from bundle. */
    private static final String PARAM_KEY = "params";

    /** key to get from bundle. */
    private static final String CATEGORY_KEY = "category";

    /** the message id. */
    private int msgID;

    /** the severity. */
    private Severity severity;

    /** the raw message text. */
    private String message;

    /** the description. */
    private String description;

    /** the instruction. */
    private String instruction;

    /** the category. */
    private String category;

    /** the number of expected parameters. */
    private int paramCount;

    /**
     * Constructor for creation with default bundle. The bundle will be looked up from the
     * classloader of the real (sub)class. protected to allow only creation from subclasses
     * 
     * @param aMsgID
     *        id of the message
     */
    protected AbstractOperationalMessage(final int aMsgID) {
        this(aMsgID, (String) null);
    }

    /**
     * Constructor for creation with a given bundle. protected to allow only creation from
     * subclasses
     * 
     * @param aMsgID
     *        id of the message
     * @param aBundle
     *        bundle to use
     */
    protected AbstractOperationalMessage(final int aMsgID, final ResourceBundle aBundle) {
        super();
        this.msgID = aMsgID;
        if (aBundle == null) throw new NullPointerException("bundle may not be null");
        this.init(aBundle);
    }

    /**
     * Constructor for creation with a named bundle. The bundle will be looked up from the
     * classloader of the real (sub)class. protected to allow only creation from subclasses.
     * 
     * @param aMsgID
     *        id of the message
     * @param aBundleName
     *        name of bundle to use
     */
    protected AbstractOperationalMessage(final int aMsgID, final String aBundleName) {
        super();
        this.msgID = aMsgID;
        String theBundleName = aBundleName;
        if (theBundleName == null) {
            theBundleName = this.getClass().getName();
        }
        ResourceBundle theBundle = ResourceBundle.getBundle(theBundleName, Locale.getDefault(), this.getClass().getClassLoader());
        this.init(theBundle);
    }

    /**
     * Gets the category.
     * 
     * @return the category
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }

    /**
     * Gets the instruction.
     * 
     * @return the instruction
     */
    public final String getInstruction() {
        return this.instruction;
    }

    /**
     * Gets the message id.
     * 
     * @return the message id
     */
    public final int getMsgID() {
        return this.msgID;
    }

    /**
     * Gets the number of expected parameters.
     * 
     * @return the number of expected parameters
     */
    public final int getParamCount() {
        return this.paramCount;
    }

    /**
     * default implementation intended to be overwritten by implementing sub-classes.
     * 
     * @return the qualified name
     * 
     * @see org.eclipse.swordfish.core.management.operations.OperationalMessage#getQualifiedName()
     */
    public String getQualifiedName() {
        return this.getClass().getName() + "." + this.msgID;
    }

    /**
     * Gets the raw message text.
     * 
     * @return the raw message text
     */
    public final String getRawMessage() {
        return this.message;
    }

    /**
     * Gets the severity.
     * 
     * @return the severity
     */
    public final Severity getSeverity() {
        return this.severity;
    }

    /**
     * Initializer to be overwritten by subclasses. This will be called after all parameters are
     * read from the ressource bundle. Subclasses may validate the parameters or read further
     * information.
     * 
     * This is a noop implementation.
     * 
     * @param anBundle
     *        bundle to be used
     * @param aBaseKey
     *        prefix for all keys to access bundle
     */
    protected void initialize(final ResourceBundle anBundle, final String aBaseKey) {
        // Noop , just to allow subclasses to do something else
    }

    /**
     * intialization method to read all parameters from a ressource bundle.
     * 
     * @param aBundle
     *        bundle to use
     */
    private void init(final ResourceBundle aBundle) {
        String baseKey = this.getClass().getName() + "." + this.msgID + ".";
        String levelName = aBundle.getString(baseKey + SEVERITY_KEY);
        this.severity = Severity.getByName(levelName);
        if (this.severity == null) throw new IllegalArgumentException("Unknown severity : " + levelName);
        this.message = aBundle.getString(baseKey + MESSAGE_KEY);
        this.category = aBundle.getString(baseKey + CATEGORY_KEY);
        this.description = aBundle.getString(baseKey + DESCRIPTION_KEY);
        this.instruction = aBundle.getString(baseKey + INSTRUCTION_KEY);
        this.paramCount = Integer.parseInt(aBundle.getString(baseKey + PARAM_KEY));
        this.initialize(aBundle, baseKey);
    }
}
