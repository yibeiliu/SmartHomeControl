package com.smartlab.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartlab.R;
import com.smartlab.Utils.StaticValues;
import com.smartlab.data.SmartDevice;

public class QrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_qr_code);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        Intent intent = getIntent();
        SmartDevice smartDevice = (SmartDevice) intent.getSerializableExtra(StaticValues.Key.KEY_QRCODE_SHOW);

        ImageView imageView = findViewById(R.id.qrcode_iv);

        if (StaticValues.AIR_PURIFIER.equals(smartDevice.getDeviceName())) {
            imageView.setImageResource(R.drawable.qrcode_air_purifier);
        } else if (StaticValues.HUMIDIFIER.equals(smartDevice.getDeviceName())) {
            imageView.setImageResource(R.drawable.qrcode_humidifier);
        } else if (StaticValues.WATER_PURIFIER.equals(smartDevice.getDeviceName())) {
            imageView.setImageResource(R.drawable.qrcode_water_purifier);
        }

        ImageButton imageButton = findViewById(R.id.close_btn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    }
}
