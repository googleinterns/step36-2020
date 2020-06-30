import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/location")
public class LocationServlet extends HttpServlet {
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private final String API_KEY = ""; // Insert actual API key to test.

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
      
    }

}