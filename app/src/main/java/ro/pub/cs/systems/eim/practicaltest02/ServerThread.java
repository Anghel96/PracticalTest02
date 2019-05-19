package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.WeakHashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;

public class ServerThread extends Thread {

    private int port = 0;
    ServerSocket serverSocket = null;
    HashMap<String, WeatherForecastInformation> retainData = null;

    public ServerThread(int port) {
        this.port = port;

        try{
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An error has occurred during server run: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        retainData = new HashMap<>();
    }

    public void setRetainData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.retainData.put(city, weatherForecastInformation);
    }

    public HashMap<String, WeatherForecastInformation> getRetainData() {
        return retainData;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();

                if (socket == null) {
                    Log.e(Constants.TAG, "[SERVER THREAD] Exception has occured...");
                    return;
                }
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());

                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();

            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
            if (Constants.DEBUG) {
                clientProtocolException.printStackTrace();
            }
        }catch (IOException ioException) {
            Log.e(Constants.TAG, "An error has occurred during server run: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void stopThread() {
        interrupt();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioException) {
            Log.e("TAG EXCEPTION", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }

    }
}
