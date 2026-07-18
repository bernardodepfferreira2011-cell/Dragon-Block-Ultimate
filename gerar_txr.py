#!/usr/bin/env python3
import os
from pathlib import Path

ROOT = Path(__file__).resolve().parent
OUTPUT = ROOT / "arquivo.txr"
ALLOWED_EXTENSIONS = {".vsh", ".fsh", ".json", ".java"}
IGNORED_DIRS = {".git", ".gradle", "build", "out", "node_modules", ".idea", ".vscode", "run", "tmp"}


def should_skip(path: Path) -> bool:
    for part in path.parts:
        if part in IGNORED_DIRS:
            return True
    return False


def collect_files(root: Path):
    files = []
    for path in root.rglob("*"):
        if path.is_file() and not should_skip(path):
            if path.suffix.lower() in ALLOWED_EXTENSIONS:
                files.append(path)
    return sorted(files)


def build_output(files):
    sections = []
    for path in files:
        try:
            text = path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            try:
                text = path.read_text(encoding="latin-1")
            except Exception:
                continue

        rel = path.relative_to(ROOT).as_posix()
        sections.append(f"===== FILE: {rel} =====\n{text}\n")
    return "\n".join(sections)


if __name__ == "__main__":
    files = collect_files(ROOT)
    content = build_output(files)
    OUTPUT.write_text(content, encoding="utf-8")
    print(f"Arquivo criado em: {OUTPUT}")
    print(f"Arquivos incluídos: {len(files)}")
