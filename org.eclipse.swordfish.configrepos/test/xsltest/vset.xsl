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
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:vset="http:/www.ora.com/XSLTCookbook/namespaces/vset">
 
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
   
<!-- The default implementation of element equality. Override in the importing 
stylesheet as neccessary. -->
<xsl:template match="node(  ) | @*" mode="vset:element-equality">
  <xsl:param name="other"/>
  <xsl:if test=". = $other">  
    <xsl:value-of select="true(  )"/>
  </xsl:if>
</xsl:template>
   
<!-- The default set membership test uses element equality. You will rarely need to 
override this in the importing stylesheet. -->
<xsl:template match="node(  ) | @*" mode="vset:member-of">
  <xsl:param name="elem"/>
  <xsl:variable name="member-of">
    <xsl:for-each select=".">
      <xsl:apply-templates select="." mode="vset:element-equality">
        <xsl:with-param name="other" select="$elem"/>
      </xsl:apply-templates>
    </xsl:for-each>
  </xsl:variable>
  <xsl:value-of select="string($member-of)"/>
</xsl:template>
   
<!-- Compute the union of two sets using "by value" equality. -->
<xsl:template name="vset:union">
  <xsl:param name="nodes1" select="/.." />
  <xsl:param name="nodes2" select="/.." />
  <!-- for internal use -->
  <xsl:param name="nodes" select="$nodes1 | $nodes2" />
  <xsl:param name="union" select="/.." />
  <xsl:choose>
    <xsl:when test="$nodes">
      <xsl:variable name="test">
        <xsl:apply-templates select="$union" mode="vset:member-of">
          <xsl:with-param name="elem" select="$nodes[1]" />
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:call-template name="vset:union">
        <xsl:with-param name="nodes" select="$nodes[position(  ) > 1]" />
        <xsl:with-param name="union" 
                        select="$union | $nodes[1][not(string($test))]" />
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="$union" mode="vset:union" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
   
<!-- Return a copy of union by default. Override in importing stylesheet  to recieve
reults as a "callback"-->
<xsl:template match="/ | node(  ) | @*" mode="vset:union">
  <xsl:copy-of select="."/>
</xsl:template>
   
<!-- Compute the intersection of two sets using "by value" equality. -->
<xsl:template name="vset:intersection">
  <xsl:param name="nodes1" select="/.."/>
  <xsl:param name="nodes2" select="/.."/>
  <!-- For internal use -->
  <xsl:param name="intersect" select="/.."/>
  
  <xsl:choose>
    <xsl:when test="not($nodes1)">
      <xsl:apply-templates select="$intersect" mode="vset:intersection"/>
    </xsl:when>
    <xsl:when test="not($nodes2)">
      <xsl:apply-templates select="$intersect" mode="vset:intersection"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="test1">
        <xsl:apply-templates select="$nodes2" mode="vset:member-of">
          <xsl:with-param name="elem" select="$nodes1[1]"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="test2">
        <xsl:apply-templates select="$intersect" mode="vset:member-of">
          <xsl:with-param name="elem" select="$nodes1[1]"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="string($test1) and not(string($test2))">
          <xsl:call-template name="vset:intersection">
            <xsl:with-param name="nodes1" 
                    select="$nodes1[position(  ) > 1]"/>
            <xsl:with-param name="nodes2" select="$nodes2"/>
            <xsl:with-param name="intersect" 
                    select="$intersect | $nodes1[1]"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="vset:intersection">
            <xsl:with-param name="nodes1" 
                    select="$nodes1[position(  ) > 1]"/>
            <xsl:with-param name="nodes2" select="$nodes2"/>
            <xsl:with-param name="intersect" select="$intersect"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
   
<!-- Return a copy of intersection by default. Override in importing stylesheet to
recieve results as a "callback"-->
<xsl:template match="/ | node(  ) | @*" mode="vset:intersection">
  <xsl:copy-of select="."/>
</xsl:template>
   
<!-- Compute the differnce between two sets (node1 - nodes2) using "by value" 
equality. -->
<xsl:template name="vset:difference">
  <xsl:param name="nodes1" select="/.."/>
  <xsl:param name="nodes2" select="/.."/>
  <!-- For internal use -->
  <xsl:param name="difference" select="/.."/>
  
  <xsl:choose>
    <xsl:when test="not($nodes1)">
      <xsl:apply-templates select="$difference" mode="vset:difference"/>
    </xsl:when>
    <xsl:when test="not($nodes2)">
      <xsl:apply-templates select="$nodes1" mode="vset:difference"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="test1">
        <xsl:apply-templates select="$nodes2" mode="vset:member-of">
          <xsl:with-param name="elem" select="$nodes1[1]"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:variable name="test2">
        <xsl:apply-templates select="$difference" mode="vset:member-of">
          <xsl:with-param name="elem" select="$nodes1[1]"/>
        </xsl:apply-templates>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="string($test1) or string($test2)">
          <xsl:call-template name="vset:difference">
            <xsl:with-param name="nodes1" 
                    select="$nodes1[position(  ) > 1]"/>
            <xsl:with-param name="nodes2" select="$nodes2"/>
            <xsl:with-param name="difference" select="$difference"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="vset:difference">
            <xsl:with-param name="nodes1" 
                    select="$nodes1[position(  ) > 1]"/>
            <xsl:with-param name="nodes2" select="$nodes2"/>
            <xsl:with-param name="difference" 
                    select="$difference | $nodes1[1]"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
   
<!-- Return a copy of difference by default. Override in importing stylesheet to
recieve results as a "callback"-->
<xsl:template match="/ | node(  ) | @*" mode="vset:difference">
  <xsl:copy-of select="."/>
</xsl:template>
   
</xsl:stylesheet>
