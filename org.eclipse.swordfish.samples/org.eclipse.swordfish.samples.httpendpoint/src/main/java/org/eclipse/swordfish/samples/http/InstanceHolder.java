package org.eclipse.swordfish.samples.http;

public class InstanceHolder<T> {
    private T instance;

    public T getInstance() {
        return instance;
    }

    public void setInstance(T instance) {
        this.instance = instance;
    }
}
