package org.ex9.contractorservice.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.function.Function;

/**
 * Сервис для работы с JWT-токенами.
 * @author Краковцев Артём
 */
@Service
public class JwtService {

    /**
     * Секретный ключ для подписи токена.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Извлекает логин пользователя из токена.
     *
     * @param token JWT-токен.
     * @return Логин пользователя.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает роли пользователя из токена.
     *
     * @param token JWT-токен.
     * @return Список ролей пользователя.
     */
    public List<String> getRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    private  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает claims из JWT-токена.
     *
     * @param token JWT-токен.
     * @return Объект Claims с данными токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Генерирует ключ используемый в jwt-токене.
     *
     * @return ключ токена.
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
