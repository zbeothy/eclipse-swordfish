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
        <xsl:apply-templates mode="config"/>
    </xsl:template>
    <xsl:template match="/config" mode="config">
        <xsl:copy>
            <xsl:comment>configuration automatically merged for the configrepos-proxy module. Do not
                edit!</xsl:comment>
            <xsl:call-template name="merge_nodelists">
                <xsl:with-param name="leaders" select="./child::*"/>
                <xsl:with-param name="followers" select="document($follower_file)/config/child::*"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    <!-- apply to all nodes for both processed documents in parallel -->
    <xsl:template name="merge_nodelists" mode="config">
        <xsl:param name="leaders"/>
        <xsl:param name="followers"/>
        <xsl:variable name="fixed_leaders"
            select="$leaders[./@sbb_configuration_attribute_fixed='true']"/>
        <xsl:variable name="disjoint_leaders" select="$leaders[not(./@name = $followers/@name)]"/>
        <xsl:variable name="disjoint_followers" select="$followers[not(./@name = $leaders/@name)]"/>
        <xsl:variable name="joint_leaders" select="$leaders[not(./@name = $fixed_leaders/@name) and
            not(./@name = $disjoint_leaders/@name)]"/>
        <xsl:variable name="joint_followers" select="$followers[not(./@name = $fixed_leaders/@name)
            and not(./@name = $disjoint_followers/@name)]"/>
        <xsl:apply-templates select="$fixed_leaders" mode="config"/>
        <xsl:apply-templates select="$disjoint_leaders" mode="config"/>
        <xsl:apply-templates select="$disjoint_followers" mode="config"/>
        <xsl:if test="not(count($joint_leaders | $joint_followers) = 0)">
            <xsl:for-each select="$joint_leaders">
                <xsl:copy>
                    <xsl:call-template name="merge_nodelists">
                        <xsl:with-param name="leaders"
                            select="$joint_leaders[./@name=current()/@name]/child::*"/>
                        <xsl:with-param name="followers"
                            select="$joint_followers[./@name=current()/@name]/child::*"/>
                    </xsl:call-template>
                </xsl:copy>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    <xsl:template match="*" mode="config">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>
