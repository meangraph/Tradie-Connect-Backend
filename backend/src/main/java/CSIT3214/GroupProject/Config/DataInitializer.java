package CSIT3214.GroupProject.Config;

import CSIT3214.GroupProject.DataAccessLayer.SystemAdminRepository;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.SystemAdmin;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final SystemAdminRepository systemAdminRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SystemAdminRepository systemAdminRepository, PasswordEncoder passwordEncoder) {
        this.systemAdminRepository = systemAdminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createSystemAdmin() {
        String email = "admin@example.com";
        if (systemAdminRepository.findByEmail(email).isEmpty()) {
            SystemAdmin systemAdmin = new SystemAdmin();
            systemAdmin.setEmail(email);
            systemAdmin.setPassword(passwordEncoder.encode("password"));
            systemAdmin.setRole(Role.ROLE_SYSTEM_ADMIN);


            systemAdminRepository.save(systemAdmin);
        }
    }
}
