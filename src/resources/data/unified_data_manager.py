#!/usr/bin/env python3
"""
Unified Data Manager for Roma Game
Combines all data processing, generation, and validation functions into one optimized system.
Replaces multiple scattered Python files with a single comprehensive solution.
"""

# flake8: noqa

import json
import csv
from pathlib import Path
import argparse
import sys
import time
from multiprocessing import Pool, cpu_count
import cv2  # type: ignore
import numpy as np  # type: ignore
from scipy import ndimage  # type: ignore

try:
    from PIL import Image # type: ignore
except ImportError:
    print("Warning: PIL not available. Some advanced features will be disabled.")

class UnifiedDataManager:
    def __init__(self, base_path=None):
        if base_path is None:
            base_path = Path.cwd()
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
    
    def find_image_path(self, filename, search_paths=None):
        """Find image file in various possible locations"""
        if search_paths is None:
            search_paths = [
                filename,
                f"../img/{filename}",
                f"../../img/{filename}",
                f"C:/Users/taylo/Documents/projects/Roma Game/src/resources/img/{filename}",
                f"/mnt/c/Users/taylo/Documents/projects/Roma Game/src/resources/img/{filename}"
            ]
        
        for path in search_paths:
            if Path(path).exists():
                return path
        return None
    
    def generate_provinces_from_mask(self, min_size=20):
        """Generate provinces from province_mask.png using connected regions"""
        mask_path = self.find_image_path("province_mask.png")
        
        if mask_path is None:
            print("Error: province_mask.png not found")
            return None
        
        print(f"Loading province mask from: {mask_path}")
        
        if cv2.cv2_available:
            return self._generate_provinces_cv2(mask_path, min_size)
        else:
            return self._generate_provinces_pil(mask_path, min_size)
    
    def _generate_provinces_cv2(self, mask_path, min_size):
        """Generate provinces using OpenCV (faster)"""
        mask_image = cv2.imread(str(mask_path))
        if mask_image is None:
            print(f"Error: Could not load {mask_path}")
            return None
        
        mask_rgb = cv2.cvtColor(mask_image, cv2.COLOR_BGR2RGB)
        height, width = mask_rgb.shape[:2]
        
        print(f"Province mask loaded: {width}x{height} pixels")
        
        color_mapping = self.load_owner_color_mapping()
        print(f"Loaded {len(color_mapping)} owner color mappings")
        
        provinces = self._find_connected_regions_cv2(mask_rgb, color_mapping, min_size)
        
        print(f"Generated {len(provinces)} provinces from connected regions")
        return provinces
    
    def _generate_provinces_pil(self, mask_path, min_size):
        """Generate provinces using PIL (fallback)"""
        try:
            image = Image.open(mask_path)
            image_array = np.array(image)
            print(f"Province mask loaded: {image_array.shape}")
        except Exception as e:
            print(f"Error loading image: {e}")
            return None
        
        color_mapping = self.load_owner_color_mapping()
        print(f"Loaded {len(color_mapping)} owner color mappings")
        
        provinces = self._find_connected_regions_pil(image_array, color_mapping, min_size)
        
        print(f"Generated {len(provinces)} provinces from connected regions")
        return provinces
    
    def _find_connected_regions_cv2(self, image, color_mapping, min_size=10):
        """Find connected regions using OpenCV and scipy"""
        provinces = []
        province_id = 1
        
        # Find unique colors (excluding black)
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
            
            # Find connected components with 8-connectivity
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
                
                # Get region properties
                coords = np.where(region_mask)
                centroid_y = int(np.mean(coords[0]))
                centroid_x = int(np.mean(coords[1]))
                
                # Determine owner name from color mapping
                color_tuple = tuple(int(c) for c in color)
                owner_name = color_mapping.get(color_tuple, f"Color_{color_tuple[0]}_{color_tuple[1]}_{color_tuple[2]}")
                
                province = {
                    "province_id": f"province_{province_id:04d}",
                    "mask_color": [int(c) for c in color],
                    "owner_color": [int(c) for c in color],
                    "owner_name": owner_name,
                    "owner": owner_name,
                    "pixel_count": int(region_size),
                    "centroid_x": centroid_x,
                    "centroid_y": centroid_y,
                    "region_id": region_id
                }
                provinces.append(province)
                
                # Single-line debug output
                print(f"DEBUG: Province[{province_id-1}] id={province['province_id']} owner={owner_name} color={[int(c) for c in color]} px={region_size} cx={centroid_x} cy={centroid_y}")
                
                province_id += 1
                
                # Progress indicator
                if province_id % 1000 == 0:
                    print(f"  Created {province_id} provinces so far...")
        
        return provinces
    
    def _find_connected_regions_pil(self, image_array, color_mapping, min_size=10):
        """Find connected regions using PIL and basic flood fill"""
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
            for y in range(height):
                for x in range(width):
                    if tuple(image_array[y, x]) == color and not visited[y, x]:
                        # Start flood fill from this pixel
                        region_pixels = self._flood_fill(image_array, visited, x, y, color)
                        if len(region_pixels) >= min_size:
                            # Calculate centroid
                            centroid_x = int(sum(p[0] for p in region_pixels) / len(region_pixels))
                            centroid_y = int(sum(p[1] for p in region_pixels) / len(region_pixels))
                            
                            # Determine owner name from color mapping
                            color_tuple = tuple(int(c) for c in color)
                            owner_name = color_mapping.get(color_tuple, f"Color_{color_tuple[0]}_{color_tuple[1]}_{color_tuple[2]}")
                            
                            province = {
                                "province_id": f"province_{province_id:04d}",
                                "mask_color": list(color),
                                "owner_color": list(color),
                                "owner_name": owner_name,
                                "owner": owner_name,
                                "pixel_count": len(region_pixels),
                                "centroid_x": centroid_x,
                                "centroid_y": centroid_y
                            }
                            provinces.append(province)
                            
                            # Single-line debug output
                            print(f"DEBUG: Province[{province_id-1}] id={province['province_id']} owner={owner_name} color={list(color)} px={len(region_pixels)} cx={centroid_x} cy={centroid_y}")
                            
                            province_id += 1
        
        return provinces
    
    def _flood_fill(self, image_array, visited, start_x, start_y, target_color):
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
    
    def generate_province_files(self, provinces):
        """Generate all province CSV files"""
        
        # Generate province_owners.csv
        with open(self.base_path / "province_owners.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['province_id', 'owner_name'])
            for province in provinces:
                writer.writerow([province['province_id'], province['owner']])
        
        # Generate province_ownership_report.csv
        owner_counts = {}
        for province in provinces:
            owner = province['owner']
            owner_counts[owner] = owner_counts.get(owner, 0) + 1
        
        with open(self.base_path / "province_ownership_report.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['owner_name', 'province_count'])
            for owner, count in sorted(owner_counts.items()):
                writer.writerow([owner, count])
        
        # Generate province_color_map.csv
        with open(self.base_path / "province_color_map.csv", 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['province_id', 'r', 'g', 'b', 'owner', 'pixel_count'])
            for province in provinces:
                province_id = province['province_id']
                mask_color = province['mask_color']
                owner = province['owner']
                pixel_count = province['pixel_count']
                writer.writerow([province_id, mask_color[0], mask_color[1], mask_color[2], owner, pixel_count])
        
        print("Generated province files:", (f"- province_owners.csv ({len(provinces)} provinces)", f"- province_ownership_report.csv ({len(owner_counts)} owners)"), f"- province_color_map.csv ({len(provinces)} provinces)")
    
    def update_json_data(self, provinces):
        """Update nations_and_provinces.json with new province data"""
        try:
            with open(self.json_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
        except FileNotFoundError:
            data = {"nations": [], "provinces": []}
        
        data['provinces'] = provinces
        
        with open(self.json_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        print(f"Updated {self.json_path} with {len(provinces)} provinces")
    
    def validate_data(self):
        """Validate all data files for consistency"""
        print("Validating data files...")
        
        # Check if required files exist
        required_files = [
            "nations_and_provinces.json",
            "owner_color_name.csv",
            "province_owners.csv",
            "province_color_map.csv"
        ]
        
        missing_files = []
        for file in required_files:
            if not (self.base_path / file).exists():
                missing_files.append(file)
        
        if missing_files:
            print(f"Missing required files: {missing_files}")
            return False
        
        # Load and validate JSON data
        data = self.load_json_data()
        if not data:
            return False
        
        print(f"JSON data: {len(data.get('nations', []))} nations, {len(data.get('provinces', []))} provinces")
        
        # Validate CSV files
        try:
            with open(self.base_path / "province_owners.csv", 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                province_owners = list(reader)
            print(f"Province owners CSV: {len(province_owners)} entries")
        except Exception as e:
            print(f"Error reading province_owners.csv: {e}")
            return False
        
        print("Data validation complete!")
        return True
    
    def find_unknown_nations(self):
        """Find and list all unknown nations in the data"""
        data = self.load_json_data()
        if not data:
            return []
        
        unknown_nations = []
        for province in data.get('provinces', []):
            owner = province.get('owner', '')
            if owner.startswith('Unknown_') or owner.startswith('Color_'):
                unknown_nations.append({
                    'province_id': province.get('province_id', ''),
                    'owner': owner,
                    'pixel_count': province.get('pixel_count', 0)
                })
        
        return unknown_nations

def main():
    parser = argparse.ArgumentParser(description="Unified Data Manager for Roma Game")
    parser.add_argument('--action', choices=['generate', 'validate', 'unknown', 'files'], 
                       default='generate', help='Action to perform')
    parser.add_argument('--min-size', type=int, default=20, 
                       help='Minimum province size in pixels')
    parser.add_argument('--base-path', type=str, 
                       help='Base path for data files')
    
    args = parser.parse_args()
    
    manager = UnifiedDataManager(args.base_path)
    
    if args.action == 'generate':
        print("Generating provinces from province_mask.png...")
        provinces = manager.generate_provinces_from_mask(min_size=args.min_size)
        if provinces:
            manager.update_json_data(provinces)
            manager.generate_province_files(provinces)
            print(f"Province generation complete! Created {len(provinces)} provinces.")
    
    elif args.action == 'validate':
        if manager.validate_data():
            print("Data validation passed!")
        else:
            print("Data validation failed!")
            sys.exit(1)
    
    elif args.action == 'unknown':
        unknown_nations = manager.find_unknown_nations()
        if unknown_nations:
            print(f"Found {len(unknown_nations)} unknown nations:")
            for nation in unknown_nations:
                print(f"  {nation['province_id']}: {nation['owner']} ({nation['pixel_count']} pixels)")
        else:
            print("No unknown nations found!")
    
    elif args.action == 'files':
        provinces = manager.load_json_data()
        if provinces and 'provinces' in provinces:
            manager.generate_province_files(provinces['provinces'])
            print("Generated all province files from existing JSON data.")

if __name__ == "__main__":
    main() 