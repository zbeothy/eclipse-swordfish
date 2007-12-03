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
   
<xsl:import href="vset.xsl"/>
   
<xsl:output method="xml" indent="yes"/>
   
<xsl:param name="doc2"/> 
   
<xsl:template match="/*">
  <xsl:copy>
    <xsl:call-template name="vset:union">
      <xsl:with-param name="nodes1" select="*"/>
      <xsl:with-param name="nodes2" select="document($doc2)/*/*"/>
    </xsl:call-template>
  </xsl:copy>
</xsl:template>
   
</xsl:stylesheet>
