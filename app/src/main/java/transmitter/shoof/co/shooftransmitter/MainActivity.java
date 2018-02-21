package transmitter.shoof.co.shooftransmitter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Transmitter
    private TextView mText;
    private Button mAdvertiseButton;
    private Button mDiscoverButton;

    //Discover
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    private List<ScanFilter> filters=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById( R.id.text );
        mDiscoverButton = (Button) findViewById( R.id.discover_btn );
        mAdvertiseButton = (Button) findViewById( R.id.advertise_btn );

        mDiscoverButton.setOnClickListener( this );
        mAdvertiseButton.setOnClickListener( this );

        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Toast.makeText( this, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
            mAdvertiseButton.setEnabled( false );
            mDiscoverButton.setEnabled( false );
        }

        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 10000);

    }

    // Transmitter Code
    AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.e( "BLE", "Advertising success: " );
            super.onStartSuccess(settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
            switch (errorCode){
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    Log.e( "BLE", "ADVERTISE_FAILED_ALREADY_STARTED: " + errorCode );
                    break;
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    Log.e( "BLE", "ADVERTISE_FAILED_DATA_TOO_LARGE: " + errorCode );
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    Log.e( "BLE", "ADVERTISE_FAILED_FEATURE_UNSUPPORTED: " + errorCode );
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    Log.e( "BLE", "ADVERTISE_FAILED_INTERNAL_ERROR: " + errorCode );
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    Log.e( "BLE", "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS: " + errorCode );
                    break;

            }
            super.onStartFailure(errorCode);
        }
    };
    /*=====================================================================================================================*/

    //Discover code




    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if( result == null
                    || result.getDevice() == null
                    || TextUtils.isEmpty(result.getDevice().getName()) )
                return;

            StringBuilder builder = new StringBuilder( result.getDevice().getName() );

            builder.append("\n").append(new String(result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0)), Charset.forName("UTF-8")));

            mText.setText(builder.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {

            switch (errorCode){
                case SCAN_FAILED_ALREADY_STARTED:
                    Log.e( "BLE", "SCAN_FAILED_ALREADY_STARTED: " + errorCode );
                    break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    Log.e( "BLE", "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED: " + errorCode );
                    break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    Log.e( "BLE", "SCAN_FAILED_FEATURE_UNSUPPORTED: " + errorCode );
                    break;
                case SCAN_FAILED_INTERNAL_ERROR:
                    Log.e( "BLE", "SCAN_FAILED_INTERNAL_ERROR: " + errorCode );
                    break;

            }
            super.onScanFailed(errorCode);
        }
    };

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.discover_btn ) {
            discover();
        } else if( v.getId() == R.id.advertise_btn ) {
            advertise();
        }
    }

    private void discover() {
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid(UUID.fromString( getString(R.string.ble_uuid ) ) ) )
                .build();
        filters.add( filter );

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .build();

        mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

    }

    private void advertise() {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();

        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );
        AdvertiseData data = new AdvertiseData.Builder()
                .addServiceUuid( pUuid )
                .addServiceData( pUuid, "Data".getBytes( Charset.forName( "UTF-8" ) ) )
                .build();

        advertiser.startAdvertising( settings, data, advertisingCallback );
    }
    
    

}
