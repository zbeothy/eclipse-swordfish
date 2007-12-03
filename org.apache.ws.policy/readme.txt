This is an adaptation of the official neethi sources for use in SOPware. 
It is based on the svn head version as of 2006-10-19T08:37:11.325726Z (revision 465525).
at http://svn.apache.org/viewcvs.cgi/webservices/commons/trunk/modules/neethi

Changes to the sources from neethi svn:

- removed experimental org.apache.neethi package
- included proposed fixes from
	http://issues.apache.org/jira/browse/WSCOMMONS-116
	http://issues.apache.org/jira/browse/WSCOMMONS-20
- removed org.apache.ws.policy.attachment.WSDLPolicyProcessor to eliminate dependency on wsdl4j
- removed org.apache.ws.policy.util.OMPolicyReader to eliminate dependency on axiom
