package com.backend.callingapp.jingleExtension.ice.providers;

import com.backend.callingapp.jingleExtension.ice.elements.Fingerprint;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransport;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransportCandidate;
import com.backend.callingapp.jingleExtension.ice.elements.Sctpmap;

import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.parsing.StandardExtensionElementProvider;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.jivesoftware.smackx.jingle.provider.JingleContentTransportProvider;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class JingleICEContentTransportProvider extends JingleContentTransportProvider<JingleICEContentTransport> {
    @Override
    public JingleICEContentTransport parse(XmlPullParser parser, int initialDepth) throws Exception {
        StandardExtensionElement standardExtensionElement = StandardExtensionElementProvider.INSTANCE.parse(parser);

        List<StandardExtensionElement> candidateElements = standardExtensionElement.getElements(JingleContentTransportCandidate.ELEMENT,JingleICEContentTransportCandidate.NAMESPACE);
        List<JingleContentTransportCandidate> candidates = new ArrayList<>();


        for (StandardExtensionElement element :  candidateElements) {
            JingleICEContentTransportCandidate candidate = new JingleICEContentTransportCandidate();
            candidate.setComponent(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_COMPONENT));
            candidate.setFoundation(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_FOUNDATION));
            candidate.setGeneration(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_GENERATION));
            candidate.setId(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_ID));
            candidate.setIp(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_IP));
            candidate.setNetwork(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_NETWORK));
            candidate.setPort(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_PORT));
            candidate.setPriority(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_PRIORITY));
            candidate.setProtocol(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_PROTOCOL));
            candidate.setRel_addr(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_REL_ADDR));
            candidate.setRel_port(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_REL_PORT));
            candidate.setType(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_TYPE));
            candidate.setXmlns(element.getAttributeValue(JingleICEContentTransportCandidate.ATTR_XMLNS));
            candidates.add(candidate);
        }

        StandardExtensionElement fingerprintElement = standardExtensionElement.getFirstElement(Fingerprint.ELEMENT, Fingerprint.NAMESPACE);
        Fingerprint fingerprint = null;
        if (fingerprintElement != null){
            String setup = fingerprintElement.getAttributeValue(Fingerprint.ATTR_SETUP);
            String hash = fingerprintElement.getAttributeValue(Fingerprint.ATTR_HASH);
            String hashValue = fingerprintElement.getText();
            fingerprint = new Fingerprint(hash,setup,hashValue);
        }

        StandardExtensionElement sctpmapElement = standardExtensionElement.getFirstElement(Sctpmap.ELEMENT, Sctpmap.NAMESPACE);
        Sctpmap sctpmap = null;
        if (sctpmapElement != null){
            String number = sctpmapElement.getAttributeValue(Sctpmap.ATTR_NUMBER);
            String protocol = sctpmapElement.getAttributeValue(Sctpmap.ATTR_PROTOCOL);
            String streams = sctpmapElement.getAttributeValue(Sctpmap.ATTR_STREAMS);
            sctpmap = new Sctpmap(number,protocol,streams);
        }

        String pwd = standardExtensionElement.getAttributeValue(JingleICEContentTransport.ATTR_PWD);
        String ufrag = standardExtensionElement.getAttributeValue(JingleICEContentTransport.ATTR_UFRAG);

        return new JingleICEContentTransport(candidates,pwd,ufrag,fingerprint,sctpmap);
    }
}
