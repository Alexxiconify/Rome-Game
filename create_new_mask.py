#!/usr/bin/env python3
"""
Create a new province mask with better color distribution and realistic shapes.
"""

import cv2
import numpy as np
import random
import os

def create_province_mask():
    """Create a new province mask with improved design."""
    print("Creating new province mask...")
    
    # Create a larger, more detailed mask
    width, height = 2048, 1024
    
    # Create base image with ocean background
    mask = np.zeros((height, width, 3), dtype=np.uint8)
    
    # Add ocean gradient (darker blue)
    for y in range(height):
        for x in range(width):
            # Ocean gradient from deep blue to lighter blue
            ocean_intensity = int(15 + (y / height) * 30)
            mask[y, x] = [ocean_intensity, ocean_intensity + 15, ocean_intensity + 35]
    
    # Generate provinces with better color distribution
    num_provinces = 200  # More provinces for better detail
    provinces = []
    
    # Create province centers with better distribution
    for i in range(num_provinces):
        # Generate random center points, avoiding edges
        center_x = random.randint(100, width - 100)
        center_y = random.randint(100, height - 100)
        
        # Generate unique color with good contrast and avoid ocean-like colors
        r = random.randint(80, 255)
        g = random.randint(80, 255)
        b = random.randint(80, 255)
        
        # Ensure minimum distance from ocean colors
        if r < 60 and g < 60 and b < 60:
            r = random.randint(120, 255)
            g = random.randint(120, 255)
            b = random.randint(120, 255)
        
        provinces.append({
            'center': (center_x, center_y),
            'color': np.array([r, g, b]),
            'radius': random.randint(40, 150)
        })
    
    # Fill provinces using distance-based approach
    print("Generating province shapes...")
    for y in range(height):
        for x in range(width):
            min_dist = float('inf')
            closest_province = None
            
            # Find closest province center
            for province in provinces:
                dx = x - province['center'][0]
                dy = y - province['center'][1]
                dist = np.sqrt(dx*dx + dy*dy)
                
                if dist < min_dist:
                    min_dist = dist
                    closest_province = province
            
            # Fill pixel with province color if within radius
            if closest_province and min_dist < closest_province['radius']:
                mask[y, x] = closest_province['color']
    
    # Add some islands and coastal variations
    print("Adding coastal variations...")
    for _ in range(30):
        # Create small islands
        center_x = random.randint(150, width - 150)
        center_y = random.randint(150, height - 150)
        radius = random.randint(15, 60)
        
        r = random.randint(100, 255)
        g = random.randint(100, 255)
        b = random.randint(100, 255)
        
        for y in range(max(0, center_y - radius), min(height, center_y + radius)):
            for x in range(max(0, center_x - radius), min(width, center_x + radius)):
                dx = x - center_x
                dy = y - center_y
                if dx*dx + dy*dy < radius*radius:
                    mask[y, x] = [r, g, b]
    
    # Add some peninsulas and coastal features
    print("Adding coastal features...")
    for _ in range(20):
        # Create peninsulas
        start_x = random.randint(200, width - 200)
        start_y = random.randint(200, height - 200)
        length = random.randint(50, 150)
        angle = random.uniform(0, 2 * np.pi)
        
        r = random.randint(100, 255)
        g = random.randint(100, 255)
        b = random.randint(100, 255)
        
        for i in range(length):
            x = int(start_x + i * np.cos(angle))
            y = int(start_y + i * np.sin(angle))
            width_peninsula = max(5, 20 - i // 10)
            
            for dy in range(-width_peninsula, width_peninsula):
                for dx in range(-width_peninsula, width_peninsula):
                    px = x + dx
                    py = y + dy
                    if 0 <= px < width and 0 <= py < height:
                        if dx*dx + dy*dy < width_peninsula*width_peninsula:
                            mask[py, px] = [r, g, b]
    
    # Save the new mask
    cv2.imwrite("province_mask.png", mask)
    print("Saved new province_mask.png")
    
    # Create a preview image
    preview = cv2.resize(mask, (800, 400))
    cv2.imwrite("province_mask_preview.png", preview)
    print("Saved province_mask_preview.png")
    
    return mask

if __name__ == "__main__":
    create_province_mask() 