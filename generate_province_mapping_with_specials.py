import numpy as np
from PIL import Image
import csv
import re

# File paths
start_img_path = "start.png"
mask_img_path = "province_mask.png"
nation_java_path = "nation.java"
output_csv_path = "province_ownership_report.csv"

# Parse nation.java for RGB to nation mapping
rgb_to_nation = {}
with open(nation_java_path, "r") as f:
    for line in f:
        m = re.match(r"Nation\((\d+),\s*(\d+),\s*(\d+),\s*([^)]+)\);", line.strip())
        if m:
            r, g, b, name = int(m.group(1)), int(m.group(2)), int(m.group(3)), m.group(4)
            rgb_to_nation[(r, g, b)] = name

# Load images as numpy arrays
start_img = np.array(Image.open(start_img_path).convert("RGBA"))
mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))

# Only consider non-transparent mask pixels
mask_alpha = mask_img[..., 3] > 0
mask_colors = mask_img[..., :3][mask_alpha]
start_pixels = start_img[mask_alpha]

# Find unique province colors and their first occurrence
_, idx = np.unique(mask_colors, axis=0, return_index=True)
province_colors = mask_colors[idx]
province_ids = [f"province_{i+1:04d}" for i in range(len(province_colors))]
province_start_pixels = start_pixels[idx]

# Prepare output arrays
r = province_start_pixels[:, 0]
g = province_start_pixels[:, 1]
b = province_start_pixels[:, 2]
a = province_start_pixels[:, 3]

# Vectorized owner assignment
owners = np.full(len(province_ids), "", dtype=object)
remove_mask = a < 255
border_mask = (r == 255) & (g == 255) & (b == 255) & (a == 255)

owners[remove_mask] = "REMOVE_FROM_MAP"
owners[border_mask] = "BORDER"

# For the rest, use nation.java mapping or fallback
for i in range(len(owners)):
    if owners[i] == "":
        key = (int(r[i]), int(g[i]), int(b[i]))
        owners[i] = rgb_to_nation.get(key, f"rgb_{r[i]}_{g[i]}_{b[i]}")

# Write CSV
with open(output_csv_path, "w", newline="") as csvfile:
    writer = csv.writer(csvfile)
    writer.writerow(["province_id", "r", "g", "b", "owner"])
    for prov_id, rv, gv, bv, owner in zip(province_ids, r, g, b, owners):
        writer.writerow([prov_id, int(rv), int(gv), int(bv), owner])

print(f"Written: {output_csv_path}")