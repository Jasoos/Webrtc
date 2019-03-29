package com.backend.callingapp.jingleExtension.ice.providers;

import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentDescription;
import com.backend.callingapp.jingleExtension.ice.elements.JingleICEContentDescriptionBuilder;

import org.jivesoftware.smack.packet.StandardExtensionElement;
import org.jivesoftware.smack.parsing.StandardExtensionElementProvider;
import org.jivesoftware.smackx.jingle.provider.JingleContentDescriptionProvider;
import org.xmlpull.v1.XmlPullParser;

public class JingleICEContentDescriptionProvider extends JingleContentDescriptionProvider<JingleICEContentDescription> {
    @Override
    public JingleICEContentDescription parse(XmlPullParser parser, int initialDepth) throws Exception {
        StandardExtensionElement standardExtensionElement = StandardExtensionElementProvider.INSTANCE.parse(parser);

        return new JingleICEContentDescriptionBuilder()
                .setMedia(standardExtensionElement.getAttributeValue("media"))
                .setPayloads(standardExtensionElement.getElements())
                .createJingleContentDescriptionExt();
    }
}
