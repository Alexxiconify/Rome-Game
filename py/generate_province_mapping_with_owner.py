import cv2
import numpy as np
import csv
import os
from collections import defaultdict

def rgb_to_argb(r, g, b):
    return (255 << 24) | (r << 16) | (g << 8) | b

def argb_to_hex(argb):
    return f"0x{argb & 0xFFFFFFFF:08X}"

def main():
    mask_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_mask.png"
    start_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/start.png"
    csv_path = "resources/province_color_map.csv"
    out_csv_path = "resources/province_color_map_with_owner.csv"

    if not (os.path.exists(mask_path) and os.path.exists(start_path)):
        print("province_mask.png or start.png not found!")
        return

    mask = cv2.imread(mask_path)
    start = cv2.imread(start_path)
    mask_rgb = cv2.cvtColor(mask, cv2.COLOR_BGR2RGB)
    start_rgb = cv2.cvtColor(start, cv2.COLOR_BGR2RGB)
    height, width = mask_rgb.shape[:2]

    # Find all unique province colors
    unique_colors = set()
    for y in range(height):
        for x in range(width):
            r, g, b = mask_rgb[y, x]
            if (r, g, b) != (0, 0, 0):  # skip background
                unique_colors.add((r, g, b))

    # Map province color to a sample pixel
    color_to_sample = {}
    for y in range(height):
        for x in range(width):
            r, g, b = mask_rgb[y, x]
            if (r, g, b) in unique_colors and (r, g, b) not in color_to_sample:
                color_to_sample[(r, g, b)] = (x, y)

    # Map owner colors to names (fill this out as needed)
    owner_color_to_name = defaultdict(lambda: "Uninhabited")
    # Example: owner_color_to_name[(220, 40, 40)] = "Roman Empire"
    # You should fill this mapping with your actual nation colors!

    # Build province mapping with owner
    province_rows = []
    for i, (r, g, b) in enumerate(sorted(unique_colors)):
        argb = rgb_to_argb(r, g, b)
        province_id = f"province_{i+1:04d}"
        x, y = color_to_sample[(r, g, b)]
        owner_rgb = tuple(start_rgb[y, x])
        owner = owner_color_to_name[owner_rgb]
        province_rows.append([argb, argb_to_hex(argb), province_id, owner])

    # Write new CSV
    os.makedirs(os.path.dirname(out_csv_path), exist_ok=True)
    with open(out_csv_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['ARGB', 'ARGB_HEX', 'provinceId', 'owner'])
        for row in province_rows:
            writer.writerow(row)
    print(f"Wrote {out_csv_path} with {len(province_rows)} provinces.")

if __name__ == "__main__":
    main()