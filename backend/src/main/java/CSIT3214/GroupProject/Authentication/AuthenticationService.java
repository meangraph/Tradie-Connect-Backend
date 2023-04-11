package CSIT3214.GroupProject.Authentication;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.CustomUserDetails;
import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.Model.Customer;
import CSIT3214.GroupProject.Model.Role;
import CSIT3214.GroupProject.Model.ServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        UserDetails userDetails;
        if (request.getRole() == Role.CUSTOMER) {
            var customer = new Customer();
            customer.setEmail(request.getEmail());
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setRole(Role.CUSTOMER);
            customerRepository.save(customer);
            userDetails = new CustomUserDetails(customer, List.of(new SimpleGrantedAuthority(customer.getRole().toString())));
        } else {
            var serviceProvider = new ServiceProvider();
            serviceProvider.setEmail(request.getEmail());
            serviceProvider.setPassword(passwordEncoder.encode(request.getPassword()));
            serviceProvider.setRole(Role.SERVICE_PROVIDER);
            serviceProviderRepository.save(serviceProvider);
            userDetails = new CustomUserDetails(serviceProvider, List.of(new SimpleGrantedAuthority(serviceProvider.getRole().toString())));
        }

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails;
        var customer = customerRepository.findByEmail(request.getEmail());
        if (customer.isPresent()) {
            userDetails = new CustomUserDetails(customer.get(), List.of(new SimpleGrantedAuthority(customer.get().getRole().toString())));
        } else {
            var serviceProvider = serviceProviderRepository.findByEmail(request.getEmail()).orElseThrow();
            userDetails = new CustomUserDetails(serviceProvider, List.of(new SimpleGrantedAuthority(serviceProvider.getRole().toString())));
        }

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}

