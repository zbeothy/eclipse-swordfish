package org.eclipse.swordfish.samples.configuration;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swordfish.api.context.SwordfishContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


public class ConfigurationProvider  implements InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProvider.class);

    private SwordfishContext swordfishContext;
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setContext(SwordfishContext swordfishContext) {
        this.swordfishContext = swordfishContext;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(id);
        final Map<String, Object> configData = new HashMap<String, Object>();
        configData.put("testProperty1", "Updated by ConfigurationProvider");
        configData.put("currentDateTime",  DateFormat.getDateTimeInstance().format(new Date()));
        LOG.info("Updating configuration");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                swordfishContext.getConfigurationService().updateConfiguration(id, configData);

            }
        }, 5000);


    }

}