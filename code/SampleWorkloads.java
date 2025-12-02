package edu.hcu.triage;

import java.util.Random;

/** Deterministic workload generators for performance tests. */
public final class SampleWorkloads {
    // TODO: enqueueNRandomPatients(registry, triage, N, seed, distribution)
    private SampleWorkloads() {
    }

    // Enqueue N random patients into the registry and triage queue
    public static void enqueueNRandomPatients(PatientRegistry registry, TriageQueue triage,
                                              int N, long seed, String distribution) {
        Random rand = new Random(seed); // Using seed ensures repeatable randomness

        for (int i = 0; i < N; i++) {
            // Generate ID and name
            String id = "P" + i;
            String name = "Patient" + i;

            // Generate age 1–100
            int age = rand.nextInt(100) + 1;

            // Generate severity
            int severity = 1;
            if ("skewed".equalsIgnoreCase(distribution)) {
                // Example skew: more low-severity patients
                double r = rand.nextDouble();
                if (r < 0.5) {
                    severity = rand.nextInt(2) + 4; // 4-5: 50% mild cases
                } else if (r < 0.8) {
                    severity = rand.nextInt(2) + 2; // 2-3: 30% medium cases
                } else {
                    severity = 1; // 1: 20% severe
                }
            } else if ("uniform".equalsIgnoreCase(distribution)) {
                // Uniform distribution 1–5
                severity = rand.nextInt(5) + 1;
            }

            // Register and enqueue patients
            registry.registerNew(id, name, age, severity);
            triage.enqueueById(registry, id);
        }
    }

    // TODO: dequeueK(triage, K) with empty-checks
    // Dequeues K patients with check for empty queues
    public static void dequeueKPatients(TriageQueue triage, int K) {
        for (int i = 0; i < K; i++) {
            if (triage.peekNext().isEmpty()) {
                break; // queue is empty
            }
            triage.dequeueNext(); // Otherwise dequeue patient
        }
    }

    // TODO: knobs: severity distribution (uniform vs. skewed), ratios enqueue/dequeue
    public static void runWorkload(
            PatientRegistry registry,
            TriageQueue triage,
            int workloadSize,
            double enqueueRatio,
            String distribution, // "uniform" or "skewed"
                                 // skewed has more mild patients, less medium, and few severe
            long seed
    ) {
        if (workloadSize <= 0) {
            throw new IllegalArgumentException("Workload size must be positive");
        }
        if (enqueueRatio < 0.0 || enqueueRatio > 1.0) {
            throw new IllegalArgumentException("Enqueue ratio must be between 0 and 1");
        }
        if (!"uniform".equalsIgnoreCase(distribution) && !"skewed".equalsIgnoreCase(distribution)) {
            throw new IllegalArgumentException("Distribution must be 'uniform' or 'skewed'");
        }

        Random rand = new Random(seed); // Ensures repeatable randomness
        int enqueueOps = (int) (workloadSize * enqueueRatio);
        int dequeueOps = workloadSize - enqueueOps;

        // Enqueue patients
        for (int i = 0; i < enqueueOps; i++) {
            String id = "P" + i;
            String name = "Patient" + id;

            // Generate random patients ages from 1-100
            int age = rand.nextInt(100) + 1;

            int severity = 1;

            if ("skewed".equalsIgnoreCase(distribution)) {
                double r = rand.nextDouble();
                if (r < 0.5) {
                    severity = 4 + rand.nextInt(2); // mild: 4–5 (50% distribution)
                } else if (r < 0.8) {
                    severity = 2 + rand.nextInt(2); // moderate: 2–3 (30% distribution)
                } else {
                    severity = 1; // critical: 1 (20% distribution)
                }
            } else if ("uniform".equalsIgnoreCase(distribution)) {
                severity = rand.nextInt(5) + 1; // uniform 1–5
            }

            // Enqueue patients
            registry.registerNew(id, name, age, severity);
            triage.enqueueById(registry, id);
        }

        // Dequeue remaining patients according to chosen ratio
        for (int i = 0; i < dequeueOps; i++) {
            if (!triage.peekNext().isEmpty()) {
                triage.dequeueNext();
            }
        }
    }
}
