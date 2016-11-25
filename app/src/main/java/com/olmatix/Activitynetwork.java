package com.olmatix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.olamatix.R;
import com.olmatix.model.ConnectionModel;

/**
 * Created by android on 11/24/2016.
 */

public class Activitynetwork extends AppCompatActivity{

    Toolbar toolbar;
    TextView txt_domain,txt_port,txt_username,txt_password;
    private ConnectionModel formModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        formModel = new ConnectionModel();

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

        txt_domain = (TextView) findViewById(R.id.txt_domain);
        txt_port = (TextView) findViewById(R.id.txt_port);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_password = (TextView) findViewById(R.id.txt_password);

        txt_domain.setText(formModel.getServerHostName());
        txt_port.setText(formModel.getServerPort()+"");
        txt_username.setText(formModel.getUsername());
        txt_password.setText(formModel.getPassword());



    }
}
