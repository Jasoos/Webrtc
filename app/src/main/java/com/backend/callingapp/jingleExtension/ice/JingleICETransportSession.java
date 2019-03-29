package com.backend.callingapp.jingleExtension.ice;


import com.backend.callingapp.jingleExtension.JingleManagerExt;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransport;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleUtil;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentTransport;
import org.jivesoftware.smackx.jingle.transports.JingleTransportInitiationCallback;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;


import java.util.logging.Level;
import java.util.logging.Logger;

public class JingleICETransportSession extends JingleTransportSession<JingleICEContentTransport> {

    private static final Logger LOGGER = Logger.getLogger(JingleICETransportSession.class.getName());

    private final JingleICETransportManager transportManager;


    public JingleICETransportSession(JingleSession session) {
        super(session);
        transportManager = JingleICETransportManager.getInstanceFor(session.getConnection());
    }

    @Override
    public JingleICEContentTransport createTransport() {
        if (theirProposal == null) {
            return new JingleICEContentTransport();
        } else {
            return new JingleICEContentTransport(theirProposal.getCandidates(),theirProposal.getPwd(),
                    theirProposal.getUfrag(),theirProposal.getFingerprint(),theirProposal.getSctpmap());
        }
    }

    @Override
    public void setTheirProposal(JingleContentTransport transport) {
        theirProposal = (JingleICEContentTransport) transport;
    }


    @Override
    public void initiateOutgoingSession(JingleTransportInitiationCallback callback) {
        LOGGER.log(Level.INFO, "Initiate Jingle ICE session.");
    }

    @Override
    public void initiateIncomingSession(JingleTransportInitiationCallback callback) {
        LOGGER.log(Level.INFO, "Await Jingle ICE session.");
    }

    @Override
    public String getNamespace() {
        return transportManager.getNamespace();
    }

    @Override
    public IQ handleTransportInfo(Jingle transportInfo) {
        try {
            transportManager.getConnection().sendStanza(IQ.createResultIQ(transportInfo));
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JingleManagerExt.getInstanceFor(transportManager.getConnection()).registerJingleSessionHandler(transportInfo.getFrom().asFullJidIfPossible(),
                transportInfo.getSid(),jingleSession);

        return new JingleUtil(transportManager.getConnection()).createTransportAccept(transportInfo.getFrom().asFullJidIfPossible(),
                transportInfo.getInitiator(),transportInfo.getSid(), JingleContent.Creator.responder,"audio",null);
    }

    @Override
    public JingleTransportManager<JingleICEContentTransport> transportManager() {
        return JingleICETransportManager.getInstanceFor(jingleSession.getConnection());
    }
}
