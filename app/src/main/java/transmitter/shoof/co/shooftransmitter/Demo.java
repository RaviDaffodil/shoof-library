package transmitter.shoof.co.shooftransmitter;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.List;

import transmitter.shoof.co.shooflibrary.ShoofAdvertiseListener;
import transmitter.shoof.co.shooflibrary.scanner.ShoofScanner;

public class Demo extends AppCompatActivity implements ShoofAdvertiseListener {

    //list of uuid to listen
    List<String> uuidList=new ArrayList<>();
    List<String> mTopics=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uuidList.add("de3ef22b-5e41-46fe-a517-f7af5f4ba681");
        mTopics.add("st/downstream");

        //Init scanner
        ShoofScanner.getInstance(this);




    }


    @Override
    protected void onResume() {
        super.onResume();
        //init mqtt server
        ShoofScanner.initMqttServer(this,Constant.MQTT_BROKER_URL,Constant.CLIENT_ID,uuidList,Constant.USER_ID,Constant.PASSWORD);

        //add list of bluetooth device uuids to listen from
        ShoofScanner.addUUIDToListen(uuidList);

        //start listening from bluetooth devices
        ShoofScanner.startScan();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Stop scanning
        ShoofScanner.stopScanning();
    }

    @Override
    public void onScanResult(int callBackType, ScanResult scanResult) {

        if( scanResult == null
                || scanResult.getDevice() == null
                || TextUtils.isEmpty(scanResult.getDevice().getName()) )
            return;

        String data=scanResult.toString();

        //Send Data to MQTT SERVER
        ShoofScanner.sendMqttData(data,"");

    }

    @Override
    public void onBatchResult(List<ScanResult> results) {

    }

    @Override
    public void onScanError(int errorCode) {


        // int errorCode = 1; Fails to start scan as BLE scan with the same settings is already started by the app.
        // int errorCode = 2; Fails to start scan as app cannot be registered.
        // int errorCode = 3; Fails to start scan due an internal error
        // int errorCode = 4; Fails to start power optimized scan as this feature is not supported.
        // int errorCode = 5; Fails to start scan as it is out of hardware resources.
        // int errorCode = 0; No Error.

    }

    @Override
    public void onBluetoothError(String errorMessage) {
        Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
    }
}
