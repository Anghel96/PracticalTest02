package ro.pub.cs.systems.eim.practicaltest02;

import android.provider.DocumentsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {

    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread (ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            PrintWriter printWriter = Utilities.getWriter(socket);
            BufferedReader bufferedReader = Utilities.getReader(socket);

            if(printWriter == null || bufferedReader == null) {
                Log.e("TAG", "[SERVER THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String city = bufferedReader.readLine();
            String getInfo = bufferedReader.readLine();

            if (city == null || city.isEmpty() || getInfo == null || getInfo.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            HashMap<String, WeatherForecastInformation> data = serverThread.getRetainData();
            WeatherForecastInformation weatherForecastInformation = null;
            if(data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));

                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String content = httpClient.execute(httpPost, responseHandler);

                if (content == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                Document document = Jsoup.parse(content);
                Element htmlTag = document.child(0);
                Elements elements = htmlTag.getElementsByTag(Constants.SCRIPT_TAG);

                for (Element element : elements) {
                    String getData = element.data();
                    if (getData.contains(Constants.SEARCH_KEY)) {
                        int position = getData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
                        getData = getData.substring(position);
                        JSONObject jsonObject = new JSONObject(getData);
                        JSONObject current = jsonObject.getJSONObject(Constants.CURRENT_OBSERVATION);
                        String temperature = current.getString(Constants.TEMPERATURE);
                        String windSpeed = current.getString(Constants.WIND_SPEED);
                        String condition = current.getString(Constants.CONDITION);
                        String pressure = current.getString(Constants.PRESSURE);
                        String humidity = current.getString(Constants.HUMIDITY);
                        weatherForecastInformation = new WeatherForecastInformation(temperature, humidity, windSpeed, condition, pressure);
                        serverThread.setRetainData(city, weatherForecastInformation);
                        break;
                    }
                }
                if (weatherForecastInformation == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                    return;
                }

                String result = null;

                switch (getInfo) {
                    case Constants.ALL:
                        result = weatherForecastInformation.toString();
                        break;
                    case "temperature":
                        result = weatherForecastInformation.getTemperature();
                        break;
                    case "humidity":
                        result = weatherForecastInformation.getHumidity();
                        break;
                    case Constants.CONDITION:
                        result = weatherForecastInformation.getCondition();
                        break;
                    case Constants.WIND_SPEED:
                        result = weatherForecastInformation.getWindSpeed();
                        break;
                    case Constants.PRESSURE:
                        result = weatherForecastInformation.getPressure();
                        break;
                    default:
                        result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
                        break;
                }
                printWriter.println(result);
                printWriter.flush();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
