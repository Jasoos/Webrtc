package com.backend.callingapp;

import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

class MucRoom implements MessageListener, InvitationRejectionListener, PresenceListener, ParticipantStatusListener, SubjectUpdatedListener {

    private final XMPPBOSHConnection mConnection;
    private final String TAG = MucRoom.class.getSimpleName();
    private MultiUserChatManager mMucManager;
    private MultiUserChat mMultiUserChat;

    MucRoom(XMPPBOSHConnection connection) {
        this.mConnection = connection;
        mMucManager = MultiUserChatManager.getInstanceFor(mConnection);
        mMucManager.setAutoJoinOnReconnect(true);
    }

    void createRoom(String roomName) {

        try {
            mMultiUserChat = mMucManager.getMultiUserChat(JidCreate.from(roomName + "@conference.meet.jit.si").asEntityBareJidIfPossible());
            mMultiUserChat.createOrJoinIfNecessary(Resourcepart.from(mConnection.getUser().asEntityBareJidString()), "");

            if (!mMultiUserChat.isJoined()){
                mMultiUserChat.join(Resourcepart.from(roomName + "@conference.meet.jit.si"));
            }

            mMultiUserChat.addMessageListener(this);
            mMultiUserChat.addInvitationRejectionListener(this);
            mMultiUserChat.addParticipantListener(this);
            mMultiUserChat.addParticipantStatusListener(this);
            mMultiUserChat.addSubjectUpdatedListener(this);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (MultiUserChatException.NotAMucServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processMessage(Message message) {
       Log.d(TAG, "processMessage: ");
    }

    @Override
    public void invitationDeclined(EntityBareJid invitee, String reason, Message message, MUCUser.Decline rejection) {
       Log.d(TAG, "invitationDeclined: ");
    }

    @Override
    public void processPresence(Presence presence) {
       Log.d(TAG, "processPresence: ");
    }

    @Override
    public void joined(EntityFullJid participant) {
       Log.d(TAG, "joined: ");
    }

    @Override
    public void left(EntityFullJid participant) {
       Log.d(TAG, "left: ");
    }

    @Override
    public void kicked(EntityFullJid participant, Jid actor, String reason) {
       Log.d(TAG, "kicked: ");
    }

    @Override
    public void voiceGranted(EntityFullJid participant) {
       Log.d(TAG, "voiceGranted: ");
    }

    @Override
    public void voiceRevoked(EntityFullJid participant) {
       Log.d(TAG, "voiceRevoked: ");
    }

    @Override
    public void banned(EntityFullJid participant, Jid actor, String reason) {
       Log.d(TAG, "banned: ");
    }

    @Override
    public void membershipGranted(EntityFullJid participant) {
       Log.d(TAG, "membershipGranted: ");
    }

    @Override
    public void membershipRevoked(EntityFullJid participant) {
       Log.d(TAG, "membershipRevoked: ");
    }

    @Override
    public void moderatorGranted(EntityFullJid participant) {
       Log.d(TAG, "moderatorGranted: ");
    }

    @Override
    public void moderatorRevoked(EntityFullJid participant) {
       Log.d(TAG, "moderatorRevoked: ");
    }

    @Override
    public void ownershipGranted(EntityFullJid participant) {
       Log.d(TAG, "ownershipGranted: ");
    }

    @Override
    public void ownershipRevoked(EntityFullJid participant) {
       Log.d(TAG, "ownershipRevoked: ");
    }

    @Override
    public void adminGranted(EntityFullJid participant) {
       Log.d(TAG, "adminGranted: ");
    }

    @Override
    public void adminRevoked(EntityFullJid participant) {
       Log.d(TAG, "adminRevoked: ");
    }

    @Override
    public void nicknameChanged(EntityFullJid participant, Resourcepart newNickname) {
       Log.d(TAG, "nicknameChanged: ");
    }

    @Override
    public void subjectUpdated(String subject, EntityFullJid from) {
       Log.d(TAG, "subjectUpdated: ");
    }
}
