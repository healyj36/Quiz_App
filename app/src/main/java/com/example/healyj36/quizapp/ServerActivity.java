package com.example.healyj36.quizapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Jordan on 09/03/2016.
 */
public class ServerActivity extends Activity {

    EditText serverMsg;

    TextView infoIp, msg;
    String message = "";
    ServerSocket serverSocket;
    Socket socket;

    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);

        infoIp = (TextView) findViewById(R.id.infoIp);
        msg = (TextView) findViewById(R.id.msg);
        serverMsg = (EditText) findViewById(R.id.title);

        infoIp.setText(getIpAddress());

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // in case finally block isn't reached (doesn't run)
        // close sockets here
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        static final int SocketServerPORT = 8080;
        int count = 0;

        @Override
        public void run() {
            socket = null;
            dataInputStream = null;
            dataOutputStream = null;

            try {
                Log.d(ServerActivity.class.getSimpleName(), "THE PORT NUMBER IS " + SocketServerPORT);
                serverSocket = new ServerSocket(SocketServerPORT);
                while (true) {
                    Log.d(ServerActivity.class.getSimpleName(), "BEFORE SOCKET ACCEPT");
                    socket = serverSocket.accept();
                    Log.d(ServerActivity.class.getSimpleName(), "AFTER SOCKET ACCEPT");
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String messageFromClient = "";

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();

                    count++;
                    message += "#" + count + " from " + socket.getInetAddress()
                            + ":" + socket.getPort() + "\n"
                            + "Msg from client: " + messageFromClient + "\n";

                    ServerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msg.setText(message);
                        }
                    });

                    String msgReply = count + ": " + serverMsg.getText().toString();
                    dataOutputStream.writeUTF(msgReply);
                    dataOutputStream.flush();

                }
            } catch (IOException e) {
                e.printStackTrace();
                final String errMsg = e.toString();
                ServerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText(errMsg);
                    }
                });

            } finally {
                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                        Log.d(ServerActivity.class.getSimpleName(), "SOCKET CLOSED FROM EXCEPTION");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private String getIpAddress(){
        // TODO for finding address at home
        /*
        String ip = "My IP address is: ";
        String addr = "UNAVAILABLE";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        addr = inetAddress.getHostAddress();
                    }

                }

            }

        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip + addr;
        */

        // TODO for finding address in DCU
        String ip = "My IP address is: ";
        String addr = "UNAVAILABLE";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(enumNetworkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while(enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (!(inetAddress.isLoopbackAddress() || inetAddress.isSiteLocalAddress() || inetAddress.isLinkLocalAddress() || inetAddress.isMulticastAddress())) {
                        if (inetAddress.getHostAddress().matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")) {
                            addr = inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip + addr;
    }
}