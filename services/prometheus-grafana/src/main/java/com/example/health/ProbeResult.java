package com.example.health;

public record ProbeResult(String status, String message) {

    public static ProbeResult healthy() {
        return new ProbeResult("healthy", null);
    }

    public static ProbeResult degraded(String message) {
        return new ProbeResult("degraded", message);
    }

    public static ProbeResult unhealthy(String message) {
        return new ProbeResult("unhealthy", message);
    }
}
