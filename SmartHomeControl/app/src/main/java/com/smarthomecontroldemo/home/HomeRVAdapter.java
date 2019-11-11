package com.smarthomecontroldemo.home;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.data.HomeMultiItem;
import com.smarthomecontroldemo.data.SmartDevice;

import java.util.List;

/**
 * @Author peiyi.liu
 * @Date 11/4/2019 1:21 PM
 */
public class HomeRVAdapter extends BaseMultiItemQuickAdapter<HomeMultiItem, BaseViewHolder> {


    public HomeRVAdapter(List<HomeMultiItem> data) {
        super(data);
        addItemType(HomeMultiItem.DEVICE, R.layout.item_homerv_device);
        addItemType(HomeMultiItem.ADD_PAGE, R.layout.item_homerv_add_page);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, HomeMultiItem homeMultiItem) {
        switch (baseViewHolder.getItemViewType()) {
            case HomeMultiItem.DEVICE:
                SmartDevice smartDevice = ((SmartDevice)homeMultiItem);
                baseViewHolder.setText(R.id.item_homerv_device_name, smartDevice.getDeviceName());
                baseViewHolder.setImageResource(R.id.item_homerv_device_icon, smartDevice.getIcon());
                break;
            case HomeMultiItem.ADD_PAGE:
                baseViewHolder.setImageResource(R.id.item_homerv_add_page_icon, R.drawable.add_device);
                break;
            default:
                break;
        }
    }
}