package com.example.nadar.remotecontrol;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends Activity implements View.OnClickListener{

    String IP_global;
    Integer port_global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        addOnClickListener();
        addOnTouchListener();

    }

    private void addOnClickListener(){
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.beep).setOnClickListener(this);
        findViewById(R.id.click).setOnClickListener(this);
        findViewById(R.id.set).setOnClickListener(this);
    }

    private void addOnTouchListener(){
        View trackPad = (View)findViewById(R.id.touchPad);
        trackPad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                SendInstruction s = new SendInstruction();
                s.execute(new String[]{"move " + (int) x + " " + (int) y + "\n"});

                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s1 = new SendInstruction();
                    s1.execute(new String[]{"previous\n"});
                }
                break;

            case R.id.right:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s2 = new SendInstruction();
                    s2.execute(new String[]{"next\n"});
                }
                break;

            case R.id.up:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s3 = new SendInstruction();
                    s3.execute(new String[]{"up\n"});
                }
                break;

            case R.id.down:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s4 = new SendInstruction();
                    s4.execute(new String[]{"down\n"});
                }
                break;

            case R.id.beep:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s5 = new SendInstruction();
                    s5.execute(new String[]{"beep\n"});
                }
                break;

            case R.id.click:
                if(checkIP())Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                else {
                    SendInstruction s6 = new SendInstruction();
                    s6.execute(new String[]{"click\n"});
                }
                break;

            case R.id.set:
                    EditText ip_bar = (EditText) findViewById(R.id.ip);
                    EditText port_bar = (EditText) findViewById(R.id.port);
                    String ip = ip_bar.getText().toString();
                    String port = port_bar.getText().toString();

                    if(ip.equals("") || port.equals(""))  Toast.makeText(getApplicationContext(), "Please enter IP and port first!!", Toast.LENGTH_SHORT).show();
                    else {
                        IP_global = ip;
                        port_global = Integer.parseInt(port);
                    }
                break;
                }
        }


    private boolean checkIP(){
        return IP_global == null || port_global == null;
    }

    private class SendInstruction extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                Socket socket = new Socket(IP_global, port_global);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.write(urls[0].getBytes());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Connection failed";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Command sent to the server!", Toast.LENGTH_SHORT).show();
        }
    }

    private class ReceiveScreenWidth extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                Socket socket = new Socket(IP_global, port_global);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.write("w".getBytes());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Connection failed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}

