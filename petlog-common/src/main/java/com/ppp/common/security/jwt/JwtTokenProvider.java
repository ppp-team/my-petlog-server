package com.ppp.common.security.jwt;

import com.ppp.common.exception.ErrorCode;
import com.ppp.common.exception.TokenException;
import com.ppp.domain.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtTokenProvider {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.refresh-token.secret-key}")
    private String secretRefreshKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;
    

    public String generateAccessToken(User user){
        Map<String, Object> payloads = new HashMap<>();
//        payloads.put("userId", user.getUserId());
        payloads.put("email",user.getEmail());
//        payloads.put("username",user.getUsername());

        Date expireDate = createExpireDate(refreshExpiration);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setSubject("user-info")
                .setClaims(payloads)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        Map<String, Object> payloads = new HashMap<>();
//        payloads.put("userId", user.getUserId());
        payloads.put("email",user.getEmail());
//        payloads.put("username",user.getUsername());

        Date expireDate = createExpireDate(refreshExpiration);

        Key key= Keys.hmacShaKeyFor(secretRefreshKey.getBytes());
        return Jwts.builder()
                .setSubject("user-info")
                .setClaims(payloads)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken( Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildRefreshToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> payloads,
            UserDetails userDetails,
            long expiration
    ) {
//        payloads.put("userId", user.getUserId());
        payloads.put("email",userDetails.getUsername());
//        payloads.put("username",user.getUsername());

        Key key= Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .setSubject("user-info")
                .setClaims(payloads)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(createExpireDate(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildRefreshToken(
            Map<String, Object> payloads,
            UserDetails userDetails,
            long expiration
    ) {
//        payloads.put("userId", user.getUserId());
        payloads.put("email",userDetails.getUsername());
//        payloads.put("username",user.getUsername());

        Key key= Keys.hmacShaKeyFor(secretRefreshKey.getBytes());
        return Jwts.builder()
                .setSubject("user-info")
                .setClaims(payloads)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(createExpireDate(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    private Date createExpireDate(long expirationInMs){
        long expireTime = new Date().getTime() + expirationInMs;
        return new Date(expireTime);
    }

    public Map<String, Object> getUserFromJwt(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    public Claims getUserFromRefreshToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretRefreshKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getJwtFromRequestHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring("Bearer".length());
        }
        return null;
    }

    public boolean validateAccessToken(String token, HttpServletRequest request){
        Key key= Keys.hmacShaKeyFor(secretKey.getBytes());
        return validateToken(token, key, request);
    }

    public boolean validateRefreshToken(String token, HttpServletRequest request){
        Key key= Keys.hmacShaKeyFor(secretRefreshKey.getBytes());
        return validateToken(token, key, request);
    }

    private boolean validateToken(String token, Key key, HttpServletRequest request){
        try{
            Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
            // EntryPoint 로 이동
        }catch(io.jsonwebtoken.security.SecurityException ex) {
            log.error("Invalid JWT signature");
            request.setAttribute("exception", ErrorCode.INVALID_SIGNATURE.getCode());
            throw new TokenException(ErrorCode.INVALID_SIGNATURE);
        }catch(MalformedJwtException ex) {
            log.error("Invalid JWT token");
            request.setAttribute("exception", ErrorCode.MALFORMED_TOKEN.getCode());
            throw new TokenException(ErrorCode.MALFORMED_TOKEN);
        }catch(ExpiredJwtException ex) {
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
            throw new TokenException(ErrorCode.EXPIRED_TOKEN);
        }catch(UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN.getCode());
            throw new TokenException(ErrorCode.UNSUPPORTED_TOKEN);
        }catch(IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
            request.setAttribute("exception", ErrorCode.ILLEGALARGUMENT_TOKEN.getCode());
            throw new TokenException(ErrorCode.ILLEGALARGUMENT_TOKEN);
        }
    }
}
