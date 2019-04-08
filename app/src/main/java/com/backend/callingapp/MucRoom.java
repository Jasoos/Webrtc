package com.backend.callingapp;

import android.content.Context;
import android.util.Log;

import com.backend.callingapp.jingleExtension.ice.JingleICESessionHandler;
import com.backend.callingapp.jingleExtension.ice.JingleICETransportManager;
import com.backend.callingapp.jingleExtension.JingleManagerExt;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransport;
import com.backend.callingapp.webrtcc.JingleToSdp;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PresenceListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.jingle.JingleHandler;
import org.jivesoftware.smackx.jingle.JingleUtil;
import org.jivesoftware.smackx.jingle.Role;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;
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
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.LinkedList;
import java.util.List;


class MucRoom implements MessageListener, InvitationRejectionListener, PresenceListener, ParticipantStatusListener, SubjectUpdatedListener, JingleHandler {

    private final XMPPBOSHConnection mConnection;
    private final String TAG = MucRoom.class.getSimpleName();
    private MultiUserChatManager mMucManager;
    private MultiUserChat mMultiUserChat;
    private JingleManagerExt mJingleManager;
    private JingleUtil mJingleUtil;
    private Context context;
    PeerConnection peerConnection;
    SDPObserver sdpObserver;
    MediaConstraints sdpMediaConstraints;
    PeerConnectionFactory peer;
    SessionDescription sessionDescription;

    public SessionDescription getSessionDescription() {
        return sessionDescription;
    }

    public void setSessionDescription(SessionDescription sessionDescription) {
        this.sessionDescription = sessionDescription;
    }

    MucRoom(XMPPBOSHConnection connection, Context context) {
        this.mConnection = connection;
        this.context = context;
        mMucManager = MultiUserChatManager.getInstanceFor(mConnection);
        mMucManager.setAutoJoinOnReconnect(true);
    }

    void createRoom(String roomName) {

        try {

            mJingleManager = JingleManagerExt.getInstanceFor(mConnection);
            mJingleManager.registerDescriptionHandler(Jingle.NAMESPACE, this);
            mJingleUtil = new JingleUtil(mConnection);

            mMultiUserChat = mMucManager.getMultiUserChat(JidCreate.from(roomName + "@conference.meet.jit.si").asEntityBareJidIfPossible());
            mMultiUserChat.createOrJoinIfNecessary(Resourcepart.from(mConnection.getUser().asEntityBareJidString()), "");

            if (!mMultiUserChat.isJoined()) {
                mMultiUserChat.join(Resourcepart.from(roomName + "@conference.meet.jit.si"));
            }

            mMultiUserChat.addMessageListener(this);
            mMultiUserChat.addInvitationRejectionListener(this);
            mMultiUserChat.addParticipantListener(this);
            mMultiUserChat.addParticipantStatusListener(this);
            mMultiUserChat.addSubjectUpdatedListener(this);

            PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions());
            peer = PeerConnectionFactory.builder().createPeerConnectionFactory();
            createPeerConnection();

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
        if (peerConnection != null) {
//            peerConnection.setRemoteDescription(sdpObserver, getSessionDescription());
//            peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
        }
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

    @Override
    public IQ handleJingleRequest(Jingle jingle) {
        JingleICESessionHandler jingleIceSessionHandler = new JingleICESessionHandler(jingle.getInitiator(),
                mConnection.getUser().asFullJidIfPossible(), Role.responder, jingle.getSid(), mConnection);

        JingleTransportSession<JingleICEContentTransport> transportSessionn = JingleICETransportManager.getInstanceFor(mConnection).transportSession(jingleIceSessionHandler);
        jingleIceSessionHandler.setTransportSession(transportSessionn);

        if (jingle.getAction() == JingleAction.transport_info) {
            SessionDescription sessionDescription = JingleToSdp.toSdp(jingle, SessionDescription.Type.OFFER);
            peerConnection.setRemoteDescription(sdpObserver, sessionDescription);
            peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
        }
//      peerConnection.addIceCandidate()
        return jingleIceSessionHandler.handleJingleSessionRequest(jingle);
        //return IQ.createResultIQ(jingle);
    }

    private void createPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new LinkedList<>();
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                peerConnection.addIceCandidate(iceCandidate);
                Log.d(TAG, "onIceCandidate: ");
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: ");
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

            }
        };
        String AUDIO_TRACK_ID = "ARDAMSa0";
        peerConnection = peer.createPeerConnection(rtcConfig, pcObserver);
        // Create audio constraints.
        MediaConstraints audioConstraints = new MediaConstraints();
        // added for audio performance measurements
//        if (peerConnectionParameters.noAudioProcessing) {
//            Log.d(TAG, "Disabling audio processing");
//            audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
//            audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
//            audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
//            audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
//                    AUDIO_NOISE_SUPPRESSION_CONSTRAINT , "false"));
//        } else {
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googNoiseSupression", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googNoisesuppression2", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googEchoCancellation2", "true"));
        audioConstraints.optional.add(
                new MediaConstraints.KeyValuePair("googAutoGainControl2", "true"));
//        }
        MediaStream audioMediaStream = peer.createLocalMediaStream("ARDAMS");
        audioMediaStream.addTrack(peer.createAudioTrack(
                AUDIO_TRACK_ID,
                peer.createAudioSource(audioConstraints)));
        peerConnection.addStream(audioMediaStream);
        LinkedList<IceCandidate> iceCandidates = new LinkedList<>();
        for (IceCandidate iceCandidate : iceCandidates) {
            peerConnection.addIceCandidate(iceCandidate);
        }
        sdpObserver = new SDPObserver();
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
    }

    private class SDPObserver implements SdpObserver {
        @Override
        public void onCreateSuccess(final SessionDescription origSdp) {
            Log.d(TAG, "onCreateSuccess: ");
            peerConnection.setLocalDescription(sdpObserver, origSdp);
            setSessionDescription(origSdp);
//            peerConnection.setRemoteDescription(sdpObserver, origSdp);

//            peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
//            if (localSdp != null) {
//                reportError("Multiple SDP create.");
//                return;
//            }
//            String sdpDescription = origSdp.description;
//            if (preferIsac) {
//                sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
//            }
//            if (videoCallEnabled && preferH264) {
//                sdpDescription = preferCodec(sdpDescription, VIDEO_CODEC_H264, false);
//            }
//            final SessionDescription sdp = new SessionDescription(
//                    origSdp.type, sdpDescription);
//            localSdp = sdp;
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    if (peerConnection != null && !isError) {
//                        Log.d(TAG, "Set local SDP from " + sdp.type);
//                        peerConnection.setLocalDescription(sdpObserver, sdp);
//                    }
        }

        @Override
        public void onSetSuccess() {
            Log.d(TAG, "onSetSuccess: ");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.d(TAG, "onCreateFailure: ");
        }

        @Override
        public void onSetFailure(String s) {
            Log.d(TAG, "onSetFailure: ");
        }
    }


}

