package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.DataAccessLayer.CreateReviewDTO;
import CSIT3214.GroupProject.DataAccessLayer.ReviewResponseDTO;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Review;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.ServiceRequest;
import CSIT3214.GroupProject.Service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;
    @InjectMocks
    private ReviewController reviewController;

    private Review review;
    private ReviewResponseDTO reviewResponseDTO;
    private CreateReviewDTO createReviewDTO;

    @BeforeEach
    void setUp() {
        review = new Review();
        review.setId(1L);

        // Set up a Customer for the Review
        Customer customer = new Customer();
        customer.setId(1L);
        review.setCustomer(customer);

        // Set up ServiceProvider for the Review
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(1L);
        review.setServiceProvider(serviceProvider);

        // Set up ServiceRequest for the Review
        ServiceRequest serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        review.setServiceRequest(serviceRequest);

        reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setId(review.getId());
        reviewResponseDTO.setCustomerId(review.getCustomer().getId());
        reviewResponseDTO.setServiceProviderId(review.getServiceProvider().getId());
        reviewResponseDTO.setServiceRequestId(review.getServiceRequest().getId());

        createReviewDTO = new CreateReviewDTO();
    }

    @Test
    void getAllReviews() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.findAllReviews()).thenReturn(reviews);
        List<Review> result = reviewController.getAllReviews();
        assertEquals(reviews, result);
    }

    @Test
    void getReview() {
        when(reviewService.findReviewById(any(Long.class))).thenReturn(review);
        ReviewResponseDTO result = reviewController.getReview(1L);
        assertEquals(reviewResponseDTO, result);
    }

    @Test
    void createReview() {
        when(reviewService.saveReview(any(CreateReviewDTO.class))).thenReturn(review);
        Review result = reviewController.createReview(createReviewDTO);
        assertEquals(review, result);
    }

    @Test
    void deleteReview() {
        doNothing().when(reviewService).deleteReview(any(Long.class));
        assertDoesNotThrow(() -> reviewController.deleteReview(1L));
        verify(reviewService, times(1)).deleteReview(any(Long.class));
    }
}