package com.olmatix;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.olamatix.R;
import com.olmatix.internal.Connections;
import com.olmatix.model.Subscription;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ratan on 7/29/2015.
 */
public class FavoriteFragment extends Fragment {

    FloatingActionButton fab;
    ArrayList<Subscription> subscriptions;
    Connection connection;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle  = this.getArguments();
        String connectionHandle = bundle.getString(ActivityConstants.CONNECTION_KEY);
        Map<String, Connection> connections = Connections.getInstance(this.getActivity())
                .getConnections();
        connection = connections.get(connectionHandle);
        subscriptions = connection.getSubscriptions();

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view =  inflater.inflate(R.layout.favorite_layout,null);


        fab= (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();
                bundle.putString(ActivityConstants.CONNECTION_KEY, connection.handle());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SubscriptionFragment fragment = new SubscriptionFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerView,fragment);
                fragmentTransaction.commit();
            }
        });

        return view;


    }
}
