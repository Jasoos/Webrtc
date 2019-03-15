package com.backend.callingapp;

import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;

public class TestIQ extends IQ {
    public TestIQ(IQ iq) {
        super(iq);
    }

    protected TestIQ(String childElementName) {
        super(childElementName);
    }

    protected TestIQ(String childElementName, String childElementNamespace) {
        super(childElementName, childElementNamespace);
    }

    @Override
    protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
        xml.setEmptyElement();
        return xml;
    }
}
