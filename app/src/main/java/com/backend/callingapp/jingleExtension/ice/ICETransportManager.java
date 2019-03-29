package com.backend.callingapp.jingleExtension.ice;

import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.ConnectionCreationListener;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPConnectionRegistry;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class ICETransportManager extends Manager {

    /**
     * Stanzas that can be used to encapsulate In-Band Bytestream data packets.
     */
    public enum StanzaType {

        /**
         * IQ stanza.
         */
        IQ,

        /**
         * Message stanza.
         */
        MESSAGE
    }

    /*
     * create a new ICETransportManager and register its shutdown listener on every established
     * connection
     */
    static {
        XMPPConnectionRegistry.addConnectionCreationListener(new ConnectionCreationListener() {
            @Override
            public void connectionCreated(final XMPPConnection connection) {
                // create the manager for this connection
                ICETransportManager.getICEStreamManager(connection);

                // register shutdown listener
                connection.addConnectionListener(new AbstractConnectionClosedListener() {

                    @Override
                    public void connectionTerminated() {
                        ICETransportManager.getICEStreamManager(connection).disableService();
                    }

                    @Override
                    public void connected(XMPPConnection connection) {
                        ICETransportManager.getICEStreamManager(connection);
                    }

                });

            }
        });
    }


    /**
     * Maximum block size that is allowed for In-Band Bytestreams.
     */
    public static final int MAXIMUM_BLOCK_SIZE = 65535;

    /* prefix used to generate session IDs */
    private static final String SESSION_ID_PREFIX = "jibb_";

    /* random generator to create session IDs */
    private static final Random randomGenerator = new Random();

    /* stores one ICETransportManager for each XMPP connection */
    private static final Map<XMPPConnection, ICETransportManager> managers = new WeakHashMap<>();

    /* block size used for new In-Band Bytestreams */
    private int defaultBlockSize = 4096;

    /* maximum block size allowed for this connection */
    private int maximumBlockSize = MAXIMUM_BLOCK_SIZE;

    /* the stanza used to send data packets */
    private ICETransportManager.StanzaType stanza = ICETransportManager.StanzaType.IQ;

    /*
     * list containing session IDs of In-Band Bytestream open packets that should be ignored by the
     * InitiationListener
     */
    private final List<String> ignoredBytestreamRequests = Collections.synchronizedList(new LinkedList<String>());



    public static ICETransportManager getICEStreamManager(XMPPConnection connection) {
        if (connection == null)
            return null;

        ICETransportManager manager = managers.get(connection);
        if (manager == null) {
            manager = new ICETransportManager(connection);
            managers.put(connection, manager);
        }
        return manager;
    }

    /**
     * Constructor.
     *
     * @param connection the XMPP connection
     */
    private ICETransportManager(XMPPConnection connection) {
        super(connection);
    }



    /**
     * Disables the ICETransportManager by removing its stanza listeners and resetting its
     * internal status, which includes removing this instance from the managers map.
     */
    private void disableService() {
        final XMPPConnection connection = connection();

        // remove manager from static managers map
        managers.remove(connection);

       this.ignoredBytestreamRequests.clear();

    }
}
