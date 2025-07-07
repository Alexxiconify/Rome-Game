#!/usr/bin/env python3
"""
Comprehensive Data Manager for Roma Game
Combines all data processing, generation, and validation functions into one optimized system.
"""

import json
import csv
import numpy as np
from PIL import Image
from pathlib import Path
import argparse
import sys

class DataManager:
    def __init__(self, base_path="/mnt/c/Users/taylo/Documents/projects/Roma Game/src/resources/data"):
        self.base_path = Path(base_path)
        self.json_path = self.base_path / "nations_and_provinces.json"
        self.csv_path = self.base_path / "owner_color_name.csv"
        
    def load_owner_color_mapping(self):
        """Load owner color to name mapping from CSV"""
        mapping = {}
        if not self.csv_path.exists():
            print(f"Warning: {self.csv_path} not found")
            return mapping
            
        with open(self.csv_path, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                color_str = row['owner_color']
                name = row['owner_name']
                color_str = color_str.strip('()')
                rgb = tuple(int(x.strip()) for x in color_str.split(','))
                mapping[rgb] = name
        return mapping
    
    def load_json_data(self):
        """Load nations_and_provinces.json"""
        if not self.json_path.exists():
            print(f"Error: {self.json_path} not found")
            return None
            
        with open(self.json_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    
    def save_json_data(self, data):
        """Save data to nations_and_provinces.json"""
        with open(self.json_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
    
    def generate_province_owners_from_mask(self, mask_path, output_path=None):
        """Generate province owners from mask image"""
        if output_path is None:
            output_path = self.base_path / "province_owners.csv"
            
        mask_img = np.array(Image.open(mask_path).convert('RGB'))
        color_mapping = self.load_owner_color_mapping()
        
        # Find unique colors and map to owners
        unique_colors = set()
        for y in range(mask_img.shape[0]):
            for x in range(mask_img.shape[1]):
                color = tuple(mask_img[y, x])
                if color != (0, 0, 0):  # Skip black (ocean)
                    unique_colors.add(color)
        
        # Generate province data
        provinces = []
        province_id = 1
        for color in sorted(unique_colors):
            owner_name = color_mapping.get(color, f"Unknown_{province_id}")
            provinces.append({
                "province_id": f"province_{province_id:04d}",
                "owner_color": list(color),
                "owner": owner_name
            })
            province_id += 1
        
        # Save to CSV
        with open(output_path, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['province_id', 'owner_name'])
            for province in provinces:
                writer.writerow([province['province_id'], province['owner']])
        
        print(f"Generated {len(provinces)} provinces in {output_path}")
        return provinces
    
    def update_nations_from_csv(self):
        """Update nations_and_provinces.json with owner names from CSV"""
        data = self.load_json_data()
        if not data:
            return
            
        color_mapping = self.load_owner_color_mapping()
        missing_colors = set()
        
        # Update nations
        for nation in data['nations']:
            color = tuple(nation['color'])
            if color in color_mapping:
                nation['name'] = color_mapping[color]
            else:
                missing_colors.add(color)
        
        # Update provinces
        for province in data['provinces']:
            if 'owner_color' in province:
                color = tuple(province['owner_color'])
                if color in color_mapping:
                    province['owner'] = color_mapping[color]
                else:
                    province['owner'] = f"MISSING_IN_CSV_[{color[0]},{color[1]},{color[2]}]"
                    missing_colors.add(color)
            else:
                province['owner'] = "MISSING_NO_COLOR"
        
        self.save_json_data(data)
        
        print(f"Updated {len(data['nations'])} nations and {len(data['provinces'])} provinces")
        if missing_colors:
            print(f"Warning: {len(missing_colors)} colors missing from CSV")
        else:
            print("All colors matched CSV successfully!")
    
    def regenerate_all_files(self):
        """Regenerate all data files from nations_and_provinces.json"""
        data = self.load_json_data()
        if not data:
            return
            
        color_mapping = self.load_owner_color_mapping()
        missing_colors = set()
        
        # Helper function to get owner name
        def get_owner_name(color):
            color_tuple = tuple(color)
            if color_tuple in color_mapping:
                return color_mapping[color_tuple]
            else:
                missing_name = f"MISSING_IN_CSV_[{color_tuple[0]},{color_tuple[1]},{color_tuple[2]}]"
                missing_colors.add(color_tuple)
                return missing_name
        
        # Regenerate province_owners.csv
        with open(self.base_path / "province_owners.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['province_id', 'owner_name'])
            for province in data['provinces']:
                province_id = province['province_id']
                if 'owner_color' in province:
                    owner = get_owner_name(province['owner_color'])
                else:
                    owner = province.get('owner', 'MISSING_NO_COLOR')
                writer.writerow([province_id, owner])
        
        # Regenerate province_ownership_report.csv
        owner_counts = {}
        for province in data['provinces']:
            if 'owner_color' in province:
                owner = get_owner_name(province['owner_color'])
            else:
                owner = province.get('owner', 'MISSING_NO_COLOR')
            owner_counts[owner] = owner_counts.get(owner, 0) + 1
        
        with open(self.base_path / "province_ownership_report.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['owner_name', 'province_count'])
            for owner, count in sorted(owner_counts.items()):
                writer.writerow([owner, count])
        
        # Regenerate nation.txt
        with open(self.base_path / "nation.txt", 'w', encoding='utf-8') as f:
            for nation in data['nations']:
                color = nation['color']
                name = get_owner_name(color)
                f.write(f"{name},{color[0]},{color[1]},{color[2]}\n")
        
        # Regenerate province_color_map.csv
        with open(self.base_path / "province_color_map.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['province_id', 'r', 'g', 'b', 'owner'])
            for province in data['provinces']:
                province_id = province['province_id']
                owner_color = province.get('owner_color', [0, 0, 0])
                if 'owner_color' in province:
                    owner = get_owner_name(owner_color)
                else:
                    owner = province.get('owner', 'MISSING_NO_COLOR')
                writer.writerow([province_id, owner_color[0], owner_color[1], owner_color[2], owner])
        
        print("Regenerated all data files:")
        print("- province_owners.csv")
        print("- province_ownership_report.csv") 
        print("- nation.txt")
        print("- province_color_map.csv")
        
        if missing_colors:
            print(f"\nWarning: {len(missing_colors)} colors missing from CSV")
        else:
            print("All colors matched CSV successfully!")
    
    def validate_data(self):
        """Validate data consistency"""
        data = self.load_json_data()
        if not data:
            return
            
        color_mapping = self.load_owner_color_mapping()
        
        # Check for missing colors
        missing_colors = set()
        for nation in data['nations']:
            color = tuple(nation['color'])
            if color not in color_mapping:
                missing_colors.add(color)
        
        for province in data['provinces']:
            if 'owner_color' in province:
                color = tuple(province['owner_color'])
                if color not in color_mapping:
                    missing_colors.add(color)
        
        print(f"Data validation results:")
        print(f"- Nations: {len(data['nations'])}")
        print(f"- Provinces: {len(data['provinces'])}")
        print(f"- CSV mappings: {len(color_mapping)}")
        print(f"- Missing colors: {len(missing_colors)}")
        
        if missing_colors:
            print("\nMissing colors:")
            for color in sorted(missing_colors):
                print(f"  {color}")
    
    def create_simple_mask(self, output_path=None):
        """Create a simple province mask"""
        if output_path is None:
            output_path = self.base_path / "province_mask.png"
        
        # Create a simple test mask
        width, height = 800, 600
        mask = np.zeros((height, width, 3), dtype=np.uint8)
        
        # Add some colored regions
        mask[100:200, 100:300] = [255, 0, 0]    # Red region
        mask[250:350, 150:400] = [0, 255, 0]    # Green region
        mask[400:500, 200:500] = [0, 0, 255]    # Blue region
        
        # Save mask
        Image.fromarray(mask).save(output_path)
        print(f"Created simple mask: {output_path}")

def main():
    parser = argparse.ArgumentParser(description="Roma Game Data Manager")
    parser.add_argument("action", choices=[
        "generate_owners", "update_nations", "regenerate_all", 
        "validate", "create_mask", "help"
    ], help="Action to perform")
    parser.add_argument("--mask", help="Path to province mask image")
    parser.add_argument("--output", help="Output file path")
    
    args = parser.parse_args()
    
    dm = DataManager()
    
    if args.action == "generate_owners":
        if not args.mask:
            print("Error: --mask required for generate_owners")
            sys.exit(1)
        dm.generate_province_owners_from_mask(args.mask, args.output)
    
    elif args.action == "update_nations":
        dm.update_nations_from_csv()
    
    elif args.action == "regenerate_all":
        dm.regenerate_all_files()
    
    elif args.action == "validate":
        dm.validate_data()
    
    elif args.action == "create_mask":
        dm.create_simple_mask(args.output)
    
    elif args.action == "help":
        print("""
Roma Game Data Manager

Actions:
  generate_owners --mask <path>  Generate province owners from mask image
  update_nations                  Update nations from owner_color_name.csv
  regenerate_all                 Regenerate all data files
  validate                       Validate data consistency
  create_mask                    Create a simple test mask
  help                           Show this help

Examples:
  python3 data_manager.py generate_owners --mask province_mask.png
  python3 data_manager.py update_nations
  python3 data_manager.py regenerate_all
  python3 data_manager.py validate
        """)

if __name__ == "__main__":
    main() 