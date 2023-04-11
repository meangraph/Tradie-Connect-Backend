package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends BaseUserRepository<Customer> {
}