package transmitter.shoof.co.shooflibrary.scanner;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import transmitter.shoof.co.shooflibrary.ShoofAdvertiseListener;
import transmitter.shoof.co.shooflibrary.mqtt.PahoMqttClient;

public class ShoofScanner {

    //MQTT client
    private  MqttAndroidClient client;
    private String TAG = "ShoofScanner";
    private  PahoMqttClient pahoMqttClient;

    //Discover
    private  BluetoothLeScanner mBluetoothLeScanner;
    private  Handler mHandler = new Handler();
    private  List<ScanFilter> filters = new ArrayList<>();


    //Shoof Listener
    private  ShoofAdvertiseListener mShoofAdvertiseListener;

    //lazy instance of scanner class
    private  ShoofScanner instance;


    //private constructor to make singleton pattern
    public ShoofScanner(ShoofAdvertiseListener shoofAdvertiseListener) {
        this.mShoofAdvertiseListener = shoofAdvertiseListener;
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }



    //init scanner class
    public  ShoofScanner getInstance(ShoofAdvertiseListener shoofAdvertiseListener) {
        if (instance == null) {
            instance = new ShoofScanner(shoofAdvertiseListener);
            mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

        }
        return instance;
    }

    /**
     * Method will connect to mqtt broker
     * @param context Application context
     * @param BROKER_URL URL of BROKER
     * @param CLIENT_ID Client id
     */
    public  void initMqttServer(Context context, String BROKER_URL, String CLIENT_ID, List<String> topics, String username, String pass, MqttConnectOptions mqttConnectOptions,String upTopic,ShoofAdvertiseListener shoofAdvertiseListener){
        pahoMqttClient = new PahoMqttClient();
        client=pahoMqttClient.getMqttClient(context,BROKER_URL,CLIENT_ID,topics,username,pass, mqttConnectOptions,upTopic,shoofAdvertiseListener);

    }


    /**
     * Call method to send data to mqtt server
     * @param msg string object to send data on mqtt server
     * @param topic topic name to send data on mqtt server
     */
    public  void sendMqttData(String msg,String topic){
        try{
            pahoMqttClient.publishMessage(client, msg, 1, topic);

        }catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Scan callback for client
    private  ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (mShoofAdvertiseListener != null) {
                mShoofAdvertiseListener.onScanResult(callbackType, result);
            }
            super.onScanResult(callbackType, result);

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            if (mShoofAdvertiseListener != null) {
                mShoofAdvertiseListener.onBatchResult(results);
            }
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            if (mShoofAdvertiseListener != null) {
                mShoofAdvertiseListener.onScanError(errorCode);
            }
            super.onScanFailed(errorCode);
        }
    };

    /**
     * Init scanner for scan
     *
     * @param time millisecond time ,after that scan will be terminated
     */
    public  void initScanner(int time) {
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

    }

    /**
     * Call function when want to listen only specific bluetooth devices
     *
     * @param uuidList
     */
    public  void addUUIDToListen(List<String> uuidList) {
        for (int i = 0; i < uuidList.size(); i++) {
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(UUID.fromString(uuidList.get(i))))
                    .build();
            filters.add(filter);
        }
    }

    public  boolean checkBluetooth(){
        boolean isError=false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mShoofAdvertiseListener.onBluetoothError(1);
            isError=true;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
               mShoofAdvertiseListener.onBluetoothError(2);
               isError=true;
            }
        }
        return isError;
    }

    /**
     * Will start listen the
     */
    public  void startScan() {


       /* if(checkBluetooth()){
            return;
        }*/

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        if (mScanCallback != null)
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        else
            System.out.println( "Scan listener not initialized");
    }



    /**
     * Call when client want to stop listening the bluetooth devices
     */
    public  void stopScanning() {
        if (mBluetoothLeScanner != null && mScanCallback != null)
            mBluetoothLeScanner.stopScan(mScanCallback);
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
