package com.backend.callingapp;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.Nonza;

public class TestElement implements Nonza {
    String xml;
    public TestElement(String xml) {
        this.xml = xml;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        return this.xml;
    }
}
