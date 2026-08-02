"""Create the vanilla-client custom-model resource pack for dungeon mob IDs."""
from __future__ import annotations

import json
from pathlib import Path
import shutil
import zipfile

from PIL import Image


SOURCE_PACK = Path("resource-pack/mossbound-brute")
OUT = Path("resource-pack/dungeon-mob-models")
ARCHIVE = Path("dist/InfiniteSkies-Dungeon-Mob-Models.zip")

# Encounter ID -> (geometry family, shadow, highlight, glow/accent).
MOBS = {
    "mossbound_brute": ("golem", (52, 65, 52), (151, 163, 133), (191, 210, 70)),
    "crumbling_husk": ("humanoid", (61, 54, 43), (158, 139, 98), (191, 151, 84)),
    "ruin_crawler": ("spider", (26, 35, 30), (85, 117, 70), (163, 203, 77)),
    "dust_wretch": ("humanoid", (53, 49, 43), (137, 121, 90), (183, 143, 80)),
    "shard_slinger": ("humanoid", (47, 42, 57), (126, 111, 154), (205, 122, 242)),
    "observatory_watcher": ("humanoid", (36, 43, 49), (120, 139, 149), (104, 225, 235)),
    "venom_spitter": ("spider", (20, 33, 26), (72, 116, 62), (159, 220, 54)),
    "gale_acolyte": ("wisp", (42, 69, 60), (133, 183, 150), (212, 249, 183)),
    "moss_priest": ("humanoid", (43, 55, 37), (123, 139, 75), (190, 203, 94)),
    "void_mender": ("humanoid", (37, 29, 52), (111, 80, 148), (204, 130, 245)),
    "grave_chanter": ("humanoid", (43, 48, 37), (115, 125, 78), (202, 192, 111)),
    "ruin_standard_bearer": ("humanoid", (44, 45, 44), (131, 121, 103), (187, 73, 56)),
    "stone_binder": ("humanoid", (42, 37, 54), (107, 91, 132), (177, 148, 211)),
    "ceiling_stalker": ("spider", (20, 22, 21), (76, 81, 73), (199, 165, 91)),
    "mimic_husk": ("humanoid", (49, 41, 35), (128, 98, 66), (218, 143, 59)),
    "dust_phantom": ("winged", (34, 34, 40), (115, 119, 126), (206, 168, 230)),
    "buried_guardian": ("humanoid", (54, 48, 39), (146, 128, 93), (190, 160, 89)),
    "false_statue": ("humanoid", (42, 47, 45), (126, 132, 119), (195, 186, 133)),
    "ember_wisp": ("cube", (66, 24, 12), (192, 69, 28), (255, 197, 66)),
    "storm_wisp": ("wisp", (28, 47, 64), (91, 145, 191), (150, 229, 252)),
    "frost_wisp": ("wisp", (45, 71, 83), (137, 187, 202), (211, 250, 255)),
    "rift_mine": ("cube", (34, 23, 51), (95, 61, 144), (196, 109, 232)),
    "rift_archer": ("humanoid", (42, 57, 61), (130, 175, 176), (93, 225, 230)),
    "storm_caller": ("humanoid", (27, 50, 60), (76, 131, 139), (235, 174, 63)),
    "sky_reaver": ("winged", (24, 21, 39), (90, 68, 124), (183, 105, 231)),
    "ruin_colossus": ("warden", (38, 43, 43), (119, 125, 111), (112, 213, 174)),
}


def face() -> dict[str, object]:
    return {"uv": [0, 0, 16, 16], "texture": "#layer0"}


def cuboid(start: list[int], end: list[int]) -> dict[str, object]:
    return {"from": start, "to": end, "faces": {side: face() for side in ("north", "south", "east", "west", "up", "down")}}


