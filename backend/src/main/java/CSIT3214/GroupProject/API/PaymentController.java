package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.*;
import CSIT3214.GroupProject.Service.CustomerService;
import CSIT3214.GroupProject.Service.MembershipService;
import CSIT3214.GroupProject.Service.PaymentService;
import CSIT3214.GroupProject.Service.ServiceProviderService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ServiceProviderService serviceProviderService;
    @Autowired
    MembershipService membershipService;

    @PostMapping("/pay-membership")
    public ResponseEntity<String> payMembership(@RequestParam("stripeToken") String stripeToken,
                                      @RequestParam("userId") Long userId,
                                      @RequestParam("userType") String userType,
                                      @RequestParam("membershipType") MembershipType membershipType) {
        try {
            User user = null;
            if (userType.equalsIgnoreCase("customer")) {
                user = customerService.findCustomerById(userId);
            } else if (userType.equalsIgnoreCase("serviceprovider")) {
                user = serviceProviderService.findServiceProviderById(userId);
            }

            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }

            Membership membership = user.getMembership();

            if (membership == null) {
                membership = new Membership();
                membership.setMembershipType(membershipType);
                user.setMembership(membership);
                membershipService.saveMembership(membership);
            } else {
                membership.setMembershipType(membershipType);
            }

            Double amount = null;

            switch (membershipType) {
                case CLIENT_SUBSCRIPTION:
                    amount = 1200.0;
                    break;
                case PAY_ON_DEMAND:
                case COMMISSION:
                    amount = 0.0;
                    break;
                case SERVICE_PROVIDER_SUBSCRIPTION:
                    amount = 2400.0;
                    break;
            }

            if (amount == null) {
                return new ResponseEntity<>("Invalid payment type", HttpStatus.BAD_REQUEST);
            }

            Charge charge = paymentService.createCharge(stripeToken, (int) (amount * 100));

            Payment payment = new Payment();
            if (user instanceof Customer) {
                payment.setCustomer((Customer) user);
            } else if (user instanceof ServiceProvider) {
                payment.setServiceProvider((ServiceProvider) user);
            }
            payment.setAmount(amount);
            payment.setTransactionDate(LocalDateTime.now());
            paymentService.savePayment(payment);

            user.getMembership().setPrice(amount);
            user.getMembership().setDescription(membership.getMembershipType().toString());

            if (user instanceof Customer) {
                customerService.saveCustomer((Customer) user);
            } else if (user instanceof ServiceProvider) {
                serviceProviderService.saveServiceProvider((ServiceProvider) user);
            }

            return new ResponseEntity<>("Payment successful", HttpStatus.OK);
        } catch (StripeException e) {
            return new ResponseEntity<>("Error processing payment: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }
}