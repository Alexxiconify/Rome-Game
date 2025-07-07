#!/usr/bin/env python3

import csv
import json

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

def main():
    print("Analyzing unknown nations from province data...")
    
    # Load province data
    provinces = load_province_color_map()
    unknown_nations = find_unknown_nations(provinces)
    
    print(f"\nFound {len(unknown_nations)} unknown nations:")
    print("=" * 50)
    
    for province_id, data in unknown_nations.items():
        rgb = data['rgb']
        owner = data['owner']
        pixel_count = data['pixel_count']
        
        print(f"\n{owner} (Province {province_id}):")
        print(f"  RGB Color: {rgb}")
        print(f"  Pixel Count: {pixel_count}")
        
        # Since we can't access the actual image, we'll provide the RGB values
        # which can be used to locate these provinces on the map
        print(f"  Note: This color {rgb} represents the province location")
        print(f"  To find coordinates, search for RGB({rgb[0]}, {rgb[1]}, {rgb[2]}) in province_mask.png")
    
    print(f"\n" + "=" * 50)
    print("Summary:")
    print(f"Total unknown nations: {len(unknown_nations)}")
    
    # List all unique unknown nation names
    unique_names = set(data['owner'] for data in unknown_nations.values())
    print(f"Unique unknown nation names: {sorted(unique_names)}")

if __name__ == "__main__":
    main() 