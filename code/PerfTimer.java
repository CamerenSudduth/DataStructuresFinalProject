package edu.hcu.triage;

/** Tiny helper for timing blocks via try-with-resources. */
public final class PerfTimer implements AutoCloseable {
    private final String label;
    private final long start;

    public PerfTimer(String label) {
        this.label = label;
        this.start = System.nanoTime();
    }

    @Override
    public void close() {
        long ns = System.nanoTime() - start;
        // TODO: print label + elapsed milliseconds (format nicely)
        System.out.print(label + "   Time: " + (ns*0.000001) + " milliseconds");
    }
}
