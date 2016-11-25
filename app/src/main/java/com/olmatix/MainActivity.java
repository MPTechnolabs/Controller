package com.olmatix;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.olamatix.R;
import com.olmatix.internal.Connections;
import com.olmatix.model.ConnectionModel;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

import static com.olamatix.R.mipmap.connection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ChangeListener changeListener = new ChangeListener();
    private ArrayList<String> connectionMap;
    private ConnectionModel formModel;
    FloatingActionButton fab_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Map<String, Connection> connections =  Connections.getInstance(getApplicationContext())
                .getConnections();*/
        formModel = new ConnectionModel();
        formModel.setClientId("Olmatix");
        formModel.setClientHandle("Olmatix");
        formModel.setServerHostName("cloud.olmatix.com");
        formModel.setServerPort(1883);
        formModel.setUsername("olmatix");
        formModel.setPassword("olmatix");
        connectionMap = new ArrayList<String>();

        fab_connect = (FloatingActionButton) findViewById(R.id.fab_connect);
        fab_connect.setOnClickListener(this);
        persistAndConnect(formModel);
    }



    public void persistAndConnect(ConnectionModel model){
        Log.i("MainActivity", "Persisting new connection:" + model.getClientHandle());
        Connection connection = Connection.createConnection("Olmatix",model.getClientId(),model.getServerHostName(),model.getServerPort(),this,true);
        connection.registerChangeListener(changeListener);
        connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);


        String[] actionArgs = new String[1];
        actionArgs[0] = model.getClientId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));



        connection.getClient().setTraceCallback(new MqttTraceCallback());

        MqttConnectOptions connOpts = optionsFromModel(model);

        connection.addConnectionOptions(connOpts);
        Connections.getInstance(this).addConnection(connection);
        connectionMap.add(model.getClientHandle());
        try {
            connection.getClient().connect(connOpts, null, callback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        /*try {
           *//* connection.getClient().connect(connOpts, null, callback);
            Fragment fragment  = new ConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            bundle.putBoolean(ActivityConstants.CONNECTED, true);
            fragment.setArguments(bundle);
            String title = connection.getId();*//*
            //displayFragment(fragment, title);

        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException Occured", e);
        }*/

    }

    public void updateAndConnect(ConnectionModel model){
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();

        Log.i("MainActivity", "Updating connection: " + connections.keySet().toString());
        try {
            Connection connection = connections.get(model.getClientHandle());
            // First disconnect the current instance of this connection
            if(connection.isConnected()){
                connection.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTING);
                connection.getClient().disconnect();
            }
            // Update the connection.
            connection.updateConnection(model.getClientId(), model.getServerHostName(), model.getServerPort(), model.isTlsConnection());
            connection.changeConnectionStatus(Connection.ConnectionStatus.CONNECTING);

            String[] actionArgs = new String[1];
            actionArgs[0] = model.getClientId();
            final ActionListener callback = new ActionListener(this,
                    ActionListener.Action.CONNECT, connection, actionArgs);
            connection.getClient().setCallback(new MqttCallbackHandler(this, model.getClientHandle()));

            connection.getClient().setTraceCallback(new MqttTraceCallback());
            MqttConnectOptions connOpts = optionsFromModel(model);
            connection.addConnectionOptions(connOpts);
           // Connections.getInstance(this).updateConnection(connection);
           // drawerFragment.updateConnection(connection);

            connection.getClient().connect(connOpts, null, callback);
           /* Fragment fragment  = new ConnectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            fragment.setArguments(bundle);
            String title = connection.getId();
            displayFragment(fragment, title);
*/

        } catch (MqttException ex){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab_connect:
                startActivity(new Intent(this,SubscriptionFragment.class));

                break;
            default:
                break;
        }
    }

    private class ChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(PropertyChangeEvent event) {

            if (!event.getPropertyName().equals(ActivityConstants.ConnectionStatusProperty)) {
                return;
            }


        }

    }
    private MqttConnectOptions optionsFromModel(ConnectionModel model){

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(model.isCleanSession());
        connOpts.setConnectionTimeout(model.getTimeout());
        connOpts.setKeepAliveInterval(model.getKeepAlive());
        if(!model.getUsername().equals(ActivityConstants.empty)){
            connOpts.setUserName(model.getUsername());
        }

        if(!model.getPassword().equals(ActivityConstants.empty)){
            connOpts.setPassword(model.getPassword().toCharArray());
        }
        if(!model.getLwtTopic().equals(ActivityConstants.empty) && !model.getLwtMessage().equals(ActivityConstants.empty)){
            connOpts.setWill(model.getLwtTopic(), model.getLwtMessage().getBytes(), model.getLwtQos(), model.isLwtRetain());
        }
        //   if(tlsConnection){
        //       // TODO Add Keys to conOpts here
        //       //connOpts.setSocketFactory();
        //   }
        return connOpts;
    }
    public void connect(Connection connection) {
        String[] actionArgs = new String[1];
        actionArgs[0] = connection.getId();
        final ActionListener callback = new ActionListener(this,
                ActionListener.Action.CONNECT, connection, actionArgs);
        connection.getClient().setCallback(new MqttCallbackHandler(this, connection.handle()));
        try {
            connection.getClient().connect(connection.getConnectionOptions(), null, callback);
        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException Occured", e);
        }
    }

    public void disconnect(Connection connection){

        try {
            connection.getClient().disconnect();
        } catch( MqttException ex){
            Log.e("MainActivity", "Exception occured during disconnect: " + ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.setting, menu);//Menu Resource, Menu
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:

                startActivity(new Intent(this,ActivitySetting.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
