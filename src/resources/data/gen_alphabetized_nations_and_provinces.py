import pandas as pd #type: ignore
import json
import csv
import os

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    csv_path = 'owner_color_name.csv'
    
    if not os.path.exists(csv_path):
        print(f"Error: {csv_path} not found")
        return mapping
        
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            color_str = row['owner_color']
            name = row['owner_name']
            # Parse color string like "(130, 175, 255)" to RGB list
            color_str = color_str.strip('()')
            rgb = [int(x.strip()) for x in color_str.split(',')]
            mapping[tuple(rgb)] = name
    return mapping

def main():
    # Load owner color mapping from CSV
    color_mapping = load_owner_color_mapping()
    if not color_mapping:
        print("Error: No owner color mappings found")
        return
    
    print(f"Loaded {len(color_mapping)} owner color mappings from owner_color_name.csv")
    
    # Create nations list from color mapping
    nations_list = []
    for color, name in color_mapping.items():
        nations_list.append({
            'name': name,
            'color': list(color)
        })
    
    # Sort nations alphabetically
    nations_list.sort(key=lambda x: x['name'])
    
    # Create provinces list (if province_owners.csv exists)
    provinces_list = []
    if os.path.exists('province_owners.csv'):
        with open('province_owners.csv', 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                provinces_list.append({
                    'province_id': row['province_id'],
                    'owner': row['owner_name']
                })
    
    # Output JSON
    out = {
        'nations': nations_list,
        'provinces': provinces_list
    }
    
    with open('nations_and_provinces.json', 'w', encoding='utf-8') as f:
        json.dump(out, f, indent=2, ensure_ascii=False)
    
    print(f'Wrote nations_and_provinces.json with {len(nations_list)} nations and {len(provinces_list)} provinces')
    print('All nation names sourced from owner_color_name.csv')

if __name__ == '__main__':
    main() 