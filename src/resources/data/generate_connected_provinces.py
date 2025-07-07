#!/usr/bin/env python3
"""
Generate provinces from start.png by finding connected regions
Each connected region of the same color becomes a separate province
"""

import cv2
import numpy as np
import json
import csv
from pathlib import Path
from scipy import ndimage

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    csv_path = 'owner_color_name.csv'
    
    if Path(csv_path).exists():
        with open(csv_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                color_str = row['owner_color']
                name = row['owner_name']
                color_str = color_str.strip('()')
                rgb = tuple(int(x.strip()) for x in color_str.split(','))
                mapping[rgb] = name
    return mapping

def find_connected_regions(image, color_mapping, min_size=10):
    """Find connected regions in the image - optimized version"""
    provinces = []
    province_id = 1
    
    # Find unique colors (excluding black) - more efficient
    unique_colors = np.unique(image.reshape(-1, 3), axis=0)
    unique_colors = unique_colors[~np.all(unique_colors == [0, 0, 0], axis=1)]
    
    print(f"Processing {len(unique_colors)} unique colors...")
    
    for i, color in enumerate(unique_colors):
        print(f"Processing color {i+1}/{len(unique_colors)}: {color}")
        
        # Create binary mask for this color
        mask = np.all(image == color, axis=2)
        
        # Skip if no pixels of this color
        if not np.any(mask):
            continue
        
        # Find connected components with 8-connectivity for better region detection
        labeled, num_features = ndimage.label(mask, structure=np.ones((3,3)))
        
        if num_features > 0:
            print(f"  Found {num_features} connected regions")
        
        # Process each connected region
        for region_id in range(1, num_features + 1):
            region_mask = (labeled == region_id)
            region_size = np.sum(region_mask)
            
            # Skip very small regions
            if region_size < min_size:
                continue
            
            # Get region properties more efficiently
            coords = np.where(region_mask)
            centroid_y = int(np.mean(coords[0]))
            centroid_x = int(np.mean(coords[1]))
            
            # Determine owner name from color mapping
            color_tuple = tuple(int(c) for c in color)
            owner_name = color_mapping.get(color_tuple, f"Color_{color_tuple[0]}_{color_tuple[1]}_{color_tuple[2]}")
            
            province = {
                "province_id": f"province_{province_id:04d}",
                "owner_color": [int(c) for c in color],
                "owner": owner_name,
                "pixel_count": int(region_size),
                "centroid_x": centroid_x,
                "centroid_y": centroid_y,
                "region_id": region_id
            }
            provinces.append(province)
            province_id += 1
            
            # Progress indicator for large numbers of provinces
            if province_id % 1000 == 0:
                print(f"  Created {province_id} provinces so far...")
    
    return provinces

def generate_provinces_from_image():
    """Generate provinces from start.png image using connected regions"""
    
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
    start = cv2.imread(start_path)
    if start is None:
        print(f"Error: Could not load {start_path}")
        return None
    
    # Convert BGR to RGB
    start_rgb = cv2.cvtColor(start, cv2.COLOR_BGR2RGB)
    height, width = start_rgb.shape[:2]
    
    print(f"Image loaded: {width}x{height} pixels")
    
    # Find connected regions - increase min_size to filter out tiny regions and speed up processing
    provinces = find_connected_regions(start_rgb, min_size=20)
    
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