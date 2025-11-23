package edu.hcu.triage;

import java.time.Instant;
import java.util.*;

/** O(1)-ish lookup of patients by id. */
public class PatientRegistry {
    private final Map<String, Patient> byId = new HashMap<>();
    private long nextArrivalSeq = 0L;

    // TODO: registerNew(id, name, age, severity): create Patient with arrivalSeq = nextArrivalSeq++
    // Add new patient to patient registry
    public void registerNew(String id, String name, int age, int severity) {
        if (id == null) { // Checks if id is null
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (name == null) { // Checks if name is null
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (age < 0 || age > 120) { // Checks if age is within normal bounds
            throw new IllegalArgumentException("Age must be between 0 and 120");
        }
        if (severity < 1 || severity > 5) { // Checks if severity level is between 1-5
            throw new IllegalArgumentException("Severity must be between 1 and 5");
        }

        // Check if patient already exists
        if (byId.containsKey(id)) {
            // Update existing patient if id already exists
            updateExisting(id, name, age, severity);
            return; // Finish updating
        }

        // Assign arrival timestamp
        Instant arrival =  Instant.now();

        // Assign arrival sequence
        long arrivalSeq = nextArrivalSeq++;

        // Create the Patient object
        Patient patient = new Patient(
                arrivalSeq,
                arrival,
                severity,
                age,
                name,
                id
        );

        // Add patient to registry
        byId.put(id, patient);

    }
    // TODO: updateExisting(id, name?, age?, severity?): partial, validated updates
    public void updateExisting(String id, String name, int age, int severity) {
        // Search for patient in the registry
        // If user passes null for name, the name stays the same
        // If the user passes -1 for age or severity, that field stays the same
        // Any other input will raise an exception
        Patient patient = byId.get(id);
        if (patient == null) {
            throw new IllegalArgumentException("Patient with ID " + id + " does not exist");
        }

        // Update name if provided
        if (name != null) {
            patient.setName(name);
        }

        if (age != -1) { // Skips updating age if user enters -1
            if (age < 0 || age > 120) {
                throw new IllegalArgumentException("Age must be between 0 and 120");
            }
            patient.setAge(age);
        }

        if (severity != -1) { // Skips updating severity if user enters -1
            if (severity < 1 || severity > 5) {
                throw new IllegalArgumentException("Severity must be between 1 and 5");
            }
            patient.setSeverity(severity);
        }
    }

    // TODO: get(id): Optional<Patient>
    // Retrieve patient by ID
    public Optional<Patient> get(String id) {
        if (id == null) { // Checks if id is null
            throw new IllegalArgumentException("ID cannot be null");
        }
        return Optional.ofNullable(byId.get(id)); // returns Patient object or null if not found
    }
    // TODO: contains(id)
    // Check if a patient with a given ID exists
    public boolean contains(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return byId.containsKey(id);
    }
    // TODO: size()
    // Return the number of patients currently registered
    public int size() {
        return byId.size();
    }
}
