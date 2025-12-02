package edu.hcu.triage;

import java.util.Random;

/** Deterministic workload generators for performance tests. */
public final class SampleWorkloads {
    // TODO: enqueueNRandomPatients(registry, triage, N, seed, distribution)
    private SampleWorkloads() {
        // prevent instantiation
    }

    /**
     * Enqueues N random patients into the registry and triage queue.
     *
     * @param registry     The patient registry to register new patients
     * @param triage       The triage queue to enqueue patients
     * @param N            Number of patients to generate
     * @param seed         Random seed for deterministic results
     * @param distribution "uniform" or "skewed" severity distribution
     */
    public static void enqueueNRandomPatients(PatientRegistry registry, TriageQueue triage,
                                              int N, long seed, String distribution) {
        Random rand = new Random(seed);

        for (int i = 0; i < N; i++) {
            // Generate ID and name
            String id = "P" + i;
            String name = "Patient" + i;

            // Generate age 1–100
            int age = rand.nextInt(100) + 1;

            // Generate severity
            int severity;
            if ("skewed".equalsIgnoreCase(distribution)) {
                // Example skew: more low-severity patients
                double r = rand.nextDouble();
                if (r < 0.5) {
                    severity = rand.nextInt(2) + 1; // 1–2
                } else if (r < 0.8) {
                    severity = rand.nextInt(2) + 3; // 3–4
                } else {
                    severity = 5; // 20% chance
                }
            } else {
                // Uniform distribution 1–5
                severity = rand.nextInt(5) + 1;
            }

            // Register and enqueue
            registry.registerNew(id, name, age, severity);
            triage.enqueueById(registry, id);
        }
    }

    // TODO: dequeueK(triage, K) with empty-checks
    /**
     * Dequeues K patients from the triage queue if not empty.
     *
     * @param triage The triage queue
     * @param K      Number of patients to dequeue
     */
    public static void dequeueK(TriageQueue triage, int K) {
        for (int i = 0; i < K; i++) {
            if (triage.peekNext().isEmpty()) {
                break; // queue is empty
            }
            triage.dequeueNext();
        }
    }
    // TODO: knobs: severity distribution (uniform vs. skewed), ratios enqueue/dequeue
    /**
     * Runs a workload with knobs for enqueue/dequeue ratio and severity distribution.
     *
     * @param registry      The patient registry
     * @param triage        The triage queue
     * @param iterations    Total number of operations (enqueue + dequeue)
     * @param enqueueRatio  Fraction of operations that should be enqueues (0–1)
     * @param severityDist  Severity distribution: "uniform" or "skewed"
     * @param seed          Random seed
     */
    public static void runWorkload(PatientRegistry registry,
                                   TriageQueue triage,
                                   int iterations,
                                   double enqueueRatio,
                                   String severityDist,
                                   long seed) {
        Random rand = new Random(seed);
        int patientCounter = 0;

        for (int i = 0; i < iterations; i++) {
            if (rand.nextDouble() < enqueueRatio) {
                // Enqueue a single patient
                String id = "P" + patientCounter++;
                String name = "Patient" + id;
                int age = rand.nextInt(100) + 1;

                int severity;
                if ("skewed".equalsIgnoreCase(severityDist)) {
                    double r = rand.nextDouble();
                    if (r < 0.5) severity = rand.nextInt(2) + 1; // 1–2
                    else if (r < 0.8) severity = rand.nextInt(2) + 3; // 3–4
                    else severity = 5; // 20% chance
                } else {
                    severity = rand.nextInt(5) + 1; // uniform 1–5
                }
                registry.registerNew(id, name, age, severity);
                triage.enqueueById(registry, id);
            } else {
                // Dequeue a patient if available
                if (!triage.peekNext().isEmpty()) {
                    triage.dequeueNext();
                }
            }
        }
    }
}
