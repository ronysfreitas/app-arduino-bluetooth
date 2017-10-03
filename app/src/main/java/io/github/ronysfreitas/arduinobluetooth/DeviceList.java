package io.github.ronysfreitas.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class DeviceList extends AppCompatActivity {

    // Views
    Button btnPaired;
    ListView devicelist;

    // Bluetooth
    private BluetoothAdapter myBluetooth = null;

    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // Views
        btnPaired = (Button) findViewById(R.id.button);
        devicelist = (ListView) findViewById(R.id.listView);

        // se o dispositivo tem bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {

            // Mostra uma mensagem dizendo que o dispositivo não tem suporte ao bluetooth
            Toast.makeText(getApplicationContext(), "Bluetooth não está disponível", Toast.LENGTH_LONG).show();

            // Finalizar APP
            finish();

        } else if (!myBluetooth.isEnabled()) {
            // Pedindo o user que ligue o bluetooth
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon, 1);
        }

        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });

    }

    private void pairedDevicesList() {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                // Pega o nome e o endereço do aparelho
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "Não há dispositivos emparelhados.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);

        // Metodo que controla clicks na lista de dispositivos
        devicelist.setOnItemClickListener(myListClickListener);

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Obter o endereço MAC do dispositivo
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Iniciando a nova atividade e passando parametros
            Intent i = new Intent(DeviceList.this, ControlActivity.class);
            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };

}
