package org.eclipse.swordfish.core.context;

import java.util.Map;

import org.eclipse.swordfish.api.context.SwordfishContext;
import org.eclipse.swordfish.api.context.SwordfishContextAware;
import org.eclipse.swordfish.core.RegistryImpl;
import org.springframework.util.Assert;

public class SwordfishContextAwareRegistry extends RegistryImpl<SwordfishContextAware>{
    private SwordfishContext swordfishContext;
    public SwordfishContext getSwordfishContext() {

        return swordfishContext;
    }
    public void setSwordfishContext(SwordfishContext swordfishContext) {
        this.swordfishContext = swordfishContext;
    }
    @Override
    protected void doRegister(SwordfishContextAware swordfishContextAware, Map<String, ?> properties) throws Exception {
        Assert.notNull(swordfishContextAware);
        Assert.notNull(swordfishContext, "swordfishContext is null");
        swordfishContextAware.setContext(swordfishContext);
        super.doRegister(swordfishContextAware, properties);
    }

}
