package CSIT3214.GroupProject.Config;

import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.Model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter implements Ordered {

    private final JwtService jwtService;
    private final CustomerRepository customerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final UserDetailsService userDetailsService;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //extract info from the cookie
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Cookie jwtCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JWT")).findFirst().orElse(null);

        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = jwtCookie.getValue();
        final String email = jwtService.extractEmail(jwt);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            User user = findUserByEmail(email);

            if (user != null && jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Check if the token is close to expiring
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

    private User findUserByEmail(String email) {
        User user = customerRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = serviceProviderRepository.findByEmail(email).orElse(null);
        }
        return user;
    }
}