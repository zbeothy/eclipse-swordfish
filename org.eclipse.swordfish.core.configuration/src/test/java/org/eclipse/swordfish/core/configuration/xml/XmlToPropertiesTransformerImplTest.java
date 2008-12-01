package org.eclipse.swordfish.core.configuration.xml;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class XmlToPropertiesTransformerImplTest {
    private XmlToPropertiesTransformerImpl xmlToPropertiesTransformer;
    @Before
    public void setUp() {
        xmlToPropertiesTransformer = new XmlToPropertiesTransformerImpl();
    }
    @Test
    public void test1SimpleConfiguration() throws Exception {
        URL url = getClass().getResource("SimplePidXmlProperties.xml");
        xmlToPropertiesTransformer.loadConfiguration(url);
        assertNotNull(xmlToPropertiesTransformer.getProperties());
        Map<String, Map<String, String>> propsToPIDs = xmlToPropertiesTransformer.getPropertiesForPids();
        assertNotNull(propsToPIDs);
        assertTrue(propsToPIDs.size() == 2);
        Map<String, String> component1Props = propsToPIDs.get("component1");
        assertEquals(component1Props.size(), 2);
        assertEquals(component1Props.get("SomeProperty"), "SomeValue");
        assertEquals(component1Props.get("SomeOtherProperty"), "SomeOtherValue");
        Map<String, String> component2Props = propsToPIDs.get("component2");
        assertEquals(component2Props.size(), 1);
        assertEquals(component2Props.get("YetAnotherProperty"), "YetAnotherValue");
    }
    @Test
    public void test2ComplexConfiguration() throws Exception {
        URL url = getClass().getResource("ComplexPidXmlProperties.xml");
        xmlToPropertiesTransformer.loadConfiguration(url);
        assertNotNull(xmlToPropertiesTransformer.getProperties());
        Map<String, Map<String, String>> propsToPIDs = xmlToPropertiesTransformer.getPropertiesForPids();
        assertNotNull(propsToPIDs);
        assertTrue(propsToPIDs.size() == 2);
        Map<String, String> component1Props = propsToPIDs.get("component1");
        assertTrue(component1Props.containsKey("rowsPerPage{1}"));
        assertTrue(component1Props.containsKey("rowsPerPage{2}"));
        assertTrue(component1Props.containsKey("colors.link[@visited]"));
        assertEquals(component1Props.size(), 8);
        Map<String, String> component2Props = propsToPIDs.get("component2");

        assertEquals(component2Props.size(), 2);
    }
    @Test(expected = IllegalStateException.class)
    public void test3ConfigurationNotLoaded() throws Exception {
        xmlToPropertiesTransformer.getPropertiesForPids();
    }
}
