#!/usr/bin/env python3
"""
Generate provinces from start.png by analyzing image pixels
"""

import cv2
import numpy as np
import json
import csv
from pathlib import Path

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    csv_path = 'owner_color_name.csv'
    
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            color_str = row['owner_color']
            name = row['owner_name']
            color_str = color_str.strip('()')
            rgb = tuple(int(x.strip()) for x in color_str.split(','))
            mapping[rgb] = name
    return mapping

def generate_provinces_from_image():
    """Generate provinces from start.png image"""
    
    # Load the start.png image
    start_path = "../../resources/img/start.png"
    if not Path(start_path).exists():
        print(f"Error: {start_path} not found")
        return None
    
    # Load image
    start = cv2.imread(start_path)
    if start is None:
        print(f"Error: Could not load {start_path}")
        return None
    
    # Convert BGR to RGB
    start_rgb = cv2.cvtColor(start, cv2.COLOR_BGR2RGB)
    height, width = start_rgb.shape[:2]
    
    print(f"Image loaded: {width}x{height} pixels")
    
    # Load owner color mapping
    color_mapping = load_owner_color_mapping()
    print(f"Loaded {len(color_mapping)} owner color mappings")
    
    # Find unique colors in the image
    unique_colors = set()
    for y in range(height):
        for x in range(width):
            color = tuple(start_rgb[y, x])
            if color != (0, 0, 0):  # Skip black (ocean)
                unique_colors.add(color)
    
    print(f"Found {len(unique_colors)} unique colors (excluding black)")
    
    # Generate provinces for each unique color
    provinces = []
    province_id = 1
    
    for color in sorted(unique_colors):
        owner_name = color_mapping.get(color, f"Unknown_{province_id}")
        
        # Count pixels of this color
        pixel_count = np.sum(np.all(start_rgb == color, axis=2))
        
        # Create province
        province = {
            "province_id": f"province_{province_id:04d}",
            "owner_color": list(color),
            "owner": owner_name,
            "pixel_count": int(pixel_count)
        }
        provinces.append(province)
        province_id += 1
    
    print(f"Generated {len(provinces)} provinces")
    
    # Update nations_and_provinces.json
    with open('nations_and_provinces.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    data['provinces'] = provinces
    
    with open('nations_and_provinces.json', 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    return provinces

def generate_province_files(provinces):
    """Generate province CSV files"""
    
    # Generate province_owners.csv
    with open('province_owners.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['province_id', 'owner_name'])
        for province in provinces:
            writer.writerow([province['province_id'], province['owner']])
    
    # Generate province_ownership_report.csv
    owner_counts = {}
    for province in provinces:
        owner = province['owner']
        owner_counts[owner] = owner_counts.get(owner, 0) + 1
    
    with open('province_ownership_report.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['owner_name', 'province_count'])
        for owner, count in sorted(owner_counts.items()):
            writer.writerow([owner, count])
    
    # Generate province_color_map.csv
    with open('province_color_map.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['province_id', 'r', 'g', 'b', 'owner', 'pixel_count'])
        for province in provinces:
            province_id = province['province_id']
            owner_color = province['owner_color']
            owner = province['owner']
            pixel_count = province['pixel_count']
            writer.writerow([province_id, owner_color[0], owner_color[1], owner_color[2], owner, pixel_count])
    
    print("Generated province files:")
    print(f"- province_owners.csv ({len(provinces)} provinces)")
    print(f"- province_ownership_report.csv ({len(owner_counts)} owners)")
    print(f"- province_color_map.csv ({len(provinces)} provinces)")

def main():
    print("Generating provinces from start.png...")
    
    # Generate provinces from image
    provinces = generate_provinces_from_image()
    if not provinces:
        return
    
    # Generate province files
    generate_province_files(provinces)
    
    print("Province generation from image complete!")

if __name__ == "__main__":
    main() 