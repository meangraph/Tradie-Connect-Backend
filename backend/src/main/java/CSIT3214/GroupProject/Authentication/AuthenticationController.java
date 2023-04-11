package CSIT3214.GroupProject.Authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.register(request);
        createHttpOnlyCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authenticationService.authenticate(request);
        createHttpOnlyCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    private void createHttpOnlyCookie(HttpServletResponse response, String jwt) {
        Cookie jwtCookie = new Cookie("JWT", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @GetMapping("/CustomerTest")
    public String helloCust() {
        return "You have made it Cust fam";
    }

    @PreAuthorize("hasAuthority('ROLE_SERVICE_PROVIDER')")
    @GetMapping("/ServiceProviderTest")
    public String helloSP() {
        return "You have made it SP fam";
    }
}


