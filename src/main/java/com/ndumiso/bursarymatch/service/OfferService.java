package com.ndumiso.bursarymatch.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ndumiso.bursarymatch.db.DataBase;
import com.ndumiso.bursarymatch.model.Offer;

/**
 * Reads and writes tblOffers. Used by MainScreenGUI's "My Offers" tab
 * (providers) and will also back the Search tab once eligibility ranking
 * is wired in.
 */
public class OfferService {

    public List<Offer> getOffersByProvider(String providerId) throws SQLException {
        DataBase db = new DataBase();
        List<Offer> offers = new ArrayList<>();
        try {
            String sql = "SELECT offer_id, offer_name, additional_info, aps_required, avg_required, "
                    + "hl_required, fal_required, math_required, sub4_required, sub5_required, sub6_required, "
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

    public void addOffer(String offerName, String addInfo, int apsRequired, int avgRequired,
            int hlRequired, int falRequired, int mathRequired, int sub4Required, int sub5Required,
            int sub6Required, String faculty, LocalDate deadline, int numBursariesLeft,
            String providerId) throws SQLException {
        DataBase db = new DataBase();
        try {
            String sql = "INSERT INTO tblOffers "
                    + "(offer_name, additional_info, aps_required, avg_required, hl_required, fal_required, "
                    + "math_required, sub4_required, sub5_required, sub6_required, faculty, deadline, "
                    + "num_bursaries_left, provider_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            db.update(sql, offerName, addInfo, apsRequired, avgRequired, hlRequired, falRequired,
                    mathRequired, sub4Required, sub5Required, sub6Required, faculty,
                    Date.valueOf(deadline), numBursariesLeft, providerId);
        } finally {
            db.close();
        }
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
                rs.getInt("sub4_required"),
                rs.getInt("sub5_required"),
                rs.getInt("sub6_required"),
                rs.getString("faculty"),
                rs.getDate("deadline").toLocalDate(),
                rs.getInt("num_bursaries_left"),
                rs.getString("provider_id")
        );
    }
}