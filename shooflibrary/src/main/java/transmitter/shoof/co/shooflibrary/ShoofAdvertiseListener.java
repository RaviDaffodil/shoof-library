package transmitter.shoof.co.shooflibrary;

import android.bluetooth.le.ScanResult;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;

public interface ShoofAdvertiseListener {

    //scan result
     void onScanResult(int callBackType, ScanResult scanResult);

    //on batch result
     void onBatchResult(List<ScanResult> results);

    /**
     * Will be call when scan error occured
     * @param errorCode error
     */
     void onScanError(int errorCode);

    /**
     * will be called when bluetooth error occures
     * @param errorMessage
     */
    void onBluetoothError(int errorCode);

    void connectionLost();

    void messageArrived(String s, MqttMessage mqttMessage);

    void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken);


}
