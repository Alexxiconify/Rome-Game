#!/usr/bin/env python3
"""
Generate province color mapping from province mask image.
This script analyzes the province_mask.png and creates a CSV mapping
of ARGB colors to province IDs, robust for any real/historical mask.
"""

import cv2
import numpy as np
import csv
import os
from collections import defaultdict

def rgb_to_argb(r, g, b):
    """Convert RGB to ARGB integer format used by Java."""
    return (255 << 24) | (r << 16) | (g << 8) | b

def argb_to_hex(argb):
    return f"0x{argb & 0xFFFFFFFF:08X}"

def generate_province_mapping(mask_path="province_mask.png", csv_path="resources/province_color_map.csv", background_rgb=(0,0,0)):
    if not os.path.exists(mask_path):
        print(f"Error: {mask_path} not found!")
        return
    print(f"Loading province mask: {mask_path}")
    mask = cv2.imread(mask_path)
    if mask is None:
        print(f"Error: Could not load {mask_path}")
        return
    mask_rgb = cv2.cvtColor(mask, cv2.COLOR_BGR2RGB)
    height, width = mask_rgb.shape[:2]
    unique_colors = set()
    bg_r, bg_g, bg_b = background_rgb
    for y in range(height):
        for x in range(width):
            r, g, b = mask_rgb[y, x]
            # Skip background/ocean color
            if (r, g, b) == (bg_r, bg_g, bg_b):
                continue
            argb = rgb_to_argb(r, g, b)
            unique_colors.add(argb)
    print(f"Found {len(unique_colors)} unique province colors (excluding background {background_rgb})")
    province_ids = []
    for i, argb in enumerate(sorted(unique_colors)):
        r = (argb >> 16) & 0xFF
        g = (argb >> 8) & 0xFF
        b = argb & 0xFF
        province_id = f"province_{i+1:04d}"
        province_ids.append((argb, province_id))
        print(f"Province {i+1}: ARGB={argb} ({argb_to_hex(argb)}), RGB=({r},{g},{b}), ID={province_id}")
    os.makedirs(os.path.dirname(csv_path), exist_ok=True)
    print(f"\nWriting mapping to {csv_path}...")
    with open(csv_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['ARGB', 'ARGB_HEX', 'provinceId'])
        for argb, province_id in province_ids:
            writer.writerow([argb, argb_to_hex(argb), province_id])
    print(f"Successfully created {csv_path} with {len(province_ids)} province mappings.")
    print("If you see magenta in-game, compare ARGB_HEX here to your Java debug output.")

if __name__ == "__main__":
    # You can change background_rgb if your ocean is not black
    generate_province_mapping(background_rgb=(0,0,0)) 