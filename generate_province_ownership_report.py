import cv2
import numpy as np
import csv
import re
import os

# Hardcoded paths
mask_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_mask.png"
start_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/start.png"
nation_java_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/nation.java"
csv_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/resources/province_color_map.csv"
out_csv_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_ownership_report.csv"

# Parse nation.java for color to nation name mapping
color_to_nation = {}
with open(nation_java_path, "r") as f:
    for line in f:
        match = re.search(r'Nation\((\d+),\s*(\d+),\s*(\d+),\s*"?([^"]*)"?\)', line)
        if match:
            r, g, b, name = match.groups()
            color = (int(r), int(g), int(b))
            name = name.strip() if name.strip() else f"Unknown_{r}_{g}_{b}"
            color_to_nation[color] = name

# Load images
mask = cv2.imread(mask_path)
start = cv2.imread(start_path)
mask_rgb = cv2.cvtColor(mask, cv2.COLOR_BGR2RGB)
start_rgb = cv2.cvtColor(start, cv2.COLOR_BGR2RGB)
height, width = mask_rgb.shape[:2]

# Load province color mapping for provinceID ordering
province_colors = []
province_ids = []
with open(csv_path, "r") as f:
    reader = csv.reader(f)
    next(reader)
    for row in reader:
        argb = int(row[0])
        province_id = row[2]
        b = argb & 0xFF
        g = (argb >> 8) & 0xFF
        r = (argb >> 16) & 0xFF
        province_colors.append((r, g, b))
        province_ids.append(province_id)

# Build a set for fast lookup
province_color_set = set(province_colors)
color_to_location = {}

# Loop through all pixels ONCE
for y in range(height):
    for x in range(width):
        color = tuple(mask_rgb[y, x])
        if color in province_color_set and color not in color_to_location:
            color_to_location[color] = (x, y)
            # Optional: break early if all found
            if len(color_to_location) == len(province_colors):
                break
    if len(color_to_location) == len(province_colors):
        break

# Output CSV
with open(out_csv_path, 'w', newline='') as out:
    writer = csv.writer(out)
    writer.writerow(["provinceID", "x", "y", "province_r", "province_g", "province_b", "owner_r", "owner_g", "owner_b", "owner_name"])
    for color, province_id in zip(province_colors, province_ids):
        x, y = color_to_location.get(color, (-1, -1))
        owner_rgb = tuple(start_rgb[y, x]) if (0 <= x < width and 0 <= y < height) else (0, 0, 0)
        owner_name = color_to_nation.get(owner_rgb, f"Unknown_{owner_rgb[0]}_{owner_rgb[1]}_{owner_rgb[2]}")
        writer.writerow([province_id, x, y, color[0], color[1], color[2], owner_rgb[0], owner_rgb[1], owner_rgb[2], owner_name])

print(f"Wrote {out_csv_path}") 