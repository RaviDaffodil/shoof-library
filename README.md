# Shoof Library

Shoof Library is based on Bluetooth peripherals and MQTT Server.

  - Connect to MQTT Broker
  - Listen Specific Bluetooth peripherals
  - Send Advertising data to MQTT Broker


### Installation

1. Include dependency in your app level build.gradle file.

```sh
compile 'com.github.RaviDaffodil:shoof-library:1.1'
```

2. Include dependency in your project level build.gradle file.

```sh
 maven { url 'https://jitpack.io' }
```

3. Add following permissions in your Manifeast file

```sh
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET"/>
```

4. Add MQTT Service in your Manifeast File:
```sh
  <service android:name="org.eclipse.paho.android.service.MqttService" />
```

5. Implement ShoofAdvertiseListener in your Activity:
```sh
  public class Demo extends AppCompatActivity implements ShoofAdvertiseListener 
```

6. Add Following code in your onCreate() Method :
```sh
   //Init scanner
        ShoofScanner.getInstance(this);
    //init mqtt server
        ShoofScanner.initMqttServer(this,Constant.MQTT_BROKER_URL,Constant.CLIENT_ID,topicList,Constant.USER_ID,Constant.PASSWORD);
        
   //add list of bluetooth device uuids to listen from
        ShoofScanner.addUUIDToListen(uuidList);
        
    //start listening from bluetooth devices
        ShoofScanner.startScan();
  
  <!--uuidList is list of bluetooth perepherals uuid's which need to be listen-->
  
  <!--topicList is list of topics which need to be subscribe on MQTT Broker-->
```

7. Add Following code in your onDestroy() Method :
```sh
  //Stop scanning
        ShoofScanner.stopScanning();
  
```

8. To send data on MQTT Broker , use following code in onScanResult():
```sh
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
  
```

License
----

MIT


**Free Software, Hell Yeah!**


