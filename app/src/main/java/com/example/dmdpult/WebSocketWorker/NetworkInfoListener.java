package com.example.dmdpult.WebSocketWorker;

import android.net.NetworkInfo;

interface NetworkInfoListener {
    void niListener(NetworkInfo.State state);
}
