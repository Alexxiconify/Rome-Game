#!/usr/bin/env python3

import numpy as np
from PIL import Image
import os

def find_province_0069_location():
    """Find the exact pixel coordinates of province_0069"""
    
    # Province 0069 data
    province_id = "province_0069"
    rgb_color = (220, 155, 255)  # Bright purple
    owner = "Unknown_69"
    pixel_count = 18
    
    print(f"Searching for {province_id} ({owner})")
    print(f"RGB Color: {rgb_color}")
    print(f"Expected pixel count: {pixel_count}")
    print("=" * 50)
    
    # Try to find the province mask image
    mask_paths = [
        "province_mask.png",
        "../img/province_mask.png",
        "../../img/province_mask.png",
        "start.png",
        "../img/start.png"
    ]
    
    mask_image = None
    mask_path = None
    
    for path in mask_paths:
        if os.path.exists(path):
            try:
                mask_image = Image.open(path)
                mask_path = path
                print(f"Found mask image: {path}")
                break
            except Exception as e:
                print(f"Could not open {path}: {e}")
    
    if mask_image is None:
        print("ERROR: Could not find province mask image!")
        print("Available files in current directory:")
        for file in os.listdir("."):
            if file.endswith((".png", ".jpg", ".jpeg")):
                print(f"  - {file}")
        return
    
    # Convert to numpy array
    mask_array = np.array(mask_image)
    print(f"Image dimensions: {mask_array.shape}")
    
    # Search for pixels with the target RGB color
    target_r, target_g, target_b = rgb_color
    
    # Find all pixels matching the color
    matches = np.where((mask_array[:, :, 0] == target_r) & 
                      (mask_array[:, :, 1] == target_g) & 
                      (mask_array[:, :, 2] == target_b))
    
    if len(matches[0]) == 0:
        print(f"WARNING: Color {rgb_color} not found in the image!")
        print("This province might not exist on the actual map.")
        return
    
    y_coords = matches[0]
    x_coords = matches[1]
    
    print(f"Found {len(y_coords)} pixels with color {rgb_color}")
    
    if len(y_coords) != pixel_count:
        print(f"WARNING: Expected {pixel_count} pixels, but found {len(y_coords)}")
    
    # Calculate centroid
    centroid_x = int(np.mean(x_coords))
    centroid_y = int(np.mean(y_coords))
    
    print(f"\nProvince {province_id} Location:")
    print(f"  Centroid: ({centroid_x}, {centroid_y})")
    print(f"  Bounds: X({min(x_coords)}-{max(x_coords)}), Y({min(y_coords)}-{max(y_coords)})")
    print(f"  Size: {max(x_coords) - min(x_coords) + 1} x {max(y_coords) - min(y_coords) + 1} pixels")
    
    # Convert to approximate lat/lon (assuming equirectangular projection)
    map_width = mask_array.shape[1]
    map_height = mask_array.shape[0]
    
    lon = (centroid_x / map_width * 360) - 180
    lat = 90 - (centroid_y / map_height * 180)
    
    print(f"  Approximate Coordinates: Lat {lat:.2f}°, Lon {lon:.2f}°")
    
    # Show all pixel coordinates
    print(f"\nAll pixel coordinates for {province_id}:")
    for i, (y, x) in enumerate(zip(y_coords, x_coords)):
        print(f"  Pixel {i+1}: ({x}, {y})")
    
    # Check if this is a very small area
    if len(y_coords) <= 20:
        print(f"\nNOTE: This is a very small province with only {len(y_coords)} pixels.")
        print("It might be barely visible on the map or represent a tiny territory.")

if __name__ == "__main__":
    find_province_0069_location() 