import numpy as np
from PIL import Image
import json
import csv
from concurrent.futures import ProcessPoolExecutor, as_completed
import os

# File paths
province_mask_path = 'src/resources/data/province_mask.png'
owner_color_csv_path = 'owner_color_name.csv'
output_path = 'province_owners.csv'

def load_owner_color_mapping():
    """Load owner color to name mapping from CSV"""
    mapping = {}
    if not os.path.exists(owner_color_csv_path):
        print(f"Warning: {owner_color_csv_path} not found")
        return mapping
        
    with open(owner_color_csv_path, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            color_str = row['owner_color']
            name = row['owner_name']
            # Parse color string like "(130, 175, 255)" to RGB tuple
            color_str = color_str.strip('()')
            rgb = tuple(int(x.strip()) for x in color_str.split(','))
            mapping[rgb] = name
    return mapping

def process_chunk(args):
    chunk, color_to_owner = args
    flat_chunk = chunk.reshape(-1, 3)
    results = []
    for rgb in flat_chunk:
        color = tuple(rgb)
        owner = color_to_owner.get(color)
        if owner:
            # Generate province ID based on color
            province_id = f"province_{hash(color) % 10000:04d}"
            results.append((province_id, owner))
    return results

def main():
    # Load province mask as numpy array
    if not os.path.exists(province_mask_path):
        print(f"Error: {province_mask_path} not found")
        return
        
    mask_img = np.array(Image.open(province_mask_path).convert('RGB'))
    h, w, _ = mask_img.shape

    # Load owner color mapping from CSV
    color_to_owner = load_owner_color_mapping()
    if not color_to_owner:
        print("Error: No owner color mappings found")
        return

    print(f"Loaded {len(color_to_owner)} owner color mappings")

    # Split image into chunks for parallel processing
    num_workers = os.cpu_count() or 4
    chunk_size = h // num_workers
    chunks = []
    for i in range(num_workers):
        start = i * chunk_size
        end = h if i == num_workers - 1 else (i + 1) * chunk_size
        chunks.append(mask_img[start:end])

    # Prepare args for each worker
    worker_args = [(chunk, color_to_owner) for chunk in chunks]

    # Process in parallel
    results = []
    with ProcessPoolExecutor(max_workers=num_workers) as executor:
        futures = [executor.submit(process_chunk, arg) for arg in worker_args]
        for future in as_completed(futures):
            results.extend(future.result())

    # Remove duplicates and write output
    unique_results = list(set(results))
    with open(output_path, 'w', newline='', encoding='utf-8') as f:
        f.write('province_id,owner_name\n')
        for pid, owner in unique_results:
            f.write(f'{pid},{owner}\n')

    print(f"Generated {len(unique_results)} province owners in {output_path}")
    print(f"Using owner names from {owner_color_csv_path}")

if __name__ == '__main__':
    main()