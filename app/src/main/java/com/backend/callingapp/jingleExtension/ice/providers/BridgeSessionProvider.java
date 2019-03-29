package com.backend.callingapp.jingleExtension.ice.providers;

import com.backend.callingapp.jingleExtension.ice.elements.BridgeSession;

import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static org.jivesoftware.smack.util.Async.ThrowingRunnable.LOGGER;

public class BridgeSessionProvider extends ExtensionElementProvider<BridgeSession> {

    @Override
    public BridgeSession parse(XmlPullParser parser, int initialDepth) {
        
        try{
            outerloop: while (true) {
                int eventType = parser.next();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tagName = parser.getName();
                        switch (tagName) {
                            
                            case BridgeSession.ELEMENT:
                                parser.next();

                                break;
                            default:
                                LOGGER.severe("Unknown BridgeSession element: " + tagName);
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getDepth() == initialDepth) {
                            break outerloop;
                        }
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new BridgeSession();
    }
}
