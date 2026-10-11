package com.donut.builder.module.builder;

public class SequenceTracker {
    private static int seq = 0;
    private static final int MASK = 0x7FFF;
    public static synchronized int next() { int v = seq; seq = (seq + 1) & MASK; return v; }
    public static synchronized int current() { return seq; }
    public static synchronized void ack(int serverSeq) { seq = (serverSeq + 1) & MASK; }
}
