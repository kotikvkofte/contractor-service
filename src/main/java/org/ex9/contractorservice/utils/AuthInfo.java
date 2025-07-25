package org.ex9.contractorservice.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Утилитарный класс для получения информации об аутентифицированном пользователе.
 * @author Краковцев Артём
 */
public final class AuthInfo {

    private AuthInfo() {
    }

    /**
     * Получает имя авторизированного пользователя.
     * @return имя пользователя
     */
    public static String getUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    /**
     * Получает список ролей авторизованного пользователя
     * @return список ролей.
     */
    public static List<String> getRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).toList();
    }

}
