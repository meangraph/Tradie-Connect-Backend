package CSIT3214.GroupProject.Model.GeoCoding;

import lombok.Data;

import java.util.List;

@Data
public class GeocodingResponse {
    private List<GeocodingResult> results;
    private String status;
}
