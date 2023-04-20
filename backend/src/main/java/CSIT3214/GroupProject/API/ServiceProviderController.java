package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.*;
import CSIT3214.GroupProject.Model.GeoCoding.LatLng;
import CSIT3214.GroupProject.Service.GeocodingService;
import CSIT3214.GroupProject.Service.ServiceProviderService;
import CSIT3214.GroupProject.Service.SuburbService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

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

    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
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
    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    @PutMapping
    public ServiceProvider updateCurrentServiceProvider(@RequestBody Map < String, Object > updatedFields, HttpServletRequest request) {
        UserIdAndRole userIdAndRole = getUserIdAndRoleFromJwt(request);
        Long userId = userIdAndRole.getUserId();

        ServiceProvider existingServiceProvider = serviceProviderService.findServiceProviderById(userId);
        if (existingServiceProvider == null) {
            throw new IllegalArgumentException("Service provider not found");
        }

        // Iterate through the updatedFields map and update the existingServiceProvider object using Reflection API
        for (Map.Entry < String, Object > entry: updatedFields.entrySet()) {
            if ("suburb".equals(entry.getKey())) {
                // Skip the suburb field here, as we will handle it separately later.
                continue;
            }

            try {
                Field field;
                try {
                    field = ServiceProvider.class.getDeclaredField(entry.getKey());
                } catch (NoSuchFieldException e) {
                    // Try to find the field in the base class
                    field = User.class.getDeclaredField(entry.getKey());
                }
                field.setAccessible(true);
                field.set(existingServiceProvider, entry.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Handle the exception or log it
            }
        }

        // Handle suburb update separately
        if (updatedFields.containsKey("suburb")) {
            Map < String, String > suburbData = (Map < String, String > ) updatedFields.get("suburb");
            String suburbName = suburbData.get("name");
            String suburbState = suburbData.get("state");

            Suburb existingSuburb = suburbService.findSuburbByNameAndState(suburbName, suburbState);

            if (existingSuburb == null || existingSuburb.getLatitude() == 0.0 || existingSuburb.getLongitude() == 0.0) {
                LatLng latLng = geocodingService.getLatLng(suburbName, suburbState);

                Suburb suburb = suburbService.findOrCreateSuburb(suburbName, suburbState, latLng.getLat(), latLng.getLng());
                existingServiceProvider.setSuburb(suburb);
            } else {
                existingServiceProvider.setSuburb(existingSuburb);
            }
        }

        return serviceProviderService.saveServiceProvider(existingServiceProvider);
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
