"""Package the tested direct-entity textures without global player armor edits."""
from pathlib import Path
import json
import shutil
import zipfile

SOURCE = Path("resource-pack/mossbound-brute")
OUT = Path("resource-pack/stable-entity-textures")
ARCHIVE = Path("dist/InfiniteSkies-Dungeon-Entity-Textures.zip")


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    entity_source = SOURCE / "assets/minecraft/textures/entity"
    destination = OUT / "assets/minecraft/textures/entity"
    if destination.exists():
        shutil.rmtree(destination)
    # The old source pack also contains its retired global armor folder beneath
    # textures/entity. Exclude it so player armor remains entirely vanilla.
    shutil.copytree(entity_source, destination, ignore=shutil.ignore_patterns("equipment"))
    (OUT / "pack.mcmeta").write_text(json.dumps({
        "pack": {
            "min_format": [88, 0],
            "max_format": [88, 0],
            "description": "Infinite Skies Dungeons — Stable Entity Textures"
        }
    }, indent=2) + "\n")
    (OUT / "README.md").write_text(
        "# Stable Infinite Skies entity textures\n\n"
        "This pack uses direct vanilla entity texture overrides for reliability on Minecraft 26.2. "
        "It intentionally contains no armor textures.\n"
    )
    ARCHIVE.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(ARCHIVE, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        for file in sorted(OUT.rglob("*")):
            if file.is_file():
                archive.write(file, file.relative_to(OUT))
    print(ARCHIVE)


if __name__ == "__main__":
    main()
