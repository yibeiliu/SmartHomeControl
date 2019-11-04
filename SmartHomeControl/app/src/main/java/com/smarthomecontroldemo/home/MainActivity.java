package com.smarthomecontroldemo.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.base.BaseActivity;
import com.smarthomecontroldemo.data.AddPageItem;
import com.smarthomecontroldemo.data.HomeMultiItem;
import com.smarthomecontroldemo.data.SmartDevice;

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
        //todo 获取绑定设备列表
        List<HomeMultiItem> lists = new ArrayList<>();
        lists.add(new SmartDevice("water1", "mac1", R.mipmap.ic_launcher));
        lists.add(new SmartDevice("water2", "mac2", R.mipmap.ic_launcher));
        lists.add(new SmartDevice("water3", "mac3", R.mipmap.ic_launcher));
        lists.add(new SmartDevice("water4", "mac4", R.mipmap.ic_launcher));
        lists.add(new SmartDevice("water5", "mac5", R.mipmap.ic_launcher));
        lists.add(new SmartDevice("water6", "mac6", R.mipmap.ic_launcher));
        lists.add(new AddPageItem());
        homeRVAdapter = new HomeRVAdapter(lists);
        recyclerView.setAdapter(homeRVAdapter);
        homeRVAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, final View view, int position) {
                Log.d("lpy","pos = " + position);
                HomeMultiItem multiItem = (HomeMultiItem) adapter.getItem(position);
                switch (adapter.getItemViewType(position)) {
                    case HomeMultiItem.ADD_PAGE:
                        final String[] items = new String[]{"通过蓝牙添加", "扫描二维码添加"};
                        new QMUIDialog.MenuDialogBuilder(view.getContext())
                                .addItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //todo 跳转扫描页
                                        Toast.makeText(view.getContext(), "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                })
                                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                        break;
                    case HomeMultiItem.DEVICE:
                        SmartDevice smartDevice = (SmartDevice) multiItem;
                        Toast.makeText(MainActivity.this, "" + smartDevice.getDeviceName(), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        });
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
