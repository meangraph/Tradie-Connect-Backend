package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.GeoCoding.LatLng;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import CSIT3214.GroupProject.Model.Suburb;
import CSIT3214.GroupProject.Service.GeocodingService;
import CSIT3214.GroupProject.Service.ServiceProviderService;
import CSIT3214.GroupProject.Service.SuburbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/service-providers")
public class ServiceProviderController extends BaseController {

    @Autowired
    private ServiceProviderService serviceProviderService;
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private SuburbService suburbService;

    @GetMapping("/all")
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderService.findAllServiceProviders();
    }

    @GetMapping
    public ServiceProvider getCurrentServiceProvider(HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();
        return serviceProviderService.findServiceProviderById(userId);
    }

    @PostMapping
    public ServiceProvider createServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        return serviceProviderService.saveServiceProvider(serviceProvider);
    }

    @PutMapping
    public ServiceProvider updateCurrentServiceProvider(@RequestBody ServiceProvider serviceProvider, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();
        serviceProvider.setId(userId);
        serviceProvider.setRole(Role.ROLE_SERVICE_PROVIDER);
        Suburb existingSuburb = suburbService.findSuburbByNameAndState(serviceProvider.getSuburb().getName(), serviceProvider.getSuburb().getState());

        ServiceProvider existingServiceProvider = serviceProviderService.findServiceProviderById(userId);

        serviceProvider.setReviews(existingServiceProvider.getReviews());
        serviceProvider.setSkills(existingServiceProvider.getSkills());
        serviceProvider.setServiceRequests(existingServiceProvider.getServiceRequests());


        if (existingSuburb == null || existingSuburb.getLatitude() == 0.0 || existingSuburb.getLongitude() == 0.0) {
            // Get latitude and longitude for the suburb
            LatLng latLng = geocodingService.getLatLng(serviceProvider.getSuburb().getName(), serviceProvider.getSuburb().getState());

            // Create or update the suburb
            Suburb suburb = suburbService.findOrCreateSuburb(serviceProvider.getSuburb().getName(), serviceProvider.getSuburb().getState(), latLng.getLat(), latLng.getLng());

            // Set the suburb for the customer
            serviceProvider.setSuburb(suburb);
        } else {
            // Use the existing suburb
            serviceProvider.setSuburb(existingSuburb);
        }

        return serviceProviderService.saveServiceProvider(serviceProvider);
    }


    @DeleteMapping
    public void deleteCurrentServiceProvider(HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();
        serviceProviderService.deleteServiceProvider(userId);
    }

    @PostMapping("/skills/{skill}")
    public void addSkillToCurrentServiceProvider(@PathVariable Skill skill, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long serviceProviderId = userIdAndRole.getUserId();
        serviceProviderService.addSkillToServiceProvider(serviceProviderId, skill);
    }

    @DeleteMapping("/skills/{skill}")
    public void removeSkillFromCurrentServiceProvider(@PathVariable Skill skill, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long serviceProviderId = userIdAndRole.getUserId();
        serviceProviderService.removeSkillFromServiceProvider(serviceProviderId, skill);
    }

    @GetMapping("/{id}")
    public ServiceProvider getServiceProviderById(@PathVariable Long id) {
        return serviceProviderService.findServiceProviderById(id);
    }

}
