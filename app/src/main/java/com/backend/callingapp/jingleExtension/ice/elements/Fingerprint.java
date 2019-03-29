package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Fingerprint implements NamedElement {

    public static final String ELEMENT = "fingerprint";

    public static final String NAMESPACE = "urn:xmpp:jingle:apps:dtls:0";

    public static final String ATTR_HASH = "hash";

    public static final String ATTR_SETUP = "setup";

    private final String hash;

    private final String setup;

    private final String hashValue;

    public Fingerprint(String hash, String setup, String hashValue) {
        this.hash = hash;
        this.setup = setup;
        this.hashValue = hashValue;
    }

    public String getHash() {
        return hash;
    }

    public String getSetup() {
        return setup;
    }

    public String getHashValue() {
        return hashValue;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML(String enclosingNamespace) {
        XmlStringBuilder xml = new XmlStringBuilder();
        xml.halfOpenElement(this);
        xml.attribute(ATTR_HASH,hash);
        xml.attribute(ATTR_SETUP,setup);
        xml.append(hashValue);

        xml.closeEmptyElement();
        return xml;
    }
}
