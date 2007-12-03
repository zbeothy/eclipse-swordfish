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

/**
 * The Interface LocalVsRemotefallbackDelegate.
 * 
 */
public interface LocalVsRemotefallbackDelegate {

    /**
     * Commit.
     * 
     * @param aTxid
     *        the a txid
     */
    void commit(String aTxid);

    /**
     * Execute.
     * 
     * @param aTxid
     *        the a txid
     */
    void execute(String aTxid);

    /**
     * Prepare.
     * 
     * @param aTxid
     *        the a txid
     */
    void prepare(String aTxid);
}
