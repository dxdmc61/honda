package com.honda.aem.core.listeners;

import java.util.EventListener;

import javax.jcr.observation.Event;

import org.osgi.service.component.annotations.Component;

@Component(service = EventListener.class,
  property ={
    "event.topic=org/observer/EVENT"

  })

public class JcrObserver implements EventListener {

    public void onEvent(Event event){

    }
    
}
