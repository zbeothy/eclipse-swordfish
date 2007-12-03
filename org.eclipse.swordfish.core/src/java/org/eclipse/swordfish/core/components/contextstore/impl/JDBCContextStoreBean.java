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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.eclipse.swordfish.core.papi.impl.untyped.messaging.CallContextExtension;
import org.eclipse.swordfish.papi.internal.exception.InternalConfigurationException;
import org.eclipse.swordfish.papi.internal.exception.InternalIllegalInputException;
import org.eclipse.swordfish.papi.internal.exception.InternalInfrastructureException;
import org.eclipse.swordfish.papi.internal.exception.InvalidContextException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.util.FileCopyUtils;

/**
 * this class implements a jdbc based context storage. TODO review this code regarding efficiency
 */
public class JDBCContextStoreBean extends AbstractContextStore {

    /** The data source. */
    private DataSource dataSource;

    /** The key field. */
    private String keyField;

    /** The data field. */
    private String dataField;

    /** The table. */
    private String table;

    /** The transient cache. */
    private Map transientCache;

    /**
     * Instantiates a new JDBC context store bean.
     */
    public JDBCContextStoreBean() {
        this.transientCache = new HashMap();
    }

    /**
     * Destroy.
     */
    public void destroy() {
        // Auto-generated method stub

    }

    /**
     * Gets the data field.
     * 
     * @return Returns the dataField.
     */
    public String getDataField() {
        return this.dataField;
    }

    /**
     * Gets the data source.
     * 
     * @return Returns the dataSource.
     */
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Gets the key field.
     * 
     * @return Returns the keyField.
     */
    public String getKeyField() {
        return this.keyField;
    }

    /**
     * Gets the table.
     * 
     * @return Returns the table.
     */
    public String getTable() {
        return this.table;
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
        if (this.transientCache.containsKey(key)) {
            this.transientCache.remove(key);
        } else {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
            String statement = "DELETE FROM " + this.getTable() + " where " + this.getKeyField() + "='" + key + "'";
            jdbcTemplate.execute(statement);
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
        CallContextExtension ctx = null;
        if (this.transientCache.containsKey(key)) {
            ctx = (CallContextExtension) this.transientCache.get(key);
            this.removeCallContext(key);
            return ctx;
        } else {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
            final LobHandler lobHandler = new DefaultLobHandler();
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String statement =
                    "SELECT " + this.getDataField() + " FROM " + this.getTable() + " WHERE " + this.getKeyField() + "='" + key
                            + "'";
            jdbcTemplate.query(statement, new AbstractLobStreamingResultSetExtractor() {

                @Override
                public void streamData(final ResultSet rs) throws SQLException, IOException {
                    FileCopyUtils.copy(lobHandler.getBlobAsBinaryStream(rs, 1), bos);
                }
            });
            if (bos.size() == 0) throw new InternalConfigurationException("could not find any context for key " + key);
            try {
                byte[] ba = bos.toByteArray();
                ctx = this.deserializeContext(ba);
                this.removeCallContext(key);
            } catch (InvalidContextException e) {
                throw new InternalInfrastructureException("could restore the context for key " + key, e);
            } catch (IOException e) {
                throw new InternalInfrastructureException("could restore the context for key " + key, e);
            }
            return ctx;
        }
    }

    /**
     * Sets the data field.
     * 
     * @param dataField
     *        The dataField to set.
     */
    public void setDataField(final String dataField) {
        this.dataField = dataField;
    }

    /**
     * Sets the data source.
     * 
     * @param dataSource
     *        The dataSource to set.
     */
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sets the key field.
     * 
     * @param keyField
     *        The keyField to set.
     */
    public void setKeyField(final String keyField) {
        this.keyField = keyField;
    }

    /**
     * Sets the table.
     * 
     * @param table
     *        The table to set.
     */
    public void setTable(final String table) {
        this.table = table;
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
        final String key = this.buildKey(ctx);

        try {
            final byte[] data = this.serializeContext(ctx);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(this.getDataSource());
            // TODO maybe the LobHandler needs to get injected to be able to
            // deal with different JDBC's
            LobHandler lobHandler = new DefaultLobHandler();
            String statement = "INSERT INTO " + this.getTable() + " (" + this.keyField + "," + this.dataField + ") VALUES (?,?)";
            jdbcTemplate.execute(statement, new AbstractLobCreatingPreparedStatementCallback(lobHandler) {

                @Override
                protected void setValues(final PreparedStatement ps, final LobCreator lobCreator) throws SQLException {
                    ps.setString(1, key);
                    lobCreator.setBlobAsBytes(ps, 2, data);
                }
            });
        } catch (IOException e) {
            throw new InternalInfrastructureException("failed to serialize the InternalCallContext", e);
        }

        return key;
    }

}
