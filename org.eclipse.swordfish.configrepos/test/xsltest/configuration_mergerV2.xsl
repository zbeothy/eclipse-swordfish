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
    
  <xsl:param name="config2"/> 
  
  <xsl:template name="merge_nodelists">
    <xsl:param name="nodes1"/>
    <xsl:param name="nodes2"/>
    <xsl:for-each select="$nodes1/*">
      <xsl:variable name="key-value1">
        <xsl:call-template name="key-value"/>
      </xsl:variable>
      <xsl:variable name="element1" select="."/>
      <xsl:for-each select="$nodes2">
        <xsl:choose>
          <xsl:when test="not(key('id', $key-value1))">
            <xsl:copy-of select="$element1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:variable name="element2" select="key('id', $key-value1)"/>           
            <xsl:for-each select="$element1">             
              <xsl:call-template name="copyNodes">
                <xsl:with-param name="nodes2" select="$element2"/>    
              </xsl:call-template>
            </xsl:for-each>                      
           </xsl:otherwise>          
        </xsl:choose>
      </xsl:for-each>          
    </xsl:for-each>
    <xsl:for-each select="$nodes2/*">
      <xsl:variable name="key-value2">
        <xsl:call-template name="key-value"/>
      </xsl:variable>
      <xsl:variable name="element2" select="."/>
      <xsl:for-each select="$nodes1">
        <xsl:if test="not(key('id', $key-value2))">
          <xsl:copy-of select="$element2"/>
        </xsl:if>
      </xsl:for-each>
    </xsl:for-each>
  </xsl:template>
    
  <xsl:template name="copyNodes">
    <xsl:param name="nodes2"/>
    <xsl:copy>    
      <xsl:copy-of select="@*"/>   
      <xsl:copy-of select="text()"/>           
      <xsl:call-template name="merge_nodelists">
        <xsl:with-param name="nodes1" select="current()"/>
        <xsl:with-param name="nodes2" select="$nodes2"/>
      </xsl:call-template>  
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="/*">    
    <!--Copy the outer most element of the source document -->
    <xsl:copy>
      <xsl:call-template name="merge_nodelists">
        <xsl:with-param name="nodes1" select="current()"/>
        <xsl:with-param name="nodes2" select="document($config2)/*"/>
      </xsl:call-template>    
    </xsl:copy>
  </xsl:template>

  <xsl:key name="id" match="*" 
    use="concat(name(..),concat('/',concat(name(),concat('/',@name))))"/>
 
  <!-- This template retrives the key value for an element -->
  <xsl:template name="key-value">
    <xsl:value-of select="concat(name(..),concat('/',
    concat(name(),concat('/',@name))))"/>
  </xsl:template>  
   
</xsl:stylesheet>
