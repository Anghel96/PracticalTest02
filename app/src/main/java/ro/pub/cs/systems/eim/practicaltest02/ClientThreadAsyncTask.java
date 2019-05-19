package ro.pub.cs.systems.eim.practicaltest02;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadAsyncTask extends AsyncTask<String, String, Void> {

    private TextView messageTextView;

    public ClientThreadAsyncTask(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    @Override
    protected Void doInBackground(String... params) {
        String address;
        int port;
        String city;
        String getWeatherInfo;
        Socket socket = null;
        try {
            port = Integer.parseInt(params[0]);
            address = params[1];
            city = params[2];
            getWeatherInfo = params[3];
            socket = new Socket(address, port);

            if (socket == null) {
                Log.e("TAG", "[CLIENT THREAD] Could not create socket!");
            }
            PrintWriter printWriter = Utilities.getWriter(socket);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            if(printWriter == null || bufferedReader == null) {
                Log.e("TAG", "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
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
                        publishProgress(finalContent);
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
        return null;
    }

    @Override
    protected void onPreExecute() {
        messageTextView.setText("");
    }

    @Override
    protected void onProgressUpdate(String... values) {
        messageTextView.append(values[0] + "\n");
    }

    @Override
    protected void onPostExecute(Void aVoid) {}
}
