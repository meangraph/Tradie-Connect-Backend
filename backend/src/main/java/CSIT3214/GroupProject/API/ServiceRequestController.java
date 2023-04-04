package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.DataAccessLayer.AcceptServiceRequestDTO;
import CSIT3214.GroupProject.Model.ServiceRequest;
import CSIT3214.GroupProject.Model.Skill;
import CSIT3214.GroupProject.Service.ServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    @Autowired
    private ServiceRequestService serviceRequestService;

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

    @PutMapping("/accept/{serviceRequestId}/customer")
    @ResponseStatus(HttpStatus.OK)
    public ServiceRequest customerAcceptsRequest(@PathVariable Long serviceRequestId) {
        ServiceRequest acceptedServiceRequest = serviceRequestService.customerAcceptsRequest(serviceRequestId);
        if (acceptedServiceRequest == null) {
            throw new BadRequestException("Unable to accept service request. Invalid service request ID.");
        }
        return acceptedServiceRequest;
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
