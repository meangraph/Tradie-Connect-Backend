package CSIT3214.GroupProject.API;

import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.Skill;
import CSIT3214.GroupProject.Service.ServiceProviderService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceProviderControllerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private ServiceProviderService serviceProviderService;
    @InjectMocks
    private ServiceProviderController serviceProviderController;
    private ServiceProvider serviceProvider;

    @BeforeEach
    void setUp() {
        serviceProvider = new ServiceProvider();
        serviceProvider.setId(1L);
    }

    @Test
    public void testGetAllServiceProviders() {
        List<ServiceProvider> expectedServiceProviders = List.of(serviceProvider);
        when(serviceProviderService.findAllServiceProviders()).thenReturn(expectedServiceProviders);
        List<ServiceProvider> actualServiceProviders = serviceProviderService.findAllServiceProviders();
        assertEquals(expectedServiceProviders, actualServiceProviders);
    }

    @Test
    public void testCreateServiceProvider() {
        when(serviceProviderService.saveServiceProvider(any(ServiceProvider.class))).thenReturn(serviceProvider);
        ServiceProvider actualServiceProvider = serviceProviderController.createServiceProvider(serviceProvider);
        assertEquals(serviceProvider, actualServiceProvider);
    }

    @Test
    public void testUpdateCurrentServiceProvider() {
        when(serviceProviderService.saveServiceProvider(any(ServiceProvider.class))).thenReturn(serviceProvider);
        ServiceProvider actualServiceProvider = serviceProviderService.saveServiceProvider(serviceProvider);
        assertEquals(serviceProvider, actualServiceProvider);
    }

    @Test
    public void testDeleteCurrentServiceProvider() {
        doNothing().when(serviceProviderService).deleteServiceProvider(anyLong());
        serviceProviderService.deleteServiceProvider(anyLong());
        verify(serviceProviderService, times(1)).deleteServiceProvider(anyLong());
    }

    @Test
    public void testAddSkillToCurrentServiceProvider() {
        Skill skill = Skill.OVEN_REPAIRS;
        doNothing().when(serviceProviderService).addSkillToServiceProvider(anyLong(), any(Skill.class));
        serviceProviderService.addSkillToServiceProvider(1L, skill);
        verify(serviceProviderService, times(1)).addSkillToServiceProvider(anyLong(), any(Skill.class));
    }

    @Test
    public void testRemoveSkillFromCurrentServiceProvider() {
        Skill skill = Skill.OVEN_REPAIRS;
        doNothing().when(serviceProviderService).removeSkillFromServiceProvider(anyLong(), any(Skill.class));
        serviceProviderService.removeSkillFromServiceProvider(1L, skill);
        verify(serviceProviderService, times(1)).removeSkillFromServiceProvider(anyLong(), any(Skill.class));
    }

    @Test
    public void testGetServiceProviderById() {
        when(serviceProviderService.findServiceProviderById(anyLong())).thenReturn(serviceProvider);
        ServiceProvider actualServiceProvider = serviceProviderController.getServiceProviderById(1L);
        assertEquals(serviceProvider, actualServiceProvider);
    }
}
