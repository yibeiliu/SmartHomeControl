package com.smarthomecontroldemo.bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.data.SmartDevice;

import java.util.List;

public class BtConnectListAdapter extends BaseQuickAdapter<SmartDevice, BaseViewHolder> {
    public BtConnectListAdapter(@Nullable List<SmartDevice> data) {
        super(R.layout.item_bt_connect_rv_list, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SmartDevice item) {
        helper.setText(R.id.item_bt_connect_rv_title_tv, item.getDeviceName());
        helper.setText(R.id.item_bt_connect_rv_subTitle_tv, item.getDeviceMacAddress());
    }
}
