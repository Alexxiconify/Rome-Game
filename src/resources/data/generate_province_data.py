#!/usr/bin/env python3
"""
Generate province data from nations and populate missing province files
"""

import json
import csv
import random
from pathlib import Path

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    csv_path = 'owner_color_name.csv'
    
    with open(csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            color_str = row['owner_color']
            name = row['owner_name']
            color_str = color_str.strip('()')
            rgb = tuple(int(x.strip()) for x in color_str.split(','))
            mapping[rgb] = name
    return mapping

def generate_province_data():
    """Generate province data from nations"""
    
    # Load nations data
    with open('nations_and_provinces.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    nations = data['nations']
    color_mapping = load_owner_color_mapping()
    
    # Generate provinces for each nation
    provinces = []
    province_id = 1
    
    for nation in nations:
        nation_name = nation['name']
        nation_color = tuple(nation['color'])
        
        # Generate 1-10 provinces per nation (random distribution)
        num_provinces = random.randint(1, 10)
        
        for i in range(num_provinces):
            province = {
                "province_id": f"province_{province_id:04d}",
                "owner_color": list(nation_color),
                "owner": nation_name
            }
            provinces.append(province)
            province_id += 1
    
    # Update JSON with provinces
    data['provinces'] = provinces
    
    # Save updated JSON
    with open('nations_and_provinces.json', 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"Generated {len(provinces)} provinces for {len(nations)} nations")
    return provinces

def generate_province_files(provinces):
    """Generate province CSV files"""
    
    # Generate province_owners.csv
    with open('province_owners.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['province_id', 'owner_name'])
        for province in provinces:
            writer.writerow([province['province_id'], province['owner']])
    
    # Generate province_ownership_report.csv
    owner_counts = {}
    for province in provinces:
        owner = province['owner']
        owner_counts[owner] = owner_counts.get(owner, 0) + 1
    
    with open('province_ownership_report.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['owner_name', 'province_count'])
        for owner, count in sorted(owner_counts.items()):
            writer.writerow([owner, count])
    
    # Generate province_color_map.csv
    with open('province_color_map.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['province_id', 'r', 'g', 'b', 'owner'])
        for province in provinces:
            province_id = province['province_id']
            owner_color = province['owner_color']
            owner = province['owner']
            writer.writerow([province_id, owner_color[0], owner_color[1], owner_color[2], owner])
    
    print("Generated province files:")
    print(f"- province_owners.csv ({len(provinces)} provinces)")
    print(f"- province_ownership_report.csv ({len(owner_counts)} owners)")
    print(f"- province_color_map.csv ({len(provinces)} provinces)")

def main():
    print("Generating province data from nations...")
    
    # Generate province data
    provinces = generate_province_data()
    
    # Generate province files
    generate_province_files(provinces)
    
    print("Province data generation complete!")

if __name__ == "__main__":
    main() 