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
<xsl:stylesheet version="1.0" 
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:merge="http:www.ora.com/XSLTCookbook/mnamespaces/merge">
   
<xsl:include href="merge-simple-using-key.xsl"/>
   
<!--A person is uniquely defined by the concatenation of 
    last and first names -->
<xsl:key name="merge:key" match="person" 
         use="concat(@lastname,@firstname)"/>
   
<xsl:output method="xml" indent="yes"/>
   
<!-- This template retrives the key value for an element -->
<xsl:template name="merge:key-value">
  <xsl:value-of select="concat(@lastname,@firstname)"/>
</xsl:template>
   
</xsl:stylesheet>
