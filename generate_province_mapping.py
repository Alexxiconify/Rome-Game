#!/usr/bin/env python3
"""
Generate province color mapping from province mask image.
This script analyzes the province_mask.png and creates a CSV mapping
of ARGB colors to province IDs.
"""

import cv2
import numpy as np
import csv
import os
from collections import defaultdict

def rgb_to_argb(r, g, b):
    """Convert RGB to ARGB integer format used by Java."""
    return (255 << 24) | (r << 16) | (g << 8) | b

def generate_province_mapping():
    """Generate color to province ID mapping from province mask."""
    
    # Check if province mask exists
    mask_path = "province_mask.png"
    if not os.path.exists(mask_path):
        print(f"Error: {mask_path} not found!")
        print("Please ensure you have generated the province mask first.")
        return
    
    # Load the mask image
    print("Loading province mask...")
    mask = cv2.imread(mask_path)
    if mask is None:
        print(f"Error: Could not load {mask_path}")
        return
    
    # Convert BGR to RGB (OpenCV loads as BGR)
    mask_rgb = cv2.cvtColor(mask, cv2.COLOR_BGR2RGB)
    
    # Find unique colors (excluding black background)
    print("Analyzing unique province colors...")
    unique_colors = set()
    height, width = mask_rgb.shape[:2]
    
    for y in range(height):
        for x in range(width):
            r, g, b = mask_rgb[y, x]
            # Skip black background (0,0,0)
            if r > 0 or g > 0 or b > 0:
                argb = rgb_to_argb(r, g, b)
                unique_colors.add(argb)
    
    print(f"Found {len(unique_colors)} unique province colors")
    
    # Generate province IDs
    province_ids = []
    for i, argb in enumerate(sorted(unique_colors)):
        # Convert ARGB back to RGB for display
        r = (argb >> 16) & 0xFF
        g = (argb >> 8) & 0xFF
        b = argb & 0xFF
        
        # Generate province ID based on color
        province_id = f"province_{i+1:03d}"
        province_ids.append((argb, province_id))
        
        print(f"Province {i+1}: ARGB={argb}, RGB=({r},{g},{b}), ID={province_id}")
    
    # Write CSV mapping
    csv_path = "resources/province_color_map.csv"
    print(f"\nWriting mapping to {csv_path}...")
    
    with open(csv_path, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['ARGB', 'provinceId'])
        for argb, province_id in province_ids:
            writer.writerow([argb, province_id])
    
    print(f"Successfully created {csv_path} with {len(province_ids)} province mappings")
    print("\nNext steps:")
    print("1. Review the generated mapping file")
    print("2. Update province IDs to match your historical data if needed")
    print("3. The Java code will automatically load this mapping")

if __name__ == "__main__":
    generate_province_mapping() 