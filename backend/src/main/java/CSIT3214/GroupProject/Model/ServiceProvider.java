package CSIT3214.GroupProject.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class ServiceProvider extends User {
    private String companyName;
    private String serviceArea;
    private Double rating;

    @ElementCollection(targetClass = Skill.class)
    @CollectionTable(name = "service_provider_skill",
            joinColumns = @JoinColumn(name = "service_provider_id"))
    @Enumerated(EnumType.STRING)
    private Set<Skill> skills = EnumSet.noneOf(Skill.class);

    @OneToMany(mappedBy = "serviceProvider")
    @JsonManagedReference("serviceProvider-serviceRequests")
    private List<ServiceRequest> serviceRequests;

    @OneToMany(mappedBy = "serviceProvider")
    private List<Review> reviews;

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
    }
}