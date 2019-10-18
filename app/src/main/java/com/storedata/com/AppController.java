package com.storedata.com;

import android.app.Application;
import android.util.Log;

import com.couchbase.lite.android.AndroidContext;
import com.storedata.com.createnote.CouchDbController;
import com.storedata.com.utility.DeviceUuidFactory;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by ${3embed} on ${27-10-2017}.
 * Banglore
 */

public class AppController extends Application {
    private static CouchDbController db;
    private static AppController mInstance;
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private String deviceId;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        db = new CouchDbController(new AndroidContext(this));

        deviceId = DeviceUuidFactory.getInstance().getDeviceUuid(this);


         createMQttConnection("kishore",true);


    }

    public Observable <List<Note>> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<List<Note>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Note>> emitter) throws Exception {

                    if (!emitter.isDisposed()) {
                        emitter.onNext( notes);
                    }

                // all notes are emitted
                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Buy tooth paste!"));
        notes.add(new Note(2, "Call brother!"));
        notes.add(new Note(3, "Watch Narcos tonight!"));
        notes.add(new Note(4, "Pay power bill!"));
        return notes;
    }




    /**
     * @param clientId is same as the userId
     */
    @SuppressWarnings("unchecked")
    public MqttAndroidClient createMQttConnection(String clientId, boolean notFromJobScheduler) {
        clientId = "yelo_" + clientId + deviceId;
        Log.d("case21", "Yes called" + " " + clientId);
        if (mqttAndroidClient == null || mqttConnectOptions == null) {
            String serverUri = "tcp://" +"13.210.239.31" + ":" + "1883";
            mqttAndroidClient = new MqttAndroidClient(mInstance, serverUri, clientId);
            mqttAndroidClient.setCallback(initMqttListener());
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setUserName("swapyaa");
            String password = "YbwaUY3Wjv5rF479vh";
            mqttConnectOptions.setPassword(password.toCharArray());
            mqttConnectOptions.setAutomaticReconnect(true);

        }



        if (notFromJobScheduler) {
            connectMqttClient();
        }

        return mqttAndroidClient;

        /*
     * Has been removed from here to avoid the reace condition for the mqtt
      * connection with the mqtt broker.
     */
        //connectMqttClient();
    }
    /*
    * Connect mqtt client*/
    public void connectMqttClient() {
        Log.d("cas21", "Yes called");
        try {
            Log.d("exe", "mqttAndroidClient" + mqttAndroidClient);

            mqttAndroidClient.connect(mqttConnectOptions, mInstance, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("exe", "connected success"+asyncActionToken.getResponse());


                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("log57", "Failed to connect to: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            Log.d("cas57", "Yes called" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * initialization of the MQtt call back.
     */
    private MqttCallback initMqttListener() {
//        MqttCallbackExtended mlistener=new MqttCallbackExtended() {
        MqttCallback mlistener = new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
              ///  handelconnectionLost(cause);
                Log.d("exe","cause"+cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                //handdleArrivedmessage(topic, message);
                Log.d("exe","topic"+topic+"message"+message);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
               // handledeliveryComplete(token);
            }
        };
        return mlistener;
    }








    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public  CouchDbController  getCounchDatabase(){
        return db;
    }
}
