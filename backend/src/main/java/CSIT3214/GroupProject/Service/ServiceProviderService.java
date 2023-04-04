package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ServiceProvider saveServiceProvider(ServiceProvider serviceProvider) {
        return serviceProviderRepository.save(serviceProvider);
    }

    public void deleteServiceProvider(Long id) {
        serviceProviderRepository.deleteById(id);
    }

    public void addSkillToServiceProvider(Long serviceProviderId, Skill skill) {
        ServiceProvider serviceProvider = findServiceProviderById(serviceProviderId);
        if (serviceProvider != null) {
            serviceProvider.addSkill(skill);
            saveServiceProvider(serviceProvider);
        }
    }

    public void removeSkillFromServiceProvider(Long serviceProviderId, Skill skill) {
        ServiceProvider serviceProvider = findServiceProviderById(serviceProviderId);
        if (serviceProvider != null) {
            serviceProvider.removeSkill(skill);
            saveServiceProvider(serviceProvider);
        }
    }
}