package edu.hcu.triage;

import java.util.*;

/** Thin wrapper around PriorityQueue to enforce triage policy and utilities. */
public class TriageQueue {
    private final PriorityQueue<Patient> pq = new PriorityQueue<>(new TriageOrder());

    // TODO: enqueue(Patient p)
    public void enqueue(Patient p){
        if (p == null) { // Checks if patient is null
            throw new IllegalArgumentException("Patient cannot be null");
        }
        pq.add(p); // Enqueue patient
    }
    // TODO: enqueueById(PatientRegistry reg, String id) - lookup then enqueue
    public boolean enqueueById(PatientRegistry reg, String id) {
        //makes sure input is not null
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (reg == null) {
            throw new IllegalArgumentException("Patient Registry cannot be null");
        }
        Optional<Patient> p = reg.get(id);
        if (p.isPresent()) { // Checks if patient is in registry
            enqueue(p.get()); // Enqueue patient
        }
        return false;
    }

    // TODO: peekNext(): Optional<Patient>
    public Optional<Patient> peekNext() {
        return Optional.ofNullable(pq.peek()); // Peeks at next patient
    }

    // TODO: dequeueNext(): Optional<Patient>
    public Optional<Patient> dequeueNext() {
        return Optional.ofNullable(pq.poll()); // Retrieves and removes next patient
    }

    // TODO: size()
    public int size() {
        return pq.size(); // Returns size of priority queue
    }

    // TODO: snapshotOrder(): List<Patient> in triage order without mutating the queue
    //       Hint: clone PQ or copy to an array then sort with same comparator.
    public List<Patient> snapshotOrder() {
        PriorityQueue<Patient> clone = new PriorityQueue<>(pq); // Clone priority queue
        // takes clone structure and mutates it into a list of patient objects
        List<Patient> patientList = new ArrayList<>();

        while (!clone.isEmpty()) {
            patientList.add(clone.poll()); // Remove from clone to maintain priority order
        }
        return patientList; //returns list following triage order
    }

    // TODO: clear()
    public void clear() {
        pq.clear(); // Clears priority queue
    }

}
