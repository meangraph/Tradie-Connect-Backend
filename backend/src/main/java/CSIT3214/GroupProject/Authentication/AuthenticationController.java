package CSIT3214.GroupProject.Authentication;

import CSIT3214.GroupProject.DataAccessLayer.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    // Inject the AuthenticationService
    private final AuthenticationService authenticationService;

    // Endpoint to handle user registration
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/SignUp")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            // Call the AuthenticationService's register method
            AuthenticationResponse authResponse = authenticationService.register(userDTO);
            // Create an HttpOnly cookie containing the JWT token
            createHttpOnlyCookie(response, authResponse.getToken());
            // Return the registration response
            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationService.EmailAlreadyExistsException e) {
            // If the EmailAlreadyExistsException is thrown, return a BAD_REQUEST response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // Endpoint to handle user authentication
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/SignIn")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        // Call the AuthenticationService's authenticate method
        AuthenticationResponse authResponse = authenticationService.authenticate(request);
        // Create an HttpOnly cookie containing the JWT token
        createHttpOnlyCookie(response, authResponse.getToken());
        // Return the authentication response
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/Logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT", "");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(0);
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
        return ResponseEntity.ok().build();
    }

    private void createHttpOnlyCookie(HttpServletResponse response, String jwt) {
        Cookie jwtCookie = new Cookie("JWT", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
        jwtCookie.setPath("/");
        response.addCookie(jwtCookie);
    }

    //Used for testing role based endpoint authorization
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


