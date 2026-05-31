package org.example;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.loot.item.Items;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.DesertPyramid;
import com.seedfinding.mcfeature.structure.generator.structure.DesertPyramidGenerator;

public class Main {
    public static void main(String[] args) {

        MCVersion version = MCVersion.v1_17;
        ChunkRand rand = new ChunkRand();
        DesertPyramid temple = new DesertPyramid(version);
        DesertPyramidGenerator dtg = new DesertPyramidGenerator(version);

        for (long structureSeed = 1; structureSeed < 100_000_000_000L; structureSeed++) {
            if (structureSeed % 100_000 == 0) {
                System.out.println("Checking seed: " + structureSeed);
            }
            CPos dtPos = temple.getInRegion(structureSeed, 0, 0, rand);
            if (dtPos.distanceTo(CPos.ZERO, DistanceMetric.CHEBYSHEV) > 5) {
                continue;
            }

            dtg.generate(null, dtPos);
            var loot = temple.getLoot(structureSeed, dtg, rand, false);

            int notchCount = 0;

            for (var chest : loot) {
                notchCount += chest.getCount(Items.ENCHANTED_GOLDEN_APPLE);
            }
            int ironingotcount = 0;

            for (var chest : loot) {
                ironingotcount += chest.getCount(Items.IRON_INGOT);
            }
            int goldingotcount = 0;

            for (var chest : loot) {
                goldingotcount += chest.getCount(Items.GOLD_INGOT);
            }
            if (notchCount < 4) continue;
            if (goldingotcount < 5) continue;
            if (ironingotcount < 4) continue;

            System.out.println("checking sister seeds");
            WorldSeed.getSisterSeeds(structureSeed).asStream().boxed()
                    .forEach(worldseed -> {
                        BiomeSource obs = BiomeSource.of(Dimension.OVERWORLD, version, worldseed);
                        if (!temple.canSpawn(dtPos, obs))
                            return;

                        CPos spawnPos = SpawnPoint.getApproximateSpawn((OverworldBiomeSource) obs).toChunkPos();
                        if (spawnPos.distanceTo(dtPos, DistanceMetric.CHEBYSHEV) != 1) {
                            return;
                        }
                        System.out.println(worldseed);

                    });

        }
    }
}


