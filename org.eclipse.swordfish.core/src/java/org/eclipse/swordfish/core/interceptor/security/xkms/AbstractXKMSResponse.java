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
package org.eclipse.swordfish.core.interceptor.security.xkms;

/**
 * The Class AbstractXKMSResponse.
 * 
 */
public class AbstractXKMSResponse {

    /** constant tags. */
    public static final String XKMS_LOCATE_RESPONSE = "LocateResult";

    /** constant tags. */
    public static final String XKMS_VALIDATE_RESPONSE = "ValidateResult";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMAJOR_SUCCESS = "Success";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMAJOR_VERSIONMISMATCH = "VersionMismatch";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMAJOR_RECEIVER = "Receiver";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMAJOR_SENDER = "Sender";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_NOMATCH = "NoMatch";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_TOOMANYRESPONSES = "TooManyResponses";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_INCOMPLETE = "Incomplete";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_FAILURE = "Failure";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_REFUSED = "Refused";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_NOAUTH = "NoAuthentication";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_MESSAGENOTSUPPORTED = "MessageNotSupported";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_UNKNOWNRESID = "UnknownResponseId";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_REPRESENTREQ = "RepresentRequired";

    /** constant tags. */
    public static final String XKMS_RESPONSE_RESULTMINOR_NOTSYNC = "NotSynchronous";

    /** constant tags. */
    public static final String XKMS_RESPONSE_KEYBINDING_VERIFIED = "KeyBinding";

    /** constant tags. */
    public static final String XKMS_RESPONSE_KEYBINDING_UNVERIFIED = "UnverifiedKeyBinding";

}
