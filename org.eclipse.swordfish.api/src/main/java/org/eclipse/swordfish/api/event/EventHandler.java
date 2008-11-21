package org.eclipse.swordfish.api.event;
/**
 * Interface providing ability to listen for Swordifsh messages sending on specified topic.
 */
public interface EventHandler<T extends Event> {
     /**
      * specified event topic name
      */
     String getSubscribedTopic();
     /**
      * specified severity level of events  current event handler want to receive. 
      * See org.eclipse.swordfish.api.event.Severity
      */

     /**
      * invokes asynchronously with event creator thread, used osgi EventAdmin service as a transport.
      */
     public void handleEvent(T event);
}
