package CSIT3214.GroupProject.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Customer extends User {
    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "customer")
    @JsonManagedReference("customer-serviceRequests")
    private List<ServiceRequest> serviceRequests;

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;

    @Override
    public String getPassword()  { return super.getPassword(); }
    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}