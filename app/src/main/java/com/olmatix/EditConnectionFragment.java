package com.olmatix;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import com.olamatix.R;
import com.olmatix.internal.Connections;
import com.olmatix.model.ConnectionModel;

import java.util.Map;
import java.util.Random;


public class EditConnectionFragment extends AppCompatActivity {

    private EditText serverHostname;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText tlsServerKey;
    private EditText tlsClientKey;
    private EditText timeout;
    private EditText keepAlive;
    private EditText lwtTopic;
    private EditText lwtMessage;
    private Spinner lwtQos;
    private Switch lwtRetain;
    private EditText clientId;
    private Switch cleanSession;

    Toolbar toolbar;
    boolean newConnection = false;
    private ConnectionModel formModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.fragment_edit_connection1);

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

        clientId = (EditText) findViewById(R.id.client_id);
        serverHostname = (EditText) findViewById(R.id.hostname);
        serverPort = (EditText) findViewById(R.id.add_connection_port);
        serverPort.setText("");
        cleanSession = (Switch) findViewById(R.id.clean_session_switch);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        tlsServerKey = (EditText) findViewById(R.id.tls_server_key);
        tlsClientKey = (EditText) findViewById(R.id.tls_client_key);
        timeout = (EditText) findViewById(R.id.timeout);
        keepAlive = (EditText) findViewById(R.id.keepalive);
        lwtTopic = (EditText) findViewById(R.id.lwt_topic);
        lwtMessage = (EditText) findViewById(R.id.lwt_message);
        lwtQos = (Spinner) findViewById(R.id.lwt_qos_spinner);
        lwtRetain = (Switch) findViewById(R.id.retain_switch);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.qos_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lwtQos.setAdapter(adapter);

        if(formModel.getClientHandle() != null ){


            newConnection = false;

            populateFromConnectionModel(formModel);
            formModel.setClientHandle(formModel.getClientHandle());

        }
        setFormItemListeners();

    }

    private void setFormItemListeners(){

        clientId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formModel.setClientId(s.toString());
            }
        });

        serverHostname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formModel.setServerHostName(s.toString());
            }
        });

        serverPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    formModel.setServerPort(Integer.parseInt(s.toString()));
                }
            }
        });


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().equals("")) {
                    formModel.setUsername(s.toString());
                } else {
                    formModel.setUsername(ActivityConstants.empty);
                }

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().equals("")) {
                    formModel.setPassword(s.toString());
                } else {
                    formModel.setPassword(ActivityConstants.empty);
                }
            }
        });
        tlsServerKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formModel.setTlsServerKey(s.toString());
            }
        });
        tlsClientKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formModel.setTlsClientKey(s.toString());
            }
        });
        timeout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    formModel.setTimeout(Integer.parseInt(s.toString()));
                }
            }
        });
        keepAlive.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    formModel.setKeepAlive(Integer.parseInt(s.toString()));
                }
            }
        });
        lwtTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    formModel.setLwtTopic(s.toString());
            }
        });
        lwtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formModel.setLwtMessage(s.toString());
            }
        });
        lwtQos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                formModel.setLwtQos(Integer.parseInt(getResources().getStringArray(R.array.qos_options)[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lwtRetain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                formModel.setLwtRetain(isChecked);
            }
        });

    }

    private void populateFromConnectionModel(ConnectionModel connectionModel) {
        clientId.setText(connectionModel.getClientId());

        serverHostname.setText(connectionModel.getServerHostName());
        serverPort.setText(Integer.toString(connectionModel.getServerPort()));
        username.setText(connectionModel.getUsername());
        password.setText(connectionModel.getPassword());

        tlsServerKey.setText(connectionModel.getTlsServerKey());
        tlsClientKey.setText(connectionModel.getTlsClientKey());
        timeout.setText(Integer.toString(connectionModel.getTimeout()));
        keepAlive.setText(Integer.toString(connectionModel.getKeepAlive()));
        lwtTopic.setText(connectionModel.getLwtTopic());
        lwtMessage.setText(connectionModel.getLwtMessage());
        lwtQos.setSelection(connectionModel.getLwtQos());
        lwtRetain.setChecked(connectionModel.isLwtRetain());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_connection, menu);//Menu Resource, Menu
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_connection:

                MainActivity mainActivity = new MainActivity();
                mainActivity.updateAndConnect(formModel);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}