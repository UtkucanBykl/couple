package com.example.couple.dto.query;

public record UserListSearchQuery(
        String friendCode,
        String email,
        String username
) {
    public boolean hasAnyFilter() {
        return hasText(friendCode) || hasText(email) || hasText(username);
    }

    public String friendCodeOrNull() { return normalize(friendCode); }
    public String emailOrNull() { return normalize(email); }
    public String usernameOrNull() { return normalize(username); }

    private static boolean hasText(String v) {
        return v != null && !v.isBlank();
    }

    private static String normalize(String v) {
        return hasText(v) ? v.trim() : null;
    }
}
