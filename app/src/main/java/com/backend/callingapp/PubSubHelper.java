package com.backend.callingapp;

import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jxmpp.jid.BareJid;

import java.util.List;

class PubSubHelper {

    private static final String TAG = PubSubHelper.class.getSimpleName();

    private final XMPPBOSHConnection mConnection;
    private final String mRoom;
    private final ConvoServicesInterface.PreBindResult mPreBindResult;
    private final ConvoServicesInterface.MapperResponse mMapperResponse;

    PubSubHelper(XMPPBOSHConnection connection, String roomName, ConvoServicesInterface.PreBindResult mPreBindResult, ConvoServicesInterface.MapperResponse mMapperResponse) {
        this.mConnection = connection;
        this.mRoom = roomName;

        this.mPreBindResult = mPreBindResult;
        this.mMapperResponse = mMapperResponse;

        // Create a pubsub manager using an existing XMPPConnection
        PubSubManager pubSubManager = PubSubManager.getInstance(mConnection);

        // Create the node
        try {
            LeafNode leaf = pubSubManager.getLeafNode(mMapperResponse.conference);

            //List<Affiliation> affilations = pubSubManager.getAffiliations();
            BareJid serviceJid = pubSubManager.getServiceJid();
            List<Subscription> subscriptions = pubSubManager.getSubscriptions();
            DiscoverInfo supportedFeatures = pubSubManager.getSupportedFeatures();
            Log.d(TAG, "PubSubHelper: ");

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (PubSubException.NotALeafNodeException e) {
            e.printStackTrace();
        } catch (PubSubException.NotAPubSubNodeException e) {
            e.printStackTrace();
        }
    }


}
