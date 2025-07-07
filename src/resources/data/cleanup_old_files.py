#!/usr/bin/env python3
"""
Cleanup script to remove old redundant Python files
Run this after confirming the unified_data_manager.py works correctly
"""

import os
from pathlib import Path

def cleanup_old_files():
    """Remove old redundant Python files"""
    
    # List of files to remove (replaced by unified_data_manager.py)
    files_to_remove = [
        "generate_provinces_from_image.py",
        "simple_connected_provinces.py", 
        "simple_province_0069_finder.py",
        "find_province_0069_location.py",
        "simple_unknown_nations.py",
        "extract_unknown_nations_coordinates.py",
        "generate_connected_provinces.py",
        "data_manager.py",
        "generate_province_data.py",
        "list_unique_colors.py",
        "gen_alphabetized_nations_and_provinces.py",
        "gen_province_owners.py",
        "generate_province_data_json.py"
    ]
    
    # List of files to keep (still useful)
    files_to_keep = [
        "unified_data_manager.py",
        "cleanup_old_files.py",
        "create_simple_mask.py",
        "create_new_mask.py", 
        "province_mask_gui.py",
        "README_DATA_MANAGER.md"
    ]
    
    print("Files to be removed:")
    for file in files_to_remove:
        if Path(file).exists():
            print(f"  - {file}")
        else:
            print(f"  - {file} (not found)")
    
    print("\nFiles to keep:")
    for file in files_to_keep:
        if Path(file).exists():
            print(f"  - {file}")
        else:
            print(f"  - {file} (not found)")
    
    # Ask for confirmation
    response = input("\nDo you want to remove the old files? (y/N): ")
    if response.lower() != 'y':
        print("Cleanup cancelled.")
        return
    
    # Remove files
    removed_count = 0
    for file in files_to_remove:
        file_path = Path(file)
        if file_path.exists():
            try:
                file_path.unlink()
                print(f"Removed: {file}")
                removed_count += 1
            except Exception as e:
                print(f"Error removing {file}: {e}")
    
    print(f"\nCleanup complete! Removed {removed_count} files.")
    print("\nThe unified_data_manager.py now provides all the functionality:")
    print("  - Province generation from mask")
    print("  - Data validation")
    print("  - Unknown nation detection")
    print("  - File generation")
    print("\nUsage examples:")
    print("  python unified_data_manager.py --action generate")
    print("  python unified_data_manager.py --action validate")
    print("  python unified_data_manager.py --action unknown")
    print("  python unified_data_manager.py --action files")

if __name__ == "__main__":
    cleanup_old_files() 