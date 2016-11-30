package com.olmatix;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.olamatix.R;
import com.olmatix.internal.Connections;
import com.olmatix.model.ConnectionModel;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity{


    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Toolbar toolbar;
    private ChangeListener changeListener = new ChangeListener();
    private ArrayList<String> connectionMap;
    private ConnectionModel formModel;
    static int length = 8;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random random = new Random();
    Button btn_connect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Map<String, Connection> connections =  Connections.getInstance(getApplicationContext())
                .getConnections();*/
        formModel = new ConnectionModel();
        if(formModel.getClientHandle() == null && formModel.getServerHostName() == null) {
            formModel.setClientId("OlmatixApp-08e756ab30");
            formModel.setServerHostName("cloud.olmatix.com");
            formModel.setServerPort(1883);
            formModel.setUsername("olmatix");
            formModel.setPassword("olmatix");
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(AB.charAt(random.nextInt(AB.length())));
            }
            String clientHandle = sb.toString() + '-' + formModel.getServerHostName() + '-' + formModel.getClientId();
            formModel.setClientHandle(clientHandle);
        }
        connectionMap = new ArrayList<String>();


        findId();
        setEvent();
        populateConnectionList();
        persistAndConnect(formModel);



    }

    private void findId() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        btn_connect = (Button) findViewById(R.id.btn_connect);
        //mFragmentTransaction.replace(R.id.containerView,new TabFragment()).addToBackStack(null).commit();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
    }

    private void setEvent() {

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistAndConnect(formModel);

            }
        });
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();



                if (menuItem.getItemId() == R.id.nav_home) {


/*
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView,new TabFragment()).addToBackStack(null).commit();
*/

                }

                if (menuItem.getItemId() == R.id.nav_item_regular) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
                }

                return false;
            }

        });
    }

    public void persistAndConnect(ConnectionModel model){
        Log.i("MainActivity", "Persisting new connection:" + model.getClientHandle());
        Connection connection = Connection.createConnection(model.getClientHandle(),model.getClientId(),model.getServerHostName(),model.getServerPort(),this,model.isTlsConnection());
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
            Fragment fragment  = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            bundle.putBoolean(ActivityConstants.CONNECTED, true);
            fragment.setArguments(bundle);
            String title = connection.getId();
            displayFragment(fragment, title);

        }
        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException Occured", e);
        }

    }

    private void displayFragment(Fragment fragment, String title){
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, fragment);
            fragmentTransaction.commit();

            // Set Toolbar Title
            getSupportActionBar().setTitle("Olmatix");


        }
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
            Connections.getInstance(this).updateConnection(connection);

            connection.getClient().connect(connOpts, null, callback);
           /* Fragment fragment  = new TabFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());
            fragment.setArguments(bundle);
            String title = connection.getId();
            displayFragment(fragment, title);*/


        } catch (MqttException ex){

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
            Toast.makeText(getApplicationContext(),"Connection Established Successfully",Toast.LENGTH_LONG).show();
        }

        catch (MqttException e) {
            Log.e(this.getClass().getCanonicalName(),
                    "MqttException Occured", e);
        }

    }

    private void addToHistory(String mainText){
        System.out.println("LOG: " + mainText);
        //mAdapter.add(mainText);
        Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

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

                Intent i = new Intent(this,ActivitySetting.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateConnectionList(){
        // Clear drawerFragment

        // get all the available connections
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();
        int connectionIndex = 0;
        connectionMap = new ArrayList<String>();

        Iterator connectionIterator = connections.entrySet().iterator();
        while (connectionIterator.hasNext()){
            Map.Entry pair = (Map.Entry) connectionIterator.next();
            connectionMap.add((String) pair.getKey());
            ++connectionIndex;
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Map<String, Connection> connections = Connections.getInstance(this)
                .getConnections();

        Log.i("MainActivity", "Updating connection: " + connections.keySet().toString());
        try {
            Connection connection = connections.get(formModel.getClientHandle());
            connection.getClient().close();
        }catch (Exception e)
        {}

    }

}
