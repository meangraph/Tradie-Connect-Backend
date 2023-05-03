package CSIT3214.GroupProject.Config;

import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.DataAccessLayer.SystemAdminRepository;
import CSIT3214.GroupProject.Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final SystemAdminRepository systemAdminRepository;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Extract JWT token from the cookie
        Cookie[] cookies = request.getCookies();

        // If no cookies are present, continue with the filter chain
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }
        // Find the JWT cookie from the array of cookies
        Cookie jwtCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JWT")).findFirst().orElse(null);

        // If the JWT cookie is not present, continue with the filter chain
        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the email from the JWT token
        final String jwt = jwtCookie.getValue();
        final String email = jwtService.extractEmail(jwt);

        // If the email is not null and there is no authentication in the SecurityContextHolder, attempt to authenticate the user
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load the user details and find the user by email
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            User user = findUserByEmail(email);

            // If the token is valid, create an authentication token and set it in the SecurityContextHolder
            if (user != null && jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Check if the token is close to expiring and refresh it if necessary
                if (jwtService.isTokenCloseToExpiring(jwt)) {
                    // Add user ID and role to the extraClaims map
                    Map<String, Object> extraClaims = new HashMap<>();
                    extraClaims.put("userId", user.getId());
                    extraClaims.put("role", user.getRole().name());

                    // Generate token with extraClaims
                    String newJwt = jwtService.generateToken(extraClaims, userDetails);
                    // Set the new JWT as a cookie
                    Cookie newJwtCookie = new Cookie("JWT", newJwt);
                    newJwtCookie.setHttpOnly(true);
                    response.addCookie(newJwtCookie);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    // Method to find a user by email
    private User findUserByEmail(String email) {
        User user = customerRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = serviceProviderRepository.findByEmail(email).orElse(null);
        }
        if (user == null) { // Add this block
            user = systemAdminRepository.findByEmail(email).orElse(null);
        }
        return user;
    }
}