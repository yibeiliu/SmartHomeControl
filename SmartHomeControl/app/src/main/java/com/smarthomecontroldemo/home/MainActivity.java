package com.smarthomecontroldemo.home;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarthomecontroldemo.R;
import com.smarthomecontroldemo.base.BaseActivity;

public class MainActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private HomeRVAdapter homeRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.main_rv);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        homeRVAdapter = new HomeRVAdapter(null);
        recyclerView.setAdapter(homeRVAdapter);
    }
}
