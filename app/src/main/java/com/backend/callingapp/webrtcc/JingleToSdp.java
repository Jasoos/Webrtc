package com.backend.callingapp.webrtcc;

import android.util.Log;

import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentDescription;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransport;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransportCandidate;

import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.jivesoftware.smackx.jingle.element.JingleContentTransportCandidate;
import org.webrtc.SessionDescription;

import java.util.ArrayList;

public class JingleToSdp {

    private static final String NL = "\r\n";

    /**
     * Tag used for Android logging
     */

    private static final String TAG = "JingleToSdp";

    public static SessionDescription toSdp(Jingle iq,
                                           SessionDescription.Type type) {
        StringBuilder sb = new StringBuilder();

        sb.append("v=0").append(NL);
        sb.append("o=- 1923518516 2 IN IP4 0.0.0.0").append(NL);
        sb.append("s=-").append(NL);
        sb.append("t=0 0").append(NL);
        sb.append("a=group:BUNDLE audio video data").append(NL);

        for (JingleContent cpe : iq.getContents()) {
            if (cpe.getAttributeNames().size() == 1) {
                // FIXME: duplicate empty ContentPacketExtensions
                continue;
            }

            if (!"data".equals(cpe.getName())) {
            appendMLine(cpe, sb);
            } else {
                appendSCTPLines(cpe, sb);
            }
        }

        return new SessionDescription(type, sb.toString());
    }

    private void candidateToIceCandidate(JingleICEContentTransportCandidate candidate) {
        StringBuilder sb = new StringBuilder();
        sb.append(candidate.getFoundation()).append(NL);
        sb.append(candidate.getComponent()).append(NL);
        sb.append(candidate.getProtocol()).append(NL);
        sb.append(candidate.getProtocol()).append(NL);
    }

    private static void appendMLine(JingleContent cpe, StringBuilder sb) {
        if (cpe.getDescription() != null) {
            JingleContentDescription description
                    = cpe.getDescription();


        DtlsFingerprintPacketExtension dtls
                = transport.getFirstChildOfType(DtlsFingerprintPacketExtension.class);

            sb.append("m=").append(cpe.getName()).append(" 1 RTP/SAVPF");
            if (description.getJingleContentDescriptionChildren() != null) {
                for (NamedElement pt : description.getJingleContentDescriptionChildren()) {
                    sb.append(" ").append(pt.getElementName());
                }
            }
        }
        JingleICEContentTransport transport
                = (JingleICEContentTransport) cpe.getTransport();
        sb.append(NL);

        sb.append("c=IN IP4 0.0.0.0").append(NL);
        sb.append("a=rtcp:1 IN IP4 0.0.0.0").append(NL);

        sb.append("a=ice-ufrag:").append(transport.getUfrag()).append(NL);
        sb.append("a=ice-pwd:").append(transport.getPwd()).append(NL);

        sb.append("a=fingerprint:").append(transport.getFingerprint().getHash()).append(' ').append(transport.getFingerprint()).append(NL);
        sb.append("a=sendrecv").append(NL);
        sb.append("a=mid:").append(cpe.getName()).append(NL); // XXX cpe.getName or description.getMedia()?
        sb.append("a=rtcp-mux").append(NL);

        for (NamedElement pt : description.getJingleContentDescriptionChildren()) {
            sb.append("a=rtpmap:").append(pt.getID()).append(' ').append(pt.getName()).append('/').append(pt.getClockrate());
            if (pt.getChannels() != 1)
                sb.append('/').append(pt.getChannels());
            sb.append(NL);

            for (ParameterPacketExtension ppe : pt.getParameters())
                sb.append("a=fmtp:").append(pt.getID()).append(' ').append(ppe.getName()).append('=').append(ppe.getValue()).append(NL);

            for (RtcpFbPacketExtension rtcpFb
                    : pt.getRtcpFeedbackTypeList()) {
                sb.append("a=rtcp-fb:").append(pt.getID())
                        .append(" ").append(rtcpFb.getFeedbackType());
                if (!StringUtils.isNullOrEmpty(rtcpFb.getFeedbackSubtype())) {
                    sb.append(" ").append(rtcpFb.getFeedbackSubtype());
                }
                sb.append(NL);
            }
        }

        for (RTPHdrExtPacketExtension ext : description.getExtmapList()) {
            sb.append("a=extmap:").append(ext.getID()).append(' ').append(ext.getURI()).append(NL);
        }

        for (SourcePacketExtension ssrc
                : description.getChildExtensionsOfType(
                SourcePacketExtension.class)) {
            long ssrcL = ssrc.getSSRC();
            for (ParameterPacketExtension param : ssrc.getParameters()) {
                sb.append("a=ssrc:").append(ssrcL).append(" ")
                        .append(param.getName())
                        .append(":").append(param.getValue()).append(NL);
            }
        }
        ArrayList<JingleICEContentTransportCandidate> iceCand = new ArrayList<>();
        for (JingleContentTransportCandidate cand : transport.getCandidates()) {
            iceCand.add((JingleICEContentTransportCandidate) cand);
        }


        for (JingleICEContentTransportCandidate candidate : iceCand) {
            sb.append("a=candidate:").append(candidate.getFoundation()).append(' ').append(candidate.getComponent());
            sb.append(' ').append(candidate.getProtocol()).append(' ').append(candidate.getPriority());
            sb.append(' ').append(candidate.getIp()).append(' ').append(candidate.getPort()).append(" typ ");
            sb.append(candidate.getType().toString()).append(" generation ").append(candidate.getGeneration());
            sb.append(NL);
        }
    }

