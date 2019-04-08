/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber.extensions.jingle;

import java.util.*;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleReason;
import org.jxmpp.jid.Jid;

/**
 * A utility class containing methods for creating {@link Jingle}
 * instances for various situations.
 *
 * @author Emil Ivov
 */
public class JinglePacketFactory {
    /**
     * Creates a {@link Jingle} <tt>session-info</tt> packet carrying a
     * <tt>ringing</tt> payload.
     *
     * @param sessionInitiate the {@link Jingle} that established the session
     *                        which the response is going to belong to.
     * @return a {@link Jingle} <tt>session-info</tt> packet carrying a
     * <tt>ringing</tt> payload.
     */
    public static Jingle createRinging(Jingle sessionInitiate) {
        return createSessionInfo(sessionInitiate.getTo(),
                sessionInitiate.getFrom(),
                sessionInitiate.getSid(),
                SessionInfoType.ringing);
    }

    /**
     * Creates a {@link Jingle} <tt>session-info</tt> packet carrying a
     * the specified payload type.
     *
     * @param from our full jid
     * @param to   their full jid
     * @param sid  the ID of the Jingle session this IQ will belong to.
     * @return a {@link Jingle} <tt>session-info</tt> packet carrying a
     * the specified payload type.
     */
    public static Jingle createSessionInfo(Jid from,
                                           Jid to,
                                           String sid) {
        Jingle sessionInfo = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.session_info)
                .build();

        sessionInfo.setFrom(from);
        sessionInfo.setTo(to);
        sessionInfo.setType(IQ.Type.set);

