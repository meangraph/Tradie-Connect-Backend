package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends BaseUserRepository<ServiceProvider> {
    @Query("SELECT sp FROM ServiceProvider sp JOIN sp.skills s WHERE s = :skill")
    List<ServiceProvider> findByServiceType(@Param("skill") Skill skill);
}