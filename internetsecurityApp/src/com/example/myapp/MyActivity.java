package com.example.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.mobilesecuritylab.R;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        final Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonActionTaskOne("https://www.liu.se");
            }
        });
        final Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonActionTaskOne("https://tal-front.itn.liu.se");
            }
        });
        final Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonAction("https://tal-front.itn.liu.se:4001");
            }
        });
        final Button button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonAction("https://tal-front.itn.liu.se:4020");
            }
        });
    }

    // Task1: Starts https connection without keystore
    public void buttonActionTaskOne(final String url_s) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL(url_s);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    showAlert(url_s, "HTTP status: " + urlConnection.getResponseCode() + " message: " +  urlConnection.getResponseMessage());

                } catch (Exception e) {
                    showAlert(url_s, "error: " + e.getMessage());
                }
            }
        });
        thread.start();
    }

    // Task2: Start https connection with trusted certificates
    public void buttonAction(final String url_s) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Imported the trusted keystore that contains the trusted CAs
                    // created in the Bouncy castle keystore format
                    KeyStore trusted = KeyStore.getInstance("BKS");
                    InputStream in = getApplicationContext().getResources().openRawResource(R.raw.keystorebks);
                    trusted.load(in, "hannes".toCharArray());

                    // Adding information to trust manager that contains the CAs from our keystore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(trusted); //Initialize keystore to trustmanager

                    // Create ssl context, that uses the trustmanager to create a connection
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, tmf.getTrustManagers(), null);

                    // Create connection where the ssl context is used
                    URL url = new URL(url_s);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());

                    //showAlert(url_s, "HTTP status: " + urlConnection.getResponseCode() + " message: " +  urlConnection.getResponseMessage());

                    if (urlConnection.getResponseCode() == 200){
                        showAlert(url_s, "The site is to be trusted!");
                    }
                    else {
                        showAlert(url_s, "OH NO! This site is not trusted. Abort!");
                    }

                } catch (Exception e) {
                    //showAlert(url_s, "error: " + e.getMessage());
                    showAlert(url_s, "OH NO! This site is not trusted. Abort!");
                }
            }
        });
        thread.start();
    }

    public void showAlert(final String header, final String message) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(MyActivity.this).create();
                alertDialog.setTitle(header);
                alertDialog.setMessage(message);
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
    }
}