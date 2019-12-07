package com.smartlab.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.smartlab.R;
import com.smartlab.Utils.DeviceUtils;
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
import com.smartlab.model.Constants;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private HomeRVAdapter homeRVAdapter;
    private static final int REQUEST_CODE = 1000;

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
                                                Intent intent1 = new Intent(MainActivity.this, BtConnectActivity.class);
                                                startActivity(intent1);
                                                break;
                                            case 1:
                                                //go to QR code activity
                                                Intent intent2 = new Intent(MainActivity.this, CaptureActivity.class);
                                                startActivityForResult(intent2, REQUEST_CODE);
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
        homeRVAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                HomeMultiItem multiItem = (HomeMultiItem) adapter.getItem(position);
                switch (adapter.getItemViewType(position)) {
                    case HomeMultiItem.ADD_PAGE:
                        return false;
                    case HomeMultiItem.DEVICE:
                        SmartDevice smartDevice = (SmartDevice) multiItem;
                        showDetailPage(smartDevice);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void showDetailPage(final SmartDevice smartDevice) {
        String showText = getResources().getString(R.string.main_dialog_detail_device,
                smartDevice.getDeviceChineseName() + " 01", smartDevice.getDeviceMacAddress(),
                smartDevice.getDeviceWifiMacAddress(), smartDevice.getIpAddress());
        SpannableString spannableString = new SpannableString(showText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //todo show page
                Intent intent = new Intent(MainActivity.this, QrCodeActivity.class);
                intent.putExtra(StaticValues.Key.KEY_QRCODE_SHOW, smartDevice);
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, showText.length() - 4, showText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(MainActivity.this)
                .setTitle(smartDevice.getDeviceChineseName())
                .setMessage(spannableString)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                });
        builder.create(com.qmuiteam.qmui.R.style.QMUI_Dialog);
        builder.show();

        try {
            Class<?> bookClass = Class.forName("com.qmuiteam.qmui.widget.dialog.QMUIDialog$MessageDialogBuilder");//完整类名
            Field field = bookClass.getDeclaredField("mTextView");
            field.setAccessible(true);
            Object object = field.get(builder);

            Class<?> textViewClass = Class.forName("android.widget.TextView");
            Method movementMethod = textViewClass.getDeclaredMethod("setMovementMethod", MovementMethod.class);
            movementMethod.setAccessible(true);
            movementMethod.invoke(object, LinkMovementMethod.getInstance());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<HomeMultiItem> getDatabaseDevicesList() {
        UserAndDevice userAndDevice = SharePre.getUserAndDevices(getApplicationContext());
        List<HomeMultiItem> lists;
        if (userAndDevice != null) {
            lists = new ArrayList<HomeMultiItem>(userAndDevice.getLists());
        } else {
            lists = new ArrayList<>();
        }
//        lists.add(new SmartDevice(StaticValues.WATER_PURIFIER, "mac1",
//                R.drawable.svg_water_purifier, Constants.ChineseName.WATER_PURIFIER,
//                Constants.WifiMac.WATER_PURIFIER_WIFI, Constants.IpAddress.WATER_PURIFIER_IP));
//        lists.add(new SmartDevice(StaticValues.HUMIDIFIER, "mac2",
//                R.drawable.svg_humidifier, Constants.ChineseName.MOISTURIZER,
//                Constants.WifiMac.MOISTURIZER_WIFI, Constants.IpAddress.MOISTURIZER_IP));
//        lists.add(new SmartDevice(StaticValues.AIR_PURIFIER, "mac3",
//                R.drawable.svg_air_purifier, Constants.ChineseName.AIR_CLEANER,
//                Constants.WifiMac.AIR_CLEANER_WIFI, Constants.IpAddress.AIR_CLEANER_IP));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    if (TextUtils.isEmpty(result) || !result.startsWith("DD")) {
                        showDialog(MainActivity.this, DialogType.INFO, true, "无效的二维码", 1500);
                    }
                    String[] resultArray = result.split(Constants.QRCode.COMMA);
                    String btName = resultArray[0];
                    String deviceID = resultArray[1];
                    String wifiMacAddress = resultArray[2];
                    String btMacAddress = resultArray[3];

                    int iconId;
                    String chineseName;
                    String ipAddress;
                    if (StaticValues.AIR_PURIFIER.equals(btName)) {
                        chineseName = Constants.ChineseName.AIR_CLEANER;
                        iconId = R.drawable.svg_air_purifier;
                        ipAddress = Constants.IpAddress.AIR_CLEANER_IP;
                    } else if (StaticValues.HUMIDIFIER.equals(btName)) {
                        chineseName = Constants.ChineseName.MOISTURIZER;
                        iconId = R.drawable.svg_humidifier;
                        ipAddress = Constants.IpAddress.MOISTURIZER_IP;
                    } else if (StaticValues.WATER_PURIFIER.equals(btName)) {
                        chineseName = Constants.ChineseName.WATER_PURIFIER;
                        iconId = R.drawable.svg_water_purifier;
                        ipAddress = Constants.IpAddress.WATER_PURIFIER_IP;
                    } else {
                        throw new IllegalStateException("The QR code is wrong!");
                    }
                    SmartDevice smartDevice = new SmartDevice(btName,
                            btMacAddress, iconId, chineseName, wifiMacAddress, ipAddress);
                    tryBindDevice(smartDevice);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "无效的二维码", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void tryBindDevice(final SmartDevice smartDevice) {
        if (!DeviceUtils.isDeviceInThreeList(smartDevice)) {
            //提示不支持当前设备
            showDialog(MainActivity.this, DialogType.INFO, true,
                    smartDevice.getDeviceName() + " 该设备不支持绑定", 1500);
            return;
        }

        final UserAndDevice oldUserAndDevice = SharePre.getUserAndDevices(getApplicationContext());
        if (oldUserAndDevice != null) {
            for (SmartDevice device : oldUserAndDevice.getLists()) {
                assert smartDevice != null;
                if (device.getDeviceName().equals(smartDevice.getDeviceName())) {
                    //提示已经绑定过了
                    showDialog(MainActivity.this, DialogType.INFO, true,
                            smartDevice.getDeviceName() + "01 已经绑定过了，无需重复绑定", 1500);
                    return;
                }
            }
        }
        new QMUIDialog.MessageDialogBuilder(MainActivity.this)
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
                        SharePre.setUserAndDevice(MainActivity.this, userAndDevice);
                        Toast.makeText(MainActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                        homeRVAdapter.setNewData(getDatabaseDevicesList());
                        dialog.dismiss();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }
}
