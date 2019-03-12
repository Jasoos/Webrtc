package com.backend.callingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.bosh.BOSHConfiguration;
import org.jivesoftware.smack.bosh.XMPPBOSHConnection;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.net.InetAddress;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    public String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        HostnameVerifier verifier = new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return false;
                            }
                        };

//                        DomainBareJid serviceName = JidCreate.domainBareFrom("meet.jit.si");
                        InetAddress addr = InetAddress.getByName("meet.jit.si");
//                        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
//                                .setXmppDomain("//meet.jit.si/http-bind/http-pre-bind")
//                                .setHost("//meet.jit.si/http-bind/http-pre-bind")
//                                .setPort(443)
//                                .build();
                        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                                .setPort(443)
                                .setHost("meet.jit.si")
                                .setXmppDomain("https://meet.jit.si/http-bind")
                                .setHostAddress(addr)
                                .setSendPresence(false)
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .setSocketFactory(SSLSocketFactory.getDefault())
                                .setConnectTimeout(5000)
                                .build();

//                        BOSHConfiguration config = BOSHConfiguration.builder()
////                                .setUsernameAndPassword("ba38c7e7-7c94-4336-b1e7-95229380e37c@meet.jit.si/395dc039-2622-4a08-b627-d9f927b70786", "")
//                                .useHttps()
//                                .setHost("meet.jit.si")
//                                .setPort(443)
//                                .setFile("/http-bind/")
//                                .setXmppDomain("https://meet.jit.si")
//                                .setSendPresence(false)
//                                .setSocketFactory(SSLSocketFactory.getDefault())
//                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                                .build();
//                        SASLAuthentication.registerSASLMechanism(new SASLPlainMechanism());
                        AbstractXMPPConnection conn2 = new XMPPTCPConnection(config);
                        conn2.addConnectionListener(new ConnectionListener() {
                            @Override
                            public void connected(XMPPConnection connection) {
                                Log.d(TAG, "connected: ");
                            }

                            @Override
                            public void authenticated(XMPPConnection connection, boolean resumed) {
                                Log.d(TAG, "authenticated: ");
                            }

                            @Override
                            public void connectionClosed() {
                                Log.d(TAG, "connectionClosed: ");
                            }

                            @Override
                            public void connectionClosedOnError(Exception e) {
                                Log.d(TAG, "connectionClosedOnError: ");
                            }


                        });
                        conn2.addAsyncStanzaListener(new StanzaListener() {
                            @Override
                            public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException, SmackException.NotLoggedInException {
                                Log.d(TAG, "processStanza: " + packet);
                            }
                        }, new StanzaFilter() {
                            @Override
                            public boolean accept(Stanza stanza) {
                                return true;
                            }
                        });
                        conn2.connect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}
