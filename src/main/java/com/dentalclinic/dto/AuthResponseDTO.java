package com.dentalclinic.dto;

/**
 * Data Transfer Object (DTO) for Authentication Response.
 * Compatible with JSP Expression Language (${user.fullName}) and REST APIs.
 */
public class AuthResponseDTO {

    private String token;
    private String tokenType = "Bearer";
    private Integer userId;
    private String username;
    private String fullName;
    private String role;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, Integer userId, String username, String fullName, String role) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public AuthResponseDTO(String token, String tokenType, Integer userId, String username, String fullName, String role) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // Standard JavaBeans Getters & Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Record-style accessor aliases
    public String token() { return token; }
    public String tokenType() { return tokenType; }
    public Integer userId() { return userId; }
    public String username() { return username; }
    public String fullName() { return fullName; }
    public String role() { return role; }
}
