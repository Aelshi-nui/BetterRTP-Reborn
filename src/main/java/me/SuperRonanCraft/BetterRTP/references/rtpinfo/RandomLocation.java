package me.SuperRonanCraft.BetterRTP.references.rtpinfo;

import me.SuperRonanCraft.BetterRTP.BetterRTP;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.RTPWorld;
import me.SuperRonanCraft.BetterRTP.references.rtpinfo.worlds.WORLD_TYPE;
import me.SuperRonanCraft.BetterRTP.versions.AsyncHandler;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomLocation {

    public static Location generateLocation(RTPWorld rtpWorld) {
        Location loc;
        switch (rtpWorld.getShape()) {
            case CIRCLE: loc = generateRound(rtpWorld); break;
            case SQUARE:
            default: loc = generateSquare(rtpWorld); break;
        }
        return loc;
    }

    private static Location generateSquare(RTPWorld rtpWorld) {
        //Generate a random X and Z based off the quadrant selected
        int min = rtpWorld.getMinRadius();
        int max = rtpWorld.getMaxRadius() - min;
        if (max < 0)
            return null;
        int x, z;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int quadrant = random.nextInt(4);
        try {
            switch (quadrant) {
                case 0: // Positive X and Z
                    x = random.nextInt(max + 1) + min;
                    z = random.nextInt(max + 1) + min;
                    break;
                case 1: // Negative X and Z
                    x = -random.nextInt(max + 1) - min;
                    z = -(random.nextInt(max + 1) + min);
                    break;
                case 2: // Negative X and Positive Z
                    x = -random.nextInt(max + 1) - min;
                    z = random.nextInt(max + 1) + min;
                    break;
                default: // Positive X and Negative Z
                    x = random.nextInt(max + 1) + min;
                    z = -(random.nextInt(max + 1) + min);
                    break;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            BetterRTP.getInstance().getLogger().warning("A bounding location was negative! Please check your config only has positive x/z for max/min radius!");
            BetterRTP.getInstance().getLogger().warning("Max: " + rtpWorld.getMaxRadius() + " Min: " + rtpWorld.getMinRadius());
            return null;
        }
        x += rtpWorld.getCenterX();
        z += rtpWorld.getCenterZ();
        //System.out.println(quadrant);
        return new Location(rtpWorld.getWorld(), x, 69, z);
    }

    private static Location generateRound(RTPWorld rtpWorld) {
        //Generate a random X and Z based off location on a spiral curve
        int min = rtpWorld.getMinRadius();
        int max = rtpWorld.getMaxRadius();
        if (max < min)
            return null;
        int x, z;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double radius = Math.sqrt(random.nextDouble(min * min, (max * max) + 1.0D));
        double theta = random.nextDouble(0, Math.PI * 2);

        // polar to cartesian
        x = (int) (radius * Math.cos(theta));
        z = (int) (radius * Math.sin(theta));
        x += rtpWorld.getCenterX();
        z += rtpWorld.getCenterZ();
        return new Location(rtpWorld.getWorld(), x, 69, z);
    }

    public static Location getSafeLocation(WORLD_TYPE type, World world, Location loc, int minY, int maxY, List<String> biomes) {
        if (world == null || loc == null)
            return null;
        minY = Math.max(minY, getWorldMinHeight(world));
        maxY = Math.min(maxY, world.getMaxHeight() - 1);
        if (minY >= maxY)
            return null;
        switch (type) { //Get a Y position and check for bad blocks
            case NETHER: return getLocAtNether(loc.getBlockX(), loc.getBlockZ(), minY, maxY, world, biomes);
            case NORMAL:
            default: return getLocAtNormal(loc.getBlockX(), loc.getBlockZ(), minY, maxY, world, biomes);
        }
    }
    private static Location getLocAtNormal(int x, int z, int minY, int maxY, World world, List<String> biomes) {
        Block b = getHighestBlock(x, z, world);
        if (!b.getType().isSolid()) { //Water, lava, shrubs...
            if (!badBlock(b.getType().name(), x, z, world, null)) { //Make sure it's not an invalid block (ex: water, lava...)
                //int y = world.getHighestBlockYAt(x, z);
                b = world.getBlockAt(x, b.getY() - 1, z);
            }
        }
        //Between max and min y
        if (    b.getY() >= minY
                && b.getY() <= maxY
                && !badBlock(b.getType().name(), x, z, world, biomes)) {
            return new Location(world, x, b.getY() + 1, z);
        }
        return null;
    }

    public static Block getHighestBlock(int x, int z, World world) {
        Block b = world.getHighestBlockAt(x, z);
        if (b.getType().toString().endsWith("AIR") && b.getY() > getWorldMinHeight(world)) //1.15.1 or less
            b = world.getBlockAt(x, b.getY() - 1, z);
        return b;
    }

    private static int getWorldMinHeight(World world) {
        try {
            return (int) World.class.getMethod("getMinHeight").invoke(world);
        } catch (ReflectiveOperationException ignored) {
            return 0;
        }
    }

    private static Location getLocAtNether(int x, int z, int minY, int maxY, World world, List<String> biomes) {
        //Max and Min Y
        for (int y = minY + 1; y < maxY/*world.getMaxHeight()*/; y++) {
            Block block_current = world.getBlockAt(x, y, z);
            if (block_current.getType().name().endsWith("AIR") || !block_current.getType().isSolid()) {
                if (!block_current.getType().name().endsWith("AIR") &&
                        !block_current.getType().isSolid()) { //Block is not a solid (ex: lava, water...)
                    String block_in = block_current.getType().name();
                    if (badBlock(block_in, x, z, world, null))
                        continue;
                }
                String block = world.getBlockAt(x, y - 1, z).getType().name();
                if (block.endsWith("AIR")) //Block below is air, skip
                    continue;
                if (world.getBlockAt(x, y + 1, z).getType().name().endsWith("AIR") //Head space
                        && !badBlock(block, x, z, world, biomes)) //Valid block
                    return new Location(world, x, y, z);
            }
        }
        return null;
    }

    // Bad blocks, or bad biome
    public static boolean badBlock(String block, int x, int z, World world, List<String> biomes) {
        for (String currentBlock : BetterRTP.getInstance().getRTP().getBlockList()) //Check Block
            if (currentBlock.equalsIgnoreCase(block))
                return true;
        //Check Biomes
        if (biomes == null || biomes.isEmpty())
            return false;
        String biomeCurrent = world.getBiome(x, z).name();
        for (String biome : biomes)
            if (biomeCurrent.toUpperCase().contains(biome.toUpperCase()))
                return false;
        return true;
        //FALSE MEANS NO BAD BLOCKS/BIOME WHERE FOUND!
    }

    public static void runChunkTest() {
        BetterRTP.getInstance().getLogger().info("---------------- Starting chunk test!");
        World world = Bukkit.getWorld("world");
        cacheChunkAt(world, 32, -32, -32, -32);
    }

    private static void cacheTask(World world, int goal, int start, int xat, int zat) {
        zat += 1;
        if (zat > goal) {
            zat = start;
            xat += 1;
        }
        if (xat <= goal)
            cacheChunkAt(world, goal, start, xat, zat);
    }

    private static void cacheChunkAt(World world, int goal, int start, int xat, int zat) {
        Location location = new Location(world, xat * 16, 0, zat * 16);
        AsyncHandler.syncAtLocation(location, () -> {
            try {
                Chunk chunk = location.getChunk();
                ChunkSnapshot snapshot = chunk.getChunkSnapshot(true, true, false);
                int maxy = snapshot.getHighestBlockYAt(8, 8);
                Biome biome = snapshot.getBiome(8, 8);
                //BetterRTP.getInstance().getLogger().info("Added " + chunk.getX() + " " + chunk.getZ());
                BetterRTP.getInstance().getDatabaseHandler().getDatabaseChunks().addChunk(chunk, maxy, biome);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException();
                //BetterRTP.getInstance().getLogger().info("Tried Adding " + chunk.getX() + " " + chunk.getZ());
            }
            cacheTask(world, goal, start, xat, zat);
        });
    }

}
