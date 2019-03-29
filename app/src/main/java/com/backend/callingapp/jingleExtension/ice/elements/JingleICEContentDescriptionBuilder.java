package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.packet.NamedElement;

import java.util.List;

public class JingleICEContentDescriptionBuilder {
    private List<? extends NamedElement> payloads;

    private String media;

    public JingleICEContentDescriptionBuilder setPayloads(List<? extends NamedElement> payloads) {
        this.payloads = payloads;
        return this;
    }

    public JingleICEContentDescriptionBuilder setMedia(String media) {
        this.media = media;
        return this;
    }

    public JingleICEContentDescription createJingleContentDescriptionExt() {
        return new JingleICEContentDescription(payloads, media);
    }
}
