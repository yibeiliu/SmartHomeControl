package com.smartlab.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.smartlab.R;
import com.smartlab.Utils.SharePre;
import com.smartlab.Utils.StaticValues;
import com.smartlab.base.BaseActivity;
import com.smartlab.bluetooth.BtConnectActivity;
import com.smartlab.control.AirPurifierActivity;
import com.smartlab.control.HumidifierActivity;
import com.smartlab.control.WaterPurifierActivity;
import com.smartlab.data.AddPageItem;
import com.smartlab.data.HomeMultiItem;
import com.smartlab.data.SmartDevice;
import com.smartlab.data.UserAndDevice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HomeRVAdapter homeRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureToolbar();
        recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        List<HomeMultiItem> lists = getDatabaseDevicesList();
        homeRVAdapter = new HomeRVAdapter(lists);
        recyclerView.setAdapter(homeRVAdapter);
        homeRVAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                Log.d("lpy", "pos = " + position);
                HomeMultiItem multiItem = (HomeMultiItem) adapter.getItem(position);
                switch (adapter.getItemViewType(position)) {
                    case HomeMultiItem.ADD_PAGE:
                        final String[] items = new String[]{"通过蓝牙添加", "扫描二维码添加"};
                        new QMUIDialog.MenuDialogBuilder(view.getContext())
                                .addItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        switch (which) {
                                            case 0:
                                                //go to bt activity
                                                Intent intent = new Intent(MainActivity.this, BtConnectActivity.class);
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                //go to QR code activity
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                })
                                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                        break;
                    case HomeMultiItem.DEVICE:
                        SmartDevice smartDevice = (SmartDevice) multiItem;
                        if (StaticValues.WATER_PURIFIER.equals(smartDevice.getDeviceName())) {
                            Intent intent = new Intent(MainActivity.this, WaterPurifierActivity.class);
                            startActivity(intent);
                        } else if (StaticValues.HUMIDIFIER.equals(smartDevice.getDeviceName())) {
                            Intent intent = new Intent(MainActivity.this, HumidifierActivity.class);
                            startActivity(intent);
                        } else if (StaticValues.AIR_PURIFIER.equals(smartDevice.getDeviceName())) {
                            Intent intent = new Intent(MainActivity.this, AirPurifierActivity.class);
                            startActivity(intent);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private List<HomeMultiItem> getDatabaseDevicesList() {
        UserAndDevice userAndDevice = SharePre.getUserAndDevices(getApplicationContext());
        List<HomeMultiItem> lists;
        if (userAndDevice != null) {
            lists = new ArrayList<HomeMultiItem>(userAndDevice.getLists());
        } else {
            lists = new ArrayList<>();
        }
        lists.add(new SmartDevice(StaticValues.WATER_PURIFIER, "mac1", R.mipmap.ic_launcher));
        lists.add(new SmartDevice(StaticValues.HUMIDIFIER, "mac2", R.mipmap.ic_launcher));
        lists.add(new SmartDevice(StaticValues.AIR_PURIFIER, "mac3", R.mipmap.ic_launcher));
        //todo mock data for PENG
        lists.add(new AddPageItem());
        return lists;
    }

    @Override
    protected void onResume() {
        super.onResume();
        homeRVAdapter.setNewData(getDatabaseDevicesList());
    }

    private void configureToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title_tv);
        title.setText("睿管家");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}
