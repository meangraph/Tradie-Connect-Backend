package CSIT3214.GroupProject.Config;

import CSIT3214.GroupProject.DataAccessLayer.CustomUserDetails;
import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.DataAccessLayer.SystemAdminRepository;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.ServiceProvider;
import CSIT3214.GroupProject.Model.SystemAdmin;
import CSIT3214.GroupProject.Model.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {


    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final SystemAdminRepository systemAdminRepository;

    @Bean
    @Transactional
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = null;
            try {
                Customer customer = customerRepository.findByEmail(username).orElse(null);
                ServiceProvider serviceProvider = serviceProviderRepository.findByEmail(username).orElse(null);
                SystemAdmin systemAdmin = systemAdminRepository.findByEmail(username).orElse(null); // Add this line

                if (customer != null) {
                    user = customer;
                } else if (serviceProvider != null) {
                    user = serviceProvider;
                } else if (systemAdmin != null) {
                    user = systemAdmin;
                } else {
                    throw new UsernameNotFoundException("User not found with username: " + username);
                }

            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));

            return new CustomUserDetails(user, authorities);
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
