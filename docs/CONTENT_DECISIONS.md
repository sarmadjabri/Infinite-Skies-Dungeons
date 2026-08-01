# Approved Content Decisions

This is the source of truth for user-approved creature and boss choices. New
combat content must not be implemented until its vanilla controller, attacks,
weapons/equipment, and important behavioral constraints have been approved.

## Global equipment rule

- Every monster's weapon and armor must be marked unbreakable and have a zero
  drop chance. This rule applies to all current and future monster definitions
  unless the user explicitly overrides it.

## Mossbound Brute

- **Vanilla controller:** Iron Golem.
- **Core attacks:** normal close-range Iron Golem-style melee and a single
  heavy thrown moss-block projectile.
- **Projectile rule:** the Brute appears to create the moss block in its hands;
  it never changes world blocks. The shot is straight and telegraphed, aimed at
  the selected position at cast time, with no homing.
- **Health:** 40 HP.
- **Projectile damage:** 10 HP.
- **Projectile cooldown:** 5 seconds (100 server ticks).
- **Movement speed:** the normal Iron Golem base speed.
- **Armor/equipment:** no armor.
- **Melee damage:** 17 HP.

## Rift Archer

- **Vanilla controller:** Stray.
- **Equipment:** full leather armor and a bow.
- **Core ranged attack:** wind-charge shots fired while using the bow
  presentation. These deal 5 HP direct damage and apply knockback.
- **Health:** 40 HP.
- **Shot cooldown:** 3 seconds (60 server ticks).
- **Movement speed:** normal Stray base speed.
- **Low-health escape:** below 10% health, it randomly attempts a three-block
  displacement along either the X or Z axis. Implementation must validate the
  destination, preserve dungeon-region limits, and never teleport it into an
  unsafe or invalid location. This can occur at most once every 5 seconds
  (100 server ticks).

## Voidling

- Deferred by user. Do not implement or configure this creature until a later
  design decision is supplied.

## Storm Caller

- **Vanilla controller:** Drowned.
- **Equipment:** full chainmail armor and a trident.
- **Core attack:** a thrown trident that, on hitting a player, triggers a
  controlled lightning strike at that player's position. The strike must use
  plugin damage and visuals so it never ignite fires, damage terrain, or create
  uncontrolled vanilla-lightning side effects.
- **Health:** 50 HP.
- **Lightning damage:** 7 HP, with an intentionally infrequent cast cadence.
- **Direct trident-hit damage:** 10 HP.
- **Lightning throw cooldown:** 5 seconds (100 server ticks).
- **Movement:** 10% faster than the normal Drowned base speed.
- **Additional attack:** a 3-second 360-degree spin toward a player. Each
  controlled contact during the spin deals 2 HP. It has a 25-second cooldown
  (500 server ticks); a player must not be damaged repeatedly each tick.
- **Balance values still awaiting approval:** scaling.

## Sky Reaver

- **Vanilla controller:** Phantom.
- **Equipment:** no weapon.
- **Health:** 20 HP.
- **Attack:** swoops into a player and deals 5 HP damage on a successful hit.
- **Movement speed:** normal Phantom base speed.
- **Armor/equipment:** none.
- **Movement speed:** normal Warden base speed.
- **Swoop cooldown:** 7 seconds (140 server ticks).
- **Balance values still awaiting approval:** scaling.

## Ruin Colossus

- **Vanilla controller:** Warden.
- **Combat:** Warden-style close-range melee plus one heavy, straight,
  telegraphed block projectile. The projectile is purely visual/combat logic
  and cannot alter terrain.
- **Health:** 100 HP.
- **Melee damage:** 15 HP.
- **Projectile damage:** 15 HP.
- **Block-throw cooldown:** 5 seconds (100 server ticks).
- **Armor/equipment:** none.
- **Prohibited vanilla behavior:** no sonic boom or other unintended Warden
  attacks; implementation must suppress them rather than expose them as part
  of the encounter.
- **Balance values still awaiting approval:** scaling.

## Hollow Sentinel

- Deferred by user. Do not implement or configure this boss until a later
  design decision is supplied.
