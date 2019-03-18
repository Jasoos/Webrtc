package com.backend.callingapp;

import android.util.Log;

import org.igniterealtime.jbosh.BOSHClient;
import org.igniterealtime.jbosh.BOSHClientConfig;
import org.igniterealtime.jbosh.BOSHClientConnEvent;
import org.igniterealtime.jbosh.BOSHClientConnListener;
import org.igniterealtime.jbosh.BOSHClientRequestListener;
import org.igniterealtime.jbosh.BOSHClientResponseListener;
import org.igniterealtime.jbosh.BOSHException;
import org.igniterealtime.jbosh.BOSHMessageEvent;
import org.igniterealtime.jbosh.BodyQName;
import org.igniterealtime.jbosh.ComposableBody;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.BOSHConfiguration;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;

public class SmackConnection implements BOSHClientConnListener, RosterListener, ConnectionListener, StanzaListener, StanzaFilter, BOSHClientResponseListener, BOSHClientRequestListener {

    private static final String TAG = SmackConnection.class.getSimpleName();
    private XMPPBOSHConnection connection = null;
    ConvoServicesInterface.PreBindResult mPreBindResult = null;
    private ConvoServicesInterface.MapperResponse mMapperResponse = null;
    private String mRoomName;

    public SmackConnection(ConvoServicesInterface.PreBindResult mPreBindResult, ConvoServicesInterface.MapperResponse mMapperResponse) {
        this.mPreBindResult = mPreBindResult;
        this.mMapperResponse = mMapperResponse;
    }

    public void connect(String roomName) {
        try {
            mRoomName = roomName;
            BOSHConfiguration config = BOSHConfiguration.builder()
                    .useHttps()
                    .setHost("meet.jit.si")
                    .setPort(443)
                    .setFile("/http-bind?room=" + roomName)
                    .setXmppDomain("meet.jit.si")
                    .setSocketFactory(SSLSocketFactory.getDefault())
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .addEnabledSaslMechanism("ANONYMOUS")
                    .build();
            connection = new XMPPBOSHConnection(config);
            connection.addConnectionListener(this);
            connection.addAsyncStanzaListener(this, this);
            connection.connect().login(mPreBindResult.jid, null);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void entriesAdded(Collection<Jid> addresses) {
        Log.d(TAG, "entriesAdded: ");
    }

    @Override
    public void entriesUpdated(Collection<Jid> addresses) {
        Log.d(TAG, "entriesUpdated: ");
    }

    @Override
    public void entriesDeleted(Collection<Jid> addresses) {
        Log.d(TAG, "entriesDeleted: ");
    }

    @Override
    public void presenceChanged(Presence presence) {
        Log.d(TAG, "presenceChanged: ");
    }

    @Override
    public void connectionEvent(BOSHClientConnEvent connEvent) {
        Log.d(TAG, "connectionEvent: ");
        /*try {
            if (connEvent.isConnected()) {
                PingManager pingManager = PingManager.getInstanceFor(this.connection);

                pingManager.ping(JidCreate.from(mPreBindResult.jid));
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.d(TAG, "connected: ");
        try {

            BOSHClientConfig boshClientConfig = BOSHClientConfig.Builder
                    .create(URI.create("https://meet.jit.si/http-bind?room=" + mRoomName), "meet.jit.si")
                    .setFrom(mPreBindResult.jid)
                    .build();

            BOSHClient mClient = BOSHClient.create(boshClientConfig);

            mClient.addBOSHClientResponseListener(this);
            mClient.addBOSHClientConnListener(this);
            mClient.addBOSHClientRequestListener(this);

            mClient.send(ComposableBody.builder().setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind", "sid"), mPreBindResult.sid).build());


        } catch (BOSHException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.d(TAG, "authenticated: ");
    }

    @Override
    public void connectionClosed() {
        Log.d(TAG, "connectionClosed: ");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.d(TAG, "connectionClosedOnError: ");
    }

    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
        Log.d(TAG, "processStanza: ");
    }

    @Override
    public boolean accept(Stanza stanza) {
        Log.d(TAG, "accept: ");
        return true;
    }

    @Override
    public void responseReceived(BOSHMessageEvent event) {
        Log.d(TAG, "responseReceived: ");
    }

    @Override
    public void requestSent(BOSHMessageEvent event) {
        Log.d(TAG, "requestSent: ");
    }


    public void createPacket() throws XmppStringprepException {
        ComposableBody composableBody = ComposableBody.builder()
                .setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind", "sid"), mPreBindResult.sid)
                .setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind", "rid"), mPreBindResult.rid)
                .build();
        TestIQ iq = new TestIQ("services", "urn:xmpp:extdisco:1");
        iq.setTo(JidCreate.from("meet.jit.si"));
        iq.setType(IQ.Type.get);

        TestIQ iq2 = new TestIQ(new DiscoverInfo());
        iq2.setFrom(JidCreate.from(mPreBindResult.jid));
        iq2.setTo(JidCreate.from("meet.jit.si"));
        iq2.setType(IQ.Type.get);

        composableBody = composableBody.rebuild().setPayloadXML(iq.toXML("").toString() + iq2.toXML("").toString()).build();
    }
}
