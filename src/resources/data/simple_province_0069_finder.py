#!/usr/bin/env python3

import os
import json

def find_province_0069_info():
    """Find information about province_0069"""
    
    print("Province 0069 Information:")
    print("=" * 40)
    
    # Load data from JSON file
    try:
        with open('nations_and_provinces.json', 'r') as f:
            data = json.load(f)
        
        # Find province_0069
        province_0069 = None
        for province in data['provinces']:
            if province['province_id'] == 'province_0069':
                province_0069 = province
                break
        
        if province_0069:
            print(f"Province ID: {province_0069['province_id']}")
            print(f"Owner: {province_0069['owner']}")
            print(f"RGB Color: {province_0069['owner_color']}")
            print(f"Pixel Count: {province_0069['pixel_count']}")
            
            # Convert RGB to hex for easier identification
            r, g, b = province_0069['owner_color']
            hex_color = f"#{r:02x}{g:02x}{b:02x}"
            print(f"Hex Color: {hex_color}")
            
            print(f"\nThis province has only {province_0069['pixel_count']} pixels!")
            print("This is an extremely small territory that might be:")
            print("- A single pixel or tiny dot on the map")
            print("- A data artifact from province generation")
            print("- A territory that was later removed")
            print("- Barely visible on the actual map")
            
        else:
            print("ERROR: province_0069 not found in nations_and_provinces.json")
            
    except FileNotFoundError:
        print("ERROR: nations_and_provinces.json not found")
    except Exception as e:
        print(f"ERROR: {e}")

def check_image_files():
    """Check what image files are available"""
    print(f"\nAvailable image files:")
    print("=" * 40)
    
    current_dir = os.getcwd()
    img_dir = os.path.join(current_dir, "..", "img")
    
    print(f"Current directory: {current_dir}")
    print(f"Image directory: {img_dir}")
    
    # Check current directory
    print(f"\nFiles in current directory:")
    for file in os.listdir("."):
        if file.endswith((".png", ".jpg", ".jpeg")):
            print(f"  - {file}")
    
    # Check img directory
    if os.path.exists(img_dir):
        print(f"\nFiles in img directory:")
        for file in os.listdir(img_dir):
            if file.endswith((".png", ".jpg", ".jpeg")):
                print(f"  - {file}")
    else:
        print(f"\nImage directory does not exist: {img_dir}")

def main():
    find_province_0069_info()
    check_image_files()

if __name__ == "__main__":
    main() 