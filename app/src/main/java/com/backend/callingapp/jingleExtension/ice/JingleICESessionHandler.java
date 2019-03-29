package com.backend.callingapp.jingleExtension.ice;

import android.util.Log;

import com.backend.callingapp.jingleExtension.JingleManagerExt;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.JingleUtil;
import org.jivesoftware.smackx.jingle.Role;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
import org.jxmpp.jid.FullJid;

import java.util.List;

public class JingleICESessionHandler extends JingleSession {

    private static final String TAG = JingleICESessionHandler.class.getSimpleName();

    private final XMPPConnection mConnection;

    private JingleUtil mJingleUtil;

    public JingleICESessionHandler(FullJid initiator, FullJid responder, Role role, String sid, XMPPConnection connection) {
        super(initiator, responder, role, sid);
        mConnection = connection;
        mJingleUtil = new JingleUtil(mConnection);

    }

    public JingleICESessionHandler(FullJid initiator, FullJid responder, Role role, String sid, List<JingleContent> contents, XMPPConnection connection) {
        super(initiator, responder, role, sid, contents);
        mConnection = connection;
        mJingleUtil = new JingleUtil(mConnection);
    }

    @Override
    public XMPPConnection getConnection() {
        return mConnection;
    }

    public void setTransportSession(JingleTransportSession<?> transportSession) {
        this.transportSession = transportSession;
    }

    @Override
    public IQ handleSessionInitiate(Jingle sessionInitiate) {

        JingleManagerExt.getInstanceFor(mConnection).registerJingleSessionHandler(sessionInitiate.getFrom().asFullJidIfPossible(),sessionInitiate.getSid(),this);

        /*JingleContentDescription description = null;
        JingleContentTransport transport = null;
        String contentName = "audio";

        if (!sessionInitiate.getContents().isEmpty()){
            description = sessionInitiate.getContents().get(0).getDescription();
            transport = sessionInitiate.getContents().get(0).getTransport();
            contentName = sessionInitiate.getContents().get(0).getName();
        }*/
        return mJingleUtil.createAck(sessionInitiate);
    }

    @Override
    public void onTransportMethodFailed(String namespace) {
        Log.d(TAG, "onTransportMethodFailed: ");
    }
}
