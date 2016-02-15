package io.skyway.testpeerjava;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
//import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.skyway.Peer.Browser.Canvas;
import io.skyway.Peer.Browser.MediaConstraints;
import io.skyway.Peer.Browser.MediaStream;
import io.skyway.Peer.Browser.Navigator;
import io.skyway.Peer.CallOption;
import io.skyway.Peer.MediaConnection;
import io.skyway.Peer.OnCallback;
import io.skyway.Peer.Peer;
import io.skyway.Peer.PeerError;
import io.skyway.Peer.PeerOption;

import com.memetix.mst.language.Language;
import com.memetix.mst.language.SpokenDialect;
import com.memetix.mst.speak.Speak;
import com.memetix.mst.translate.Translate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
//    private LocationSourceImpl mLocationSource = null;
    private boolean mDoFollow = true;

    private static final String TAG = MapsActivity.class.getSimpleName();

    private Peer _peer;
    private MediaConnection _media;

    private MediaStream _msLocal;
    private MediaStream _msRemote;

    private Handler _handler;

    private String   _id;
    private String[] _listPeerIds;
    private boolean  _bCalling;

    private static final int REQUEST_CODE = 0;
    private String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        _handler = new Handler(Looper.getMainLooper());
        Context context = getApplicationContext();
        //////////////////////////////////////////////////////////////////////
        //////////////////  START: Initialize SkyWay Peer ////////////////////
        //////////////////////////////////////////////////////////////////////

        // Please check this page. >> https://skyway.io/ds/
        PeerOption options = new PeerOption();

        //Enter your API Key.
        options.key = "3ed6a716-e6c7-4702-b82c-10aca7b38ab9";
        //Enter your registered Domain.
        options.domain = "Android";

        // SKWPeer has many options. Please check the document. >> http://nttcom.github.io/skyway/docs/
        _peer = new Peer(context, options);
        setPeerCallback(_peer);

        //////////////////////////////////////////////////////////////////////
        ////////////////// END: Initialize SkyWay Peer ///////////////////////
        //////////////////////////////////////////////////////////////////////


        //////////////////////////////////////////////////////////////////////
        ////////////////// START: Get Local Stream   /////////////////////////
        //////////////////////////////////////////////////////////////////////
        Navigator.initialize(_peer);
        MediaConstraints constraints = new MediaConstraints();
        _msLocal = Navigator.getUserMedia(constraints);

        Canvas canvas = (Canvas) findViewById(R.id.svSecondary);
        canvas.addSrc(_msLocal, 0);

        //////////////////////////////////////////////////////////////////////
        //////////////////// END: Get Local Stream   /////////////////////////
        //////////////////////////////////////////////////////////////////////

        _bCalling = false;


        //
        // Initialize views
        //
        Button btnAction = (Button) findViewById(R.id.btnAction);
        btnAction.setEnabled(true);
        btnAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setEnabled(false);

                if (!_bCalling)
                {
                    listingPeers();
                }
                else
                {
                    closing();
                }

                v.setEnabled(true);
            }
        });

        Button btnVoice = (Button) findViewById(R.id.btnVoice);
        btnVoice.setEnabled(true);
        btnVoice.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try {
                    // インテント作成
                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
                    intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(
                            RecognizerIntent.EXTRA_PROMPT,
                            "Translation"); // お好きな文字に変更できます

                    // インテント発行
                    startActivityForResult(intent, REQUEST_CODE);
                } catch (ActivityNotFoundException e) {
                    // このインテントに応答できるアクティビティがインストールされていない場合
//                    Toast.makeText(VoiceRecognitionTestActivity.this,
//                            "ActivityNotFoundException", Toast.LENGTH_LONG).show();
                }
            }
        });


        Button btnVoiceStop = (Button) findViewById(R.id.btnVoiceStop);
        btnVoiceStop.setEnabled(true);
        btnVoiceStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.setEnabled(false);
                sendGeo();
                v.setEnabled(true);
            }
        });

        _msLocal.switchCamera();
    }

    private void sendGeo() {
        LatLng tokyo = new LatLng(35.658581,139.745433);

        AsyncTask<Void, Void, Void> task0 = new AsyncTask<Void, Void, Void>() {
            HttpResponse response = null;
            @Override
            protected Void doInBackground(Void... paramss) {

                HttpURLConnection con = null;
//                URL url = null;
                String urlSt = "https://20151121ubuntu.cloudapp.net:8448/_matrix/client/api/v1/rooms/!kMPhYGcwRkJvDklDdp:20151121ubuntu.cloudapp.net/send/m.room.message?access_token=MDAyOWxvY2F0aW9uIDIwMTUxMTIxdWJ1bnR1LmNsb3VkYXBwLm5ldAowMDEzaWRlbnRpZmllciBrZXkKMDAxMGNpZCBnZW4gPSAxCjAwM2ZjaWQgdXNlcl9pZCA9IEB0YWRoYWNrMjAxNnRlc3Q6MjAxNTExMjF1YnVudHUuY2xvdWRhcHAubmV0CjAwMTZjaWQgdHlwZSA9IGFjY2VzcwowMDFkY2lkIHRpbWUgPCAxNDU1NDI2NDI4NjY3CjAwMmZzaWduYXR1cmUgxr3YWKsR6zD0M_JqHz5FIyb_0O24r5HRmr0krYH3wHQK";

                Map<String, Object> jsonValues = new HashMap<>();
                jsonValues.put("msgtype", "m.text");
                jsonValues.put("body", "100");
                JSONObject json = new JSONObject(jsonValues);

                DefaultHttpClient httpClient = new DefaultHttpClient();

//                HttpParams params = new BasicHttpParams();
//                SchemeRegistry registry = new SchemeRegistry();
//                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));


                org.apache.http.conn.ssl.SSLSocketFactory sf = (org.apache.http.conn.ssl.SSLSocketFactory) httpClient.getConnectionManager()
                        .getSchemeRegistry().getScheme("https").getSocketFactory();
                sf.setHostnameVerifier(new AllowAllHostnameVerifier());

//                SchemeRegistry schemeRegistry = new SchemeRegistry();
//                registry.register(new Scheme("https", 8443, sslSf));
//                registry.register(new Scheme("https", 443, sslSf));

//                SSLSocketFactory sslSocketFactory = SSLSocketFactory.getSocketFactory();
//                // ホスト名の検証を行わない。
//                sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//                registry.register(new Scheme("https", sslSocketFactory, 443));
//                registry.register(new Scheme("https", sslSocketFactory, 8448));

//                ThreadSafeClientConnManager clientConnManager = new ThreadSafeClientConnManager(params, registry);
//                HttpClient httpClient = new DefaultHttpClient(clientConnManager , params);
//                DefaultHttpClient client = new DefaultHttpClient(clientConnManager , params);

                HttpPost post = new HttpPost(urlSt);

                AbstractHttpEntity entity = null;
                try {
                    entity = new ByteArrayEntity(json.toString().getBytes("UTF8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(entity);
                try {
                    response = httpClient.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d("GEO", urlSt);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (response != null) {
                    Log.d("GEO", response.toString());
                } else {
                    Log.d("GEO", "null!!!");
                }
            }
        };
        task0.execute();
    }

    // アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String resultsString = "";

            // 結果文字列リスト
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

//            for (int i = 0; i< results.size(); i++) {
//                // ここでは、文字列が複数あった場合に結合しています
//                resultsString += results.get(i);
//            }

            res = results.get(0);

            AsyncTask<Void, Void, Void> task1 = new AsyncTask<Void, Void, Void>(){
                String translatedText = null;
                @Override
                protected Void doInBackground(Void... params) {

                    Translate.setClientId("tad2016");
                    Translate.setClientSecret("n+c88QCb5WqsID86yHC0tFK5Wtr5vlBeXBxYTJRxn9k=");

                    try {
                        translatedText = Translate.execute(res, Language.JAPANESE, Language.ENGLISH);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
                @Override
                protected void onPostExecute(Void result){
                    Log.d("VOICE", translatedText);

                    AsyncTask<Void, Void, Void> task2 = new AsyncTask<Void, Void, Void>(){
                        String url = null;
                        @Override
                        protected Void doInBackground(Void... params) {

                            Speak.setClientId("tad2016");
                            Speak.setClientSecret("n+c88QCb5WqsID86yHC0tFK5Wtr5vlBeXBxYTJRxn9k=");

                            try {
                                url = Speak.execute(translatedText, SpokenDialect.ENGLISH_UNITED_KINGDOM);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return null;
                        }
                        @Override
                        protected void onPostExecute(Void result){
//                            Log.d("VOICE", url);
                            //リソースファイルから再生
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(url);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            mediaPlayer.prepareAsync();

                        }
                    };
                    task2.execute();
                }
            };
            task1.execute();


            // トーストを使って結果を表示
            Toast.makeText(this, res, Toast.LENGTH_LONG).show();
//            Toast.makeText(this, translatedText, Toast.LENGTH_LONG).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Media connecting to remote peer.
     * @param strPeerId Remote peer.
     */
    void calling(String strPeerId)
    {
        //////////////////////////////////////////////////////////////////////
        ////////////////// START: Calling SkyWay Peer   //////////////////////
        //////////////////////////////////////////////////////////////////////

        if (null == _peer)
        {
            return;
        }

        if (null != _media)
        {
            _media.close();
            _media = null;
        }

        CallOption option = new CallOption();

        _media = _peer.call(strPeerId, _msLocal, option);

        if (null != _media)
        {
            setMediaCallback(_media);

            _bCalling = true;
        }

        //////////////////////////////////////////////////////////////////////
        /////////////////// END: Calling SkyWay Peer   ///////////////////////
        //////////////////////////////////////////////////////////////////////


        updateUI();
    }
    //////////Start:Set Peer callback////////////////
    ////////////////////////////////////////////////
    private void setPeerCallback(Peer peer)
    {
        //////////////////////////////////////////////////////////////////////////////////
        ///////////////////// START: Set SkyWay peer callback   //////////////////////////
        //////////////////////////////////////////////////////////////////////////////////

        // !!!: Event/Open
        peer.on(Peer.PeerEventEnum.OPEN, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                Log.d(TAG, "[On/Open]");

                if (object instanceof String) {
                    _id = (String) object;
                    Log.d(TAG, "ID:" + _id);

                    updateUI();
                }
            }
        });

        // !!!: Event/Call
        peer.on(Peer.PeerEventEnum.CALL, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                Log.d(TAG, "[On/Call]");
                if (!(object instanceof MediaConnection)) {
                    return;
                }

                _media = (MediaConnection) object;

                _media.answer(_msLocal);

                setMediaCallback(_media);

                _bCalling = true;

                updateUI();
            }
        });

        // !!!: Event/Close
        peer.on(Peer.PeerEventEnum.CLOSE, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                Log.d(TAG, "[On/Close]");
            }
        });

        // !!!: Event/Disconnected
        peer.on(Peer.PeerEventEnum.DISCONNECTED, new OnCallback()
        {
            @Override
            public void onCallback(Object object)
            {
                Log.d(TAG, "[On/Disconnected]");
            }
        });

        // !!!: Event/Error
        peer.on(Peer.PeerEventEnum.ERROR, new OnCallback()
        {
            @Override
            public void onCallback(Object object)
            {
                PeerError error = (PeerError) object;

                Log.d(TAG, "[On/Error]" + error);

                String strMessage = "" + error;
                String strLabel = getString(android.R.string.ok);

                MessageDialogFragment dialog = new MessageDialogFragment();
                dialog.setPositiveLabel(strLabel);
                dialog.setMessage(strMessage);

                dialog.show(getFragmentManager(), "error");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////
        /////////////////////// END: Set SkyWay peer callback   //////////////////////////
        //////////////////////////////////////////////////////////////////////////////////
    }


    //Unset peer callback
    void unsetPeerCallback(Peer peer)
    {
        peer.on(Peer.PeerEventEnum.OPEN, null);
        peer.on(Peer.PeerEventEnum.CONNECTION, null);
        peer.on(Peer.PeerEventEnum.CALL, null);
        peer.on(Peer.PeerEventEnum.CLOSE, null);
        peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
        peer.on(Peer.PeerEventEnum.ERROR, null);
    }


    void setMediaCallback(MediaConnection media) {
        //////////////////////////////////////////////////////////////////////////////////
        //////////////  START: Set SkyWay peer Media connection callback   ///////////////
        //////////////////////////////////////////////////////////////////////////////////

        // !!!: MediaEvent/Stream
        media.on(MediaConnection.MediaEventEnum.STREAM, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                _msRemote = (MediaStream) object;

                Canvas canvas = (Canvas) findViewById(R.id.svPrimary);
                canvas.addSrc(_msRemote, 0);
            }
        });

        // !!!: MediaEvent/Close
        media.on(MediaConnection.MediaEventEnum.CLOSE, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                if (null == _msRemote) {
                    return;
                }

                Canvas canvas = (Canvas) findViewById(R.id.svPrimary);
                canvas.removeSrc(_msRemote, 0);

                _msRemote = null;

                _media = null;
                _bCalling = false;

                updateUI();
            }
        });

        // !!!: MediaEvent/Error
        media.on(MediaConnection.MediaEventEnum.ERROR, new OnCallback() {
            @Override
            public void onCallback(Object object) {
                PeerError error = (PeerError) object;

                Log.d(TAG, "[On/MediaError]" + error);

                String strMessage = "" + error;
                String strLabel = getString(android.R.string.ok);

                MessageDialogFragment dialog = new MessageDialogFragment();
                dialog.setPositiveLabel(strLabel);
                dialog.setMessage(strMessage);

                dialog.show(getFragmentManager(), "error");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////
        ///////////////  END: Set SkyWay peer Media connection callback   ////////////////
        //////////////////////////////////////////////////////////////////////////////////
    }

        //Unset media connection event callback.
    void unsetMediaCallback(MediaConnection media)
    {
        media.on(MediaConnection.MediaEventEnum.STREAM, null);
        media.on(MediaConnection.MediaEventEnum.CLOSE, null);
        media.on(MediaConnection.MediaEventEnum.ERROR, null);
    }

    // Listing all peers
    void listingPeers()
    {
        if ((null == _peer) || (null == _id) || (0 == _id.length()))
        {
            return;
        }

        _peer.listAllPeers(new OnCallback() {
            @Override
            public void onCallback(Object object) {
                if (!(object instanceof JSONArray)) {
                    return;
                }

                JSONArray peers = (JSONArray) object;

                StringBuilder sbItems = new StringBuilder();
                for (int i = 0; peers.length() > i; i++) {
                    String strValue = "";
                    try {
                        strValue = peers.getString(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (0 == _id.compareToIgnoreCase(strValue)) {
                        continue;
                    }

                    if (0 < sbItems.length()) {
                        sbItems.append(",");
                    }

                    sbItems.append(strValue);
                }

                String strItems = sbItems.toString();
                _listPeerIds = strItems.split(",");

                if ((null != _listPeerIds) && (0 < _listPeerIds.length)) {
                    selectingPeer();
                }
            }
        });

    }

    /**
     * Selecting peer
     */
    void selectingPeer()
    {
        if (null == _handler)
        {
            return;
        }

        _handler.post(new Runnable() {
            @Override
            public void run() {
                android.app.FragmentManager mgr = getFragmentManager();

                PeerListDialogFragment dialog = new PeerListDialogFragment();
                dialog.setListener(
                        new PeerListDialogFragment.PeerListDialogFragmentListener() {
                            @Override
                            public void onItemClick(final String item) {

                                _handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        calling(item);
                                    }
                                });
                            }
                        });
                dialog.setItems(_listPeerIds);

                dialog.show(mgr, "peerlist");
            }
        });
    }



    /**
     * Closing connection.
     */
    void closing()
    {
        if (false == _bCalling)
        {
            return;
        }

        _bCalling = false;

        if (null != _media)
        {
            _media.close();
        }
    }

    void updateUI()
    {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                Button btnAction = (Button) findViewById(R.id.btnAction);
                if (null != btnAction) {
                    if (false == _bCalling) {
                        btnAction.setText("Calling");
                    } else {
                        btnAction.setText("Hang up");
                    }
                }

                TextView tvOwnId = (TextView) findViewById(R.id.tvOwnId);
                if (null != tvOwnId) {
                    if (null == _id) {
                        tvOwnId.setText("");
                    } else {
                        tvOwnId.setText(_id);
                    }
                }
            }
        });
    }


    /**
     * Destroy Peer object.
     */
    private void destroyPeer()
    {
        closing();

        if (null != _msRemote)
        {
            Canvas canvas = (Canvas) findViewById(R.id.svPrimary);
            canvas.removeSrc(_msRemote, 0);

            _msRemote.close();

            _msRemote = null;
        }

        if (null != _msLocal)
        {
            Canvas canvas = (Canvas) findViewById(R.id.svSecondary);
            canvas.removeSrc(_msLocal, 0);

            _msLocal.close();

            _msLocal = null;
        }

        if (null != _media)
        {
            if (_media.isOpen)
            {
                _media.close();
            }

            unsetMediaCallback(_media);

            _media = null;
        }

        Navigator.terminate();

        if (null != _peer)
        {
            unsetPeerCallback(_peer);

            if (false == _peer.isDisconnected)
            {
                _peer.disconnect();
            }

            if (false == _peer.isDestroyed)
            {
                _peer.destroy();
            }

            _peer = null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Google MapのMyLocationレイヤーを使用可能にする
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);

        // Add a marker in Tokyo and move the camera
        LatLng tokyo = new LatLng(35.658581,139.745433);
//        mMap.addMarker(new MarkerOptions().position(tokyo).title("現在地"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tokyo));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {
                LatLng curr = new LatLng(loc.getLatitude(), loc.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));
                //Google Mapの Zoom値を指定
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });

//        LatLng position = new LatLng(0, 0);
//        MarkerOptions options = new MarkerOptions();
//        options.position(position);
//        options.title("title");
//        map.addMarker(options);
        }

}
