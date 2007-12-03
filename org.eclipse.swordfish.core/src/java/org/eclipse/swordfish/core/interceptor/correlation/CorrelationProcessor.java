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
package org.eclipse.swordfish.core.interceptor.correlation;

import org.eclipse.swordfish.core.components.processing.ProcessingComponent;

/**
 * The CorrelationProcessor extracts business ids specified in the policy assertion from the message
 * and publishes those via operational logging and optionally as a SOAP header.
 * 
 */
public interface CorrelationProcessor extends ProcessingComponent {

    /** The ROLE. */
    String ROLE = CorrelationProcessor.class.getName();

}
