package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.*;
import CSIT3214.GroupProject.Model.Review;
import CSIT3214.GroupProject.Model.ServiceProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Review> findAllReviews() {
        return reviewRepository.findAll();
    }

    public Review findReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    @Transactional
    public Review saveReview(CreateReviewDTO reviewDto) {
        Review review = new Review();
        ServiceProvider sp = serviceProviderRepository.findById(reviewDto.getServiceProviderId()).orElse(null);
        review.setCustomer(customerRepository.findById(reviewDto.getCustomerId()).orElse(null));
        review.setServiceProvider(sp);
        review.setServiceRequest(serviceRequestRepository.findById(reviewDto.getServiceRequestId()).orElse(null));
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        // Save the review first to the database
        Review savedReview = reviewRepository.save(review);

        // Then update the ServiceProvider's average rating
        Double newAverageRating = calculateAverageRating(sp);
        sp.setRating(newAverageRating);

        // Save the updated ServiceProvider to the database
        serviceProviderRepository.save(sp);

        return savedReview;
    }

    public Double calculateAverageRating(ServiceProvider serviceProvider) {
        List<Review> reviews = serviceProvider.getReviews();
        if (reviews.isEmpty()) {
            return null;
        }
        Double sum = 0.0;
        for (Review review : reviews) {
            sum += review.getRating();
        }
        return sum / reviews.size();
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

}