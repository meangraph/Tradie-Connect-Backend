package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController extends BaseController{

    @Autowired
    private CustomerService customerService;

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
        return customerService.saveCustomer(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

}