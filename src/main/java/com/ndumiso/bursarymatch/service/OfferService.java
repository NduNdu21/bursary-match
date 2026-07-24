package com.ndumiso.bursarymatch.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ndumiso.bursarymatch.db.DataBase;
import com.ndumiso.bursarymatch.model.Offer;

public class OfferService {

    public List<Offer> getOffersByProvider(String providerId) throws SQLException {
        DataBase db = new DataBase();
        List<Offer> offers = new ArrayList<>();
        try {
            String sql = "SELECT offer_id, offer_name, additional_info, aps_required, avg_required, "
                    + "hl_required, fal_required, math_required, "
                    + "sub4_required, sub4_subject, sub5_required, sub5_subject, sub6_required, sub6_subject, "
                    + "faculty, deadline, num_bursaries_left, provider_id "
                    + "FROM tblOffers WHERE provider_id = ? ORDER BY deadline";
            try (ResultSet rs = db.query(sql, providerId)) {
                while (rs.next()) {
                    offers.add(mapRow(rs));
                }
            }
        } finally {
            db.close();
        }
        return offers;
    }

    /**
     * Returns every offer in the system. Used by the Search tab's eligibility
     * matching, which needs to check a student's marks against all offers,
     * not just one provider's.
     */
    public List<Offer> getAllOffers() throws SQLException {
        DataBase db = new DataBase();
        List<Offer> offers = new ArrayList<>();
        try {
            String sql = "SELECT offer_id, offer_name, additional_info, aps_required, avg_required, "
                    + "hl_required, fal_required, math_required, "
                    + "sub4_required, sub4_subject, sub5_required, sub5_subject, sub6_required, sub6_subject, "
                    + "faculty, deadline, num_bursaries_left, provider_id "
                    + "FROM tblOffers ORDER BY deadline";
            try (ResultSet rs = db.query(sql)) {
                while (rs.next()) {
                    offers.add(mapRow(rs));
                }
            }
        } finally {
            db.close();
        }
        return offers;
    }

    public void addOffer(String offerName, String addInfo, int apsRequired, int avgRequired,
            int hlRequired, int falRequired, int mathRequired,
            int sub4Required, String sub4Subject,
            int sub5Required, String sub5Subject,
            int sub6Required, String sub6Subject,
            String faculty, LocalDate deadline, int numBursariesLeft,
            String providerId) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "INSERT INTO tblOffers "
                    + "(offer_name, additional_info, aps_required, avg_required, hl_required, fal_required, "
                    + "math_required, sub4_required, sub4_subject, sub5_required, sub5_subject, "
                    + "sub6_required, sub6_subject, faculty, deadline, num_bursaries_left, provider_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            db.update(sql, offerName, addInfo, apsRequired, avgRequired, hlRequired, falRequired,
                    mathRequired,
                    sub4Required, blankToNull(sub4Subject),
                    sub5Required, blankToNull(sub5Subject),
                    sub6Required, blankToNull(sub6Subject),
                    faculty, Date.valueOf(deadline), numBursariesLeft, providerId);
        } finally {
            db.close();
        }
    }

    private String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private Offer mapRow(ResultSet rs) throws SQLException {
        return new Offer(
                rs.getInt("offer_id"),
                rs.getString("offer_name"),
                rs.getString("additional_info"),
                rs.getInt("aps_required"),
                rs.getInt("avg_required"),
                rs.getInt("hl_required"),
                rs.getInt("fal_required"),
                rs.getInt("math_required"),
                rs.getInt("sub4_required"), rs.getString("sub4_subject"),
                rs.getInt("sub5_required"), rs.getString("sub5_subject"),
                rs.getInt("sub6_required"), rs.getString("sub6_subject"),
                rs.getString("faculty"),
                rs.getDate("deadline").toLocalDate(),
                rs.getInt("num_bursaries_left"),
                rs.getString("provider_id")
        );
    }
}