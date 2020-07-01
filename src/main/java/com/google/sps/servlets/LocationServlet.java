import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/location")
public class LocationServlet extends HttpServlet {
    private double latitude = 0.0;
    private double longitude = 0.0;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private final String API_KEY = ""; // Insert actual API key to test.

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
      String queryParam = String.format("latlng=%1$f,%2$f", latitude, longitude);
      String apiKeyParam = String.format("&key=%s", API_KEY);
      String fullPath = BASE_URL + queryParam + apiKeyParam;
      try {
        URL url = new URL(queryPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
          String json = getJson(url);
          System.out.println(json); // test
        } else {
          System.out.println("Error: connection response code is: " + responseCode);
        }    
      } catch(Exception e) {
        e.printStackTrace();
     }
    }

    public void getJson() {
      // TODO: implement this method.
    }

}