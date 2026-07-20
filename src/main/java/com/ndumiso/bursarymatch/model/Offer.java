package com.ndumiso.bursarymatch.model;

import java.time.LocalDate;

/**
 * A single scholarship/bursary listing. Named "Offer" (singular) instead of
 * the original "Offers" for standard Java class-naming convention.
 */
public class Offer {

    private int offerId;
    private String offerName;
    private String addInfo;

    private int apsRequired;
    private int avgRequired;
    private int hlRequired;
    private int falRequired;
    private int mathRequired;
    private int sub4Required;
    private int sub5Required;
    private int sub6Required;

    private String faculty;
    private LocalDate deadline;
    private int numBursariesLeft;
    private String providerId;

    public Offer(int offerId, String offerName, String addInfo,
                 int apsRequired, int avgRequired, int hlRequired, int falRequired,
                 int mathRequired, int sub4Required, int sub5Required, int sub6Required,
                 String faculty, LocalDate deadline, int numBursariesLeft, String providerId) {
        this.offerId = offerId;
        this.offerName = offerName;
        this.addInfo = addInfo;
        this.apsRequired = apsRequired;
        this.avgRequired = avgRequired;
        this.hlRequired = hlRequired;
        this.falRequired = falRequired;
        this.mathRequired = mathRequired;
        this.sub4Required = sub4Required;
        this.sub5Required = sub5Required;
        this.sub6Required = sub6Required;
        this.faculty = faculty;
        this.deadline = deadline;
        this.numBursariesLeft = numBursariesLeft;
        this.providerId = providerId;
    }

    public int getOfferId() {
        return offerId;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public int getApsRequired() {
        return apsRequired;
    }

    public int getAvgRequired() {
        return avgRequired;
    }

    public int getHlRequired() {
        return hlRequired;
    }

    public int getFalRequired() {
        return falRequired;
    }

    public int getMathRequired() {
        return mathRequired;
    }

    public int getSub4Required() {
        return sub4Required;
    }

    public int getSub5Required() {
        return sub5Required;
    }

    public int getSub6Required() {
        return sub6Required;
    }

    public String getFaculty() {
        return faculty;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public int getNumBursariesLeft() {
        return numBursariesLeft;
    }

    public String getProviderId() {
        return providerId;
    }

    /**
     * Returns how many days are left until this offer's deadline.
     * Negative means the deadline has already passed.
     */
    public long daysUntilDeadline() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    /**
     * Eligibility margin from the Phase 1 spec: the difference between a
     * student's average and this offer's required average. A margin of 10
     * or more means the offer should be ranked at the top for that student.
     */
    public int eligibilityMargin(StudentMarks marks) {
        return (int) Math.round(marks.getAvg()) - avgRequired;
    }

    @Override
    public String toString() {
        return "Offer: " + offerName
                + ", Faculty: " + faculty
                + ", Deadline: " + deadline
                + ", Slots left: " + numBursariesLeft;
    }
}
