package com.backend.callingapp.webrtcc;


import android.content.Context;
import android.util.Log;


import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
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

public class RtcClient {

    public String TAG = RtcClient.class.getSimpleName();

    PeerConnection mPeerConnection;
    MediaConstraints mSdpMediaConstraints;
    PeerConnectionFactory mPeer;
    XMPPBOSHConnection mConnection;

    public RtcClient(XMPPBOSHConnection connection, Context context) {
        mConnection = connection;
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions());
        mPeer = PeerConnectionFactory
                .builder()
                .createPeerConnectionFactory();
    }

    public void createPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new LinkedList<>();

        PeerConnection.IceServer peerIceServer1 = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();
        PeerConnection.IceServer peerIceServer2 = PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer();
        PeerConnection.IceServer peerIceServer3 = PeerConnection.IceServer.builder("stun:stun2.l.google.com:19302").createIceServer();
        iceServers.add(peerIceServer1);
        iceServers.add(peerIceServer2);
        iceServers.add(peerIceServer3);

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        String AUDIO_TRACK_ID = "ARDAMSa0";
        mPeerConnection = mPeer.createPeerConnection(rtcConfig, mPCObserver);
        // Create audio constraints.
        MediaConstraints audioConstraints = new MediaConstraints();
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

        MediaStream audioMediaStream = mPeer.createLocalMediaStream("ARDAMS");
        audioMediaStream.addTrack(mPeer.createAudioTrack(
                AUDIO_TRACK_ID,
                mPeer.createAudioSource(audioConstraints)));
        mPeerConnection.addStream(audioMediaStream);
        LinkedList<IceCandidate> iceCandidates = new LinkedList<>();
        for (IceCandidate iceCandidate : iceCandidates) {
            mPeerConnection.addIceCandidate(iceCandidate);
        }
        mSdpMediaConstraints = new MediaConstraints();
        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        mPeerConnection.createOffer(mSdpObserver, mSdpMediaConstraints);
    }

    private SdpObserver mSdpObserver =  new SdpObserver() {
        @Override
        public void onCreateSuccess(final SessionDescription origSdp) {
            Log.d(TAG, "onCreateSuccess: ");
            mPeerConnection.setLocalDescription(mSdpObserver, origSdp);
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
    };

    PeerConnection.Observer mPCObserver = new PeerConnection.Observer() {
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
            mPeerConnection.addIceCandidate(iceCandidate);
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
            Log.d(TAG, "onAddTrack: ");
        }
    };
}
