package edu.hcu.triage;

import java.util.*;

/** Append-only treatment log using LinkedList. */
public class TreatmentLog {
    private final LinkedList<TreatedCase> log = new LinkedList<>();

    // TODO: append(TreatedCase tc)
    public void append(TreatedCase tc) {
        log.add(tc);
    }

    // TODO: size()
    public int size() {
        return log.size();
    }

    // TODO: asListOldestFirst() -
    public LinkedList<TreatedCase> asListOldestFirst() {
        return new LinkedList<>(log);

    }

    // TODO: asListNewestFirst() -
    public LinkedList<TreatedCase> asListNewestFirst() {
        LinkedList<TreatedCase> clone = new LinkedList<>(log);
        Collections.reverse(clone);
        return clone;
    }
}
