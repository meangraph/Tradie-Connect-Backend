package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.CreateServiceRequestDTO;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceRequestRepository;
import CSIT3214.GroupProject.Model.*;
import CSIT3214.GroupProject.Service.ServiceRequestService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @PreAuthorize("hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping
    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestService.findAllServiceRequests();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SYSTEM_ADMIN', 'ROLE_CUSTOMER', 'ROLE_SERVICE_PROVIDER')")
    @GetMapping("/{id}")
    public ServiceRequest getServiceRequestById(@PathVariable Long id) {
        ServiceRequest serviceRequest = serviceRequestService.findServiceRequestById(id);
        if (serviceRequest == null) {
            throw new ServiceRequestNotFoundException("Service request not found with ID: " + id);
        }
        return serviceRequest;
    }
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequest createServiceRequest(@RequestBody CreateServiceRequestDTO serviceRequestDTO, HttpServletRequest request) {
        Long customerId = extractUserIdFromRequest(request);
        ServiceRequest createdServiceRequest = serviceRequestService.createServiceRequest(customerId, serviceRequestDTO);
        if (createdServiceRequest == null) {
            //TODO: handle error
        }
        return createdServiceRequest;
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/{serviceRequestId}/valid-service-providers")
    public List<ServiceProvider> getValidServiceProviders(@PathVariable Long serviceRequestId) {
        ServiceRequest serviceRequest = serviceRequestService.findServiceRequestById(serviceRequestId);
        if (serviceRequest == null) {
            //TODO: handle error
        }
        return serviceRequestService.findValidServiceProviders(serviceRequest);
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    @PostMapping("/{serviceRequestId}/apply")
    public void applyForServiceRequest(@PathVariable Long serviceRequestId, HttpServletRequest request) {
        try {
            Long serviceProviderId = extractUserIdFromRequest(request);
            ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId).orElse(null);
            ServiceRequest serviceRequest = serviceRequestService.findServiceRequestById(serviceRequestId);

            if (serviceRequest == null || serviceProvider == null) {
                System.out.println("invalid service request ID or service provider ID");
            }

        // Calculate the distance between the serviceProvider and the customer
        double distance = serviceRequestService.haversine(
                serviceProvider.getSuburb().getLatitude(),
                serviceProvider.getSuburb().getLongitude(),
                serviceRequest.getCustomer().getSuburb().getLatitude(),
                serviceRequest.getCustomer().getSuburb().getLongitude());

        serviceRequestService.applyForServiceRequest(serviceRequest, serviceProvider, distance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    @PostMapping("/{serviceRequestId}/complete")
    public void completeServiceRequest(@PathVariable Long serviceRequestId,HttpServletRequest request){
        ServiceRequest serviceRequest = serviceRequestService.findServiceRequestById(serviceRequestId);

        serviceRequest.setStatus(OrderStatus.COMPLETED);
        serviceRequestRepository.save(serviceRequest);

    }


    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/{serviceRequestId}/accept-service-provider/{serviceProviderId}")
    public void acceptServiceProvider(@PathVariable Long serviceRequestId, @PathVariable Long serviceProviderId) {
        ServiceRequest serviceRequest = serviceRequestService.findServiceRequestById(serviceRequestId);
        if (serviceRequest == null) {
            //TODO:
        }
        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId).orElse(null);

        serviceRequestService.acceptServiceProvider(serviceRequest, serviceProvider);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_SERVICE_PROVIDER')")
    @GetMapping("/user-requests")
    public List<ServiceRequest> getServiceRequestsForCurrentUser(HttpServletRequest request) {
        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null) {
            // Handle the case when JWT is not found in cookies.
            throw new IllegalArgumentException("JWT not found in cookies");
        }

        Claims claims = jwtService.extractAllClaims(jwt);
        Number userIdNumber = (Number) claims.get("userId");
        if (userIdNumber == null) {
            throw new IllegalArgumentException("User ID not found in JWT claims");
        }

        Long userId = userIdNumber.longValue();
        Role role = Role.valueOf((String) claims.get("role"));

        return serviceRequestService.findServiceRequestsByUserIdAndRole(userId, role);
    }



    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    @GetMapping("/customer-details")
    public Customer getCustomerDetailsFromServiceRequest(HttpServletRequest request) {
        Long customerId = extractUserIdFromRequest(request);
        return serviceRequestService.getCustomerDetails(customerId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SERVICE_PROVIDER')")
    @GetMapping("/sp-details")
    public ServiceProvider getServiceProviderDetailsFromServiceRequest(HttpServletRequest request) {
        Long serviceProviderId = extractUserIdFromRequest(request);
        return serviceRequestService.getServiceProviderDetails(serviceProviderId);
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        Claims claims = jwtService.extractAllClaims(jwt);
        Number userIdNumber = (Number) claims.get("userId");
        if (userIdNumber == null) {
            throw new IllegalArgumentException("User ID not found in JWT claims");
        }

        return userIdNumber.longValue();
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new IllegalArgumentException("JWT not found in cookies");
    }

}
//Exceptions
@ResponseStatus(HttpStatus.NOT_FOUND)
class ServiceRequestNotFoundException extends RuntimeException {
    public ServiceRequestNotFoundException(String message) {
        super(message);
    }
}
