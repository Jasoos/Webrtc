package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Sctpmap implements NamedElement {

    public static final String ELEMENT = "sctpmap";

    public static final String NAMESPACE = "urn:xmpp:jingle:transports:dtls-sctp:1";

    public static final String ATTR_NUMBER = "number";

    public static final String ATTR_PROTOCOL = "protocol";

    public static final String ATTR_STREAMS = "streams";

    private final String number;

    private final String protocol;

    private final String streams;

    public Sctpmap(String number, String protocol, String streams) {
        this.number = number;
        this.protocol = protocol;
        this.streams = streams;
    }

    public String getNumber() {
        return number;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getStreams() {
        return streams;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(this);
        xml.attribute(ATTR_PROTOCOL, protocol);
        xml.attribute(ATTR_NUMBER, number);
        xml.attribute(ATTR_STREAMS, streams);

        xml.closeEmptyElement();
        return xml;
    }
}
