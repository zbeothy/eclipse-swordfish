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
   
<xsl:import href="mergeUnion.xsl"/>
   
<xsl:template match="person" mode="vset:element-equality">
  <xsl:param name="other"/>
  <xsl:if test="concat(@lastname,@firstname) = 
                concat($other/@lastname,$other/@firstname)">  
    <xsl:value-of select="true(  )"/>
  </xsl:if>
</xsl:template>
   
</xsl:stylesheet>
