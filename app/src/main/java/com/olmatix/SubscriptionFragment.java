package com.olmatix;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.olamatix.R;
import com.olmatix.adapter.SubscriptionListItemAdapter;
import com.olmatix.internal.Connections;
import com.olmatix.model.Subscription;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.Map;


public class SubscriptionFragment extends AppCompatActivity {

    int temp_qos_value = 0;
    GridView subscriptionListView;

    ArrayList<Subscription> subscriptions;
    SubscriptionListItemAdapter adapter;
    Toolbar toolbar;
    Connection connection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.fragment_subscriptions);
       // Bundle bundle  = getIntent().getExtras();
       // String connectionHandle = bundle.getString("Olmatix");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Map<String, Connection> connections = Connections.getInstance(getApplicationContext()).getConnections();
        connection = connections.get("Olmatix");
        subscriptions = connection.getSubscriptions();

        Button subscribeButton = (Button) findViewById(R.id.subscribe_button);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        subscriptionListView = (GridView) findViewById(R.id.subscription_list_view);
        adapter = new SubscriptionListItemAdapter(getApplicationContext(), subscriptions);

        adapter.addOnUnsubscribeListner(new SubscriptionListItemAdapter.OnUnsubscribeListner() {
            @Override
            public void onUnsubscribe(Subscription subscription) {
                try {
                    connection.unsubscribe(subscription);
                    System.out.println("Unsubscribed from: " + subscription.toString());
                } catch (MqttException ex) {
                    System.out.println("Failed to unsubscribe from " + subscription.toString() + ". " + ex.getMessage());
                }
            }
        });
        subscriptionListView.setAdapter(adapter);


    }

    protected void showInputDialog(){
        LayoutInflater layoutInflater =  (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptView = layoutInflater.inflate(R.layout.subscription_dialog, null);
        final EditText topicText = (EditText) promptView.findViewById(R.id.subscription_topic_edit_text);

        final Spinner qos = (Spinner) promptView.findViewById(R.id.subscription_qos_spinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.qos_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qos.setAdapter(adapter);
        qos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp_qos_value = Integer.parseInt(getResources().getStringArray(R.array.qos_options)[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Switch notifySwitch = (Switch) promptView.findViewById(R.id.show_notifications_switch);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SubscriptionFragment.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(true).setPositiveButton(R.string.subscribe_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String topic = topicText.getText().toString();

                Subscription subscription = new Subscription(topic, temp_qos_value, connection.handle(), notifySwitch.isChecked());
                subscriptions.add(subscription);
                try {
                    connection.addNewSubscription(subscription);

                } catch (MqttException ex) {
                    System.out.println("MqttException whilst subscribing: " + ex.getMessage());
                }
                adapter.notifyDataSetChanged();
            }

            ;
        }).setNegativeButton(R.string.subscribe_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert =  alertDialogBuilder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();
    }

}