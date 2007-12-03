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
 * The Interface XKMSDefinitions.
 * 
 */
public interface XKMSDefinitions {

    /** The ATT r_ ID. */
    String ATTR_ID = "Id";

    /** The RESUL t_ FAILURE. */
    String RESULT_FAILURE = "Failure";

    /** The RESUL t_ INCOMPLETE. */
    String RESULT_INCOMPLETE = "Incomplete";

    /** The RESUL t_ NOMATCH. */
    String RESULT_NOMATCH = "NoMatch";

    /** The RESUL t_ PENDING. */
    String RESULT_PENDING = "Pending";

    /** The RESUL t_ REFUSED. */
    String RESULT_REFUSED = "Refused";

    /** The RESUL t_ SUCCESS. */
    String RESULT_SUCCESS = "Success";

    /** The TA g_ ANSWER. */
    String TAG_ANSWER = "Answer";

    /** The TA g_ AUTHENTICATION. */
    String TAG_AUTHENTICATION = "AuthInfo";

    /** The TA g_ AUTHSERVERINFO. */
    String TAG_AUTHSERVERINFO = "AuthServerInfo";

    /** The TA g_ AUTHUSERINFO. */
    String TAG_AUTHUSERINFO = "AuthUserInfo";

    /** The TA g_ COMMONNAME. */
    String TAG_COMMONNAME = "CommonName";

    /** The TA g_ CORPCOMPANY. */
    String TAG_CORPCOMPANY = "CorpCompany";

    /** The TA g_ COUNTRY. */
    String TAG_COUNTRY = "Country";

    /** The TA g_ EXPONENT. */
    String TAG_EXPONENT = "Exponent";

    /** The TA g_ HMACSH a1. */
    String TAG_HMACSHA1 = "HmacSHA1";

    /** The TA g_ KEYBINDINGAUTH. */
    String TAG_KEYBINDINGAUTH = "KeyBindingAuth";

    /** The TA g_ KEYBINDING. */
    String TAG_KEYBINDING = "KeyBinding";

    /** The TA g_ KEYBINDINGREQUEST. */
    String TAG_KEYBINDINGREQUEST = "Prototype";

    /** The TA g_ KEYID. */
    String TAG_KEYID = "KeyID";

    /** The TA g_ KEYINFO. */
    String TAG_KEYINFO = "KeyInfo";

    /** The TA g_ KEYNAME. */
    String TAG_KEYNAME = "KeyName";

    /** The TA g_ KEYUSAGE. */
    String TAG_KEYUSAGE = "KeyUsage";

    /** The TA g_ KEYVALUE. */
    String TAG_KEYVALUE = "KeyValue";

    /** The TA g_ LOCALITY. */
    String TAG_LOCALITY = "Locality";

    /** The TA g_ LOCATE. */
    String TAG_LOCATE = "Locate";

    /** The TA g_ LOCATERESULT. */
    String TAG_LOCATERESULT = "LocateResult";

    /** The TA g_ MGMTDATA. */
    String TAG_MGMTDATA = "MgmtData";

    /** The TA g_ MODULUS. */
    String TAG_MODULUS = "Modulus";

    /** The TA g_ NOTAFTER. */
    String TAG_NOTAFTER = "NotAfter";

    /** The TA g_ NOTBEFORE. */
    String TAG_NOTBEFORE = "NotBefore";

    /** The TA g_ ORGUNIT. */
    String TAG_ORGUNIT = "OrgUnit";

    /** The TA g_ PASSPHRASEAUTH. */
    String TAG_PASSPHRASEAUTH = "PassPhraseAuth";

    /** The TA g_ PASSPHRASE. */
    String TAG_PASSPHRASE = "PassPhrase";

    /** The TA g_ PGPDATA. */
    String TAG_PGPDATA = "PGPData";

    /** The TA g_ PKC s10. */
    String TAG_PKCS10 = "PKCS10";

    /** The TA g_ PRIVATE. */
    String TAG_PRIVATE = "Private";

    /** The TA g_ PROCESSINFO. */
    String TAG_PROCESSINFO = "ProcessInfo";

    /** The TA g_ PROOFOFPOSSESSION. */
    String TAG_PROOFOFPOSSESSION = "ProofOfPossession";

    /** The TA g_ QUERY. */
    String TAG_QUERY = "Query";

    /** The TA g_ REGISTER. */
    String TAG_REGISTER = "Register";

    /** The TA g_ RESPOND. */
    String TAG_RESPOND = "Respond";

    /** The TA g_ RESULTCODE. */
    String TAG_RESULTCODE = "ResultCode";

    /** The TA g_ RESULT. */
    String TAG_RESULT = "Result";

