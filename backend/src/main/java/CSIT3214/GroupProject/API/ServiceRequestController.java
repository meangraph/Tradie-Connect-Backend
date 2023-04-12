package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.AcceptServiceRequestDTO;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.ServiceRequest;
import CSIT3214.GroupProject.Model.Skill;
import CSIT3214.GroupProject.Model.User;
import CSIT3214.GroupProject.Service.ServiceRequestService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;
    @Autowired
    private JwtService jwtService;

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
    @PostMapping("/create/{customerId}/{serviceType}")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequest createServiceRequest(@PathVariable Long customerId, @PathVariable Skill serviceType) {
        ServiceRequest createdServiceRequest = serviceRequestService.createServiceRequest(customerId, serviceType, new ServiceRequest());
        if (createdServiceRequest == null) {
            throw new BadRequestException("Unable to create service request. Invalid customer or service type ID.");
        }
        return createdServiceRequest;
    }

    @PutMapping("/accept/{serviceRequestId}/{serviceProviderId}")
    @ResponseStatus(HttpStatus.OK)
    public ServiceRequest tradieAcceptsServiceRequest(@PathVariable Long serviceProviderId,
                                                      @PathVariable Long serviceRequestId,
                                                      @RequestBody AcceptServiceRequestDTO acceptServiceRequestDTO) {
        ServiceRequest acceptedServiceRequest = serviceRequestService.acceptServiceRequest(serviceProviderId, serviceRequestId, acceptServiceRequestDTO);
        if (acceptedServiceRequest == null) {
            throw new BadRequestException("Unable to accept service request. Invalid service provider or service request ID.");
        }
        return acceptedServiceRequest;
    }
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PutMapping("/accept/{serviceRequestId}/customer")
    @ResponseStatus(HttpStatus.OK)
    public ServiceRequest customerAcceptsRequest(@PathVariable Long serviceRequestId) {
        ServiceRequest acceptedServiceRequest = serviceRequestService.customerAcceptsRequest(serviceRequestId);
        if (acceptedServiceRequest == null) {
            throw new BadRequestException("Unable to accept service request. Invalid service request ID.");
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

}
//Exceptions
    @ResponseStatus(HttpStatus.NOT_FOUND)
    class ServiceRequestNotFoundException extends RuntimeException {
    public ServiceRequestNotFoundException(String message) {
        super(message);
    }
}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
