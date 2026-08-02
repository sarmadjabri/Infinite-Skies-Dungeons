"""Converts the approved generated Mossbound Brute atlas into a Minecraft texture."""
from collections import deque
from pathlib import Path
import sys

from PIL import Image


def is_near_black(pixel: tuple[int, int, int, int]) -> bool:
    return pixel[0] < 18 and pixel[1] < 18 and pixel[2] < 18


def main(source_name: str, output_name: str) -> None:
    source = Image.open(source_name).convert("RGBA")
    pixels = source.load()
    width, height = source.size

    # Remove only the black background connected to an outer edge. This keeps
    # dark crack/shadow pixels inside the actual model faces intact.
    queue: deque[tuple[int, int]] = deque()
    visited: set[tuple[int, int]] = set()
    for x in range(width):
        queue.extend(((x, 0), (x, height - 1)))
    for y in range(height):
        queue.extend(((0, y), (width - 1, y)))
    while queue:
        x, y = queue.popleft()
        if (x, y) in visited or not (0 <= x < width and 0 <= y < height):
            continue
        visited.add((x, y))
        if not is_near_black(pixels[x, y]):
            continue
        pixels[x, y] = (0, 0, 0, 0)
        queue.extend(((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)))

    texture = source.resize((64, 64), Image.Resampling.NEAREST)
    output = Path(output_name)
    output.parent.mkdir(parents=True, exist_ok=True)
    texture.save(output)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        raise SystemExit("usage: build_mossbound_texture.py <source-png> <output-png>")
    main(sys.argv[1], sys.argv[2])
