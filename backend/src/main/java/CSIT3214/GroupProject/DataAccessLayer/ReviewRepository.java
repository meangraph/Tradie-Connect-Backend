package CSIT3214.GroupProject.DataAccessLayer;

import CSIT3214.GroupProject.Model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}