        return sessionInfo;
    }

    /**
     * Creates a {@link Jingle} <tt>session-info</tt> packet carrying a
     * the specified payload type.
     *
     * @param from our full jid
     * @param to   their full jid
     * @param sid  the ID of the Jingle session this IQ will belong to.
     * @param type the exact type (e.g. ringing, hold, mute) of the session
     *             info IQ.
     * @return a {@link Jingle} <tt>session-info</tt> packet carrying a
     * the specified payload type.
     */
    public static Jingle createSessionInfo(Jid from,
                                           Jid to,
                                           String sid,
                                           SessionInfoType type) {
        Jingle ringing = createSessionInfo(from, to, sid);
        SessionInfoPacketExtension sessionInfoType
                = new SessionInfoPacketExtension(type);

        ringing.addExtension(sessionInfoType);

        return ringing;
    }

    /**
     * Creates a {@link Jingle} <tt>session-terminate</tt> packet carrying a
     * {@link Reason#BUSY} payload.
     *
     * @param from our JID
     * @param to   the destination JID
     * @param sid  the ID of the Jingle session that this message will be
     *             terminating.
     * @return a {@link Jingle} <tt>session-terminate</tt> packet.
     */
    public static Jingle createBusy(Jid from, Jid to, String sid) {
        return createSessionTerminate(from, to, sid, JingleReason.Busy);
    }

    /**
     * Creates a {@link Jingle} <tt>session-terminate</tt> packet that is
     * meant to terminate an on-going, well established session (similar to a SIP
     * BYE request).
     *
     * @param from our JID
     * @param to   the destination JID
     * @param sid  the ID of the Jingle session that this message will be
     *             terminating.
     * @return a {@link Jingle} <tt>session-terminate</tt> packet
     * .
     */
    public static Jingle createBye(Jid from, Jid to, String sid) {
        return createSessionTerminate(from, to, sid, JingleReason.Success);
    }

    /**
     * Creates a {@link Jingle} <tt>session-terminate</tt> packet that is
     * meant to terminate a not yet established session.
     *
     * @param from our JID
     * @param to   the destination JID
     * @param sid  the ID of the Jingle session that this message will be
     *             terminating.
     * @return a {@link Jingle} <tt>session-terminate</tt> packet
     * .
     */
    public static Jingle createCancel(Jid from, Jid to, String sid) {
        return createSessionTerminate(from, to, sid, JingleReason.Cancel);
    }

    /**
     * Creates a {@link Jingle} <tt>session-terminate</tt> packet with the
     * specified src, dst, sid, and reason.
     *
     * @param from   our JID
     * @param to     the destination JID
     * @param sid    the ID of the Jingle session that this message will be
     *               terminating.
     * @param reason the reason for the termination
     * @return the newly constructed {@link Jingle} <tt>session-terminate</tt>
     * packet.
     * .
     */
    public static Jingle createSessionTerminate(Jid from,
                                                Jid to,
                                                String sid,
                                                JingleReason reason) {

        Jingle terminate = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.session_terminate)
                .setReason(reason)
                .build();

        terminate.setTo(to);
        terminate.setFrom(from);
        terminate.setType(IQ.Type.set);

        return terminate;
    }

    /**
     * Creates a {@link Jingle} <tt>session-accept</tt> packet with the
     * specified <tt>from</tt>, <tt>to</tt>, <tt>sid</tt>, and <tt>content</tt>.
     * Given our role in a conversation, we would assume that the <tt>from</tt>
     * value should also be used for the value of the Jingle <tt>responder</tt>.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>session-accept</tt>
     * packet.
     */
    public static Jingle createSessionAccept(
            Jid from,
            Jid to,
            String sid,
            Iterable<ContentPacketExtension> contentList) {
        Jingle sessionAccept = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.session_accept)
                .setResponder(from.asFullJidIfPossible())
                .build();

        sessionAccept.setTo(to);
        sessionAccept.setFrom(from);
        sessionAccept.setType(IQ.Type.set);
        

        for (ContentPacketExtension content : contentList)
            sessionAccept.addExtension(content);

        return sessionAccept;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>session-initiate</tt> action.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>session-initiate</tt>
     * packet.
     */
    public static Jingle createSessionInitiate(
            Jid from,
            Jid to,
            String sid,
            List<ContentPacketExtension> contentList) {
        Jingle sessionInitiate = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.session_initiate)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        sessionInitiate.setTo(to);
        sessionInitiate.setFrom(from);
       
        sessionInitiate.setType(IQ.Type.set);
        

        for (ContentPacketExtension content : contentList) {
            sessionInitiate.addExtension(content);
        }

        return sessionInitiate;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>content-add</tt> action.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>content-add</tt>
     * packet.
     */
    public static Jingle createContentAdd(
           Jid from,
           Jid to,
            String sid,
            List<ContentPacketExtension> contentList) {
        Jingle contentAdd = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.content_add)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        contentAdd.setTo(to);
        contentAdd.setFrom(from);
        contentAdd.setType(IQ.Type.set);
        

        for (ContentPacketExtension content : contentList)
            contentAdd.addExtension(content);

        return contentAdd;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>content-accept</tt> action.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>content-accept</tt>
     * packet.
     */
    public static Jingle createContentAccept(
           Jid from,
           Jid to,
            String sid,
            Iterable<ContentPacketExtension> contentList) {
        Jingle contentAccept = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.content_accept)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        contentAccept.setTo(to);
        contentAccept.setFrom(from);
        contentAccept.setType(IQ.Type.set);


        for (ContentPacketExtension content : contentList)
            contentAccept.addExtension(content);

        return contentAccept;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>content-reject</tt> action.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>content-reject</tt>
     * packet.
     */
    public static Jingle createContentReject(
           Jid from,
           Jid to,
            String sid,
            Iterable<ContentPacketExtension> contentList) {
        Jingle contentReject = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.content_reject)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        contentReject.setTo(to);
        contentReject.setFrom(from);
        contentReject.setType(IQ.Type.set);


        if (contentList != null) {
            for (ContentPacketExtension content : contentList)
                contentReject.addExtension(content);
        }

        return contentReject;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>content-modify</tt> action.
     *
     * @param from    our JID
     * @param to      the destination JID
     * @param sid     the ID of the Jingle session that this message will be
     *                terminating.
     * @param content the content element containing media and transport
     *                description.
     * @return the newly constructed {@link Jingle} <tt>content-modify</tt>
     * packet.
     */
    public static Jingle createContentModify(
           Jid from,
           Jid to,
            String sid,
            ContentPacketExtension content) {
        Jingle contentModify = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.content_modify)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        contentModify.setTo(to);
        contentModify.setFrom(from);
        contentModify.setType(IQ.Type.set);


        contentModify.addExtension(content);

        return contentModify;
    }

    /**
     * Creates a new {@link Jingle} with the <tt>content-remove</tt> action.
     *
     * @param from        our JID
     * @param to          the destination JID
     * @param sid         the ID of the Jingle session that this message will be
     *                    terminating.
     * @param contentList the content elements containing media and transport
     *                    descriptions.
     * @return the newly constructed {@link Jingle} <tt>content-remove</tt>
     * packet.
     */
    public static Jingle createContentRemove(
           Jid from,
           Jid to,
            String sid,
            Iterable<ContentPacketExtension> contentList) {
        Jingle contentRemove = Jingle.getBuilder()
                .setSessionId(sid)
                .setAction(JingleAction.content_remove)
                .setInitiator(from.asFullJidIfPossible())
                .build();

        contentRemove.setTo(to);
        contentRemove.setFrom(from);
        contentRemove.setType(IQ.Type.set);


        for (ContentPacketExtension content : contentList)
            contentRemove.addExtension(content);

        return contentRemove;
    }
}
