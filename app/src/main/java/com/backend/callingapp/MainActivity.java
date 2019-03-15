package com.backend.callingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.igniterealtime.jbosh.BodyQName;
import org.igniterealtime.jbosh.ComposableBody;
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
import org.jivesoftware.smack.packet.Element;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.roster.PresenceEventListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.bytestreams.ibb.provider.DataPacketProvider;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.net.InetAddress;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public String TAG = MainActivity.class.getSimpleName();
    String mRoomName = "muzammil";
    ConvoServicesInterface.PreBindResult preBuildRes = null;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private Callback<ConvoServicesInterface.PreBindResult> preBindResponseReceiver = new Callback<ConvoServicesInterface.PreBindResult>() {
        @Override
        public void onResponse(Call<ConvoServicesInterface.PreBindResult> call, Response<ConvoServicesInterface.PreBindResult> response) {
            MainActivity.this.preBuildRes = response.body();
            getConferenceMapping();
        }

        @Override
        public void onFailure(Call<ConvoServicesInterface.PreBindResult> call, Throwable t) {
            Log.d(TAG, "onFailure: ");
            //call.enqueue(preBindResponseReceiver);
        }
    };

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://meet.jit.si/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ConvoServicesInterface service = retrofit.create(ConvoServicesInterface.class);

    Retrofit retrofitApi = new Retrofit.Builder()
            .baseUrl("https://api.jitsi.net/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    ConvoServicesInterface serviceApi = retrofitApi.create(ConvoServicesInterface.class);
    private ConvoServicesInterface.MapperResponse mMapperResponse = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Call<ConvoServicesInterface.PreBindResult> call = service.getPreBindForRoom(mRoomName);
        call.enqueue(preBindResponseReceiver);

        xmppConnection();
    }

    private void getConferenceMapping() {
        Call<ConvoServicesInterface.MapperResponse> call = serviceApi.getConferenceMapper(mRoomName + "@conference.meet.jit.si");
        call.enqueue(new Callback<ConvoServicesInterface.MapperResponse>() {
            @Override
            public void onResponse(Call<ConvoServicesInterface.MapperResponse> call, Response<ConvoServicesInterface.MapperResponse> response) {
                Log.d(TAG, "onResponse: ");
                MainActivity.this.mMapperResponse = response.body();
            }

            @Override
            public void onFailure(Call<ConvoServicesInterface.MapperResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    public void xmppConnection(){
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {


                        BOSHConfiguration config = BOSHConfiguration.builder()
                                .useHttps()
                                .setHost("meet.jit.si")
                                .setPort(443)
                                .setFile("/http-pre-bind?room="+mRoomName)
                                .setXmppDomain("https://meet.jit.si")
                                .setSendPresence(false)
                                .setSocketFactory(SSLSocketFactory.getDefault())
                                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                                .build();

                        final AbstractXMPPConnection conn2 = new XMPPBOSHConnection(config);

                        conn2.addConnectionListener(new ConnectionListener() {
                            @Override
                            public void connected(XMPPConnection connection) {
                                Log.d(TAG, "connected: ");

                                ComposableBody composableBody = ComposableBody.builder()
                                        .setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind", "sid"), preBuildRes.sid)
                                        .setAttribute(BodyQName.create("http://jabber.org/protocol/httpbind","rid"), preBuildRes.rid)
                                        .build();


                                try {
                                    TestIQ iq = new TestIQ("services","urn:xmpp:extdisco:1");
                                    iq.setTo(JidCreate.from("meet.jit.si"));
                                    iq.setType(IQ.Type.get);

                                    TestIQ iq2 = new TestIQ(new DiscoverInfo());
                                    iq2.setFrom(JidCreate.from(preBuildRes.jid));
                                    iq2.setTo(JidCreate.from("meet.jit.si"));
                                    iq2.setType(IQ.Type.get);
                                    composableBody = composableBody.rebuild().setPayloadXML(iq.toXML("").toString() + iq2.toXML("").toString()).build();

                                    conn2.sendNonza(new TestElement(composableBody.toXML()));
                                    Log.d(TAG, "connected: ");
                                } catch (XmppStringprepException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
                            public void processStanza(Stanza packet) {
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
