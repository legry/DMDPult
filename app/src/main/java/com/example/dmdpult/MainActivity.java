package com.example.dmdpult;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.example.dmdpult.WebSocketWorker.ChangeListener;
import com.example.dmdpult.WebSocketWorker.WebSocketClient;
import com.example.dmdpult.databinding.ActivityMainBinding;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements ChangeListener, SeekBar.OnSeekBarChangeListener {

    private ActivityMainBinding binding;
    private byte[] brtns = new byte[1];

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        brtns[0] = (byte) seekBar.getProgress();
        blCn.WriteData(brtns);
    }

    public void nm1Click(View view) {
        myDatas.dialogCreator((byte) 0, "nm");
    }

    public void cnt1Click(View view) {
        myDatas.dialogCreator((byte) 0, "cnt");
    }

    public void nm2Click(View view) {
        myDatas.dialogCreator((byte) 1, "nm");
    }

    public void cnt2Click(View view) {
        myDatas.dialogCreator((byte) 1, "cnt");
    }

    public void nm3Click(View view) {
        myDatas.dialogCreator((byte) 2, "nm");
    }

    public void cnt3Click(View view) {
        myDatas.dialogCreator((byte) 2, "cnt");
    }

    public void nm4Click(View view) {
        myDatas.dialogCreator((byte) 3, "nm");
    }

    public void cnt4Click(View view) {
        myDatas.dialogCreator((byte) 3, "cnt");
    }

    class Pack {
        private byte dev;
        private String nm;
        private String cnt;

        Pack(byte dev, String nm, String cnt) {
            this.dev = dev;
            this.nm = nm;
            this.cnt = cnt;
        }
    }

    public class MyDatas {
        public ObservableArrayList<String> nms = new ObservableArrayList<>();
        public ObservableArrayList<String> cnts = new ObservableArrayList<>();

        MyDatas() {
            for (byte i = 0; i < 4; i++) {
                nms.add("Player " + String.valueOf(i));
                cnts.add("0");
            }
        }

        public void dialogCreator(final byte dev, final String msg) {
            final EditText edittext = new EditText(MainActivity.this);
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Введите значение");

            alert.setView(edittext);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String val = (edittext.getText().toString());
                    blCn.WriteData(
                            new Gson().toJson(new Pack(dev
                            ,(msg.equals("nm")) ? val : String.valueOf(nms.get(dev))
                            ,(msg.equals("cnt")) ? val : String.valueOf(cnts.get(dev)))));
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // what ever you want to do with No option.
                }
            });
            alert.show();
        }
    }

    public MyDatas myDatas;

    class Snacker implements Runnable {

        String data;
        int length;

        Snacker(String data, int length) {
            this.data = data;
            this.length = length;
            runOnUiThread(this);
        }

        @Override
        public void run() {
            Snackbar.make(binding.all, data, length).show();
        }
    }

    private WebSocketClient blCn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatas = new MyDatas();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMydata(myDatas);
        new Snacker("Нет связи", Snackbar.LENGTH_INDEFINITE);
        binding.seekBar.setOnSeekBarChangeListener(this);
        blCn = new WebSocketClient(MainActivity.this, this);
    }

    @Override
    public void OnChangeListener(boolean isConnect) {
        new Snacker(isConnect ? "Связь установлена" : "Связь отсутствует", isConnect ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void OnDataReadListener(String data) {
        Pack obj = new Gson().fromJson(data, Pack.class);
        myDatas.cnts.set(obj.dev, obj.cnt);
        myDatas.nms.set(obj.dev, obj.nm);
    }

    @Override
    public void OnDataReadListener(byte[] data) {

    }
}
