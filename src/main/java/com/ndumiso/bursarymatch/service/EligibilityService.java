package com.ndumiso.bursarymatch.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ndumiso.bursarymatch.model.Offer;

/**
 * Implements the Phase 1 matching rule: a student qualifies for an offer only
 * if every one of its requirements is met (average, APS is not stored on the
 * student side so it's skipped, HL, FAL, Math, and named electives). Qualifying
 * offers are then ranked by average margin, with a 10+ point margin flagged
 * as "Most Eligible" per the spec.
 */
public class EligibilityService {

    public static class RankedOffer {
        public final Offer offer;
        public final int averageMargin;
        public final boolean mostEligible;

        public RankedOffer(Offer offer, int averageMargin, boolean mostEligible) {
            this.offer = offer;
            this.averageMargin = averageMargin;
            this.mostEligible = mostEligible;
        }
    }

    private final OfferService offerService = new OfferService();

    /**
     * Returns every offer the student qualifies for, ranked most-eligible first.
     */
    public List<RankedOffer> getEligibleOffers(ProfileService.StudentMarksData marks) throws SQLException {
        List<Offer> allOffers = offerService.getAllOffers();
        List<RankedOffer> results = new ArrayList<>();

        double studentAverage = calculateAverage(marks);

        for (Offer offer : allOffers) {
            if (qualifies(marks, studentAverage, offer)) {
                int margin = (int) Math.round(studentAverage) - offer.getAvgRequired();
                results.add(new RankedOffer(offer, margin, margin >= 10));
            }
        }

        results.sort(Comparator.comparingInt((RankedOffer r) -> r.averageMargin).reversed());
        return results;
    }

    private boolean qualifies(ProfileService.StudentMarksData marks, double studentAverage, Offer offer) {
        if (studentAverage < offer.getAvgRequired()) {
            return false;
        }
        if (!meetsMinimum(marks.hlMark, offer.getHlRequired())) {
            return false;
        }
        if (!meetsMinimum(marks.falMark, offer.getFalRequired())) {
            return false;
        }
        if (!meetsMinimum(marks.mathMark, offer.getMathRequired())) {
            return false;
        }
        if (!meetsElectiveRequirement(marks, offer.getSub4Subject(), offer.getSub4Required())) {
            return false;
        }
        if (!meetsElectiveRequirement(marks, offer.getSub5Subject(), offer.getSub5Required())) {
            return false;
        }
        if (!meetsElectiveRequirement(marks, offer.getSub6Subject(), offer.getSub6Required())) {
            return false;
        }
        return true;
    }

    private boolean meetsMinimum(Integer studentMark, int required) {
        if (required <= 0) {
            return true; // offer doesn't actually require this subject
        }
        return studentMark != null && studentMark >= required;
    }

    /**
     * If the offer names a specific elective (e.g. "Physics"), searches the
     * student's sub4/sub5/sub6 for a name match and checks that subject's mark.
     * If the offer doesn't name a subject, any one elective meeting the mark
     * threshold satisfies the requirement.
     */
    private boolean meetsElectiveRequirement(ProfileService.StudentMarksData marks,
            String requiredSubjectName, int requiredMark) {
        if (requiredMark <= 0) {
            return true; // offer doesn't actually require this elective slot
        }

        if (requiredSubjectName != null && !requiredSubjectName.isBlank()) {
            return electiveMatches(marks.sub4, marks.sub4Mark, requiredSubjectName, requiredMark)
                    || electiveMatches(marks.sub5, marks.sub5Mark, requiredSubjectName, requiredMark)
                    || electiveMatches(marks.sub6, marks.sub6Mark, requiredSubjectName, requiredMark);
        }

        // No subject named - any elective mark meeting the threshold is fine
        return (marks.sub4Mark != null && marks.sub4Mark >= requiredMark)
                || (marks.sub5Mark != null && marks.sub5Mark >= requiredMark)
                || (marks.sub6Mark != null && marks.sub6Mark >= requiredMark);
    }

    private boolean electiveMatches(String studentSubject, Integer studentMark,
            String requiredSubjectName, int requiredMark) {
        if (studentSubject == null || studentMark == null) {
            return false;
        }
        return studentSubject.trim().equalsIgnoreCase(requiredSubjectName.trim())
                && studentMark >= requiredMark;
    }

    /**
     * Average across all subjects the student has entered marks for.
     * Same "sum of entered marks / count" approach as StudentMarks.getAvg()
     * in the Phase 2 design doc, since that class was never built here.
     */
    private double calculateAverage(ProfileService.StudentMarksData marks) {
        int sum = 0;
        int count = 0;

        Integer[] allMarks = {marks.hlMark, marks.falMark, marks.mathMark,
                marks.sub4Mark, marks.sub5Mark, marks.sub6Mark, marks.loMark};

        for (Integer mark : allMarks) {
            if (mark != null) {
                sum += mark;
                count++;
            }
        }

        return count == 0 ? 0 : (double) sum / count;
    }
}