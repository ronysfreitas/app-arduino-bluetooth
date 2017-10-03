package io.github.ronysfreitas.arduinobluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    private Button btnUp, btnDown, btnRigth, btnLeft;
    private EditText textUp, textDown, textRigth, textLeft;

    private Button desconectar;

    private ProgressDialog progress;

    private String address = null;

    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;

    private boolean isBtConnected = false;

    // SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receber o endereço do dispositivo bluetooth
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_control);

        // Views
        btnUp = (Button) findViewById(R.id.btnUp);
        btnDown = (Button) findViewById(R.id.btnDown);
        btnRigth = (Button) findViewById(R.id.btnRigth);
        btnLeft = (Button) findViewById(R.id.btnLeft);

        textUp = (EditText) findViewById(R.id.textUp);
        textDown = (EditText) findViewById(R.id.textDonw);
        textRigth = (EditText) findViewById(R.id.textRigth);
        textLeft = (EditText) findViewById(R.id.textLeft);


        // Call the class to connect
        new ConnectBT().execute();


        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(textUp.getText().toString());
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(textDown.getText().toString());
            }
        });

        btnRigth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(textRigth.getText().toString());
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go(textLeft.getText().toString());
            }
        });

    }

    private void Disconnect() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                alert("Erro ao tentar se desconectar.");
            }
        }
        finish();

    }

    private void go(String s) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(s.getBytes());
            } catch (IOException e) {
                alert("Ocorreu um erro - " + s);
            }
        }
    }

    private void alert(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ControlActivity.this, "Conectando...", "Por favor aguarde!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                alert("Falha na conexão. É um SPP Bluetooth? Tente novamente.");
                finish();
            } else {
                alert("Conectado");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}