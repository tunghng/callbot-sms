package com.example.callbotsms.security.model.token;

import com.im.sso.exception.JwtTokenMalformedException;
import com.im.sso.exception.JwtTokenMissingException;
import com.im.sso.security.model.SecurityUser;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenFactory implements Serializable {

    private static final String USER_ID = "userId";
    private static final String TENANT_ID = "tenantId";
    private static final String EMAIL = "email";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE = "phone";
    private static final String AUTHORITY = "authority";
    private static final String ROLE = "role";

    @Value(value = "${jwt.secret}")
    private String jwtSecret;

    @Value(value = "${jwt.exp}")
    private String jwtExp;

    @Value(value = "${jwt.refreshExp}")
    private String jwtRefreshExp;

    public String generateToken(SecurityUser userDetails, long tokenExpirationTime) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis() + tokenExpirationTime);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(String.valueOf(userDetails.getUser().getEmail()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret);

        jwtBuilder.claim(USER_ID, userDetails.getUser().getId())
                .claim(TENANT_ID, userDetails.getUser().getTenantId())
                .claim(EMAIL, userDetails.getUser().getEmail())
                .claim(FIRST_NAME, userDetails.getUser().getFirstName())
                .claim(LAST_NAME, userDetails.getUser().getLastName())
                .claim(PHONE, userDetails.getUser().getPhone())
                .claim(AUTHORITY, userDetails.getUser().getAuthority())
                .claim(ROLE, userDetails.getUser().getRole());

        return jwtBuilder.compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public void validateToken(String token) throws JwtTokenMalformedException, JwtTokenMissingException {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
        } catch (SignatureException ex) {
            throw new JwtTokenMalformedException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtTokenMalformedException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtTokenMalformedException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtTokenMalformedException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtTokenMissingException("JWT claims string is empty.");
        }
    }
}
