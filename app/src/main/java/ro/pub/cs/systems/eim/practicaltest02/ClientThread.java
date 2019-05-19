package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    int port;
    String address;
    TextView messageTextView;
    String city;
    String getWeatherInfo;

    public ClientThread (int port, String address, String city, String getWeatherInfo, TextView messageTextView) {
        this.port = port;
        this.address = address;
        this.city = city;
        this.getWeatherInfo = getWeatherInfo;
        this.messageTextView = messageTextView;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(address, port);

            if (socket == null) {
                Log.e("TAG", "[CLIENT THREAD] Could not create socket!");
                return;
            }
            PrintWriter printWriter = Utilities.getWriter(socket);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            if(printWriter == null || bufferedReader == null) {
                Log.e("TAG", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            printWriter.println(city);
            printWriter.flush();
            printWriter.println(getWeatherInfo);
            printWriter.flush();
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                final String finalContent = content;
                messageTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        messageTextView.setText(finalContent);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
