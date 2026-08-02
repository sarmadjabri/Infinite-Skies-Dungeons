"""Builds the approved global armor retextures from the installed vanilla assets."""
from io import BytesIO
from pathlib import Path
import random
import sys
import zipfile

from PIL import Image


ARMOR_FILES = (
    "humanoid/leather.png",
    "humanoid_leggings/leather.png",
    "humanoid/chainmail.png",
    "humanoid_leggings/chainmail.png",
    "humanoid/iron.png",
    "humanoid_leggings/iron.png",
)


def recolor(image: Image.Image, low: tuple[int, int, int], high: tuple[int, int, int], seed: int) -> Image.Image:
    randomizer = random.Random(seed)
    result = image.convert("RGBA")
    pixels = result.load()
    for y in range(result.height):
        for x in range(result.width):
            red, green, blue, alpha = pixels[x, y]
            if alpha == 0:
                continue
            light = (red + green + blue) / (3 * 255)
            grain = randomizer.uniform(-.08, .08)
            light = max(0.0, min(1.0, light + grain))
            pixels[x, y] = tuple(round(a + (b - a) * light) for a, b in zip(low, high)) + (alpha,)
    return result


def add_leather_detail(image: Image.Image) -> None:
    pixels = image.load()
    for y in range(2, image.height - 2):
        for x in range(2, image.width - 2):
            # Sparse seam stitches and pale worn cloth patches.
            if (x * 11 + y * 7) % 47 == 0:
                pixels[x, y] = (74, 58, 43, pixels[x, y][3])
            elif (x * 3 + y * 5) % 89 in (0, 1):
                pixels[x, y] = (135, 119, 87, pixels[x, y][3])


def add_chain_detail(image: Image.Image) -> None:
    pixels = image.load()
    for y in range(image.height):
        for x in range(image.width):
            if pixels[x, y][3] == 0:
                continue
            if (x + y) % 4 == 0:
                pixels[x, y] = (41, 47, 50, pixels[x, y][3])
            elif (x - y) % 11 == 0:
                pixels[x, y] = (112, 80, 47, pixels[x, y][3])


def add_iron_detail(image: Image.Image) -> None:
    pixels = image.load()
    for y in range(image.height):
        for x in range(image.width):
            if pixels[x, y][3] == 0:
                continue
            if x % 8 == 0 or y % 8 == 0:
                pixels[x, y] = (49, 57, 61, pixels[x, y][3])
            elif (x * 5 + y * 3) % 31 == 0:
                pixels[x, y] = (184, 192, 191, pixels[x, y][3])


def transform(name: str, image: Image.Image) -> Image.Image:
    if "leather" in name:
        result = recolor(image, (51, 40, 30), (151, 124, 80), 11)
        add_leather_detail(result)
    elif "chainmail" in name:
        result = recolor(image, (35, 42, 45), (118, 126, 123), 29)
        add_chain_detail(result)
    else:
        result = recolor(image, (53, 60, 62), (155, 166, 166), 53)
        add_iron_detail(result)
    return result


def main(jar_name: str, output_name: str) -> None:
    destination = Path(output_name) / "assets/minecraft/textures/entity/equipment"
    with zipfile.ZipFile(jar_name) as jar:
        for suffix in ARMOR_FILES:
            internal = "assets/minecraft/textures/entity/equipment/" + suffix
            with jar.open(internal) as stream:
                image = Image.open(BytesIO(stream.read()))
            target = destination / suffix
            target.parent.mkdir(parents=True, exist_ok=True)
            transform(suffix, image).save(target)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit("usage: build_global_armor_textures.py <minecraft-jar> <pack-directory>")
    main(sys.argv[1], sys.argv[2])
