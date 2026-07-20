package com.ndumiso.bursarymatch.model;

/**
 * Holds a student's subject marks and can calculate their overall average
 * and APS (Admission Point Score), matching the "StudentMarks" class from
 * the Phase 2 design document.
 *
 * APS uses the standard South African NSC 7-point scale, summed across the
 * 6 subjects used for admission (Home Language, First Additional Language,
 * Math/Math Lit, and 3 electives). Life Orientation is tracked but not
 * included in the APS total, matching common university admission rules.
 */
public class StudentMarks {

    private String studentId;

    private String homeLang;
    private int hlMark;

    private String firstAddLang;
    private int falMark;

    private boolean takesMath; // true = Mathematics, false = Math Literacy
    private String mathSubjectName;
    private int mathMark;

    private String sub4;
    private int sub4Mark;

    private String sub5;
    private int sub5Mark;

    private String sub6;
    private int sub6Mark;

    private int loMark;

    public StudentMarks(String studentId, String homeLang, int hlMark,
                         String firstAddLang, int falMark,
                         String mathSubjectName, int mathMark,
                         String sub4, int sub4Mark,
                         String sub5, int sub5Mark,
                         String sub6, int sub6Mark,
                         int loMark) {
        this.studentId = studentId;
        this.homeLang = homeLang;
        this.hlMark = hlMark;
        this.firstAddLang = firstAddLang;
        this.falMark = falMark;
        this.mathSubjectName = mathSubjectName;
        this.mathMark = mathMark;
        this.sub4 = sub4;
        this.sub4Mark = sub4Mark;
        this.sub5 = sub5;
        this.sub5Mark = sub5Mark;
        this.sub6 = sub6;
        this.sub6Mark = sub6Mark;
        this.loMark = loMark;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getHomeLang() {
        return homeLang;
    }

    public int getHlMark() {
        return hlMark;
    }

    public String getFirstAddLang() {
        return firstAddLang;
    }

    public int getFalMark() {
        return falMark;
    }

    public String getMathSubjectName() {
        return mathSubjectName;
    }

    public int getMathMark() {
        return mathMark;
    }

    public String getSub4() {
        return sub4;
    }

    public int getSub4Mark() {
        return sub4Mark;
    }

    public String getSub5() {
        return sub5;
    }

    public int getSub5Mark() {
        return sub5Mark;
    }

    public String getSub6() {
        return sub6;
    }

    public int getSub6Mark() {
        return sub6Mark;
    }

    public int getLoMark() {
        return loMark;
    }

    /**
     * Average across all 7 subjects, including Life Orientation.
     */
    public double getAvg() {
        int total = hlMark + falMark + mathMark + sub4Mark + sub5Mark + sub6Mark + loMark;
        return total / 7.0;
    }

    /**
     * Admission Point Score: converts each of the 6 admission subjects
     * (everything except Life Orientation) to a 1-7 point band and sums them.
     * Maximum possible APS on this scale is 42.
     */
    public int getAPS() {
        int aps = 0;
        aps = aps + markToPoints(hlMark);
        aps = aps + markToPoints(falMark);
        aps = aps + markToPoints(mathMark);
        aps = aps + markToPoints(sub4Mark);
        aps = aps + markToPoints(sub5Mark);
        aps = aps + markToPoints(sub6Mark);
        return aps;
    }

    /**
     * Converts a percentage mark into an NSC point-scale value (1-7).
     */
    private int markToPoints(int mark) {
        if (mark >= 80) {
            return 7;
        } else if (mark >= 70) {
            return 6;
        } else if (mark >= 60) {
            return 5;
        } else if (mark >= 50) {
            return 4;
        } else if (mark >= 40) {
            return 3;
        } else if (mark >= 30) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "StudentID: " + studentId
                + ", Average: " + getAvg()
                + ", APS: " + getAPS();
    }
}
