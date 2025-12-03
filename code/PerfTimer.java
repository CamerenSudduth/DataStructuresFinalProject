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
    public void close() { // formats the elapsed time in milliseconds 
        long ns = System.nanoTime() - start;
        // TODO: print label + elapsed milliseconds (format nicely)
        //prints label and elapsed time in milliseconds
        System.out.print(label + (ns*0.000001) + " milliseconds");
    }
}
