package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.CreateServiceRequestDTO;
import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceRequestRepository;
import CSIT3214.GroupProject.Model.ServiceRequest;
import CSIT3214.GroupProject.Service.ServiceRequestService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRequestControllerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private ServiceRequestService serviceRequestService;
    @Mock
    private ServiceRequestRepository serviceRequestRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private ServiceProviderRepository serviceProviderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private ServiceRequestController serviceRequestController;

    private ServiceRequest serviceRequest;

    @BeforeEach
    void setUp() {
        serviceRequest = new ServiceRequest();
        serviceRequest.setId(1L);
        //serviceRequestRepository.save(serviceRequest);
    }

    @Test
    void getAllServiceRequests() {
        List<ServiceRequest> serviceRequests = Arrays.asList(serviceRequest);
        when(serviceRequestService.findAllServiceRequests()).thenReturn(serviceRequests);

        List<ServiceRequest> result = serviceRequestController.getAllServiceRequests();
        assertEquals(serviceRequests, result);
    }

    @Test
    void getServiceRequestById() {
        when(serviceRequestService.findServiceRequestById(any(Long.class))).thenReturn(serviceRequest);
        ServiceRequest result = serviceRequestController.getServiceRequestById(1L);
        assertEquals(serviceRequest, result);
    }

    @Test
    void createServiceRequest() {

        CreateServiceRequestDTO serviceRequestDTO = new CreateServiceRequestDTO();
        ServiceRequest createdServiceRequest = new ServiceRequest();

        when(serviceRequestService.createServiceRequest(any(Long.class), any(CreateServiceRequestDTO.class))).thenReturn(createdServiceRequest);
        ServiceRequest result = serviceRequestService.createServiceRequest(1L,serviceRequestDTO);
        verify(serviceRequestService).createServiceRequest(1L, serviceRequestDTO);

        assertEquals(createdServiceRequest, result);
    }
}