package me.SuperRonanCraft.BetterRTP.references.player.playerdata;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final ConcurrentHashMap<Player, PlayerData> playerData = new ConcurrentHashMap<>();

    public PlayerData getData(@NonNull Player p) {
        if (!playerData.containsKey(p))
            playerData.put(p, new PlayerData(p));
        return playerData.get(p);
    }

    @Nullable
    public PlayerData getData(UUID id) {
        for (Player p : playerData.keySet())
            if (p.getUniqueId().equals(id))
                return playerData.get(p);
        return null;
    }

    public void clear() {
        playerData.clear();
    }

    public void clear(Player p) {
        playerData.remove(p);
    }
}
