package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.AcceptServiceRequestDTO;
import CSIT3214.GroupProject.DataAccessLayer.CreateServiceRequestDTO;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceRequestRepository;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.ServiceRequest;
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
    private JwtService jwtService;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    @GetMapping
    public List<ServiceRequest> getAllServiceRequests() {
        return serviceRequestService.findAllServiceRequests();
    }

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

    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    @PutMapping("/accept")
    @ResponseStatus(HttpStatus.OK)
    public ServiceRequest tradieAcceptsServiceRequest(@RequestBody AcceptServiceRequestDTO dto, HttpServletRequest request) {
        return serviceRequestService.acceptServiceRequest(dto, request);
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/accept/{serviceRequestId}/customer")
    @ResponseStatus(HttpStatus.OK)
    public ServiceRequest customerAcceptsRequest(@PathVariable Long serviceRequestId) {
        ServiceRequest acceptedServiceRequest = serviceRequestService.customerAcceptsRequest(serviceRequestId);
        if (acceptedServiceRequest == null) {
            //TODO: handle error
        }
        return acceptedServiceRequest;
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

