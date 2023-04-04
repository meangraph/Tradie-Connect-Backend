package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
