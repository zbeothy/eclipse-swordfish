package org.eclipse.swordfish.core.util.xml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.stream.StreamSource;

import org.springframework.util.Assert;

public class StringSource extends StreamSource {
    private String text;
    private String encoding = "UTF-8";

    public StringSource() {
    }

    public StringSource(String text) {
        Assert.notNull(text, "text can not be null");
        this.text = text;
    }



    public StringSource(String text, String systemId, String encoding) {
        this.text = text;
        this.encoding = encoding;
        setSystemId(systemId);
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(text.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Reader getReader() {
        return new StringReader(text);
    }

    @Override
    public String toString() {
        return "StringSource[" + text + "]";
    }

    public String getText() {
        return text;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setText(String text) {
        this.text = text;
    }
}
