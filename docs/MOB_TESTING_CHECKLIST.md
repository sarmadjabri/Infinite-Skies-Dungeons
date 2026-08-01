# Mob Configuration and Testing Checklist

This checklist tracks the user-approved roster. A check means the mob has been
configured and verified in-game, not merely compiled. Keep a mob unchecked
until its controller, equipment, attributes, attacks, cooldowns, damage, and
cleanup behavior have been tested on the Paper 26.2 server.

## Complete

- [x] `mossbound_brute` — Iron Golem controller, player targeting, 40 HP,
  17-HP melee, 20-HP moss-block throw, 5-second cooldown, large projectile
  visual, torso aim, and no world-block modification.

## Needs configuration and in-game testing

- [ ] `crumbling_husk`
- [ ] `rift_archer`
- [ ] `ruin_crawler`
- [ ] `storm_caller`
- [ ] `sky_reaver`
- [ ] `ruin_colossus`
- [ ] `observatory_watcher`
- [ ] `venom_spitter`
- [ ] `gale_acolyte`
- [ ] `moss_priest`
- [ ] `void_mender`
- [ ] `grave_chanter`
- [ ] `ruin_standard_bearer`
- [ ] `stone_binder`
- [ ] `ceiling_stalker`
- [ ] `mimic_husk`
- [ ] `dust_phantom`
- [ ] `buried_guardian`
- [ ] `false_statue`
- [ ] `ember_wisp`
- [ ] `storm_wisp`
- [ ] `frost_wisp`
- [ ] `rift_mine`

## Deferred design

- [ ] `voidling` — no approved controller, attributes, equipment, or abilities.
- [ ] `hollow_sentinel` — boss design deferred.

## Test procedure for each configured mob

1. Spawn it with `/isdungeon mob spawn <mob-id>`.
2. Verify controller entity, custom name, health, movement, equipment, and
   zero equipment drops.
3. Test every attack in Survival mode; confirm exact damage, cooldown,
   targeting, telegraph, range, and player-hit cap.
4. Verify it cannot change terrain, leave its configured boundary, retain
   temporary effects, or leave projectiles/zones/summons after removal.
5. Test its tagged spawn egg and a tagged vanilla spawner.
6. Mark it complete only after all checks pass.
