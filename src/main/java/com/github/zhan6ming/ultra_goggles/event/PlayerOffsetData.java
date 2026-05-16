package com.github.zhan6ming.ultra_goggles.event;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerOffsetData {

    private static final Map<UUID, Double> OFFSETS = new ConcurrentHashMap<>();

    public static void setOffset(UUID uuid, double offset) {
        OFFSETS.put(uuid, offset);
    }

    public static double getOffset(UUID uuid) {
        return OFFSETS.getOrDefault(uuid, 0.0);
    }
}
