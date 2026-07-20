package com.ndumiso.bursarymatch.model;

import java.time.LocalDate;

/**
 * A BursaryProvider is a Person who represents a company/institution and can
 * post scholarship Offers. Named "BursaryProvider" (singular) here instead of
 * the original "BursaryProviders" for standard Java class-naming convention.
 */
public class BursaryProvider extends Person {

    private String providerName;
    private String biography;
    private String phoneNumber;
    private String email;
    private String website;

    public BursaryProvider(String idNumber, String username, String password,
                            LocalDate dateOfBirth, String providerName, String biography,
                            String phoneNumber, String email, String website) {
        // Providers are companies, so "name"/"surname" on Person are left blank;
        // providerName is the field actually used for display.
        super(idNumber, "", "", username, password, dateOfBirth);
        this.providerName = providerName;
        this.biography = biography;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getBiography() {
        return biography;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    @Override
    public String toString() {
        return "Provider: " + providerName
                + ", Phone: " + phoneNumber
                + ", Email: " + email
                + ", Website: " + website;
    }
}
