package wsdl;

import org.springframework.core.io.Resource;

public class ResourceToStringConverter {

    private Resource resource;
    private String url;

    public ResourceToStringConverter(String resource) {
        try {
            url = getClass().getClassLoader().getResource(resource).toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
        System.out.println(resource.exists());
    }
}
