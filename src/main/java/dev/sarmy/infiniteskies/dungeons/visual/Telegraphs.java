package dev.sarmy.infiniteskies.dungeons.visual;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Lightweight, bounded vanilla particle telegraphs. */
public final class Telegraphs {
    private Telegraphs() { }
    public static void line(World world, Location start, Location end, Particle particle, int points) {
        Vector delta = end.toVector().subtract(start.toVector());
        for (int index = 0; index <= points; index++) world.spawnParticle(particle, start.clone().add(delta.clone().multiply(index / (double) points)), 1);
    }
    public static void circle(World world, Location center, double radius, Particle particle, int points) {
        for (int index = 0; index < points; index++) {
            double angle = Math.PI * 2 * index / points;
            world.spawnParticle(particle, center.clone().add(Math.cos(angle) * radius, .05, Math.sin(angle) * radius), 1);
        }
    }
}
