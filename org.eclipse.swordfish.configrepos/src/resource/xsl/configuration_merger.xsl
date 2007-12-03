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
    <xsl:output encoding="UTF-8" version="1.0" method="xml" media-type="xml"
        doctype-public="-//SOP_Group//Configuration//EN"
        doctype-system="http://www.servicebackbone.org/configrepos/2005/XMLSchema/Configuration.xsd"
        omit-xml-declaration="no" indent="yes"/>
    <xsl:variable name="follower_file">../../../test/resource/xml/local_cfg.xml</xsl:variable>
    <xsl:template match="/">
        <!-- only process documents which start with config root -->
        <xsl:apply-templates mode="configuration"/>
    </xsl:template>
    <xsl:template match="/configuration" mode="config">
        <!-- process root config element and switch to related mode -->
        <xsl:copy>
            <xsl:comment>configuration automatically merged for the configrepos-proxy module. Do not
                edit!</xsl:comment>
            <xsl:call-template name="merge_nodelists">
                <xsl:with-param name="leaders" select="./child::*"/>
                <xsl:with-param name="followers" select="document($follower_file)/configuration/child::*"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    <xsl:template name="merge_nodelists" mode="config">
        <!-- apply to all nodes for both processed documents in parallel -->
        <xsl:param name="leaders"/>
        <xsl:param name="followers"/>
        <xsl:variable name="preset_nodes"
            select="$leaders[./@sbb_configuration_attribute_fixed='true'] |
            $leaders[$followers[local-name(.) = local-name(current()) and not(./@name = current()/@name)]] |
            $followers[$leaders[local-name(.) = local-name(current()) and not(./@name = current()/@name)]]"/>
        <xsl:variable name="joint_nodes" select="$leaders[$preset_nodes[local-name(.) = local-name(current()) and not(./@name = current()/@name)]]"/>
        <xsl:apply-templates select="$preset_nodes" mode="config"/>
        <xsl:for-each select="$joint_nodes">
            <xsl:choose>
                <xsl:when test="(count(./child::*) = 0) and (count($followers[./@name =
                    current()/@name]/child::*) = 0)">
                    <xsl:copy>
                        <xsl:apply-templates select="./@*" mode="config"/>
                        <xsl:apply-templates select="$followers[./@name = current()/@name]/@*"
                            mode="config"/>
                        <xsl:apply-templates select="$followers[./@name = current()/@name]/text()"
                            mode="config"/>
                    </xsl:copy>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy>
                        <xsl:apply-templates select="./@*" mode="config"/>
                        <xsl:apply-templates select="$followers[./@name = current()/@name]/@*"
                            mode="config"/>
                        <xsl:call-template name="merge_nodelists">
                            <xsl:with-param name="leaders" select="./child::*"/>
                            <xsl:with-param name="followers"
                                select="$followers[./@name=current()/@name]/child::*"/>
                        </xsl:call-template>
                    </xsl:copy>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="*|@*" mode="config">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="config"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
