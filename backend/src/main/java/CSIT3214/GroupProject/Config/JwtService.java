package CSIT3214.GroupProject.Config;

import CSIT3214.GroupProject.DataAccessLayer.CustomerRepository;
import CSIT3214.GroupProject.DataAccessLayer.ServiceProviderRepository;
import CSIT3214.GroupProject.Model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
public class JwtService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    private static final String SECRET_KEY = "50655368566D597133743677397A244326452948404D635166546A576E5A7234";
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,  T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        User user = findUserByEmail(userDetails.getUsername());
        if (user != null) {
            claims.put("userId", user.getId());
            claims.put("role", user.getRole().name());
        }
        return generateToken(claims, userDetails);
    }

    public Boolean isTokenValid(String token, UserDetails userDetails)  {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername())) &&  !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetils) {
        return  Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetils.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))//valid for 24 hours
                .signWith(getSignInKey(),  SignatureAlgorithm.HS256)
                .compact();//generates and returns the token
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    private User findUserByEmail(String email) {
        User user = customerRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = serviceProviderRepository.findByEmail(email).orElse(null);
        }
        return user;
    }

    public boolean isTokenCloseToExpiring(String token) {
        Date expiration = extractExpiration(token);
        long timeToExpireInMillis = expiration.getTime() - System.currentTimeMillis();
        long timeToExpireInMinutes = TimeUnit.MILLISECONDS.toMinutes(timeToExpireInMillis);
        return timeToExpireInMinutes <= 30;
    }

}
