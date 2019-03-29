package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.ExtensionElement;


public class BridgeSession implements ExtensionElement {
    public static final String NAMESPACE = "http://jitsi.org/protocol/focus";

    public static final String ELEMENT = "bridge-session";

    public static final String ATTRIBUTE_ID = "id";

    public static final String ATTRIBUTE_REGION = "region";

    private String id = null;

    private String region = null;

    public BridgeSession() {
    }

    public BridgeSession(String id, String region) {
        this.id = id;
        this.region = region;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        return null;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
