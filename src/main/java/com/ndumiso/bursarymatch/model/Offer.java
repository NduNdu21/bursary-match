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
    private String sub4Subject; // null/blank = any elective accepted
    private int sub5Required;
    private String sub5Subject;
    private int sub6Required;
    private String sub6Subject;

    private String faculty;
    private LocalDate deadline;
    private int numBursariesLeft;
    private String providerId;

    public Offer(int offerId, String offerName, String addInfo,
                 int apsRequired, int avgRequired, int hlRequired, int falRequired,
                 int mathRequired,
                 int sub4Required, String sub4Subject,
                 int sub5Required, String sub5Subject,
                 int sub6Required, String sub6Subject,
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
        this.sub4Subject = sub4Subject;
        this.sub5Required = sub5Required;
        this.sub5Subject = sub5Subject;
        this.sub6Required = sub6Required;
        this.sub6Subject = sub6Subject;
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

    public String getSub4Subject() {
        return sub4Subject;
    }

    public int getSub5Required() {
        return sub5Required;
    }

    public String getSub5Subject() {
        return sub5Subject;
    }

    public int getSub6Required() {
        return sub6Required;
    }

    public String getSub6Subject() {
        return sub6Subject;
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