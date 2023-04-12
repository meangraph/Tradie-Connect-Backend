package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.Model.GeoCoding.GeocodingResponse;
import CSIT3214.GroupProject.Model.GeoCoding.GeocodingResult;
import CSIT3214.GroupProject.Model.GeoCoding.LatLng;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingService {

    public LatLng getLatLng(String suburbName, String state) {
        String baseUrl = "https://maps.googleapis.com/maps/api/geocode/json";
        String address = String.format("%s, %s, Australia", suburbName, state);
        String apiKey = "AIzaSyAV4VcwlPdfyWJA1MYMUcCwgqakZYn9wMk";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("address", address)
                .queryParam("key", apiKey);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GeocodingResponse> response = restTemplate.getForEntity(builder.toUriString(), GeocodingResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            GeocodingResponse geocodingResponse = response.getBody();
            if (geocodingResponse != null && !geocodingResponse.getResults().isEmpty()) {
                GeocodingResult result = geocodingResponse.getResults().get(0);
                LatLng location = result.getGeometry().getLocation();
                return location;
            }
        }
        return null;
    }
}