package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Service.CustomerService;
import CSIT3214.GroupProject.Service.GeocodingService;
import CSIT3214.GroupProject.Service.SuburbService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @MockBean
    private GeocodingService geocodingService;

    @MockBean
    private SuburbService suburbService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CustomerController customerController;


    @Test
    void getAllCustomers() {
        // Setup
        Customer customer1 = new Customer();
        customer1.setFirstName("Test");
        customer1.setLastName("User");

        Customer customer2 = new Customer();
        customer2.setFirstName("John");
        customer2.setLastName("Doe");

        when(customerService.findAllCustomers()).thenReturn(Arrays.asList(customer1, customer2));

        // Exercise
        List<Customer> result = customerController.getAllCustomers();

        // Verify
        assertEquals(2, result.size());
        assertEquals("Test", result.get(0).getFirstName());
        assertEquals("User", result.get(0).getLastName());
        assertEquals("John", result.get(1).getFirstName());
        assertEquals("Doe", result.get(1).getLastName());
    }

    @Test
    void createCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("User");
        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

        // Exercise
        Customer result = customerController.createCustomer(customer);

        // Verify
        assertEquals("Test", result.getFirstName());
        assertEquals("User", result.getLastName());
    }


    @Test
    void deleteCustomer() {
        assertDoesNotThrow(() -> customerController.deleteCustomer(1L));
    }
}