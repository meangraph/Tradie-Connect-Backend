package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.DataAccessLayer.PaymentRepository;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Payment;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Service.PaymentService;
import com.stripe.model.Charge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private Charge charge;
    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(200.0);
        payment.setTransactionDate(LocalDateTime.now());

        Customer customer = new Customer();
        customer.setId(1L);
        payment.setCustomer(customer);

        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setId(1L);
        payment.setServiceProvider(serviceProvider);

        ReflectionTestUtils.setField(paymentService, "stripeApiKey", "sk_test_51Ms2crA1yYV7uGsNIaCXVOP3fgZRFkzs9D8oQEih7RdtaGaVukU7hKl5lvtA79HtYduMRaqxf4I4JMHLwaj8UPKz00Jp7cAQpl");
        paymentService.init();
    }


    @Test
    void savePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment result = paymentService.savePayment(payment);

        verify(paymentRepository, times(1)).save(payment);
        assertEquals(payment, result);
    }

    @Test
    void getAllPayments() {
        List<Payment> payments = Arrays.asList(payment);
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getAllPayments();

        verify(paymentRepository, times(1)).findAll();
        assertEquals(payments, result);
    }
}
