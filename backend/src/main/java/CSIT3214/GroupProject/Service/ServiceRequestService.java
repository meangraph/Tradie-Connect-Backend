package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.*;
import CSIT3214.GroupProject.Model.*;
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
    private ServiceRequestApplicantRepository serviceRequestApplicantRepository;


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
            serviceRequest.setCost(serviceRequestDTO.getCost());
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
            return serviceRequestRepository.findById(savedServiceRequest.getId()).orElse(null);
        } else {
            return null;
        }
    }

    public void addServiceProviderToQualifiedRequests(ServiceProvider serviceProvider) {
        List<ServiceRequest> serviceRequestsByType = serviceRequestRepository.findByServiceTypeInSet(serviceProvider.getSkills());

        for (ServiceRequest serviceRequest : serviceRequestsByType) {
            if (isValidServiceProvider(serviceProvider, serviceRequest)) {
                serviceRequest.getQualifiedServiceProviders().add(serviceProvider);
                serviceRequestRepository.save(serviceRequest);
            }
        }
    }

    public boolean isValidServiceProvider(ServiceProvider serviceProvider, ServiceRequest serviceRequest) {
        Suburb customerSuburb = serviceRequest.getCustomer().getSuburb();
        double customerLatitude = customerSuburb.getLatitude();
        double customerLongitude = customerSuburb.getLongitude();
        double serviceProviderLatitude = serviceProvider.getSuburb().getLatitude();
        double serviceProviderLongitude = serviceProvider.getSuburb().getLongitude();

        double distance = haversine(customerLatitude, customerLongitude, serviceProviderLatitude, serviceProviderLongitude);

        System.out.println("Distance for ServiceProvider " + serviceProvider.getId() + ": " + distance);

        return distance <= 50;
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
                //serviceProvider.setDistance(distance);
                validServiceProviders.add(serviceProvider);
            }
        }

        return validServiceProviders;
    }

    //Haversine formula adapted from https://gist.github.com/vananth22/888ed9a22105670e7a4092bdcf0d72e4
    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public void applyForServiceRequest(ServiceRequest serviceRequest, ServiceProvider serviceProvider, Double distance) {
        ServiceRequestApplicant sra = new ServiceRequestApplicant();
        sra.setServiceRequest(serviceRequest);
        sra.setServiceProvider(serviceProvider);
        sra.setDistance(distance);

        // Save the ServiceRequestApplicant
        serviceRequestApplicantRepository.save(sra);

        // Update the ServiceRequest status
        serviceRequest.setStatus(OrderStatus.PENDING);
        serviceRequestRepository.save(serviceRequest);
    }

    public void acceptServiceProvider(ServiceRequest serviceRequest, ServiceProvider serviceProvider) {
        serviceRequest.acceptServiceProvider(serviceProvider);
        serviceRequest.setStatus(OrderStatus.ACCEPTED);
        serviceRequestRepository.save(serviceRequest);
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