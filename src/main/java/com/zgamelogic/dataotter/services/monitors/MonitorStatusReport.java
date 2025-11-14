package com.zgamelogic.dataotter.services.monitors;

public record MonitorStatusReport(long milliseconds, boolean status, int attempts, int statusCode) {}