def elements(family: str) -> list[dict[str, object]]:
    if family == "humanoid":
        return [cuboid([4, 0, 5], [7, 10, 11]), cuboid([9, 0, 5], [12, 10, 11]), cuboid([3, 10, 4], [13, 22, 12]), cuboid([0, 11, 5], [3, 21, 11]), cuboid([13, 11, 5], [16, 21, 11]), cuboid([3, 22, 3], [13, 32, 13])]
    if family == "golem":
        return [cuboid([3, 0, 4], [7, 16, 12]), cuboid([9, 0, 4], [13, 16, 12]), cuboid([2, 16, 3], [14, 31, 13]), cuboid([-3, 17, 4], [2, 31, 12]), cuboid([14, 17, 4], [19, 31, 12]), cuboid([2, 31, 2], [14, 41, 14])]
    if family == "warden":
        return [cuboid([3, 0, 4], [7, 14, 12]), cuboid([9, 0, 4], [13, 14, 12]), cuboid([2, 14, 3], [14, 29, 13]), cuboid([0, 15, 4], [3, 28, 12]), cuboid([13, 15, 4], [16, 28, 12]), cuboid([2, 29, 2], [14, 39, 14])]
    if family == "spider":
        return [cuboid([4, 5, 3], [12, 12, 13]), cuboid([3, 3, 0], [13, 10, 6]), cuboid([-3, 3, 3], [4, 5, 5]), cuboid([12, 3, 3], [19, 5, 5]), cuboid([-4, 3, 7], [4, 5, 9]), cuboid([12, 3, 7], [20, 5, 9]), cuboid([-3, 3, 11], [4, 5, 13]), cuboid([12, 3, 11], [19, 5, 13])]
    if family == "winged":
        return [cuboid([5, 5, 5], [11, 17, 11]), cuboid([4, 17, 4], [12, 24, 12]), cuboid([-12, 9, 6], [5, 15, 10]), cuboid([11, 9, 6], [28, 15, 10])]
    if family == "wisp":
        return [cuboid([4, 2, 4], [12, 14, 12]), cuboid([2, 5, 2], [14, 11, 14]), cuboid([5, 14, 5], [11, 18, 11])]
    return [cuboid([2, 1, 2], [14, 15, 14]), cuboid([4, 15, 4], [12, 18, 12])]


def texture(path: Path, low: tuple[int, int, int], high: tuple[int, int, int], accent: tuple[int, int, int], seed: int) -> None:
    image = Image.new("RGBA", (32, 32), (*low, 255))
    pixels = image.load()
    for y in range(32):
        for x in range(32):
            shade = ((x * 5 + y * 3 + seed) % 13) / 12
            pixels[x, y] = tuple(round(a + (b - a) * shade) for a, b in zip(low, high)) + (255,)
            if (x * 17 + y * 11 + seed) % 37 == 0:
                pixels[x, y] = (*accent, 255)
    image.save(path)


def write_model(mob_id: str, family: str) -> None:
    item_definition = {"model": {"type": "minecraft:model", "model": f"infiniteskies:item/mob/{mob_id}"}}
    model = {"gui_light": "front", "textures": {"layer0": f"infiniteskies:mob/{mob_id}"}, "elements": elements(family)}
    item_path = OUT / "assets/infiniteskies/items/mob" / f"{mob_id}.json"
    model_path = OUT / "assets/infiniteskies/models/item/mob" / f"{mob_id}.json"
    item_path.parent.mkdir(parents=True, exist_ok=True)
    model_path.parent.mkdir(parents=True, exist_ok=True)
    item_path.write_text(json.dumps(item_definition, indent=2) + "\n")
    model_path.write_text(json.dumps(model, indent=2) + "\n")


def copy_global_armor() -> None:
    for source in SOURCE_PACK.glob("assets/minecraft/textures/entity/equipment/**/*.png"):
        destination = OUT / source.relative_to(SOURCE_PACK)
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, destination)


def archive() -> None:
    ARCHIVE.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(ARCHIVE, "w", compression=zipfile.ZIP_DEFLATED) as output:
        for file in sorted(OUT.rglob("*")):
            if file.is_file():
                output.write(file, file.relative_to(OUT))


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / "pack.mcmeta").write_text(json.dumps({"pack": {"pack_format": 88, "description": "Infinite Skies Dungeons — Unique Mob Models"}}, indent=2) + "\n")
    (OUT / "README.md").write_text("# Infinite Skies unique mob models\n\nThis pack renders a unique custom model for each managed dungeon-mob ID. Vanilla entities are unchanged.\n")
    copy_global_armor()
    for mob_id, (family, low, high, accent) in MOBS.items():
        texture_path = OUT / "assets/infiniteskies/textures/mob" / f"{mob_id}.png"
        texture_path.parent.mkdir(parents=True, exist_ok=True)
        texture(texture_path, low, high, accent, sum(map(ord, mob_id)))
        write_model(mob_id, family)
    archive()
    print(ARCHIVE)


if __name__ == "__main__":
    main()
