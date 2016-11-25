package com.olmatix.internal;


import com.olmatix.model.ReceivedMessage;

/**
 * Created by james on 12/10/15.
 */
public interface IReceivedMessageListener {

    public void onMessageReceived(ReceivedMessage message);
}