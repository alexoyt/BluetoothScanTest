package com.gionee.ouyt.bluetooth.bluetoothscantest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class BluetoothTestActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "BluetoothTestActivity";
    private static int [] RANDOM_CLASS_INT = {5898764,7936};
    private EditText mEditText;

    private Constructor mDeviceConstructor;
    private Constructor mClassConstructor;
    private BroadcastReceiver mBroadcastReceiver;
    private ArrayList<BluetoothDevice> mBluetoothDeviceList;
    private Handler mSendBroadcastHandler;
    private Switch mUpdateSwitch;
    private boolean isHanding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        Button btn = (Button)findViewById(R.id.start_bluetooth_btn);
        btn.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.device_number);
        mUpdateSwitch = (Switch) findViewById(R.id.update_device);
        mUpdateSwitch.setChecked(true);
        initialBroadCast();
    }

    void initialBroadCast(){
        mBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                        &&mUpdateSwitch.isChecked()){
                    if(!isHanding){
                        setHandler(false);
                        isHanding = true;
                    }
                }
            }
        };
        registerReceiver(mBroadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_bluetooth_btn:
                startAmigoBluetooth();
                break;
        }
    }

    private BluetoothDevice getBluetoothDevice(Constructor constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return (BluetoothDevice) constructor.newInstance(randomAddress());
    }

    private BluetoothClass getBluetoothClass(Constructor constructor) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            return (BluetoothClass) constructor.newInstance(randomClassInt());
    }

    private String randomAddress()  {
        try {
            StringBuffer result = new StringBuffer();
            for(int i=0;i<12;i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
                if(i%2!=0&&i!=11){
                    result.append(":");
                }
            }
            return result.toString().toUpperCase();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int randomClassInt(){
        return RANDOM_CLASS_INT[new Random().nextInt(2)];
    }

    void initialConstructor(){
        try {
            Class bluetoothDeviceClass = Class.forName("android.bluetooth.BluetoothDevice");
            if(bluetoothDeviceClass != null){
                mDeviceConstructor = bluetoothDeviceClass.getDeclaredConstructor(String.class);
                mDeviceConstructor.setAccessible(true);
            }

            Class bluetoothClass = Class.forName("android.bluetooth.BluetoothClass");
            if(bluetoothClass != null){
                mClassConstructor = bluetoothClass.getDeclaredConstructor(int.class);
                mClassConstructor.setAccessible(true);
            }
        } catch (ClassNotFoundException e) {
            Log.e(TAG,"ClassNotFoundException");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG,"NoSuchMethodException");
            e.printStackTrace();
        }
    }

    void startAmigoBluetooth(){
        initialConstructor();
        if(!isHanding){
            setHandler(true);
            isHanding = true;
        }
        Intent intent = new Intent();
        intent.setClassName("com.gionee.bluetooth","com.gionee.bluetooth.BluetoothSettingsActivity");
        startActivity(intent);
    }

    void setHandler(final boolean start){
        if(mSendBroadcastHandler == null){
            mSendBroadcastHandler = new Handler();
        }
        int postTime;
        if(start){
            postTime = 5000;
        }
        else{
            postTime = 500;
        }
        mSendBroadcastHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mDeviceConstructor != null && mClassConstructor != null) {
                    int deviceNumber;
                    if(start) {
                        mBluetoothDeviceList = new ArrayList<>();
                        if (mEditText.getText().toString().trim().equals(""))
                            return;
                        deviceNumber = Integer.parseInt(mEditText.getText().toString().trim());
                        if (deviceNumber <= 0)
                            return;
                    }else{
                        deviceNumber = mBluetoothDeviceList.size();
                    }
                    for(int i=0;i<deviceNumber;i++) {
                        try {
                            BluetoothDevice bluetoothDevice;
                            BluetoothClass bluetoothClass;
                            if(start){
                                bluetoothDevice = getBluetoothDevice(mDeviceConstructor);
                            }else{
                                bluetoothDevice = mBluetoothDeviceList.get(i);
                            }
                            bluetoothClass = getBluetoothClass(mClassConstructor);
                            if(start){
                                mBluetoothDeviceList.add(bluetoothDevice);
                            }
                            Intent deviceIntent = new Intent(BluetoothDevice.ACTION_FOUND);
                            deviceIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, bluetoothDevice);
                            deviceIntent.putExtra(BluetoothDevice.EXTRA_CLASS,
                                    bluetoothClass);
                            short randomRSSI = (short)(new Random().nextInt(34)-98);
                            deviceIntent.putExtra(BluetoothDevice.EXTRA_RSSI, randomRSSI);
                            deviceIntent.putExtra(BluetoothDevice.EXTRA_NAME, bluetoothDevice.getAddress());
                            Log.d(TAG,"DeviceAddress:"+bluetoothDevice.getAddress());
                            Log.d(TAG,"DeviceRssi:"+randomRSSI);
                            Log.d(TAG,"DeviceClass:"+bluetoothClass);
                            sendBroadcast(deviceIntent);
                        } catch (IllegalAccessException e) {
                            Log.e(TAG,"IllegalAccessException");
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            Log.e(TAG,"InstantiationException");
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            Log.e(TAG,"InvocationTargetException");
                            Log.e(TAG,e.getMessage()+"....",e.getCause());
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Log.e(TAG,"mDeviceConstructor or mClassConstructor is Empty");
                }
                isHanding = false;
            }
        },postTime);
    }

    @Override
    protected void onDestroy() {

        this.unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
