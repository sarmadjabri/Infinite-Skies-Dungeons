# Resource-Pack Backlog

This document preserves visual-model decisions that are deliberately deferred
until Infinite Skies has a resource pack. Combat behavior must remain attached
to vanilla controller entities so these visuals can be added without changing
encounter logic.

## Mossbound Brute

- **Controller for the current vanilla-only stage:** Iron Golem.
- **Future visual direction:** a moss-covered ancient construct that retains
  the silhouette and readable proportions of an Iron Golem.
- **Combat direction:** close-range golem strikes plus a single heavy thrown
  moss-block projectile.
- **Projectile presentation:** the Brute visually produces a moss block in its
  hands and throws it at a player. It must never take, place, break, or alter
  world blocks. The future resource-pack projectile may replace the temporary
  vanilla block-item visual while retaining the same ability ID and hit logic.
- **Projectile behavior:** a straight, visibly telegraphed throw. It targets
  the player position chosen at cast time rather than homing after the player.

## Implementation notes

- Keep model/texture identifiers separate from mob and ability identifiers.
- The future model layer should be optional; encounters must remain playable
  without a resource pack.
- Any future resource-pack asset must preserve clear attack telegraphs and not
  make hitboxes or danger areas ambiguous.

## Ruin Colossus

- **Future visual direction:** an ancient construct textured primarily as stone
  bricks with cobblestone detail.
- **Combat relationship:** it uses the same approved combat pattern as the
  Mossbound Brute: close-range construct melee and a single heavy,
  straight, telegraphed block projectile that never alters world blocks.

## Storm Caller

- **Future visual direction:** give the Drowned's three-second 360-degree
  spinning attack a dedicated, readable animation. The current vanilla stage
  will preserve the same timing and danger area with rotations, particles, and
  sound cues; combat logic must not depend on the animation asset.
