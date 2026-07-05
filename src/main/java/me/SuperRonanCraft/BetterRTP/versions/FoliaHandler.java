package me.SuperRonanCraft.BetterRTP.versions;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.SuperRonanCraft.BetterRTP.BetterRTP;

public class FoliaHandler {

    private FoliaLib FOLIA_LIB;
    private ServerImplementation SERVER_IMPLEMENTATION;

    public void load() {
        this.FOLIA_LIB = new FoliaLib(BetterRTP.getInstance());
        this.SERVER_IMPLEMENTATION = FOLIA_LIB.getImpl();
    }

    public ServerImplementation get() {
        return SERVER_IMPLEMENTATION;
    }

    public boolean isFolia() {
        return FOLIA_LIB != null && FOLIA_LIB.isFolia();
    }

}
