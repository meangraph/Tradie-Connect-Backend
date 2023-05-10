package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.*;
import CSIT3214.GroupProject.Model.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ServiceRequestService {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private JwtService jwtService;

    public List<ServiceRequest> findAllServiceRequests() {
        return serviceRequestRepository.findAll();
    }

    public ServiceRequest findServiceRequestById(Long id) {
        return serviceRequestRepository.findById(id).orElse(null);
    }

    public ServiceRequest createServiceRequest(Long customerId, CreateServiceRequestDTO serviceRequestDTO) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isPresent()) {
            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setCustomer(customer.get());
            serviceRequest.setServiceType(serviceRequestDTO.getServiceType());
            serviceRequest.setStatus(OrderStatus.CREATED);
            serviceRequest.setRequestedTime(LocalTime.from(LocalDateTime.now()));
            serviceRequest.setDescription(serviceRequestDTO.getDescription());

            // Check if the customer has a CLIENT_SUBSCRIPTION membership type
            if (customer.get().getMembership() != null &&
                    customer.get().getMembership().getMembershipType() == MembershipType.CLIENT_SUBSCRIPTION) {
                serviceRequest.setCost(0.0);
            } else {
                serviceRequest.setCost(serviceRequestDTO.getCost());
            }

            serviceRequest.setScheduledStartDate(serviceRequestDTO.getStartDate());
            serviceRequest.setScheduledStartTime(serviceRequestDTO.getStartTime());
            serviceRequest.setScheduledEndDate(serviceRequestDTO.getEndDate());
            serviceRequest.setScheduledEndTime(serviceRequestDTO.getEndTime());

            ServiceRequest savedServiceRequest = serviceRequestRepository.save(serviceRequest);

            // Find valid service providers
            List<ServiceProvider> validServiceProviders = findValidServiceProviders(savedServiceRequest);
            System.out.println("Valid Service Providers size: " + validServiceProviders.size());

            // Add the saved service request to the valid service providers' qualified service requests
            for (ServiceProvider serviceProvider : validServiceProviders) {
                serviceProvider.getQualifiedServiceRequests().add(savedServiceRequest);
                savedServiceRequest.getQualifiedServiceProviders().add(serviceProvider);
                serviceProviderRepository.save(serviceProvider);
            }

            // Retrieve the updated ServiceRequest object from the database
            ServiceRequest updatedServiceRequest = serviceRequestRepository.findById(savedServiceRequest.getId()).orElse(null);


            return updatedServiceRequest;
        } else {
            return null;
        }
    }


    public List<ServiceProvider> findValidServiceProviders(ServiceRequest serviceRequest) {
        Suburb customerSuburb = serviceRequest.getCustomer().getSuburb();
        double customerLatitude = customerSuburb.getLatitude();
        double customerLongitude = customerSuburb.getLongitude();

        List<ServiceProvider> serviceProviderBySkill = serviceProviderRepository.findByServiceType(serviceRequest.getServiceType());

        System.out.println("Service providers by skill size: " + serviceProviderBySkill.size());

        List<ServiceProvider> validServiceProviders = new ArrayList<>();

        for (ServiceProvider serviceProvider : serviceProviderBySkill) {
            double serviceProviderLatitude = serviceProvider.getSuburb().getLatitude();
            double serviceProviderLongitude = serviceProvider.getSuburb().getLongitude();

            double distance = haversine(customerLatitude, customerLongitude, serviceProviderLatitude, serviceProviderLongitude);

            System.out.println("Distance for ServiceProvider " + serviceProvider.getId() + ": " + distance);

            if (distance <= 50) {
                validServiceProviders.add(serviceProvider);
            }
        }

        return validServiceProviders;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public void applyForServiceRequest(ServiceRequest serviceRequest, ServiceProvider serviceProvider) {
        //serviceRequest.getApplicants().add(serviceProvider);
        serviceRequest.addApplicant(serviceProvider);
        serviceRequestRepository.save(serviceRequest);
    }

    public void acceptServiceProvider(ServiceRequest serviceRequest, ServiceProvider serviceProvider) {
        serviceRequest.acceptServiceProvider(serviceProvider);
        serviceRequest.setStatus(OrderStatus.ACCEPTED);
        serviceRequestRepository.save(serviceRequest);
    }
    private String getJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Long getUserIdFromClaims(Claims claims) {
        Number userIdNumber = (Number) claims.get("userId");
        if (userIdNumber == null) {
            throw new IllegalArgumentException("User ID not found in JWT claims");
        }
        return userIdNumber.longValue();
    }

    private Role getRoleFromClaims(Claims claims) {
        String roleString = (String) claims.get("role");
        if (roleString == null) {
            throw new IllegalArgumentException("Role not found in JWT claims");
        }
        return Role.valueOf(roleString);
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = getJwtFromCookies(request);
        if (jwt == null) {
            throw new IllegalArgumentException("JWT not found in cookies");
        }
        Claims claims = jwtService.extractAllClaims(jwt);
        return getUserIdFromClaims(claims);
    }



    public ServiceRequest customerAcceptsRequest(Long serviceRequestId) {
        Optional<ServiceRequest> serviceRequest = serviceRequestRepository.findById(serviceRequestId);

        if (serviceRequest.isPresent()) {
            ServiceRequest requestToUpdate = serviceRequest.get();
            if (requestToUpdate.getStatus() == OrderStatus.QUOTED_AWAITING_CUSTOMER_APPROVAL) {
                requestToUpdate.setStatus(OrderStatus.ACCEPTED);
                return serviceRequestRepository.save(requestToUpdate);
            } else {
                throw new IllegalStateException("Service request must be in the QUOTED status to be accepted.");
            }
        } else {
            return null;
        }
    }

    public List<ServiceRequest> findServiceRequestsByUserIdAndRole(Long userId, Role role) {
        if (role == Role.ROLE_CUSTOMER) {
            return serviceRequestRepository.findByCustomerId(userId);
        } else if (role == Role.ROLE_SERVICE_PROVIDER) {
            return serviceRequestRepository.findByServiceProviderId(userId);
        } else {
            return new ArrayList<>();
        }
    }

    public Customer getCustomerDetails(Long customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    public ServiceProvider getServiceProviderDetails(Long serviceProviderId) {
        return serviceProviderRepository.findById(serviceProviderId).orElse(null);
    }
}