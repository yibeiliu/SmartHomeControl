package com.smarthomecontroldemo.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.Utils.SharePre;
import com.smarthomecontroldemo.Utils.StaticValues;
import com.smarthomecontroldemo.base.BaseActivity;
import com.smarthomecontroldemo.bluetooth.BtConnectActivity;
import com.smarthomecontroldemo.control.AirPurifierActivity;
import com.smarthomecontroldemo.control.HumidifierActivity;
import com.smarthomecontroldemo.control.WaterPurifierActivity;
import com.smarthomecontroldemo.data.AddPageItem;
import com.smarthomecontroldemo.data.HomeMultiItem;
import com.smarthomecontroldemo.data.SmartDevice;
import com.smarthomecontroldemo.data.UserAndDevice;

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
        ImageButton menuBtn = toolbar.findViewById(R.id.toolbar_menu_ib);
//        toolbar.setNavigationIcon(R.drawable.general_back_button);
        setSupportActionBar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title_tv);
        title.setText("SmartHome");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
