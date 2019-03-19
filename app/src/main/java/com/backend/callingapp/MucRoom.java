package com.backend.callingapp;

import android.util.Log;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatException;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.ParticipantStatusListener;
import org.jivesoftware.smackx.muc.SubjectUpdatedListener;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

class MucRoom implements MessageListener, InvitationRejectionListener, PresenceListener, ParticipantStatusListener, SubjectUpdatedListener {

    private final XMPPBOSHConnection mConnection;
    private final String mRoomName;
    private final String TAG = MucRoom.class.getSimpleName();

    MucRoom(XMPPBOSHConnection connection, String roomName) {
        this.mConnection = connection;
        this.mRoomName = roomName;
    }

    void createRoom() throws XmppStringprepException, InterruptedException, SmackException.NoResponseException, SmackException.NotConnectedException, XMPPException.XMPPErrorException, MultiUserChatException.NotAMucServiceException {
        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(mConnection);
        MultiUserChat mucUserChat = mucManager.getMultiUserChat((EntityBareJid) this.mConnection.getUser().asBareJid());

        mucUserChat.createOrJoinIfNecessary(Resourcepart.from(mRoomName),null);

        // room is now created by locked
        Form form = mucUserChat.getConfigurationForm();
        Form answerForm = form.createAnswerForm();
        answerForm.setAnswer("muc#roomconfig_persistentroom", true);
        mucUserChat.sendConfigurationForm(answerForm);
        // sending the configuration form unlocks the room

        mucUserChat.addMessageListener(this);
        mucUserChat.addInvitationRejectionListener(this);
        mucUserChat.addParticipantListener(this);
        mucUserChat.addParticipantStatusListener(this);
        mucUserChat.addSubjectUpdatedListener(this);
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
