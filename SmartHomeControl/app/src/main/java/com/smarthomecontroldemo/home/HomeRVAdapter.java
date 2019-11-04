package com.smarthomecontroldemo.home;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.data.SmartDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author peiyi.liu
 * @Date 11/4/2019 1:21 PM
 */
public class HomeRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SmartDevice> list;

    public HomeRVAdapter(List<SmartDevice> smartDeviceLists) {
        super();
        list = new ArrayList<>(smartDeviceLists);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == ViewType.DEVICE_PAGE.getIndex()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homerv_device, parent, false);
            viewHolder = new AddPageViewHolder(view);
        } else if (viewType == ViewType.ADD_PAGE.getIndex()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homerv_add_page, parent, false);
            viewHolder = new AddPageViewHolder(view);
        } else {
            throw new IllegalArgumentException("Wrong viewtype int HomeRVAdapter!");
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            AddPageViewHolder addPageViewHolder = (AddPageViewHolder) holder;
            addPageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to scan page
                    final String[] items = new String[]{"选项1", "选项2", "选项3"};
                    new QMUIDialog.MenuDialogBuilder(holder.itemView.getContext())
                            .addItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(holder.itemView.getContext(), "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                }
            });
        } else {
            DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;
            deviceViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ViewType.ADD_PAGE.getIndex();
        } else {
            return ViewType.DEVICE_PAGE.getIndex();
        }
    }

    private enum ViewType {

        DEVICE_PAGE(0), ADD_PAGE(1);
        private int index;

        ViewType(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    class AddPageViewHolder extends RecyclerView.ViewHolder {

        public AddPageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
