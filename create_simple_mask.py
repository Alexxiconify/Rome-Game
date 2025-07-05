#!/usr/bin/env python3
"""
Create a simple province mask quickly.
"""

import cv2
import numpy as np
import random

def create_simple_mask():
    """Create a simple province mask quickly."""
    print("Creating simple province mask...")
    
    # Create a smaller mask for speed
    width, height = 1024, 512
    
    # Create base image with ocean background
    mask = np.zeros((height, width, 3), dtype=np.uint8)
    
    # Add ocean gradient
    for y in range(height):
        for x in range(width):
            ocean_intensity = int(20 + (y / height) * 30)
            mask[y, x] = [ocean_intensity, ocean_intensity + 20, ocean_intensity + 40]
    
    # Generate fewer provinces for speed
    num_provinces = 50
    provinces = []
    
    # Create province centers
    for i in range(num_provinces):
        center_x = random.randint(50, width - 50)
        center_y = random.randint(50, height - 50)
        
        # Generate bright colors to avoid ocean confusion
        r = random.randint(100, 255)
        g = random.randint(100, 255)
        b = random.randint(100, 255)
        
        provinces.append({
            'center': (center_x, center_y),
            'color': np.array([r, g, b]),
            'radius': random.randint(30, 80)
        })
    
    # Fill provinces (simplified approach)
    print("Generating province shapes...")
    for province in provinces:
        cx, cy = province['center']
        radius = province['radius']
        color = province['color']
        
        # Fill circular province
        for y in range(max(0, cy - radius), min(height, cy + radius)):
            for x in range(max(0, cx - radius), min(width, cx + radius)):
                dx = x - cx
                dy = y - cy
                if dx*dx + dy*dy < radius*radius:
                    mask[y, x] = color
    
    # Add some small islands
    print("Adding islands...")
    for _ in range(10):
        cx = random.randint(100, width - 100)
        cy = random.randint(100, height - 100)
        radius = random.randint(10, 25)
        
        r = random.randint(120, 255)
        g = random.randint(120, 255)
        b = random.randint(120, 255)
        
        for y in range(max(0, cy - radius), min(height, cy + radius)):
            for x in range(max(0, cx - radius), min(width, cx + radius)):
                dx = x - cx
                dy = y - cy
                if dx*dx + dy*dy < radius*radius:
                    mask[y, x] = [r, g, b]
    
    # Save the mask
    cv2.imwrite("province_mask.png", mask)
    print("Saved province_mask.png")
    
    # Create preview
    preview = cv2.resize(mask, (400, 200))
    cv2.imwrite("province_mask_preview.png", preview)
    print("Saved province_mask_preview.png")
    
    return mask

if __name__ == "__main__":
    create_simple_mask() 