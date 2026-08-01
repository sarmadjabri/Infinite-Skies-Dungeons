package dev.sarmy.infiniteskies.dungeons.scheduler;

/** A main-thread object updated by the shared dungeon tick loop. */
@FunctionalInterface
public interface Tickable {
    void tick(long tick);
}
