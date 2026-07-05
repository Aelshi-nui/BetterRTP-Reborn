package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.impl.ServerImplementation;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.SuperRonanCraft.BetterRTP.BetterRTP;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.concurrent.CompletableFuture;

public class AsyncHandler {

    public static void async(Runnable runnable) {
        getFolia().runAsync(task -> runnable.run());
    }

    public static void sync(Runnable runnable) {
        getFolia().runNextTick(task -> runnable.run());
    }

    public static void syncAtEntity(Entity entity, Runnable runnable) {
        getFolia().runAtEntityWithFallback(entity, task -> runnable.run(), runnable);
    }

    public static void syncAtLocation(Location location, Runnable runnable) {
        getFolia().runAtLocation(location, task -> runnable.run());
    }

    public static WrappedTask asyncLater(Runnable runnable, long ticks) {
        return getFolia().runLaterAsync(runnable, ticks);
    }
    public static WrappedTask syncLater(Runnable runnable, long ticks) {
        return getFolia().runLater(runnable, ticks);
    }

    public static WrappedTask syncAtEntityLater(Entity entity, Runnable runnable, long ticks) {
        return getFolia().runAtEntityLater(entity, runnable, runnable, ticks);
    }

    public static WrappedTask syncAtLocationLater(Location location, Runnable runnable, long ticks) {
        return getFolia().runAtLocationLater(location, runnable, ticks);
    }

    public static CompletableFuture<Boolean> teleportAsync(Entity entity, Location location, PlayerTeleportEvent.TeleportCause cause) {
        return getFolia().teleportAsync(entity, location, cause);
    }

    public static boolean isFolia() {
        return BetterRTP.getInstance().getFoliaHandler().isFolia();
    }

    private static ServerImplementation getFolia() {
        return BetterRTP.getInstance().getFoliaHandler().get();
    }
}
