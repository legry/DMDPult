package com.example.dmdpult.WebSocketWorker;

public interface ChangeListener {
    void OnChangeListener(boolean isConnect);
    void OnDataReadListener(String data);
    void OnDataReadListener(byte[] data);
}
