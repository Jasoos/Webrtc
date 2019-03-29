package com.backend.callingapp.jingleExtension.ice.elements;

import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportInfo;

import java.util.List;

public class JingleICEContentTransport extends JingleContentTransport {

    public static final String NAMESPACE = "urn:xmpp:jingle:transports:ice-udp:1";

    public static final String ATTR_PWD = "pwd";

    public static final String ATTR_UFRAG = "ufrag";

    final String pwd;

    final String ufrag;

    final Fingerprint fingerprint;

    final Sctpmap sctpmap;


    public JingleICEContentTransport(List<JingleContentTransportCandidate> candidates, String pwd, String ufrag, Fingerprint fingerprint, Sctpmap sctpmap) {
        super(candidates);
        this.pwd = pwd;
        this.ufrag = ufrag;
        this.fingerprint = fingerprint;
        this.sctpmap = sctpmap;
    }

    protected JingleICEContentTransport(List<JingleContentTransportCandidate> candidates, JingleContentTransportInfo info, String pwd, String ufrag,Fingerprint fingerprint,Sctpmap sctpmap) {
        super(candidates, info);
        this.pwd = pwd;
        this.ufrag = ufrag;
        this.fingerprint = fingerprint;
        this.sctpmap = sctpmap;
    }

    public JingleICEContentTransport() {
        super(null);
        pwd = null;
        ufrag = null;
        this.fingerprint = null;
        this.sctpmap = null;
    }

    public String getPwd() {
        return pwd;
    }

    public Fingerprint getFingerprint() {
        return fingerprint;
    }

    public Sctpmap getSctpmap() {
        return sctpmap;
    }

    public String getUfrag() {
        return ufrag;
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }
}
