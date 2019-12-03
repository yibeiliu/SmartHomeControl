package com.smartlab.bluetooth;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smartlab.R;
import com.smartlab.Utils.StaticValues;
import com.smartlab.data.SmartDevice;
import com.smartlab.model.Constants;

import java.util.List;

public class BtConnectListAdapter extends BaseQuickAdapter<SmartDevice, BaseViewHolder> {
    public BtConnectListAdapter(@Nullable List<SmartDevice> data) {
        super(R.layout.item_bt_connect_rv_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SmartDevice item) {
        String chineseName;
        if (StaticValues.AIR_PURIFIER.equals(item.getDeviceName())) {
            chineseName = Constants.ChineseName.AIR_CLEANER;
        } else if (StaticValues.HUMIDIFIER.equals(item.getDeviceName())) {
            chineseName = Constants.ChineseName.MOISTURIZER;
        } else if (StaticValues.WATER_PURIFIER.equals(item.getDeviceName())) {
            chineseName = Constants.ChineseName.WATER_PURIFIER;
        } else {
            chineseName = "";
        }
        if (TextUtils.isEmpty(chineseName)) {
            helper.setText(R.id.item_bt_connect_rv_title_tv, item.getDeviceName());
        } else {
            helper.setText(R.id.item_bt_connect_rv_title_tv,
                    item.getDeviceName() + " (" + chineseName + ")");
        }
        helper.setText(R.id.item_bt_connect_rv_subTitle_tv, item.getDeviceMacAddress());
    }
}
