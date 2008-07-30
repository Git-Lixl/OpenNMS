package org.opennms.web.rest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.netmgt.model.OnmsEvent;

@XmlRootElement
public class OnmsEventList extends LinkedList<OnmsEvent> {

    private static final long serialVersionUID = 8031737923157780179L;
    
    public OnmsEventList() {
        super();
    }

    public OnmsEventList(Collection<? extends OnmsEvent> c) {
        super(c);
    }

    @XmlElement(name = "event")
    public List<OnmsEvent> getEvents() {
        return this;
    }
    
    public void setEvents(List<OnmsEvent> events) {
        clear();
        addAll(events);
    }

}
