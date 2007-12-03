<?xml version="1.0" encoding="UTF-8"?>
<!--============================================================================
    Copyright (c) 2007 Deutsche Post AG.
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    which accompanies this distribution, and is available at
    http://www.eclipse.org/legal/epl-v10.html
    
    Contributors:
       Deutsche Post AG - initial API and implementation
 ============================================================================-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<!-- each cluster definition 
	 creates ArtixCluster node with attributes from cluster node definition
	 creates the InboundEndpointPeerManager from the LocalPeerManager definition
	 
	 @param unique is used to make endpoints defined here to be unique for the locator proxy
	 	    this parameter is known and set by the transformer
  -->

	<xsl:param name="unique"/>

	<xsl:template match="Locator">
		<ArtixCluster>
			<xsl:attribute name="name">
				<xsl:value-of select="./@name"/>
			</xsl:attribute>
			<xsl:attribute name="default">
				<xsl:value-of select="./@default"/>
			</xsl:attribute>

			<BcExtensionMBeanName>
			   jbi:ComponentName=oracle.tip.jbi.component.SuperBC,ControlType=Extension,name=superbc
			</BcExtensionMBeanName>
			<BcSoapMessageClass>
			   com.iona.jbi.se.artixlocator.peermanager.SoapMessagesDpBC
			</BcSoapMessageClass>

			<InboundEndpointPeerManager>
				<xsl:attribute name="name">
					<xsl:value-of select="/Locator/LocalPeerManager/@name"/>
				</xsl:attribute>
				<xsl:attribute name="host">
					<xsl:value-of select="/Locator/LocalPeerManager/@host"/>
				</xsl:attribute>
				<xsl:attribute name="port">
					<xsl:value-of select="/Locator/LocalPeerManager/@port"/>
				</xsl:attribute>
				<xsl:attribute name="urlPath">
					<xsl:value-of select="/Locator/LocalPeerManager/@urlPath"/>
				</xsl:attribute>

				<LocatorEndpointForBC xmlns:_pref1="http://jbi.iona.com/locator">
					<xsl:attribute name="EndpointName"> InboundEndpointPeerManager </xsl:attribute>
						_pref1:LocatorService-<xsl:value-of select="$unique"/>
				</LocatorEndpointForBC>
				<DocumentFragmentAny>
					<wsa:EndpointReference xmlns:wsa="http://www.w3.org/2005/08/addressing"
						xmlns:wsaw="http://www.w3.org/2005/03/addressing/wsdl">
						<wsa:Address> http://<xsl:value-of select="/Locator/LocalPeerManager/@host"
								/>:<xsl:value-of select="/Locator/LocalPeerManager/@port"
								/><xsl:value-of select="/Locator/LocalPeerManager/@urlPath"/>
						</wsa:Address>
						<wsa:Metadata>
							<wsaw:ServiceName xmlns:_iona="http://jbi.iona.com/locator">
								<xsl:attribute name="EndpointName">InboundEndpointPeerManager</xsl:attribute> 
								 _iona:LocatorService-<xsl:value-of select="$unique"/>
							</wsaw:ServiceName>
						</wsa:Metadata>
					</wsa:EndpointReference>
				</DocumentFragmentAny>
				<DeactivateEndpointFragment> LocatorService-<xsl:value-of select="$unique"/>
				</DeactivateEndpointFragment>
			</InboundEndpointPeerManager>

			<OutboundEndpointClusterList>

				<xsl:apply-templates select="./ClusterNode"/>

			</OutboundEndpointClusterList>
		</ArtixCluster>
	</xsl:template>

	<!-- each single node 
  	   creates ClusterNode with the node name
  	   creates OutboundEndpointPeerManager name with the PeerManagerEndpoint name
   	   creates OutboundEndpointLocator name with the PeerManagerLocator name
    -->
	<xsl:template match="ClusterNode">
		<ClusterNode>
			<xsl:attribute name="name">
				<xsl:value-of select="./@name"/>
			</xsl:attribute>

			<OutboundEndpointPeerManager>
				<xsl:attribute name="name">
					<xsl:value-of select="./PeerManagerEndpoint/@name"/>
				</xsl:attribute>
				<DocumentFragmentAny>
					<wsa:EndpointReference xmlns:wsa="http://www.w3.org/2005/08/addressing"
						xmlns:wsaw="http://www.w3.org/2005/03/addressing/wsdl">
						<wsa:Address>
							<xsl:value-of select="./PeerManagerEndpoint"/>
						</wsa:Address>
						<wsa:Metadata/>
					</wsa:EndpointReference>
				</DocumentFragmentAny>
			</OutboundEndpointPeerManager>
			<OutboundEndpointLocator>
				<xsl:attribute name="name">
					<xsl:value-of select="./LocatorEndpoint/@name"/>
				</xsl:attribute>
				<DocumentFragmentAny>
					<wsa:EndpointReference xmlns:wsa="http://www.w3.org/2005/08/addressing"
						xmlns:wsaw="http://www.w3.org/2005/03/addressing/wsdl">
						<wsa:Address>
							<xsl:value-of select="./LocatorEndpoint"/>
						</wsa:Address>
						<wsa:Metadata/>
					</wsa:EndpointReference>
				</DocumentFragmentAny>
			</OutboundEndpointLocator>

		</ClusterNode>
	</xsl:template>
</xsl:stylesheet>
