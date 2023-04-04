package CSIT3214.GroupProject.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class ServiceRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference("customer-serviceRequests")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    @JsonBackReference("serviceProvider-serviceRequests")
    private ServiceProvider serviceProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Skill serviceType;

    @CreationTimestamp
    private LocalDateTime requestedTime;
    private LocalDateTime scheduledTime;
    private OrderStatus status;
    private Double cost;

}