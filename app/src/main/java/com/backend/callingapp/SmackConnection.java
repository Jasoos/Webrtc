package com.backend.callingapp;

import android.util.Log;

import org.igniterealtime.jbosh.BOSHClient;
import org.igniterealtime.jbosh.BOSHClientConfig;
import org.igniterealtime.jbosh.BOSHClientConnEvent;
import org.igniterealtime.jbosh.BOSHClientConnListener;
import org.igniterealtime.jbosh.BOSHClientRequestListener;
import org.igniterealtime.jbosh.BOSHClientResponseListener;
import org.igniterealtime.jbosh.BOSHMessageEvent;
import org.igniterealtime.jbosh.BodyQName;
import org.igniterealtime.jbosh.ComposableBody;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.BOSHConfiguration;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smack.roster.rosterstore.RosterStore;
import org.jivesoftware.smackx.caps.EntityCapsManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;

public class SmackConnection implements BOSHClientConnListener, RosterListener, ConnectionListener, StanzaListener, StanzaFilter, BOSHClientResponseListener, BOSHClientRequestListener, PingFailedListener, PresenceEventListener, RosterLoadedListener {

    private static final String TAG = SmackConnection.class.getSimpleName();
    private XMPPBOSHConnection mConnection = null;
    private String mRoomName;

    private MucRoom mMucRoom;
    private PubSubHelper mPubSubHelper;

    private BOSHClient mClient;

    public SmackConnection(String roomName) {

        mRoomName = roomName;

        SmackConfiguration.DEBUG = true;
        System.setProperty("smack.debuggerClass","org.jivesoftware.smack.debugger.ConsoleDebugger");
        System.setProperty("smack.debugEnabled", "true");
        SmackConfiguration.setDefaultReplyTimeout(120000);

        BOSHConfiguration config = null;
        try {
            config = BOSHConfiguration.builder()
                    .useHttps()
                    .setHost("meet.jit.si")
                    .setPort(443)
                    .allowEmptyOrNullUsernames()
                    .setFile("/http-bind?room=" + mRoomName)
                    .setXmppDomain("meet.jit.si")
                    .setSocketFactory(SSLSocketFactory.getDefault())
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setSendPresence(true)
                    .enableDefaultDebugger()
                    .performSaslAnonymousAuthentication()
                    .build();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        mConnection = new XMPPBOSHConnection(config);
        mConnection.addConnectionListener(this);
        mConnection.addAsyncStanzaListener(this, this);

        BOSHClientConfig mBoshClientConfig = BOSHClientConfig.Builder
                .create(URI.create("https://meet.jit.si/http-bind?room=" + mRoomName), "meet.jit.si")
                .build();

        mClient = BOSHClient.create(mBoshClientConfig);

        mClient.addBOSHClientResponseListener(this);
        mClient.addBOSHClientConnListener(this);
        mClient.addBOSHClientRequestListener(this);

        mMucRoom = new MucRoom(mConnection);
    }

    public void connect() {
        try {
            mConnection.connect().login();

            // Get an instance of entity caps manager for the specified connection
            EntityCapsManager mgr = EntityCapsManager.getInstanceFor(mConnection);
            // Enable entity capabilities
            mgr.enableEntityCaps();

            // sending the configuration form unlocks the room

            PingManager.setDefaultPingInterval(600); //Ping every 10 minutes
            PingManager pingManager = PingManager.getInstanceFor(mConnection);
            pingManager.registerPingFailedListener(this);

            Roster roster = Roster.getInstanceFor(mConnection);
            roster.addRosterListener(this);
            roster.addPresenceEventListener(this);
            roster.addRosterLoadedListener(this);
            Roster.setRosterLoadedAtLoginDefault(true);

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
    }

    @Override
    public void connected(XMPPConnection connection) {
       Log.d(TAG, "connected: ");
        /*try {
            mClient.send(ComposableBody.builder().setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind", "sid"), mPreBindResult.sid).build());
        } catch (BOSHException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
       Log.d(TAG, "authenticated: ");

        //mPubSubHelper = new PubSubHelper(mConnection, mRoomName, mPreBindResult, mMapperResponse);

        mMucRoom.createRoom(mRoomName);

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
    public void processStanza(Stanza packet) {
       Log.d(TAG, "processStanza: " + packet.toXML("").toString());
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

    @Override
    public void pingFailed() {
       Log.d(TAG, "pingFailed: ");
    }

    @Override
    public void presenceAvailable(FullJid address, Presence availablePresence) {
       Log.d(TAG, "presenceAvailable: ");
    }

    @Override
    public void presenceUnavailable(FullJid address, Presence presence) {
       Log.d(TAG, "presenceUnavailable: ");
    }

    @Override
    public void presenceError(Jid address, Presence errorPresence) {
       Log.d(TAG, "presenceError: ");
    }

    @Override
    public void presenceSubscribed(BareJid address, Presence subscribedPresence) {
       Log.d(TAG, "presenceSubscribed: ");
    }

    @Override
    public void presenceUnsubscribed(BareJid address, Presence unsubscribedPresence) {
       Log.d(TAG, "presenceUnsubscribed: ");
    }

    @Override
    public void onRosterLoaded(Roster roster) {
       Log.d(TAG, "onRosterLoaded: ");
    }

    @Override
    public void onRosterLoadingFailed(Exception exception) {
       Log.d(TAG, "onRosterLoadingFailed: ");
    }

}
