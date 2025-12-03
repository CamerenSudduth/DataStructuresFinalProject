package edu.hcu.triage;

import java.util.*;

/** Append-only treatment log using LinkedList. */
public class TreatmentLog {
    private final LinkedList<TreatedCase> log = new LinkedList<>();

    // TODO: append(TreatedCase tc)
    public void append(TreatedCase tc) {
        log.add(tc); //appends treated case to the log linked list
    }

    // TODO: size()
    public int size() {
        return log.size(); //returns the size of the log linked list
    }

    // TODO: asListOldestFirst() -
    public LinkedList<TreatedCase> asListOldestFirst() {
        return new LinkedList<>(log); // orders the linked list from oldest to newest
    }

    // TODO: asListNewestFirst() -
    public LinkedList<TreatedCase> asListNewestFirst() {
        LinkedList<TreatedCase> clone = new LinkedList<>(log);
        Collections.reverse(clone); //creates clone to not change normal order of log list
        return clone; //returns reversed list of normal order that orders list from newest to oldest
    }
}
