package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.Suburb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuburbRepository extends JpaRepository<Suburb, Long> {
    Optional<Suburb> findByNameAndState(String name, String state);
}
