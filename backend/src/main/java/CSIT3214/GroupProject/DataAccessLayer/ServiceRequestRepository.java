package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.ServiceRequest;
import CSIT3214.GroupProject.Model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByCustomerId(Long customerId);
    List<ServiceRequest> findByServiceProviderId(Long serviceProviderId);

    @Query("SELECT sr FROM ServiceRequest sr WHERE sr.serviceType IN :skills")
    List<ServiceRequest> findByServiceTypeInSet(@Param("skills") Set<Skill> skills);
}