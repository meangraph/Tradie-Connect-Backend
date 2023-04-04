package CSIT3214.GroupProject.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Customer extends User {
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "customer")
    @JsonManagedReference("customer-serviceRequests")
    private List<ServiceRequest> serviceRequests;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;


}