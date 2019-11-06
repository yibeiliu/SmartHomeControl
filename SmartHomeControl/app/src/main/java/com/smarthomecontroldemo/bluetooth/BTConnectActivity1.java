package com.smarthomecontroldemo.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.smarthomecontroldemo.R;

import java.util.List;

public class BTConnectActivity1 extends AppCompatActivity {
    private BluetoothAdapter bta = null;
    private BluetoothManager btm = null;
    private BluetoothLeScanner scanner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btconnect);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请定位授权
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        } else {
            btm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bta = btm.getAdapter();
            if (bta == null || !bta.isEnabled()) {
                Log.e("lpy", "BT is null or !enable ");
            } else {
                Log.d("lpy", "bta.getName(): " + bta.getName());
                Log.d("lpy", "bta.getAddress(): " + bta.getAddress());
                scanner = bta.getBluetoothLeScanner();
                scanner.startScan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if (result.getDevice().getName() == null) {
                            return;
                        }
                        Log.d("lpy", "onScanResult.getAddress(): " + result.getDevice().getAddress());
                        Log.d("lpy", "onScanResult.getName(): " + result.getDevice().getName());
                        Log.d("lpy", "onScanResult.getBondState(): " + result.getDevice().getBondState());
                        Log.d("lpy", "onScanResult.getType(): " + result.getDevice().getType());
                        Log.d("lpy", "==========================================================: ");

//                        Log.d("lpy", "onScanResult callbackType = " + callbackType +
//                                " ScanResult = " + result.toString());
//                        Log.d("lpy", "result.getScanRecord(): " + result.getScanRecord());
//                        Log.d("lpy", "result.getDevice().getName(): " + result.getDevice().getName());
//                        String a = new String(result.getScanRecord().getBytes(), StandardCharsets.ISO_8859_1);
//                        Log.d("lpy", "abc : " + bytesToHexString(result.getScanRecord().getBytes()));
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        super.onBatchScanResults(results);
                        Log.d("lpy", "onBatchScanResults: results.size = " + results.size());
                    }

                    @Override
                    public void onScanFailed(int errorCode) {
                        super.onScanFailed(errorCode);
                        Log.d("lpy", "onScanFailed: errorCode = " + errorCode);
                    }
                });
            }
        }
    }

    public String bytesToHexString(byte[] bArr) {
        StringBuffer sb = new StringBuffer(bArr.length);
        String sTmp;

        for (int i = 0; i < bArr.length; i++) {
            sTmp = Integer.toHexString(0xFF & bArr[i]);
            if (sTmp.length() < 2)
                sb.append(0);
            sb.append(sTmp.toUpperCase());
        }

        return sb.toString();
    }
}
