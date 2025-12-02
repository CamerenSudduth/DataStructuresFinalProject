package edu.hcu.triage;

import java.time.Instant;

public class TreatedCase {
    public enum Outcome { STABLE, OBSERVE, TRANSFER }

    private final Patient patient;
    private final Instant start;
    private final Instant end;
    private final Outcome outcome;
    private final String notes;

    // TODO: constructor with validation; getters; toString()
    // Does not require setter because all of these variables are finalized and are to not be
    // changed

    public TreatedCase(Patient patient, Instant start, Instant end, Outcome outcome, String notes) {
        if (patient == null) { // Checks if patient is null
            throw new IllegalArgumentException("Patient cannot be null");
        }
        else if (start == null) { // Checks if start time is null
            throw new IllegalArgumentException("Start time cannot be null");
        }
        else if (end == null) { // Checks if end time is null
            throw new IllegalArgumentException("End time cannot be null");
        }
        else if (end.isBefore(start)){ //Checks if end time is before start time which should not be valid
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        else if (outcome == null ) { // Checks if outcome is null
            throw new IllegalArgumentException("Outcome must be specified");
        }
        else if (notes == null) { // checks if notes are null
            throw new IllegalArgumentException("notes cannot be null");
        }
        this.patient = patient;
        this.start = start;
        this.end = end;
        this.outcome = outcome;
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return  "\nPatient: [" + patient +
                "]\nStart: " + start +
                "\nEnd: " + end +
                "\nOutcome: " + outcome +
                "\nNotes: " + notes+"\n";
    }
}
