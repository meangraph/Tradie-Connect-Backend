package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
}