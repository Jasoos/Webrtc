package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;

import java.util.List;

public class JingleICEContentDescription extends JingleContentDescription {

    public static final String NAMESPACE = "urn:xmpp:jingle:apps:rtp:1";

    private String mMedia;

    protected JingleICEContentDescription(List<? extends NamedElement> payloads, String media) {
        super(payloads);
        mMedia = media;
    }

    public String getMedia() {
        return mMedia;
    }

    public void setMedia(String mMedia) {
        this.mMedia = mMedia;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
