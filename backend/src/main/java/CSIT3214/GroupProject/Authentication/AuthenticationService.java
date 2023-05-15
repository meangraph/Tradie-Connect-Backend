package CSIT3214.GroupProject.Authentication;

import CSIT3214.GroupProject.Config.JwtService;
import CSIT3214.GroupProject.DataAccessLayer.*;
import CSIT3214.GroupProject.Model.*;
import CSIT3214.GroupProject.Service.MembershipService;
import CSIT3214.GroupProject.Service.PaymentService;
import CSIT3214.GroupProject.Service.ServiceRequestService;
import CSIT3214.GroupProject.Service.SuburbService;
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

    // Inject required services and repositories
    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SuburbService suburbService;
    private final MembershipService membershipService;
    private final SystemAdminRepository systemAdminRepository;
    private final ServiceRequestService serviceRequestService;

    // Method to register a new user
    public AuthenticationResponse register(UserDTO userDTO) {
        UserDetails userDetails;
        User user;
        // Check the user role and create an appropriate user object
        if (userDTO.getRole() == Role.ROLE_CUSTOMER) {
            var customer = new Customer();
            // Create a new Customer and set the properties from the provided userDTO
            customer.setFirstName(userDTO.getFirstName());
            customer.setLastName(userDTO.getLastName());
            customer.setEmail(userDTO.getEmail());
            customer.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            customer.setRole(Role.ROLE_CUSTOMER);
            customer.setPhoneNumber(userDTO.getPhoneNumber());
            customer.setStreetAddress(userDTO.getStreetAddress());
            customer.setPostCode(userDTO.getPostCode());

            Suburb suburb = suburbService.findOrCreateSuburb(userDTO.getSuburb().getName(), userDTO.getSuburb().getState(),
                    userDTO.getSuburb().getLatitude(), userDTO.getSuburb().getLongitude());
            customer.setSuburb(suburb);

            Membership membership = membershipService.saveMembership(userDTO.getMembership());

            customer.setMembership(membership);

            // Save the customer to the repository
            customerRepository.save(customer);
            // Create a new CustomUserDetails object containing the customer's information
            userDetails = new CustomUserDetails(customer, List.of(new SimpleGrantedAuthority(customer.getRole().toString())));
            user = customer;
        } else {
            var serviceProvider = new ServiceProvider();
            // Create a new ServiceProvider and set the properties from the provided userDTO
            serviceProvider.setCompanyName(userDTO.getCompanyName());
            serviceProvider.setAbn(userDTO.getAbn());
            serviceProvider.setEmail(userDTO.getEmail());
            serviceProvider.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            serviceProvider.setRole(Role.ROLE_SERVICE_PROVIDER);
            serviceProvider.setPhoneNumber(userDTO.getPhoneNumber());
            serviceProvider.setStreetAddress(userDTO.getStreetAddress());
            serviceProvider.setPostCode(userDTO.getPostCode());
            serviceProvider.setSkills(userDTO.getSkills());

            Suburb suburb = suburbService.findOrCreateSuburb(userDTO.getSuburb().getName(), userDTO.getSuburb().getState(),
                    userDTO.getSuburb().getLatitude(), userDTO.getSuburb().getLongitude());
            serviceProvider.setSuburb(suburb);

            Membership membership = membershipService.saveMembership(userDTO.getMembership());

            serviceProvider.setMembership(membership);

            // Save the service provider to the repository
            serviceProviderRepository.save(serviceProvider);
            // Create a new CustomUserDetails object containing the service provider's information
            userDetails = new CustomUserDetails(serviceProvider, List.of(new SimpleGrantedAuthority(serviceProvider.getRole().toString())));
            user = serviceProvider;
            serviceRequestService.addServiceProviderToQualifiedRequests(serviceProvider);
        }

        // Generate a JWT token for the user
        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build();
    }

    // Method to authenticate a user
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate the user using their email and password
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails;
        // Check if the user exists in the Customer repository, if not, check the ServiceProvider repository, and finally, check the SystemAdmin repository
        var customer = customerRepository.findByEmail(request.getEmail());
        if (customer.isPresent()) {
            userDetails = new CustomUserDetails(customer.get(), List.of(new SimpleGrantedAuthority(customer.get().getRole().toString())));
        } else {
            var serviceProvider = serviceProviderRepository.findByEmail(request.getEmail()).orElse(null);
            if (serviceProvider != null) {
                userDetails = new CustomUserDetails(serviceProvider, List.of(new SimpleGrantedAuthority(serviceProvider.getRole().toString())));
            } else {
                var systemAdmin = systemAdminRepository.findByEmail(request.getEmail()).orElseThrow(); // Add this line
                userDetails = new CustomUserDetails(systemAdmin, List.of(new SimpleGrantedAuthority(systemAdmin.getRole().toString()))); // Add this line
            }
        }
// Generate a JWT token for the user
        var jwtToken = jwtService.generateToken(userDetails);

// Return an AuthenticationResponse containing the JWT token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
