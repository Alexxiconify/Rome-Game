#!/usr/bin/env python3

import json
import csv
from PIL import Image
import numpy as np

def load_province_data():
    """Load province data from the JSON file"""
    with open('nations_and_provinces.json', 'r') as f:
        data = json.load(f)
    return data

def load_province_color_map():
    """Load province color mapping from CSV"""
    provinces = {}
    with open('province_color_map.csv', 'r') as f:
        reader = csv.DictReader(f)
        for row in reader:
            province_id = row['province_id']
            r, g, b = int(row['r']), int(row['g']), int(row['b'])
            owner = row['owner']
            provinces[province_id] = {
                'rgb': (r, g, b),
                'owner': owner,
                'pixel_count': int(row['pixel_count'])
            }
    return provinces

def find_unknown_nations(provinces):
    """Find all provinces owned by unknown nations"""
    unknown_nations = {}
    for province_id, data in provinces.items():
        if data['owner'].startswith('Unknown'):
            unknown_nations[province_id] = data
    return unknown_nations

def analyze_province_mask():
    """Analyze the province mask image to find coordinates for unknown nations"""
    try:
        # Try to load the province mask image
        mask_img = Image.open('province_mask.png')
        mask_array = np.array(mask_img)
        
        print(f"Province mask image loaded: {mask_array.shape}")
        
        # Load province data
        provinces = load_province_color_map()
        unknown_nations = find_unknown_nations(provinces)
        
        print(f"\nFound {len(unknown_nations)} unknown nations:")
        
        for province_id, data in unknown_nations.items():
            rgb = data['rgb']
            owner = data['owner']
            pixel_count = data['pixel_count']
            
            print(f"\n{owner} (Province {province_id}):")
            print(f"  RGB Color: {rgb}")
            print(f"  Pixel Count: {pixel_count}")
            
            # Find all pixels with this RGB color
            matches = np.where((mask_array[:, :, 0] == rgb[0]) & 
                             (mask_array[:, :, 1] == rgb[1]) & 
                             (mask_array[:, :, 2] == rgb[2]))
            
            if len(matches[0]) > 0:
                # Calculate centroid
                y_coords = matches[0]
                x_coords = matches[1]
                
                centroid_x = int(np.mean(x_coords))
                centroid_y = int(np.mean(y_coords))
                
                print(f"  Centroid: ({centroid_x}, {centroid_y})")
                print(f"  Bounds: X({min(x_coords)}-{max(x_coords)}), Y({min(y_coords)}-{max(y_coords)})")
                
                # Convert to approximate lat/lon (assuming equirectangular projection)
                # Map width is typically 360 degrees longitude, height is 180 degrees latitude
                map_width = mask_array.shape[1]
                map_height = mask_array.shape[0]
                
                lon = (centroid_x / map_width * 360) - 180
                lat = 90 - (centroid_y / map_height * 180)
                
                print(f"  Approximate Coordinates: Lat {lat:.2f}°, Lon {lon:.2f}°")
            else:
                print(f"  WARNING: Color {rgb} not found in province mask image!")
                
    except FileNotFoundError:
        print("Province mask image not found. Trying alternative approach...")
        
        # Fallback: just show the unknown nations from the data
        provinces = load_province_color_map()
        unknown_nations = find_unknown_nations(provinces)
        
        print(f"\nUnknown nations found in data:")
        for province_id, data in unknown_nations.items():
            rgb = data['rgb']
            owner = data['owner']
            pixel_count = data['pixel_count']
            
            print(f"\n{owner} (Province {province_id}):")
            print(f"  RGB Color: {rgb}")
            print(f"  Pixel Count: {pixel_count}")
            print(f"  Note: Cannot determine coordinates without province mask image")

def main():
    print("Extracting coordinates for unknown nations...")
    analyze_province_mask()

if __name__ == "__main__":
    main() 