package com.backend.callingapp.jingleExtension.ice;

import com.backend.callingapp.jingleExtension.JingleProviderExt;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentDescription;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentTransport;
import com.backend.callingapp.jingleExtension.ice.providers.JingleICEContentDescriptionProvider;
import com.backend.callingapp.jingleExtension.ice.providers.JingleICEContentTransportProvider;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.element.Jingle;
import org.jivesoftware.smackx.jingle.provider.JingleContentProviderManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportManager;
import org.jivesoftware.smackx.jingle.transports.JingleTransportSession;

import java.util.WeakHashMap;

public class JingleICETransportManager extends JingleTransportManager<JingleICEContentTransport> {

    private static final WeakHashMap<XMPPConnection, JingleICETransportManager> INSTANCES = new WeakHashMap<>();

    public JingleICETransportManager(XMPPConnection connection) {
        super(connection);

        IQProvider<IQ> provider = ProviderManager.getIQProvider(Jingle.ELEMENT, Jingle.NAMESPACE);
        ProviderManager.getIQProviders().remove(provider);
        ProviderManager.addIQProvider(Jingle.ELEMENT, Jingle.NAMESPACE, new JingleProviderExt());
        JingleContentProviderManager.addJingleContentDescriptionProvider(JingleICEContentDescription.NAMESPACE,new JingleICEContentDescriptionProvider());
        JingleContentProviderManager.addJingleContentTransportProvider(getNamespace(), new JingleICEContentTransportProvider());
    }

    public static JingleICETransportManager getInstanceFor(XMPPConnection connection) {
        JingleICETransportManager manager = INSTANCES.get(connection);
        if (manager == null) {
            manager = new JingleICETransportManager(connection);
            INSTANCES.put(connection, manager);
        }
        return manager;
    }

    @Override
    public String getNamespace() {
        return JingleICEContentTransport.NAMESPACE;
    }

    @Override
    public JingleTransportSession<JingleICEContentTransport> transportSession(JingleSession jingleSession) {
        return new JingleICETransportSession(jingleSession);
    }
}
