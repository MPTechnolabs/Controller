package com.olmatix;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.olamatix.R;

/**
 * Created by android on 11/24/2016.
 */

public class ActivitySetting extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    LinearLayout lin_network,lin_notification,lin_about;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findId();

    }

    private void findId() {
        lin_network = (LinearLayout) findViewById(R.id.lin_network);
        lin_notification = (LinearLayout) findViewById(R.id.lin_notification);
        lin_about = (LinearLayout) findViewById(R.id.lin_about);

        lin_network.setOnClickListener(this);
        lin_notification.setOnClickListener(this);
        lin_about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lin_network:

                startActivity(new Intent(this,Activitynetwork.class));
                break;
            case R.id.lin_notification:
                break;
            case R.id.lin_about:
                startActivity(new Intent(this,EditConnectionFragment.class));
                break;
            default:
                break;
        }


    }
}
