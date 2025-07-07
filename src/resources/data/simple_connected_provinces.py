#!/usr/bin/env python3
"""
Simple province generation using basic image processing
Finds connected regions of the same color
"""

import json
import csv
from pathlib import Path
from PIL import Image
import numpy as np

def find_connected_regions_simple(image_array, min_size=10):
    """Find connected regions using simple flood fill approach"""
    provinces = []
    province_id = 1
    
    height, width = image_array.shape[:2]
    visited = np.zeros((height, width), dtype=bool)
    
    # Find unique colors (excluding black)
    unique_colors = []
    for y in range(height):
        for x in range(width):
            color = tuple(image_array[y, x])
            if color != (0, 0, 0) and color not in unique_colors:
                unique_colors.append(color)
    
    print(f"Found {len(unique_colors)} unique colors")
    
    for color in unique_colors:
        print(f"Processing color {color}...")
        
        # Find all pixels of this color
        color_pixels = []
        for y in range(height):
            for x in range(width):
                if tuple(image_array[y, x]) == color and not visited[y, x]:
                    # Start flood fill from this pixel
                    region_pixels = flood_fill(image_array, visited, x, y, color)
                    if len(region_pixels) >= min_size:
                        # Calculate centroid
                        centroid_x = int(sum(p[0] for p in region_pixels) / len(region_pixels))
                        centroid_y = int(sum(p[1] for p in region_pixels) / len(region_pixels))
                        
                        province = {
                            "province_id": f"province_{province_id:04d}",
                            "owner_color": list(color),
                            "owner": f"Color_{color[0]}_{color[1]}_{color[2]}",
                            "pixel_count": len(region_pixels),
                            "centroid_x": centroid_x,
                            "centroid_y": centroid_y
                        }
                        provinces.append(province)
                        province_id += 1
    
    return provinces

def flood_fill(image_array, visited, start_x, start_y, target_color):
    """Simple flood fill to find connected region"""
    height, width = image_array.shape[:2]
    stack = [(start_x, start_y)]
    region_pixels = []
    
    while stack:
        x, y = stack.pop()
        
        if (x < 0 or x >= width or y < 0 or y >= height or 
            visited[y, x] or tuple(image_array[y, x]) != target_color):
            continue
        
        visited[y, x] = True
        region_pixels.append((x, y))
        
        # Add neighbors
        stack.extend([(x+1, y), (x-1, y), (x, y+1), (x, y-1)])
    
    return region_pixels

def generate_provinces_from_image():
    """Generate provinces from start.png image"""
    
    # Try different paths for the image
    image_paths = [
        "start.png",
        "../img/start.png",
        "../../img/start.png",
        "C:/Users/taylo/Documents/projects/Roma Game/src/resources/img/start.png"
    ]
    
    start_path = None
    for path in image_paths:
        if Path(path).exists():
            start_path = path
            break
    
    if start_path is None:
        print("Error: start.png not found in any of the expected locations")
        print("Searched in:")
        for path in image_paths:
            print(f"  - {path}")
        return None
    
    print(f"Loading image from: {start_path}")
    
    # Load image
    try:
        image = Image.open(start_path)
        image_array = np.array(image)
        print(f"Image loaded: {image_array.shape}")
    except Exception as e:
        print(f"Error loading image: {e}")
        return None
    
    # Find connected regions
    provinces = find_connected_regions_simple(image_array, min_size=5)
    
    print(f"Generated {len(provinces)} provinces from connected regions")
    
    # Update nations_and_provinces.json
    try:
        with open('nations_and_provinces.json', 'r', encoding='utf-8') as f:
            data = json.load(f)
    except FileNotFoundError:
        data = {"nations": [], "provinces": []}
    
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
    print("Generating provinces from start.png using connected regions...")
    
    # Generate provinces from image
    provinces = generate_provinces_from_image()
    if not provinces:
        return
    
    # Generate province files
    generate_province_files(provinces)
    
    print(f"Province generation complete! Created {len(provinces)} provinces.")

if __name__ == "__main__":
    main() 