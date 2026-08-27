package dev.nonsinn.miningtweaks;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MiningMode {
    private static final Set<UUID> ENABLED = ConcurrentHashMap.newKeySet();

    private MiningMode() {
    }

    public static boolean toggle(UUID playerId) {
        if (ENABLED.remove(playerId)) {
            return false;
        }
        ENABLED.add(playerId);
        return true;
    }

    public static boolean isEnabled(UUID playerId) {
        return ENABLED.contains(playerId);
    }

    public static void clear() {
        ENABLED.clear();
    }
}
