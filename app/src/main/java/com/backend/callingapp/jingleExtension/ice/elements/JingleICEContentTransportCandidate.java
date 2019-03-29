package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;

public class JingleICEContentTransportCandidate extends JingleContentTransportCandidate {

    public static final String NAMESPACE = "urn:xmpp:jingle:transports:ice-udp:1";

    public static final String ATTR_XMLNS = "xmlns:stream";
    public static final String ATTR_REL_PORT = "rel-port";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_PROTOCOL = "protocol";
    public static final String ATTR_ID = "id";
    public static final String ATTR_IP = "ip";
    public static final String ATTR_COMPONENT = "component";
    public static final String ATTR_PORT = "port";
    public static final String ATTR_FOUNDATION = "foundation";
    public static final String ATTR_GENERATION = "generation";
    public static final String ATTR_REL_ADDR = "rel-addr";
    public static final String ATTR_PRIORITY = "priority";
    public static final String ATTR_NETWORK = "network";


    private String xmlns;

    private String rel_port;

    private String type;

    private String protocol;

    private String id;

    private String component;

    private String port;

    private String foundation;

    private String generation;

    private String rel_addr;

    private String priority;

    private String network;

    private String ip;

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public String getRel_port() {
        return rel_port;
    }

    public void setRel_port(String rel_port) {
        this.rel_port = rel_port;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getFoundation() {
        return foundation;
    }

    public void setFoundation(String foundation) {
        this.foundation = foundation;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getRel_addr() {
        return rel_addr;
    }

    public void setRel_addr(String rel_addr) {
        this.rel_addr = rel_addr;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(this);
        xml.attribute(ATTR_COMPONENT, component);
        xml.attribute(ATTR_FOUNDATION, foundation);
        xml.attribute(ATTR_GENERATION, generation);

        if (Integer.parseInt(port) >= 0) {
            xml.attribute(ATTR_PORT, port);
        }
        xml.attribute(ATTR_PRIORITY, priority);
        xml.optAttribute(ATTR_TYPE, type);
        xml.attribute(ATTR_ID, id);
        xml.attribute(ATTR_IP, ip);
        xml.attribute(ATTR_REL_ADDR, rel_addr);
        xml.attribute(ATTR_REL_PORT, rel_port);
        xml.attribute(ATTR_XMLNS, xmlns);
        xml.attribute(ATTR_NETWORK, network);
        xml.attribute(ATTR_PROTOCOL, protocol);

        xml.closeEmptyElement();
        return xml;
    }



}
