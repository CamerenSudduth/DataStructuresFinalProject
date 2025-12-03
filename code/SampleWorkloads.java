package edu.hcu.triage;

import java.util.Arrays;
import java.util.Random;

// Deterministic workload generators for performance tests
public final class SampleWorkloads {

    private SampleWorkloads() {
    }

    // Result of running a workload, including counts before/after dequeues and dequeues performed
    public static class WorkloadResult {
        public final int[] beforeDequeues; // snapshot after enqueue
        public final int[] afterDequeues; // snapshot after dequeue
        public final int dequeuesPerformed;

        public WorkloadResult(int[] beforeDequeues, int[] afterDequeues, int dequeuesPerformed) {
            this.beforeDequeues = beforeDequeues;
            this.afterDequeues = afterDequeues;
            this.dequeuesPerformed = dequeuesPerformed;
        }
    }

    // Enqueue N random patients into the registry and triage queue
    public static int[] enqueueNRandomPatients(PatientRegistry registry, TriageQueue triage,
                                               int N, long seed, String distribution) {
        Random rand = new Random(seed);
        int[] severityCounts = new int[5]; // initialize an array to track each severity count

        for (int i = 0; i < N; i++) {
            String id = "P" + i;
            String name = "Patient" + i;
            int age = rand.nextInt(100) + 1; // age 1-100

            // Determine severity based on chosen distribution
            int severity;
            if ("skewed".equalsIgnoreCase(distribution)) {
                // 60% severity 4-5
                // 30% severity 2-3
                // 10% severity 1
                double r = rand.nextDouble();
                if (r < 0.6) {
                    severity = rand.nextInt(2) + 4; // 4-5
                } else if (r < 0.9) {
                    severity = rand.nextInt(2) + 2; // 2-3
                } else {
                    severity = 1; // 1
                }
            } else {
                // Uniform distribution: 1-5 with equal probability
                severity = rand.nextInt(5) + 1; // uniform 1-5
            }

            severityCounts[severity - 1]++; // Track each severity count
            // Register and enqueue patient
            registry.registerNew(id, name, age, severity);
            triage.enqueueById(registry, id);
        }

        return severityCounts;
    }

    // Dequeues K patients with check for empty queues and updates severity counts
    public static int dequeueKPatients(TriageQueue triage, int K, int[] severityCounts) {
        int count = 0;
        for (int i = 0; i < K; i++) {
            var nextOpt = triage.peekNext();
            if (nextOpt.isEmpty()) break; // stop deque if queue is empty

            int sev = nextOpt.get().getSeverity();
            if (sev >= 1 && sev <= 5) severityCounts[sev - 1]--; // decrement remaining count
            triage.dequeueNext();
            count++;
        }
        return count;
    }

    // Runs a deterministic workload and returns before/after severity counts and dequeues performed
    public static WorkloadResult runWorkload(PatientRegistry registry, TriageQueue triage,
                                             int workloadSize, double enqueueRatio,
                                             String distribution, long seed) {
        // Validate parameters
        if (workloadSize <= 0) throw new IllegalArgumentException("Workload size must be positive");
        if (enqueueRatio < 0.0 || enqueueRatio > 1.0)
            throw new IllegalArgumentException("Enqueue ratio must be between 0 and 1");
        if (!"uniform".equalsIgnoreCase(distribution) && !"skewed".equalsIgnoreCase(distribution))
            throw new IllegalArgumentException("Distribution must be 'uniform' or 'skewed'");

        // Count operations
        int enqueueOps = (int) (workloadSize * enqueueRatio);
        int dequeueOps = workloadSize - enqueueOps;

        // Enqueue patients
        int[] severityCounts = enqueueNRandomPatients(registry, triage, enqueueOps, seed, distribution);

        // Take snapshots so results are not mutated
        int[] beforeDequeues = Arrays.copyOf(severityCounts, severityCounts.length);
        int[] afterDequeues = Arrays.copyOf(severityCounts, severityCounts.length);

        // Dequeue remaining patients and update afterDequeues
        int dequeuesPerformed = dequeueKPatients(triage, dequeueOps, afterDequeues);

        return new WorkloadResult(beforeDequeues, afterDequeues, dequeuesPerformed);
    }
}
