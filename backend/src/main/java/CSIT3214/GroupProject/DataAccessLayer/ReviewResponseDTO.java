package CSIT3214.GroupProject.DataAccessLayer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReviewResponseDTO {

    private Long id;
    private Long customerId;
    private Long serviceProviderId;
    private Long serviceRequestId;
    private Double rating;
    private String comment;
}