    private static void appendSCTPLines(ContentPacketExtension cpe,
                                        StringBuilder sb) {
        String contentName = cpe.getName();
        RtpDescriptionPacketExtension rdpe
                = cpe.getFirstChildOfType(RtpDescriptionPacketExtension.class);
        if (rdpe == null) {
            Log.d(TAG, "No RtpDescPacketExtension");
            return;
        }

        IceUdpTransportPacketExtension transport
                = cpe.getFirstChildOfType(IceUdpTransportPacketExtension.class);
        if (transport == null) {
            Log.d(TAG, "No ICE packet extension");
            return;
        }

        SctpMapExtension sctpMapExtension
                = transport.getFirstChildOfType(SctpMapExtension.class);
        if (sctpMapExtension == null) {
            Log.d(TAG, "No SctpMap packet extension");
            return;
        }
        int sctpPort = sctpMapExtension.getPort();
        // m=application 1 DTLS/SCTP 5000
        sb.append("m=").append(rdpe.getMedia()).append(" 1 DTLS/SCTP ")
                .append(sctpPort).append(NL);
        // a=sctpmap:5000 webrtc-datachannel 1024
        sb.append("a=sctpmap:").append(sctpPort)
                .append(" ").append(sctpMapExtension.getProtocol())
                .append(" ").append(sctpMapExtension.getStreams()).append(NL);

        // c=IN IP4 0.0.0.0 //FIXME: what is it for ?
        //sb.append("c=IN IP4 0.0.0.0");

        DtlsFingerprintPacketExtension dtls
                = transport.getFirstChildOfType(DtlsFingerprintPacketExtension.class);

        sb.append("a=ice-ufrag:").append(transport.getUfrag()).append(NL);
        sb.append("a=ice-pwd:").append(transport.getPassword()).append(NL);

        sb.append("a=fingerprint:").append(dtls.getHash()).append(' ').append(dtls.getFingerprint()).append(NL);
        sb.append("a=sendrecv").append(NL);
        sb.append("a=mid:").append(contentName).append(NL); // XXX cpe.getName or description.getMedia()?
        sb.append("a=rtcp-mux").append(NL);

        for (CandidatePacketExtension candidate : transport.getCandidateList()) {
            sb.append("a=candidate:").append(candidate.getFoundation()).append(' ').append(candidate.getComponent());
            sb.append(' ').append(candidate.getProtocol()).append(' ').append(candidate.getPriority());
            sb.append(' ').append(candidate.getIP()).append(' ').append(candidate.getPort()).append(" typ ");
            sb.append(candidate.getType().toString()).append(" generation ").append(candidate.getGeneration());
            sb.append(NL);
        }
    }

}
