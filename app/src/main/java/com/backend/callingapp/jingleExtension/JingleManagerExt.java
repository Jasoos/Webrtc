package com.backend.callingapp.jingleExtension;

import com.backend.callingapp.jingleExtension.ice.JingleICETransportManager;
import com.backend.callingapp.jingleExtension.ice.JingleICETransportSession;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.iqrequest.AbstractIqRequestHandler;
import org.jivesoftware.smack.iqrequest.IQRequestHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.jingle.FullJidAndSessionId;
import org.jivesoftware.smackx.jingle.JingleHandler;
import org.jivesoftware.smackx.jingle.JingleSessionHandler;
import org.jivesoftware.smackx.jingle.JingleTransportMethodManager;
import org.jivesoftware.smackx.jingle.JingleUtil;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.element.JingleAction;
import org.jivesoftware.smackx.jingle.element.JingleContent;
import org.jivesoftware.smackx.jingle.element.JingleContentDescription;
import org.jivesoftware.smackx.jingle.transports.jingle_ibb.JingleIBBTransportManager;
import org.jivesoftware.smackx.jingle.transports.jingle_s5b.JingleS5BTransportManager;
import org.jxmpp.jid.FullJid;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JingleManagerExt extends Manager {

    private static final Logger LOGGER = Logger.getLogger(org.jivesoftware.smackx.jingle.JingleManager.class.getName());

    private static final Map<XMPPConnection, JingleManagerExt> INSTANCES = new WeakHashMap<>();

    private static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static ExecutorService getThreadPool() {
        return threadPool;
    }

    public static synchronized JingleManagerExt getInstanceFor(XMPPConnection connection) {
        JingleManagerExt jingleManager = INSTANCES.get(connection);
        if (jingleManager == null) {
            jingleManager = new JingleManagerExt(connection);
            INSTANCES.put(connection, jingleManager);
        }
        return jingleManager;
    }

    private final Map<String, JingleHandler> descriptionHandlers = new ConcurrentHashMap<>();

    private final Map<FullJidAndSessionId, JingleSessionHandler> jingleSessionHandlers = new ConcurrentHashMap<>();

    private final JingleUtil jutil;

    private JingleManagerExt(XMPPConnection connection) {
        super(connection);

        jutil = new JingleUtil(connection);

        connection.registerIQRequestHandler(
                new AbstractIqRequestHandler(Jingle.ELEMENT, Jingle.NAMESPACE, IQ.Type.set, IQRequestHandler.Mode.async) {
                    @Override
                    public IQ handleIQRequest(IQ iqRequest) {
                        final Jingle jingle = (Jingle) iqRequest;

                        FullJid fullFrom = jingle.getFrom().asFullJidOrThrow();
                        String sid = jingle.getSid();
                        FullJidAndSessionId fullJidAndSessionId = new FullJidAndSessionId(fullFrom, sid);

                        JingleSessionHandler sessionHandler = jingleSessionHandlers.get(fullJidAndSessionId);
                        if (sessionHandler != null) {
                            // Handle existing session
                            return sessionHandler.handleJingleSessionRequest(jingle);
                        }

                        if (jingle.getAction() == JingleAction.session_initiate || jingle.getAction() == JingleAction.transport_info) {

                            JingleHandler jingleDescriptionHandler = descriptionHandlers.get(Jingle.NAMESPACE);
                            /*if (jingle.getContents().isEmpty()){
                                jingleDescriptionHandler = descriptionHandlers.get(Jingle.NAMESPACE);
                            }else{
                                JingleContent content = jingle.getContents().get(0);
                                if (content.getDescription() == null && jingle.getAction() == JingleAction.transport_info){
                                    jingleDescriptionHandler = descriptionHandlers.get(Jingle.NAMESPACE);
                                }else{
                                    JingleContentDescription description = content.getDescription();

                                    jingleDescriptionHandler = descriptionHandlers.get(
                                            description.getNamespace());
                                }
                            }*/

                            if (jingleDescriptionHandler == null) {
                                // Unsupported Application
                                LOGGER.log(Level.WARNING, "Unsupported Jingle application.");
                                return jutil.createSessionTerminateUnsupportedApplications(fullFrom, sid);
                            }
                            return jingleDescriptionHandler.handleJingleRequest(jingle);
                        }
                        // Unknown session
                        LOGGER.log(Level.WARNING, "Unknown session.");
                        return jutil.createErrorUnknownSession(jingle);
                    }
                });
        // Register transports.
        JingleTransportMethodManager transportMethodManager = JingleTransportMethodManager.getInstanceFor(connection);
        transportMethodManager.registerTransportManager(JingleIBBTransportManager.getInstanceFor(connection));
        transportMethodManager.registerTransportManager(JingleS5BTransportManager.getInstanceFor(connection));
        transportMethodManager.registerTransportManager(JingleICETransportManager.getInstanceFor(connection));
    }

    public JingleHandler registerDescriptionHandler(String namespace, JingleHandler handler) {
        return descriptionHandlers.put(namespace, handler);
    }

    public JingleSessionHandler registerJingleSessionHandler(FullJid otherJid, String sessionId, JingleSessionHandler sessionHandler) {
        FullJidAndSessionId fullJidAndSessionId = new FullJidAndSessionId(otherJid, sessionId);
        return jingleSessionHandlers.put(fullJidAndSessionId, sessionHandler);
    }

    public JingleSessionHandler unregisterJingleSessionHandler(FullJid otherJid, String sessionId, JingleSessionHandler sessionHandler) {
        FullJidAndSessionId fullJidAndSessionId = new FullJidAndSessionId(otherJid, sessionId);
        return jingleSessionHandlers.remove(fullJidAndSessionId);
    }

    public static String randomId() {
        return StringUtils.randomString(24);
    }
}
