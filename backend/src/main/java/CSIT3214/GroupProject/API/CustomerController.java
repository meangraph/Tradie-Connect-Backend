package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.GeoCoding.LatLng;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.Suburb;
import CSIT3214.GroupProject.Service.CustomerService;
import CSIT3214.GroupProject.Service.GeocodingService;
import CSIT3214.GroupProject.Service.SuburbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController{

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private SuburbService suburbService;

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAllCustomers();
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerService.findCustomerById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping
    public Customer updateCurrentCustomer(@RequestBody Customer customer, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();
        customer.setId(userId);
        customer.setRole(Role.ROLE_CUSTOMER);

        Customer existingCustomer = customerService.findCustomerById(userId);

        customer.setServiceRequests(existingCustomer.getServiceRequests());
        customer.setReviews(existingCustomer.getReviews());

        // Find or create the suburb
        Suburb existingSuburb = suburbService.findSuburbByNameAndState(customer.getSuburb().getName(), customer.getSuburb().getState());

        if (existingSuburb == null || existingSuburb.getLatitude() == 0.0 || existingSuburb.getLongitude() == 0.0) {
            // Get latitude and longitude for the suburb
            LatLng latLng = geocodingService.getLatLng(customer.getSuburb().getName(), customer.getSuburb().getState());

            // Create or update the suburb
            Suburb suburb = suburbService.findOrCreateSuburb(customer.getSuburb().getName(), customer.getSuburb().getState(), latLng.getLat(), latLng.getLng());

            // Set the suburb for the customer
            customer.setSuburb(suburb);
        } else {
            // Use the existing suburb
            customer.setSuburb(existingSuburb);
        }

        return customerService.saveCustomer(customer);
    }
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

}