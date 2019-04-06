package com.example.dmdpult.WebSocketWorker;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WebSocketClient extends WebSocketAdapter implements NetworkInfoListener {

    private WebSocket ws;
    private ChangeListener changeListener;
    private TimerNetworkInfo info;
    private Context context;
    private NetworkInfo.State state;
    private WebSocketState stateSocket;
    private boolean recont = false;

    public WebSocketClient(Context context, ChangeListener changeListener) {
        this.changeListener = changeListener;
        this.context = context;
        info = new  TimerNetworkInfo(context, this);
    }

    public void WriteData(String data) {
        if (ws.isOpen()) ws.sendText(data);
    }

    public void WriteData(byte[] data) {
        if (ws.isOpen()) ws.sendBinary(data);
    }

    public void unreg() {
        info.unreg();
    }

    public void wsConnect() {
        if ((state == NetworkInfo.State.CONNECTED) && (stateSocket != WebSocketState.OPEN)) {
            try {
                ws = new WebSocketFactory()
                        .createSocket("ws://192.168.4.1:81/")
                        .addListener(this)
                        .connectAsynchronously();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void wsDisconnect() {
        if (ws != null) if (ws.isOpen()) ws.disconnect();
    }

    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) {
        stateSocket = newState;
        switch (newState) {
            case CLOSING:
                changeListener.OnChangeListener(false);
                break;
            case CLOSED:
                if (recont) {
                    wsConnect();
                    recont = false;
                }
                break;
        }
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
        changeListener.OnChangeListener(true);
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) {
        changeListener.OnDataReadListener(text);
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) {
        wifi_OnOff(false);
    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) {
        changeListener.OnDataReadListener(binary);
    }

    @Override
    public void niListener(NetworkInfo.State state) {
        this.state = state;
        switch (state) {
            case CONNECTED: wsConnect(); break;
            case DISCONNECTED: wifi_OnOff(true); break;
        }
    }

    private void wifi_OnOff(boolean onoff) {
        WifiManager mainWifiObj = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mainWifiObj != null) {
            if (onoff && !mainWifiObj.isWifiEnabled()) mainWifiObj.setWifiEnabled(true);
            else if (!onoff && mainWifiObj.isWifiEnabled()) mainWifiObj.setWifiEnabled(false);
        }
    }
}
