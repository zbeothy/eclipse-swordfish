package org.eclipse.swordfish.core.util.xml;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.util.Assert;

public class XmlUtil {
    public static final String DEFAULT_CHARSET_PROPERTY = "org.apache.servicemix.default.charset";
    public static final String defaultCharset = System.getProperty(DEFAULT_CHARSET_PROPERTY, "UTF-8");
    private static TransformerFactory transformerFactory;

    private static Transformer getTransformer() {
        if (transformerFactory == null) {
            transformerFactory = TransformerFactory.newInstance();
        }
        Assert.notNull(transformerFactory);
        Transformer ret;
        try {
            ret = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException ex) {
           throw new RuntimeException(ex);
        }
        Assert.notNull(ret);
        ret.setOutputProperty(OutputKeys.ENCODING, defaultCharset);
        return ret;
    }

    public static String toString(Source source) {
        if (source == null) {
            return null;
        } else if (source instanceof StringSource) {
            return ((StringSource) source).getText();
        }  else {
            StringWriter buffer = new StringWriter();
            try {
                getTransformer().transform(source, new StreamResult(buffer));
            } catch (TransformerException ex) {
                throw new RuntimeException(ex);
            }
            return buffer.toString();
        }
    }
}
