package edu.hcu.triage;
import java.time.Instant;
import java.util.Objects;

/**
 * Immutable identity (id) + mutable clinical state (severity).
 * Arrival order must be trackable for FIFO tie-breaking.
 */
public class Patient {
    private final String id;        // e.g., "P001"
    private String name;
    private int age;
    private int severity;
    // 1: Immediate: life-threatening condition
    // 2: Emergency: high-risk, vitals in danger zone
    // 3: Urgent: stable condition, multiple resources needed to stabilize patient
    // 4: Semi-Urgent: stable condition, one resource needed
    // 5: Non-Urgent: minor condition, can be treated whenever
    private final Instant arrival;  // registration time
    private final long arrivalSeq;  // monotonic sequence for FIFO ties

    // TODO: constructor(s) with validation (null/empty id, bounds for age/severity)

    public Patient(long arrivalSeq, Instant arrival, int severity, int age, String name, String id) throws IllegalArgumentException {
        this.arrivalSeq = arrivalSeq;
        if (id == null) { // Checks if ID is null
            throw new IllegalArgumentException("ID cannot be null");
        }
        else if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        else if (age < 0 || age > 120) { // Checks if each age is out of normal bounds
            throw new IllegalArgumentException("Age must be between 0 and 120");
        }
        else if (severity < 1 || severity > 5) { // Checks if severity level is outside 1-5
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }
        else if (arrival == null) {
            throw new IllegalArgumentException("Arrival cannot be null");
        }
        // Allow values to be initialized if they pass checks
        this.id = id;
        this.name = name;
        this.age = age;
        this.severity = severity;
        this.arrival = arrival;
    }
    // TODO: getters for all fields

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSeverity() {
        return severity;
    }

    public Instant getArrival() {
        return arrival;
    }

    public long getArrivalSeq() {
        return arrivalSeq;
    }

    // TODO: setters only where allowed (e.g., name, age, severity)

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    public void setAge(int age) {
        if (age < 0 || age > 120) { // Checks if each age is out of normal bounds
            throw new IllegalArgumentException("Age must be between 0 and 120");
        }
        this.age = age;
    }

    public void setSeverity(int severity) {
        if (severity < 1 || severity > 5) { // Checks if severity level is outside 1-5
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }
        this.severity = severity;
    }

    // TODO: equals/hashCode based on id only (document this in README.pdf)

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // TODO: toString() concise

    @Override
    public String toString() {
        return "Patient{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", severity=" + severity +
                ", arrival=" + arrival +
                ", arrivalSeq=" + arrivalSeq +
                "}";
    }
}
