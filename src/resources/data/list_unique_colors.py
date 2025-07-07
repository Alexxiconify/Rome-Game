import cv2
import numpy as np
import csv
import os

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    csv_path = 'owner_color_name.csv'
    
    if not os.path.exists(csv_path):
        print(f"Warning: {csv_path} not found")
        return mapping
        
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            color_str = row['owner_color']
            name = row['owner_name']
            # Parse color string like "(130, 175, 255)" to RGB tuple
            color_str = color_str.strip('()')
            rgb = tuple(int(x.strip()) for x in color_str.split(','))
            mapping[rgb] = name
    return mapping

# Path to your start.png
start_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/start.png"
mask_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_mask.png"

# Load owner color mapping
color_mapping = load_owner_color_mapping()

start = cv2.imread(start_path)
if start is None:
    print(f"Could not load {start_path}")
    exit(1)

start_rgb = cv2.cvtColor(start, cv2.COLOR_BGR2RGB)
height, width = start_rgb.shape[:2]
unique_colors = set()

for y in range(height):
    for x in range(width):
        color = tuple(start_rgb[y, x])
        unique_colors.add(color)

print(f"Found {len(unique_colors)} unique colors in {start_path}:")
print("\nColor mapping to owner names:")
for color in sorted(unique_colors):
    owner_name = color_mapping.get(color, "UNKNOWN")
    print(f"{color} -> {owner_name}")

# Count mapped vs unmapped colors
mapped_colors = sum(1 for color in unique_colors if color in color_mapping)
unmapped_colors = len(unique_colors) - mapped_colors

print(f"\nSummary:")
print(f"Total unique colors: {len(unique_colors)}")
print(f"Mapped to owners: {mapped_colors}")
print(f"Unmapped colors: {unmapped_colors}")

if unmapped_colors > 0:
    print(f"\nUnmapped colors (not in owner_color_name.csv):")
    for color in sorted(unique_colors):
        if color not in color_mapping:
            print(f"  {color}") 