    /** The TA g_ RETRIEVALMETHOD. */
    String TAG_RETRIEVALMETHOD = "RetrievalMethod";

    /** The TA g_ RSAKEYVALUE. */
    String TAG_RSAKEYVALUE = "RSAKeyValue";

    /** The TA g_ SPKIDATA. */
    String TAG_SPKIDATA = "SPKIData";

    /** The TA g_ STATE. */
    String TAG_STATE = "InternalState";

    /** The TA g_ STATUS. */
    String TAG_STATUS = "Status";

    /** The TA g_ STRING. */
    String TAG_STRING = "string";

    /** The TA g_ TRANSACTIONID. */
    String TAG_TRANSACTIONID = "TransactionID";

    /** The TA g_ VALIDATERESULT. */
    String TAG_VALIDATERESULT = "ValidateResult";

    /** The TA g_ VALIDATE. */
    String TAG_VALIDATE = "Validate";

    /** The TA g_ VALIDITYINTERVAL. */
    String TAG_VALIDITYINTERVAL = "ValidityInterval";

    /** The TA g_ x509 DATA. */
    String TAG_X509DATA = "X509Data";

    /** The XMLN s_ EMPTY. */
    String XMLNS_EMPTY = "";

    /** The XMLN s_ XKMS. */
    String XMLNS_XKMS = "http://www.w3.org/2002/03/xkms#";

    /** The XMLN s_ XMLENC. */
    String XMLNS_XMLENC = "http://www.w3.org/2001/04/xmlenc#";

    /** The XMLN s_ XMLENCPREFIX. */
    String XMLNS_XMLENCPREFIX = "xenc";

    /** The XMLN s_ XMLDS. */
    String XMLNS_XMLDS = "http://www.w3.org/2000/09/xmldsig#";

    /** The XMLN s_ XMLD s_ NEW. */
    String XMLNS_XMLDS_NEW = "xdsing.xsd";

    /** The XMLN s_ XMLDSPREFIX. */
    String XMLNS_XMLDSPREFIX = "ds";

    /** The XKM s_ PREFIX. */
    String XKMS_PREFIX = "k";

    /** The XKM s_ SERVICE. */
    String XKMS_SERVICE = "http://www.servicebackbone.org/XKMS";

    /** The XKM s_ APPLICATIO n_ NAME. */
    String XKMS_APPLICATION_NAME = "urn:sbb:msl";

    /** The XKM s_ KEYUSAG e_ SIG. */
    String XKMS_KEYUSAGE_SIG = "Signature";

    /** The XKM s_ KEYUSAG e_ ENC. */
    String XKMS_KEYUSAGE_ENC = "Encryption";

    /** The XKM s_ KEYUSAG e_ EXH. */
    String XKMS_KEYUSAGE_EXH = "Exchange";

    /** The XKM s_ RESPONDWIT h_ x509 CERT. */
    String XKMS_RESPONDWITH_X509CERT = "X509Cert";

    /** The XKM s_ RESPONDWIT h_ KEYNAME. */
    String XKMS_RESPONDWITH_KEYNAME = "KeyName";

    /** The XKM s_ RESPONDWIT h_ KEYVALUE. */
    String XKMS_RESPONDWITH_KEYVALUE = "KeyValue";

    /** The XKM s_ RESPONDWIT h_ x509 CHAIN. */
    String XKMS_RESPONDWITH_X509CHAIN = "X509Chain";

    /** The XKM s_ RESPONDWIT h_ PGPWEB. */
    String XKMS_RESPONDWITH_PGPWEB = "PGPWeb";

    /** The XKM s_ RESPONDWIT h_ PGP. */
    String XKMS_RESPONDWITH_PGP = "PGP";

    /** The XKM s_ VALIDREASO n_ SIG. */
    String XKMS_VALIDREASON_SIG = "Signature";

    /** The XKM s_ VALIDREASO n_ IT. */
    String XKMS_VALIDREASON_IT = "IssuerTrust";

    /** The XKM s_ VALIDREASO n_ RS. */
    String XKMS_VALIDREASON_RS = "RevocationStatus";

    /** The XKM s_ VALIDREASO n_ VI. */
    String XKMS_VALIDREASON_VI = "ValidityInterval";

    /** The XKM s_ STATU s_ VALID. */
    String XKMS_STATUS_VALID = "Valid";

    /** The XKM s_ STATU s_ INVALID. */
    String XKMS_STATUS_INVALID = "Invalid";

    /** The XKM s_ STATU s_ INDETERMINATE. */
    String XKMS_STATUS_INDETERMINATE = "Indeterminate";
}
