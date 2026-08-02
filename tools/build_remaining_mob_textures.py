"""Build vanilla-compatible dungeon textures for each remaining base entity.

Several encounter IDs intentionally share a vanilla body. A vanilla resource
pack has one texture per body type, so those encounter IDs share the matching
visual until a custom-model/variant layer is added later.
"""
from __future__ import annotations

from pathlib import Path
import random
import sys
import zipfile

from PIL import Image


VANILLA_JAR = Path("/home/sarmy/.minecraft/versions/26.2/26.2.jar")
PACK_ROOT = Path("resource-pack/mossbound-brute")

# file inside vanilla jar -> (shadow colour, highlight colour, accent colour)
# The original brightness controls where each source pixel falls in this range,
# preserving Minecraft's native atlas layout while supplying dungeon styling.
PROFILES: dict[str, tuple[tuple[int, int, int], tuple[int, int, int], tuple[int, int, int]]] = {
    # Mimic Husk / False Statue: cracked, dormant ruin-stone humanoid.
    "entity/zombie/zombie.png": ((40, 43, 39), (133, 125, 102), (94, 73, 58)),
    # Ruin Crawler / Venom Spitter: soot-black legs and toxic green carapace.
    "entity/spider/cave_spider.png": ((20, 28, 25), (77, 113, 72), (139, 181, 63)),
    # Rift Archer: cold pale stone bones with a cyan rift glow.
    "entity/skeleton/stray.png": ((40, 52, 57), (164, 187, 184), (87, 212, 218)),
    "entity/skeleton/stray_overlay.png": ((24, 33, 36), (93, 119, 119), (76, 190, 201)),
    # Storm Caller: soaked blue-green corpse with copper lightning scars.
    "entity/zombie/drowned.png": ((24, 43, 54), (79, 130, 139), (203, 139, 57)),
    "entity/zombie/drowned_outer_layer.png": ((20, 36, 42), (73, 105, 108), (181, 119, 46)),
    # Sky Reaver: void-black membrane with dusty violet markings.
    "entity/phantom/phantom.png": ((21, 18, 33), (91, 68, 122), (166, 92, 211)),
    "entity/phantom/phantom_eyes.png": ((42, 22, 58), (164, 86, 213), (223, 159, 255)),
    # Ruin Colossus: weathered charcoal stone with muted turquoise seams.
    "entity/warden/warden.png": ((30, 36, 38), (111, 117, 106), (77, 153, 137)),
    "entity/warden/warden_bioluminescent_layer.png": ((20, 41, 42), (67, 158, 143), (107, 220, 182)),
    "entity/warden/warden_heart.png": ((31, 54, 49), (91, 174, 145), (151, 240, 195)),
    "entity/warden/warden_pulsating_spots_1.png": ((25, 52, 47), (79, 162, 136), (141, 228, 181)),
    "entity/warden/warden_pulsating_spots_2.png": ((25, 52, 47), (79, 162, 136), (141, 228, 181)),
    # Ruin Standard-Bearer: smoke-grey soldier with faded rust-red regalia.
    "entity/illager/pillager.png": ((38, 40, 42), (126, 121, 108), (154, 66, 51)),
    # Void Mender / Stone Binder: a dark amethyst ritual caster.
    "entity/witch/witch.png": ((27, 20, 39), (104, 73, 131), (173, 100, 205)),
    # Moss Priest / Grave Chanter: decayed moss-and-lichen villagers.
    "entity/zombie_villager/zombie_villager.png": ((39, 48, 34), (125, 132, 81), (172, 177, 88)),
    # Ceiling Stalker: charcoal-black spider with pale stone dust.
    "entity/spider/spider.png": ((17, 20, 21), (78, 84, 78), (157, 150, 113)),
    "entity/spider/spider_eyes.png": ((49, 29, 20), (194, 113, 55), (247, 179, 83)),
    # Dust Phantom: pale, eroded void apparition.
    "entity/illager/vex.png": ((30, 33, 37), (121, 130, 133), (186, 151, 206)),
    "entity/illager/vex_charging.png": ((31, 27, 41), (123, 85, 147), (209, 129, 231)),
    # Ember Wisp / Rift Mine / Storm-Frost Wisp / Gale Acolyte.
    "entity/slime/magmacube.png": ((59, 20, 12), (196, 75, 27), (255, 187, 55)),
    "entity/slime/slime.png": ((29, 20, 44), (96, 55, 143), (182, 98, 226)),
    "entity/allay/allay.png": ((24, 48, 63), (91, 153, 191), (151, 229, 246)),
    "entity/breeze/breeze.png": ((36, 58, 53), (120, 168, 144), (194, 236, 179)),
    "entity/breeze/breeze_eyes.png": ((30, 59, 52), (112, 201, 149), (207, 255, 187)),
    "entity/breeze/breeze_wind.png": ((48, 78, 70), (153, 204, 173), (216, 255, 206)),
}


def tint(image: Image.Image, low: tuple[int, int, int], high: tuple[int, int, int], accent: tuple[int, int, int], seed: int) -> Image.Image:
    source = image.convert("RGBA")
    pixels = source.load()
    randomizer = random.Random(seed)
    for y in range(source.height):
        for x in range(source.width):
            red, green, blue, alpha = pixels[x, y]
            if alpha == 0:
                continue
            light = (red + green + blue) / (3.0 * 255.0)
            # A restrained colour-range remap keeps texture detail readable.
            rgb = [round(a + (b - a) * light) for a, b in zip(low, high)]
            # Sparse chips/runes add readable dungeon wear without changing UVs.
            if (x * 17 + y * 31 + seed) % 97 == 0 and randomizer.random() < .78:
                rgb = list(accent)
            pixels[x, y] = (*rgb, alpha)
    return source


def write_texture(jar: zipfile.ZipFile, source_name: str, profile: tuple[tuple[int, int, int], tuple[int, int, int], tuple[int, int, int]]) -> None:
    raw = jar.read(f"assets/minecraft/textures/{source_name}")
    output = PACK_ROOT / "assets/minecraft/textures" / source_name
    output.parent.mkdir(parents=True, exist_ok=True)
    seed = sum(ord(character) for character in source_name)
    tint(Image.open(__import__("io").BytesIO(raw)), *profile, seed).save(output)
    print(output)


def main() -> None:
    if not VANILLA_JAR.is_file():
        raise SystemExit(f"Minecraft 26.2 client jar was not found: {VANILLA_JAR}")
    with zipfile.ZipFile(VANILLA_JAR) as jar:
        for source_name, profile in PROFILES.items():
            write_texture(jar, source_name, profile)


if __name__ == "__main__":
    main()
