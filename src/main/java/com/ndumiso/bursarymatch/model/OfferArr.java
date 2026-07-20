package com.ndumiso.bursarymatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all Offer objects loaded from the database and provides sorting and
 * searching, matching the "OfferArr" class from the Phase 2 design document.
 *
 * Internally backed by an ArrayList rather than a raw array/fixed size, but
 * keeps the same public sort()/search() behaviour described in the design.
 */
public class OfferArr {

    private List<Offer> offers;

    public OfferArr() {
        offers = new ArrayList<>();
    }

    public void add(Offer offer) {
        offers.add(offer);
    }

    public int size() {
        return offers.size();
    }

    public Offer get(int index) {
        return offers.get(index);
    }

    public List<Offer> getAll() {
        return offers;
    }

    /**
     * Sorts offers by deadline, soonest first, using a simple insertion sort.
     */
    public void sort() {
        for (int i = 1; i < offers.size(); i++) {
            Offer current = offers.get(i);
            int j = i - 1;
            while (j >= 0 && offers.get(j).getDeadline().isAfter(current.getDeadline())) {
                offers.set(j + 1, offers.get(j));
                j = j - 1;
            }
            offers.set(j + 1, current);
        }
    }

    /**
     * Searches offer names and additional info for a matching substring
     * (case-insensitive), matching the Phase 2 search() method.
     */
    public List<Offer> search(String searchTerm) {
        List<Offer> results = new ArrayList<>();
        String term = searchTerm.toLowerCase();

        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            boolean nameMatch = offer.getOfferName().toLowerCase().contains(term);
            boolean infoMatch = offer.getAddInfo().toLowerCase().contains(term);
            if (nameMatch || infoMatch) {
                results.add(offer);
            }
        }
        return results;
    }

    /**
     * Ranks offers for a specific student by eligibility margin (Phase 1 spec):
     * offers where the student's average clears the requirement by the widest
     * margin are placed first. Only offers the student actually qualifies for
     * (margin >= 0) are included.
     */
    public List<Offer> rankByEligibility(StudentMarks marks) {
        List<Offer> qualifying = new ArrayList<>();
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            if (offer.eligibilityMargin(marks) >= 0) {
                qualifying.add(offer);
            }
        }

        // Simple insertion sort, highest margin first
        for (int i = 1; i < qualifying.size(); i++) {
            Offer current = qualifying.get(i);
            int currentMargin = current.eligibilityMargin(marks);
            int j = i - 1;
            while (j >= 0 && qualifying.get(j).eligibilityMargin(marks) < currentMargin) {
                qualifying.set(j + 1, qualifying.get(j));
                j = j - 1;
            }
            qualifying.set(j + 1, current);
        }

        return qualifying;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < offers.size(); i++) {
            sb.append(offers.get(i).toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
