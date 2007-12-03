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
package org.eclipse.swordfish.core.interceptor.transformation;

import javax.jbi.messaging.NormalizedMessage;
import javax.xml.transform.Source;
import org.apache.ws.policy.PrimitiveAssertion;
import org.eclipse.swordfish.core.components.processing.ProcessingComponent;

/**
 * The Interface TransformationProcessor.
 */
public interface TransformationProcessor extends ProcessingComponent {

    /** This attribute describes the role name for Transformation component. */
    String ROLE = TransformationProcessor.class.getName();

    /**
     * Transform.
     * 
     * @param nm
     *        normalized message
     * @param assertion
     *        assertion
     * 
     * @return Source source
     * 
     * @throws Exception
     *         exception
     */
    Source transform(NormalizedMessage nm, PrimitiveAssertion assertion) throws Exception;

}
