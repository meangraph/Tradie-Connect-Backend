package CSIT3214.GroupProject.Model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@MappedSuperclass
public abstract class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String phoneNumber;
    private String streetAddress;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "suburb_id")
    private Suburb suburb;
    private String postCode;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "membership_id", referencedColumnName = "id")
    private Membership membership;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private List<Payment> payments;


    public void addPayment(Payment payment) {
        this.payments.add(payment);
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}