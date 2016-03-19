package com.example.healyj36.quizapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jordan on 19/03/2016.
 */
public class ListHosts extends Activity {
    final String SERVER_ADDR = "136.206.254.6";
    Context context = ListHosts.this;
    String clientNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_hosts);

        Bundle extras = getIntent().getExtras();
        clientNickname = extras.getString("nicknameClientKey");

        ListHostsTask listHostsTask = new ListHostsTask(SERVER_ADDR);
        listHostsTask.execute();
    }

    public class ListHostsTask extends AsyncTask<Void, String, Void> {
        String dstAddr;
        String allHostIpandNames;
        String[] ary;
        // port for client is 1234
        // port for hosts is 2345
        final int dstPort = 1234;
        ListHostsTask(String addr) {
            dstAddr = addr;
        }
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Socket socket = new Socket(dstAddr, dstPort);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                allHostIpandNames = dataInputStream.readUTF();
                // "name: ipaddress\nname: ipaddress"
                ary = allHostIpandNames.split("\n");
                // ary[0] == name: ipaddress
                // ary[1] == name: ipaddress
                publishProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // display hosts on server

            ArrayList<String> al = new ArrayList<>(Arrays.asList(ary));
            ListView listHosts = (ListView) findViewById(R.id.all_hosts_list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1, al);
            listHosts.setAdapter(adapter);
            listHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent startGame = new Intent(ListHosts.this, JoinGame.class);

                    // final int result = 1; // signal
                    String ipandName = (String) parent.getItemAtPosition(position);
                    String hostNickname = ipandName.split(": ")[0];
                    String ip = ipandName.split(": ")[1];

                    startGame.putExtra("hostIpKey", ip);
                    startGame.putExtra("nicknameClientKey", clientNickname.trim());
                    startGame.putExtra("nicknameHostKey", hostNickname.trim());

                    startGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    // call activity to run and don't expect data to be sent back
                    startActivity(startGame);
                }
            });
        }
    }
}
