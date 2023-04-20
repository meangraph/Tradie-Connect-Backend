package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.GeoCoding.LatLng;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.Suburb;
import CSIT3214.GroupProject.Model.User;
import CSIT3214.GroupProject.Service.CustomerService;
import CSIT3214.GroupProject.Service.GeocodingService;
import CSIT3214.GroupProject.Service.SuburbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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
    public Customer updateCurrentCustomer(@RequestBody Map<String, Object> updatedFields, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();

        Customer existingCustomer = customerService.findCustomerById(userId);
        if (existingCustomer == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Iterate through the updatedFields map and update the existingCustomer object using Reflection API
        for (Map.Entry<String, Object> entry : updatedFields.entrySet()) {
            if ("suburb".equals(entry.getKey())) {
                // Skip the suburb field here, as we will handle it separately later.
                continue;
            }

            try {
                Field field;
                try {
                    field = Customer.class.getDeclaredField(entry.getKey());
                } catch (NoSuchFieldException e) {
                    // Try to find the field in the base class
                    field = User.class.getDeclaredField(entry.getKey());
                }
                field.setAccessible(true);
                field.set(existingCustomer, entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle the exception or log it
            }
        }

        // Handle suburb update separately
        if (updatedFields.containsKey("suburb")) {
            Map<String, String> suburbData = (Map<String, String>) updatedFields.get("suburb");
            String suburbName = suburbData.get("name");
            String suburbState = suburbData.get("state");

            Suburb existingSuburb = suburbService.findSuburbByNameAndState(suburbName, suburbState);
            // ... rest of the suburb handling logic
            if (existingSuburb == null || existingSuburb.getLatitude() == 0.0 || existingSuburb.getLongitude() == 0.0) {
                LatLng latLng = geocodingService.getLatLng(suburbName, suburbState);

                Suburb suburb = suburbService.findOrCreateSuburb(suburbName, suburbState, latLng.getLat(), latLng.getLng());
                existingCustomer.setSuburb(suburb);
            } else {
                existingCustomer.setSuburb(existingSuburb);
            }
        }

        return customerService.saveCustomer(existingCustomer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

}