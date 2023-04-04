package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import CSIT3214.GroupProject.Service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/service-providers")
public class ServiceProviderController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    @GetMapping
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderService.findAllServiceProviders();
    }

    @GetMapping("/{id}")
    public ServiceProvider getServiceProviderById(@PathVariable Long id) {
        return serviceProviderService.findServiceProviderById(id);
    }

    @PostMapping
    public ServiceProvider createServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        return serviceProviderService.saveServiceProvider(serviceProvider);
    }

    @PutMapping("/{id}")
    public ServiceProvider updateServiceProvider(@PathVariable Long id, @RequestBody ServiceProvider serviceProvider) {
        serviceProvider.setId(id);
        return serviceProviderService.saveServiceProvider(serviceProvider);
    }

    @DeleteMapping("/{id}")
    public void deleteServiceProvider(@PathVariable Long id) {
        serviceProviderService.deleteServiceProvider(id);
    }

    @PostMapping("/{serviceProviderId}/skills/{skill}")
    public void addSkillToServiceProvider(@PathVariable Long serviceProviderId, @PathVariable Skill skill) {
        serviceProviderService.addSkillToServiceProvider(serviceProviderId, skill);
    }

    @DeleteMapping("/{serviceProviderId}/skills/{skill}")
    public void removeSkillFromServiceProvider(@PathVariable Long serviceProviderId, @PathVariable Skill skill) {
        serviceProviderService.removeSkillFromServiceProvider(serviceProviderId, skill);
    }
}