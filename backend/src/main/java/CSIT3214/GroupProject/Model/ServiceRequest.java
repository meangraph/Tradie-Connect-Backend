package CSIT3214.GroupProject.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private LocalDate requestedDate;
    private LocalTime requestedTime;
    private LocalDate scheduledStartDate;
    private LocalTime scheduledStartTime;
    private LocalDate scheduledEndDate;
    private LocalTime scheduledEndTime;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Double cost;
    private String description;

}