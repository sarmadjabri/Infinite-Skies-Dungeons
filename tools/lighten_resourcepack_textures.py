"""Brighten current resource-pack textures without changing their hue palette."""
from pathlib import Path
import sys

from PIL import Image


def brighten(path: Path, factor: float) -> None:
    image = Image.open(path).convert("RGBA")
    pixels = image.load()
    for y in range(image.height):
        for x in range(image.width):
            red, green, blue, alpha = pixels[x, y]
            if alpha == 0:
                continue
            # Uniform channel scaling retains the original hue relationship.
            pixels[x, y] = (
                min(255, round(red * factor)),
                min(255, round(green * factor)),
                min(255, round(blue * factor)),
                alpha,
            )
    image.save(path)


def main() -> None:
    root = Path(sys.argv[1]) if len(sys.argv) == 2 else Path("resource-pack/mossbound-brute")
    for texture in sorted(root.glob("assets/**/*.png")):
        brighten(texture, 1.18)
        print(texture)


if __name__ == "__main__":
    main()
