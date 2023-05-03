package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServiceProviderService {

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    public List<ServiceProvider> findAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }

    public ServiceProvider findServiceProviderById(Long id) {
        return serviceProviderRepository.findById(id).orElse(null);
    }
    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    public ServiceProvider saveServiceProvider(ServiceProvider serviceProvider) {
        serviceProvider.setRole(Role.ROLE_SERVICE_PROVIDER);
        return serviceProviderRepository.save(serviceProvider);
    }

    public void deleteServiceProvider(Long id) {
        serviceProviderRepository.deleteById(id);
    }
    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    public void addSkillToServiceProvider(Long serviceProviderId, Skill skill) {
        ServiceProvider serviceProvider = findServiceProviderById(serviceProviderId);
        if (serviceProvider != null) {
            serviceProvider.addSkill(skill);
            saveServiceProvider(serviceProvider);
        }
    }
    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    public void removeSkillFromServiceProvider(Long serviceProviderId, Skill skill) {
        ServiceProvider serviceProvider = findServiceProviderById(serviceProviderId);
        if (serviceProvider != null) {
            serviceProvider.removeSkill(skill);
            saveServiceProvider(serviceProvider);
        }
    }
}