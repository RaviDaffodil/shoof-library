package transmitter.shoof.co.shooflibrary;

import android.bluetooth.le.ScanResult;

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
    void onBluetoothError(String errorMessage);


}
