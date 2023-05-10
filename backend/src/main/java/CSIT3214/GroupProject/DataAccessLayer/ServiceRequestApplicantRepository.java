package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.ServiceRequestApplicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRequestApplicantRepository extends JpaRepository<ServiceRequestApplicant, Long> {
}
