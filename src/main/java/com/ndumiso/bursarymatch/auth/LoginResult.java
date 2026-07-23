package com.ndumiso.bursarymatch.auth;

/**
 * Simple result object returned by AuthService.login() so LogInGUI doesn't
 * need to know anything about SQL or exceptions to react to a login attempt.
 */
public class LoginResult {

    private final boolean success;
    private final String userId;
    private final String userType; // "STUDENT" or "PROVIDER"
    private final String displayName;
    private final String errorMessage;

    private LoginResult(boolean success, String userId, String userType,
                         String displayName, String errorMessage) {
        this.success = success;
        this.userId = userId;
        this.userType = userType;
        this.displayName = displayName;
        this.errorMessage = errorMessage;
    }

    public static LoginResult success(String userId, String userType, String displayName) {
        return new LoginResult(true, userId, userType, displayName, null);
    }

    public static LoginResult failure(String errorMessage) {
        return new LoginResult(false, null, null, null, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
