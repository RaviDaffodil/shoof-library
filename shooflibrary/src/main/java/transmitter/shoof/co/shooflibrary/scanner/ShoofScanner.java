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
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
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
    private static MqttAndroidClient client;
    private String TAG = "ShoofScanner";
    private static PahoMqttClient pahoMqttClient;

    //Discover
    private static BluetoothLeScanner mBluetoothLeScanner;
    private static Handler mHandler = new Handler();
    private static List<ScanFilter> filters = new ArrayList<>();

    //Shoof Listener
    private static ShoofAdvertiseListener mShoofAdvertiseListener;

    //lazy instance of scanner class
    private static ShoofScanner instance;


    //private constructor to make singleton pattern
    private ShoofScanner(ShoofAdvertiseListener shoofAdvertiseListener) {
        this.mShoofAdvertiseListener = shoofAdvertiseListener;
    }

    //init scanner class
    public static ShoofScanner getInstance(ShoofAdvertiseListener shoofAdvertiseListener) {
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
    public static void initMqttServer(Context context,String BROKER_URL,String CLIENT_ID,List<String> topics,String username,String pass){
        pahoMqttClient = new PahoMqttClient();
        client=pahoMqttClient.getMqttClient(context,BROKER_URL,CLIENT_ID,topics,username,pass);

    }


    /**
     * Call method to send data to mqtt server
     * @param msg string object to send data on mqtt server
     * @param topic topic name to send data on mqtt server
     */
    public static void sendMqttData(String msg,String topic){
        try{
            pahoMqttClient.publishMessage(client, msg, 1, topic);

        }catch (MqttException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Scan callback for client
    private static ScanCallback mScanCallback = new ScanCallback() {
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
    public static void initScanner(int time) {
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();

    }

    /**
     * Call function when want to listen only specific bluetooth devices
     *
     * @param uuidList
     */
    public static void addUUIDToListen(List<String> uuidList) {
        for (int i = 0; i < uuidList.size(); i++) {
            ScanFilter filter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(UUID.fromString(uuidList.get(i))))
                    .build();
            filters.add(filter);
        }
    }

    /**
     * Will start listen the
     */
    public static void startScan() {
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        if (mScanCallback != null)
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        else
            Log.e("Scan listener error", "Scan listener not initialized");
    }



    /**
     * Call when client want to stop listening the bluetooth devices
     */
    public static void stopScanning() {
        if (mBluetoothLeScanner != null && mScanCallback != null)
            mBluetoothLeScanner.stopScan(mScanCallback);
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}
