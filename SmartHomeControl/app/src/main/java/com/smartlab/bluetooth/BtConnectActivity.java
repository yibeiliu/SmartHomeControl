package com.smartlab.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.smartlab.R;
import com.smartlab.Utils.SharePre;
import com.smartlab.Utils.StaticValues;
import com.smartlab.base.BaseActivity;
import com.smartlab.data.SmartDevice;
import com.smartlab.data.UserAndDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BtConnectActivity extends BaseActivity {

    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView recyclerView;
    private BtConnectListAdapter recyclerviewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<SmartDevice> deviceLists;
    private QMUIEmptyView mEmptyView;
    private Handler timerHandler = new Handler();

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        private HashMap<String, String> map = new HashMap<>();

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceMacAddress = device.getAddress(); // MAC address
                if (TextUtils.isEmpty(deviceName) || TextUtils.isEmpty(deviceMacAddress)) {
                    return;
                }
                Log.d("lpy", "deviceName: " + deviceName);
                Log.d("lpy", "deviceHardwareAddress: " + deviceMacAddress);
                if (map.containsKey(deviceName)) {
                    return;
                }
                map.put(deviceName, deviceMacAddress);
                SmartDevice smartDevice = new SmartDevice(deviceName, deviceMacAddress, R.drawable.jingshuiqi);
                addListAndNoticeRV(smartDevice);
            }
        }
    };

    private void addListAndNoticeRV(SmartDevice smartDevice) {
        recyclerviewAdapter.addData(smartDevice);
        if (recyclerviewAdapter.getData().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mEmptyView.show();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            mEmptyView.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_connect);
        initView();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            showErrorDialog("设备不支持蓝牙");
            return;
        }
        startBtScan();
    }

    private void initView() {
        mEmptyView = findViewById(R.id.bt_connect_empty_view);
        swipeRefreshLayout = findViewById(R.id.bt_connect_srl);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                startBtScan();
            }
        });
        recyclerView = findViewById(R.id.bt_connect_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceLists = new ArrayList<>();
        recyclerviewAdapter = new BtConnectListAdapter(deviceLists);
        if (deviceLists.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mEmptyView.show();
        }
        recyclerviewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final SmartDevice smartDevice = (SmartDevice) adapter.getItem(position);
                assert smartDevice != null;
                if(!isDeviceInThreeList(smartDevice)){
                    //提示不支持当前设备
                    showDialog(BtConnectActivity.this, DialogType.INFO, true,
                            smartDevice.getDeviceName() + " 该设备不支持绑定", 1500);
                    return;
                }

                final UserAndDevice oldUserAndDevice = SharePre.getUserAndDevices(getApplicationContext());
                if (oldUserAndDevice != null) {
                    for (SmartDevice device : oldUserAndDevice.getLists()) {
                        assert smartDevice != null;
                        if (device.getDeviceName().equals(smartDevice.getDeviceName())) {
                            //提示已经绑定过了
                            showDialog(BtConnectActivity.this, DialogType.INFO, true,
                                    smartDevice.getDeviceName() + " 已经绑定过了，无需重复绑定", 1500);
                            return;
                        }
                    }
                }
                new QMUIDialog.MessageDialogBuilder(BtConnectActivity.this)
                        .setTitle("绑定")
                        .setMessage("是否绑定 " + Objects.requireNonNull(smartDevice).getDeviceName() + " 设备?")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                //todo 保存本地
                                UserAndDevice userAndDevice = oldUserAndDevice;
                                if (oldUserAndDevice == null) {
                                    //该用户第一次绑定
                                    userAndDevice = new UserAndDevice();
                                    userAndDevice.setUsername(SharePre.getLoginUsername(getApplicationContext()));
                                    List<SmartDevice> lists = new ArrayList<>();
                                    lists.add(smartDevice);
                                    userAndDevice.setLists(lists);
                                } else {
                                    userAndDevice.getLists().add(smartDevice);
                                }
                                SharePre.setUserAndDevice(BtConnectActivity.this, userAndDevice);
                                Toast.makeText(BtConnectActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        });
        recyclerView.setAdapter(recyclerviewAdapter);
    }

    /**
     * 检查欲绑定设备是否是指定的设备
     * @param smartDevice
     * @return
     */
    private boolean isDeviceInThreeList(SmartDevice smartDevice) {
        return StaticValues.AIR_PURIFIER.equals(smartDevice.getDeviceName())
                || StaticValues.HUMIDIFIER.equals(smartDevice.getDeviceName())
                || StaticValues.WATER_PURIFIER.equals(smartDevice.getDeviceName());
    }

    private void startBtScan() {
        assert bluetoothAdapter != null;
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        Log.d("lpy", "startDiscovery");
        if (bluetoothAdapter.startDiscovery()) {
            dismissDialog();
            showDialog(this, DialogType.LOADING, true, "正在扫描蓝牙设备，请稍等...", -1);
            timerHandler.removeCallbacksAndMessages(null);
            timerHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showDialog(BtConnectActivity.this, DialogType.SUCCESS, true,
                            "扫描完成，如需刷新请下拉列表", 1500);
                    bluetoothAdapter.cancelDiscovery();
                }
            }, 10000);
        } else {
            showErrorDialog("开始扫描蓝牙失败，请检查网络或蓝牙是否正常开启");
        }
    }

    private void showErrorDialog(String msg) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .setTitle("错误")
                .setMessage(msg)
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        timerHandler.removeCallbacksAndMessages(null);
        unregisterReceiver(receiver);
    }
}
