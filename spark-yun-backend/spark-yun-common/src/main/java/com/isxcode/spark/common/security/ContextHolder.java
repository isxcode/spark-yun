package com.isxcode.spark.common.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class ContextHolder {

    private ContextHolder() {
    }

    public static String getUserId() {

        CurrentUser currentUser = getCurrentUser();
        if (currentUser != null) {
            return currentUser.userId();
        }

        return null;
    }

    public static String getTenantId() {

        CurrentUser currentUser = getCurrentUser();
        return currentUser == null ? null : currentUser.tenantId();
    }

    public static void setCurrentUser(String userId, String tenantId) {

        Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken authentication = oldAuthentication == null
            ? new UsernamePasswordAuthenticationToken(new CurrentUser(userId, tenantId), null)
            : new UsernamePasswordAuthenticationToken(new CurrentUser(userId, tenantId),
                oldAuthentication.getCredentials(), oldAuthentication.getAuthorities());
        if (oldAuthentication != null) {
            authentication.setDetails(oldAuthentication.getDetails());
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static void setTenantId(String tenantId) {

        setCurrentUser(getUserId(), tenantId);
    }

    public static void setUserId(String userId) {

        setCurrentUser(userId, getTenantId());
    }

    public static void clear() {

        SecurityContextHolder.clearContext();
    }

    private static CurrentUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return null;
        }

        return currentUser;
    }
}
