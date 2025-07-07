import numpy as np
from PIL import Image
import json
from concurrent.futures import ProcessPoolExecutor, as_completed
import os

# File paths
province_mask_path = 'src/resources/data/province_mask.png'
province_data_path = 'src/resources/province_data.json'
output_path = 'province_owners.csv'
output_owners_path = 'owner_color_name.csv'

def load_province_data():
    with open(province_data_path, 'r') as f:
        province_data = json.load(f)['provinces']
    color_to_pid = {}
    color_to_owner = {}
    color_to_owner_color = {}
    for pid, data in province_data.items():
        color = tuple(data['mask_color'])
        color_to_pid[color] = pid
        color_to_owner[color] = data['nation']
        color_to_owner_color[color] = tuple(data['owner_color'])
    return color_to_pid, color_to_owner, color_to_owner_color

def process_chunk(args):
    chunk, color_to_pid, color_to_owner, color_to_owner_color = args
    flat_chunk = chunk.reshape(-1, 3)
    results = []
    for rgb in flat_chunk:
        color = tuple(rgb)
        pid = color_to_pid.get(color)
        owner = color_to_owner.get(color)
        if pid and owner:
            results.append((pid, owner))
    return results

def main():
    # Load province mask as numpy array
    mask_img = np.array(Image.open(province_mask_path).convert('RGB'))
    h, w, _ = mask_img.shape

    # Load province data
    color_to_pid, color_to_owner, color_to_owner_color = load_province_data()

    # Split image into chunks for parallel processing
    num_workers = os.cpu_count() or 4
    chunk_size = h // num_workers
    chunks = []
    for i in range(num_workers):
        start = i * chunk_size
        end = h if i == num_workers - 1 else (i + 1) * chunk_size
        chunks.append(mask_img[start:end])

    # Prepare args for each worker
    worker_args = [(chunk, color_to_pid, color_to_owner, color_to_owner_color) for chunk in chunks]

    # Process in parallel
    results = []
    with ProcessPoolExecutor(max_workers=num_workers) as executor:
        futures = [executor.submit(process_chunk, arg) for arg in worker_args]
        for future in as_completed(futures):
            results.extend(future.result())

    # Remove duplicates and write output
    unique_results = list(set(results))
    with open(output_path, 'w') as f:
        f.write('province_id,owner_name\n')
        for pid, owner in unique_results:
            f.write(f'{pid},{owner}\n')

    # Output owner color/name pairs
    owner_color_name = set()
    for color, owner in color_to_owner.items():
        owner_color_name.add((color_to_owner_color[color], owner))
    with open(output_owners_path, 'w') as f:
        f.write('owner_color,owner_name\n')
        for color, owner in owner_color_name:
            f.write(f'"{color}",{owner}\n')

    print(f"Wrote {output_path} and {output_owners_path}")

if __name__ == '__main__':
    main()