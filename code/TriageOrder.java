package edu.hcu.triage;

import java.util.Comparator;
/** Comparator: higher severity first; break ties by smaller arrivalSeq (FIFO). */
public final class TriageOrder implements Comparator<Patient> {
    // TODO: implement compare(...) carefully; do not reverse tie order
    // TODO: document how this yields stable behavior for equal severity
    // Compares two patients for triage order.
    // Lower severity level = higher priority
    // If severity is equal, smaller arrivalSeq means higher priority (FIFO)
    // Using arrivalSeq instead of arrival ensures unique, strictly increasing numbers
    // Returning 0 only occurs if the same patient is compared to itself
    public int compare(Patient p1, Patient p2) {
        // Compare patient severity first
        if (p1.getSeverity() < p2.getSeverity()) {
            return -1; // p1 will be treated first
        }
        else if (p1.getSeverity() >  p2.getSeverity()) {
            return 1; // p2 will be treated first
        }
        // Checks arrival time if severity levels are equal
        else {
            if (p1.getArrivalSeq() < p2.getArrivalSeq()) {
                return -1; // p1 will be treated first
            }
            else if (p1.getArrivalSeq() > p2.getArrivalSeq()) {
                return 1; // p2 will be treated first
            }
            else {
                // Patient severity and arrivalSeq are identical
                // This only happens if p1 and p2 are the same patient object.
                // Returning 0 signals equal priority to the queue.
                return 0;
            }
        }
    }
}
