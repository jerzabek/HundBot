package ga.jarza.hundbot.datasendinghttp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Sender {

  public static String sendTing(String[] data){
    String targetURL = "http://localhost:80";
    String urlParameters;
    try {
       urlParameters = "song=" + URLEncoder.encode(data[0], "UTF-8")
         + "&host=" + URLEncoder.encode(data[1], "UTF-8")
         + "&guild=" + URLEncoder.encode(data[2], "UTF-8");
    }catch (Throwable e){
      e.printStackTrace();
      return null;
    }
    System.out.println(urlParameters);
    URL url;
    HttpURLConnection connection = null;
    try {
      //Create connection
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type",
        "application/x-www-form-urlencoded");

      connection.setRequestProperty("Content-Length", "" +
        Integer.toString(urlParameters.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");

      connection.setUseCaches (false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      //Send request
      DataOutputStream wr = new DataOutputStream (
        connection.getOutputStream ());
      wr.writeBytes (urlParameters);
      wr.flush ();
      wr.close ();

      //Get Response
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer();
      while((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();

    } catch (Exception e) {

      e.printStackTrace();
      return null;

    } finally {

      if(connection != null) {
        connection.disconnect();
      }
    }
  }

}
