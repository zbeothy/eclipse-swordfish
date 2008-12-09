package org.eclipse.swordfish.core.test;


import org.eclipse.osgi.framework.internal.core.FilterImpl;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class FrameworkUtil {

    public static Filter createFilter(String f) throws InvalidSyntaxException {
        return new FilterImpl(f);
    }

}
