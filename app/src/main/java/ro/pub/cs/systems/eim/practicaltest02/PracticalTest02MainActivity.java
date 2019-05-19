package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverPort = null;
    Button connectButton = null;
    EditText clientAddress = null;
    EditText clientPort = null;
    EditText specifyCity = null;
    Button weatherForecast = null;
    Spinner infoDialog = null;
    TextView messageTextView = null;

    ServerThread serverThread = null;
    ClientThread clientThread = null;

    public ConnectButtonListener connectButtonListener = new ConnectButtonListener();
    private class ConnectButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String getServerPort = serverPort.getText().toString();
            if(getServerPort == null && getServerPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(getServerPort));

            if(serverThread.getServerSocket() == null) {
                Log.e("[MAIN TAG]", "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }

            serverThread.start();
        }
    }

    public WeatherForecastButtonListener weatherForecastInformation = new WeatherForecastButtonListener();
    private class WeatherForecastButtonListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            String getClientPort = clientPort.getText().toString();
            String getClientAddress = clientAddress.getText().toString();

            if (getClientPort == null && getClientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getClientAddress == null && getClientAddress.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client address should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()){
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server!", Toast.LENGTH_SHORT).show();
                return;
            }

            String city = specifyCity.getText().toString();
            String getInfo = infoDialog.getSelectedItem().toString();

            if (city == null && city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no city!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (getInfo == null && getInfo.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no info to get!", Toast.LENGTH_SHORT).show();
                return;
            }
            clientThread = new ClientThread(Integer.parseInt(getClientPort), getClientAddress, city, getInfo, messageTextView);

            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = (EditText)findViewById(R.id.server_port);
        connectButton = (Button)findViewById(R.id.connect_to_server_button);
        connectButton.setOnClickListener(connectButtonListener);
        clientAddress = (EditText)findViewById(R.id.client_address);
        clientPort = (EditText)findViewById(R.id.client_port);
        specifyCity = (EditText)findViewById(R.id.city_text);
        weatherForecast = (Button)findViewById(R.id.weather_information_button);
        weatherForecast.setOnClickListener(weatherForecastInformation);
        infoDialog = (Spinner)findViewById(R.id.spinner_information);
        messageTextView = (TextView)findViewById(R.id.weather_forecast_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i("DESTROY TAG", "[MAIN ACTIVITY] onDestroy() callback method has been invoked");

        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
