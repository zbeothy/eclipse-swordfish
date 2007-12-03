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
package org.eclipse.swordfish.core.management.operations.impl;

import java.util.logging.LogRecord;
import org.eclipse.swordfish.core.components.iapi.UnifiedParticipantIdentity;
import org.eclipse.swordfish.core.management.operations.OperationalMessage;

/**
 * Wrapper to integrate SOPWare OperationalMessages in java.util.logging.
 * 
 */
public class OperationalMessageRecord extends LogRecord {

    /** <code>serialVersionUID</code> for Serializable. */
    private static final long serialVersionUID = -769379077036595920L;

    /** Original operational message. */
    private OperationalMessage msg;

    /** Fully qualified message ID (Msg-Classname + "." + value from getMsgID) */
    private String fqMsgId;

    /** The parameters for the instance of the message. */
    private Object[] params;

    /** Id of the application that generated the message. */
    private UnifiedParticipantIdentity participant;

    /**
     * The Constructor.
     * 
     * @param participant
     *        the participant
     * @param msg
     *        <code>InternalOperationalMessage</code> that is to be reported
     * @param params
     *        for the instance of the message
     */
    public OperationalMessageRecord(final UnifiedParticipantIdentity participant, final OperationalMessage msg,
            final Object[] params) {
        super(Level.getLevel(msg.getSeverity()), msg.getRawMessage());
        this.msg = msg;
        this.params = params;
        this.fqMsgId = msg.getQualifiedName();
        this.participant = participant;
        this.setMillis(System.currentTimeMillis());
    }

    /**
     * Gets the fq msg ID.
     * 
     * @return the fully qualified ID for the message
     */
    public String getFqMsgID() {
        return this.fqMsgId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.LogRecord#getLoggerName()
     */
    @Override
    public String getLoggerName() {
        return this.fqMsgId;
    }

    /**
     * Gets the message.
     * 
     * @return message text with acutal parameters included
     */
    @Override
    public String getMessage() {
        StringBuffer buf = new StringBuffer(this.msg.getRawMessage());
        for (int i = 0; i < this.msg.getParamCount(); i++) {
            String param = this.getParamValue(i);
            String template = "'{" + i + "}'";
            int start = buf.indexOf(template);
            if (start >= 0) {
                int end = start + template.length();
                buf = buf.replace(start, end, param);
            }
        }
        if (this.msg.getParamCount() != this.params.length) {
            buf.append("\nWarning: unexpected number of parameters. Expected: ").append(this.msg.getParamCount()).append(" Was: ")
                .append(this.params.length);
            if (this.msg.getParamCount() < this.params.length) {
                buf.append("\nUnexpected parameters:\n");
                for (int k = this.msg.getParamCount(); k < this.params.length; k++) {
                    String val;
                    if (null != this.params[k]) {
                        val = this.params[k].toString();
                    } else {
                        val = "null";
                    }
                    buf.append(val).append("\n");
                }
            }
        }
        return buf.toString();
    }

    /**
     * Gets the operational message.
     * 
     * @return the <code>InternalOperationalMessage</code>
     */
    public OperationalMessage getOperationalMessage() {
        return this.msg;
    }

    /**
     * Gets the parameters.
     * 
     * @return parameters for the instance of the message
     */
    @Override
    public Object[] getParameters() {
        return this.params;
    }

    /**
     * Gets the participant.
     * 
     * @return the participant
     */
    public UnifiedParticipantIdentity getParticipant() {
        return this.participant;
    }

    /**
     * (non-Javadoc).
     * 
     * @return the source class name
     * 
     * @see java.util.logging.LogRecord#getSourceClassName()
     */
    @Override
    public String getSourceClassName() {
        StringBuffer ret = new StringBuffer("OpMsg: ").append(this.msg.getQualifiedName());
        return ret.toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.util.logging.LogRecord#getSourceMethodName()
     */
    @Override
    public String getSourceMethodName() {
        return null;
    }

    /**
     * Gets the param value.
     * 
     * @param index
     *        of a parameter
     * 
     * @return string representation for the parameter at index
     */
    private String getParamValue(final int index) {
        String ret = null;
        try {
            Object param = this.params[index];
            if (null != param) {
                ret = param.toString();
            } else {
                ret = "{null}";
            }
        } catch (IndexOutOfBoundsException e) {
            ret = "{unknown}";
        }
        return ret;
    }

}
