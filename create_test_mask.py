#!/usr/bin/env python3
"""
Create a test province mask for demonstration.
This generates a simple mask with a few colored provinces.
"""

import cv2
import numpy as np
import os

def create_test_mask():
    """Create a simple test province mask."""
    
    # Create resources directory if it doesn't exist
    os.makedirs("resources", exist_ok=True)
    
    # Create a 800x600 test mask
    width, height = 800, 600
    
    # Start with black background
    mask = np.zeros((height, width, 3), dtype=np.uint8)
    
    # Add some colored provinces
    # Province 1 - Red
    cv2.rectangle(mask, (100, 100), (300, 200), (255, 0, 0), -1)
    
    # Province 2 - Green  
    cv2.rectangle(mask, (350, 100), (550, 200), (0, 255, 0), -1)
    
    # Province 3 - Blue
    cv2.rectangle(mask, (100, 250), (300, 350), (0, 0, 255), -1)
    
    # Province 4 - Yellow
    cv2.rectangle(mask, (350, 250), (550, 350), (255, 255, 0), -1)
    
    # Province 5 - Magenta
    cv2.rectangle(mask, (100, 400), (300, 500), (255, 0, 255), -1)
    
    # Province 6 - Cyan
    cv2.rectangle(mask, (350, 400), (550, 500), (0, 255, 255), -1)
    
    # Save the mask
    mask_path = "resources/province_mask.png"
    cv2.imwrite(mask_path, mask)
    
    print(f"Created test province mask: {mask_path}")
    print("This mask contains 6 colored provinces for testing.")
    print("You can replace this with your actual Q-BAM province mask.")

if __name__ == "__main__":
    create_test_mask() 