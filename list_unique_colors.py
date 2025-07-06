import cv2
import numpy as np

# Path to your start.png
start_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/start.png"
mask_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_mask.png"
csv_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/resources/province_color_map.csv"
out_csv_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/resources/province_color_map_with_owner.csv"

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
for color in sorted(unique_colors):
    print(color) 