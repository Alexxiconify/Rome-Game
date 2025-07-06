try:
    import numpy as np
except ImportError:
    print("Error: numpy is required. Install with: pip install numpy")
    exit(1)

try:
    from PIL import Image
except ImportError:
    print("Error: PIL (Pillow) is required. Install with: pip install Pillow")
    exit(1)

import json
import time
from multiprocessing import Pool, cpu_count

# Helper to convert numpy types to native Python types

def convert_np(obj):
    import numpy as np
    if isinstance(obj, dict):
        return {k: convert_np(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [convert_np(i) for i in obj]
    elif isinstance(obj, tuple):
        return tuple(convert_np(i) for i in obj)
    elif isinstance(obj, np.generic):
        return obj.item()
    else:
        return obj

# File paths
start_img_path = "start.png"
mask_img_path = "province_mask.png"
output_json_path = "province_data.json"

def fast_province_data_generation():
    """Fast version using NumPy vectorized operations"""
    print("Loading images...")
    start_time = time.time()
    
    # Load images
    start_img = np.array(Image.open(start_img_path).convert("RGBA"))
    mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))
    
    height, width, _ = mask_img.shape
    print(f"Image loaded: {width}x{height} pixels")
    
    # Step 1: Find unique mask colors using NumPy's unique
    print("Finding unique mask colors...")
    mask_rgb = mask_img[:, :, :3]
    
    # Reshape to 2D array for unique operation
    pixels_2d = mask_rgb.reshape(-1, 3)
    unique_colors, _ = np.unique(pixels_2d, axis=0, return_index=True)
    
    # Convert to tuples for dictionary keys
    unique_colors_tuples = [tuple(color) for color in unique_colors]
    
    # Create mask_to_province mapping
    mask_to_province = {}
    for i, mask_color in enumerate(unique_colors_tuples):
        province_id = f"province_{i+1:04d}"
        mask_to_province[mask_color] = province_id
    
    print(f"Found {len(mask_to_province)} unique provinces")
    
    # Step 2: Find owner colors using vectorized operations
    print("Finding owner colors from start.png...")
    province_data = {}
    nation_colors = {}
    
    # Process all provinces at once using vectorized operations
    processed_count = 0
    total_provinces = len(mask_to_province)
    
    for mask_color in unique_colors_tuples:
        # Find all pixels with this mask color using NumPy
        color_matches = np.all(mask_rgb == mask_color, axis=2)
        
        if np.any(color_matches):
            # Get the first matching pixel
            y_coords, x_coords = np.where(color_matches)
            y, x = y_coords[0], x_coords[0]
            owner_color = tuple(start_img[y, x][:3])
            
            province_id = mask_to_province[mask_color]
            province_data[province_id] = {
                "mask_color": mask_color,
                "owner_color": owner_color
            }
            
            # Add to nation_colors
            if owner_color not in nation_colors:
                nation_colors[owner_color] = f"nation_{len(nation_colors)+1:04d}"
        
        processed_count += 1
        if processed_count % 100 == 0 or processed_count == total_provinces:
            print(f"Processed {processed_count}/{total_provinces} provinces...")
    
    print(f"Successfully processed {len(province_data)} provinces")
    
    # Step 3: Build nation list
    print("Building nation list...")
    nations = []
    for color, name in nation_colors.items():
        nations.append({
            "name": name,
            "color": color
        })
    
    # Step 4: Output JSON
    print("Writing output...")
    output = {
        "provinces": province_data,
        "nations": nations
    }
    
    with open(output_json_path, "w") as f:
        json.dump(convert_np(output), f, indent=2)
    
    end_time = time.time()
    print(f"Output written to {output_json_path}")
    print(f"Total processing time: {end_time - start_time:.2f} seconds")
    print(f"Processed {len(province_data)} provinces and {len(nations)} nations")
    
    # Debug info
    print(f"\nDebug Info:")
    print(f"- Total unique mask colors found: {len(mask_to_province)}")
    print(f"- Provinces with owner colors: {len(province_data)}")
    print(f"- Unique nation colors: {len(nations)}")
    
    if len(province_data) == 0:
        print("WARNING: No provinces were processed! Check if images are the same size.")
        print(f"Start image shape: {start_img.shape}")
        print(f"Mask image shape: {mask_img.shape}")

def ultra_fast_province_data_generation():
    """Ultra-fast version using advanced NumPy operations"""
    print("Loading images...")
    start_time = time.time()
    
    # Load images
    start_img = np.array(Image.open(start_img_path).convert("RGBA"))
    mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))
    
    height, width, _ = mask_img.shape
    print(f"Image loaded: {width}x{height} pixels")
    
    # Step 1: Find unique mask colors using NumPy's unique
    print("Finding unique mask colors...")
    mask_rgb = mask_img[:, :, :3]
    
    # Reshape to 2D array for unique operation
    pixels_2d = mask_rgb.reshape(-1, 3)
    unique_colors, _ = np.unique(pixels_2d, axis=0, return_index=True)
    
    # Convert to tuples for dictionary keys
    unique_colors_tuples = [tuple(color) for color in unique_colors]
    
    # Create mask_to_province mapping
    mask_to_province = {}
    for i, mask_color in enumerate(unique_colors_tuples):
        province_id = f"province_{i+1:04d}"
        mask_to_province[mask_color] = province_id
    
    print(f"Found {len(mask_to_province)} unique provinces")
    
    # Step 2: Find owner colors using vectorized operations
    print("Finding owner colors...")
    province_data = {}
    nation_colors = {}
    
    # Create a mapping from mask colors to owner colors
    color_mapping = {}
    
    for mask_color in unique_colors_tuples:
        # Find all pixels with this mask color
        color_matches = np.all(mask_rgb == mask_color, axis=2)
        
        if np.any(color_matches):
            # Get the first matching pixel
            y_coords, x_coords = np.where(color_matches)
            y, x = y_coords[0], x_coords[0]
            owner_color = tuple(start_img[y, x][:3])
            color_mapping[mask_color] = owner_color
    
    # Process results
    for mask_color, owner_color in color_mapping.items():
        province_id = mask_to_province[mask_color]
        province_data[province_id] = {
            "mask_color": mask_color,
            "owner_color": owner_color
        }
        
        # Add to nation_colors
        if owner_color not in nation_colors:
            nation_colors[owner_color] = f"nation_{len(nation_colors)+1:04d}"
    
    # Step 3: Build nation list
    print("Building nation list...")
    nations = []
    for color, name in nation_colors.items():
        nations.append({
            "name": name,
            "color": color
        })
    
    # Step 4: Output JSON
    print("Writing output...")
    output = {
        "provinces": province_data,
        "nations": nations
    }
    
    with open(output_json_path, "w") as f:
        json.dump(convert_np(output), f, indent=2)
    
    end_time = time.time()
    print(f"Output written to {output_json_path}")
    print(f"Total processing time: {end_time - start_time:.2f} seconds")
    print(f"Processed {len(province_data)} provinces and {len(nations)} nations")

def simple_province_data_generation():
    """Simple, reliable version that ensures proper owner color detection"""
    print("Loading images...")
    start_time = time.time()
    
    # Load images
    start_img = np.array(Image.open(start_img_path).convert("RGBA"))
    mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))
    
    height, width, _ = mask_img.shape
    print(f"Image loaded: {width}x{height} pixels")
    
    # Step 1: Find unique mask colors
    print("Finding unique mask colors...")
    mask_to_province = {}
    province_id_counter = 1
    
    # Use a simple loop to find unique colors
    for y in range(height):
        for x in range(width):
            mask_pixel = tuple(mask_img[y, x][:3])
            if mask_pixel not in mask_to_province:
                province_id = f"province_{province_id_counter:04d}"
                mask_to_province[mask_pixel] = province_id
                province_id_counter += 1
    
    print(f"Found {len(mask_to_province)} unique provinces")
    
    # Step 2: Find owner colors for each province
    print("Finding owner colors from start.png...")
    province_data = {}
    nation_colors = {}
    
    processed_count = 0
    total_provinces = len(mask_to_province)
    
    for mask_color, province_id in mask_to_province.items():
        # Find a pixel with this mask color
        found = False
        owner_color = None
        
        for y in range(height):
            for x in range(width):
                if tuple(mask_img[y, x][:3]) == mask_color:
                    owner_color = tuple(start_img[y, x][:3])
                    found = True
                    break
            if found:
                break
        
        if found and owner_color is not None:
            province_data[province_id] = {
                "mask_color": mask_color,
                "owner_color": owner_color
            }
            
            # Add to nation_colors
            if owner_color not in nation_colors:
                nation_colors[owner_color] = f"nation_{len(nation_colors)+1:04d}"
        
        processed_count += 1
        if processed_count % 10 == 0 or processed_count == total_provinces:
            print(f"Processed {processed_count}/{total_provinces} provinces...")
    
    print(f"Successfully processed {len(province_data)} provinces")
    
    # Step 3: Build nation list
    print("Building nation list...")
    nations = []
    for color, name in nation_colors.items():
        nations.append({
            "name": name,
            "color": color
        })
    
    # Step 4: Output JSON
    print("Writing output...")
    output = {
        "provinces": province_data,
        "nations": nations
    }
    
    with open(output_json_path, "w") as f:
        json.dump(convert_np(output), f, indent=2)
    
    end_time = time.time()
    print(f"Output written to {output_json_path}")
    print(f"Total processing time: {end_time - start_time:.2f} seconds")
    print(f"Processed {len(province_data)} provinces and {len(nations)} nations")
    
    # Debug info
    print(f"\nDebug Info:")
    print(f"- Total unique mask colors found: {len(mask_to_province)}")
    print(f"- Provinces with owner colors: {len(province_data)}")
    print(f"- Unique nation colors: {len(nations)}")
    
    if len(province_data) == 0:
        print("WARNING: No provinces were processed! Check if images are the same size.")
        print(f"Start image shape: {start_img.shape}")
        print(f"Mask image shape: {mask_img.shape}")

def process_chunk(chunk_data):
    """Process a chunk of the image to find unique mask colors"""
    y_start, y_end, mask_img = chunk_data
    unique_colors = set()
    
    for y in range(y_start, y_end):
        for x in range(mask_img.shape[1]):
            mask_pixel = tuple(mask_img[y, x][:3])
            unique_colors.add(mask_pixel)
    
    return unique_colors

def find_owner_color_parallel(args):
    """Find owner color for a specific mask color using parallel processing"""
    mask_color, mask_img, start_img = args
    
    # Use numpy's where to find all pixels with this mask color
    mask_rgb = mask_img[:, :, :3]
    color_matches = np.all(mask_rgb == mask_color, axis=2)
    
    if np.any(color_matches):
        # Get the first matching pixel
        y_coords, x_coords = np.where(color_matches)
        y, x = y_coords[0], x_coords[0]
        owner_color = tuple(start_img[y, x][:3])
        return mask_color, owner_color
    
    return mask_color, None

def optimize_province_data_generation():
    print("Loading images...")
    start_time = time.time()
    
    # Load images
    start_img = np.array(Image.open(start_img_path).convert("RGBA"))
    mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))
    
    height, width, _ = mask_img.shape
    print(f"Image loaded: {width}x{height} pixels")
    
    # Step 1: Find unique mask colors using parallel processing
    print("Finding unique mask colors...")
    chunk_size = height // cpu_count()
    chunks = []
    
    for i in range(cpu_count()):
        y_start = i * chunk_size
        y_end = (i + 1) * chunk_size if i < cpu_count() - 1 else height
        chunks.append((y_start, y_end, mask_img))
    
    with Pool(processes=cpu_count()) as pool:
        chunk_results = pool.map(process_chunk, chunks)
    
    # Combine results from all chunks
    all_unique_colors = set()
    for chunk_colors in chunk_results:
        all_unique_colors.update(chunk_colors)
    
    # Create mask_to_province mapping
    mask_to_province = {}
    for i, mask_color in enumerate(sorted(all_unique_colors)):
        province_id = f"province_{i+1:04d}"
        mask_to_province[mask_color] = province_id
    
    print(f"Found {len(mask_to_province)} unique provinces")
    
    # Step 2: Find owner colors for each province using parallel processing
    print("Finding owner colors...")
    province_data = {}
    nation_colors = {}
    
    # Prepare arguments for parallel processing
    parallel_args = [(mask_color, mask_img, start_img) for mask_color in mask_to_province.keys()]
    
    with Pool(processes=cpu_count()) as pool:
        results = pool.map(find_owner_color_parallel, parallel_args)
    
    # Process results
    for mask_color, owner_color in results:
        if owner_color is not None:
            province_id = mask_to_province[mask_color]
            province_data[province_id] = {
                "mask_color": mask_color,
                "owner_color": owner_color
            }
            
            # Add to nation_colors
            if owner_color not in nation_colors:
                nation_colors[owner_color] = f"nation_{len(nation_colors)+1:04d}"
    
    # Step 3: Build nation list
    print("Building nation list...")
    nations = []
    for color, name in nation_colors.items():
        nations.append({
            "name": name,
            "color": color
        })
    
    # Step 4: Output JSON
    print("Writing output...")
    output = {
        "provinces": province_data,
        "nations": nations
    }
    
    with open(output_json_path, "w") as f:
        json.dump(convert_np(output), f, indent=2)
    
    end_time = time.time()
    print(f"Output written to {output_json_path}")
    print(f"Total processing time: {end_time - start_time:.2f} seconds")
    print(f"Processed {len(province_data)} provinces and {len(nations)} nations")

def parse_nation_txt(nation_txt_path="nation.txt"):
    color_to_nation = {}
    try:
        with open(nation_txt_path, "r", encoding="utf-8") as f:
            for line in f:
                line = line.strip()
                if line.startswith("Nation(") and line.endswith(");"):
                    content = line[len("Nation("):-2]
                    parts = [p.strip() for p in content.split(",")]
                    if len(parts) == 4:
                        try:
                            r, g, b = int(parts[0]), int(parts[1]), int(parts[2])
                            name = parts[3]
                            color_to_nation[(r, g, b)] = name
                        except Exception:
                            continue
    except Exception as e:
        print(f"Warning: Could not parse nation.txt: {e}")
    return color_to_nation

def blazing_fast_province_data_generation():
    """Blazing fast version using pure NumPy vectorization for province and nation extraction."""
    print("Loading images...")
    start_time = time.time()

    # Load images
    start_img = np.array(Image.open(start_img_path).convert("RGBA"))
    mask_img = np.array(Image.open(mask_img_path).convert("RGBA"))

    height, width, _ = mask_img.shape
    print(f"Image loaded: {width}x{height} pixels")

    # Step 1: Find unique mask colors and their first pixel index
    print("Finding unique mask colors and mapping to owner colors in one pass...")
    mask_rgb = mask_img[:, :, :3].reshape(-1, 3)
    start_rgb = start_img[:, :, :3].reshape(-1, 3)

    # Get unique mask colors and the first index where each occurs
    unique_colors, unique_indices = np.unique(mask_rgb, axis=0, return_index=True)

    # Get the owner color for each province (using the first pixel index for each unique mask color)
    owner_colors = start_rgb[unique_indices]

    # Parse nation.txt for color-to-nation mapping
    color_to_nation = parse_nation_txt()

    # Build province_id mapping
    province_ids = [f"province_{i+1:04d}" for i in range(len(unique_colors))]

    # Build nation_colors dict and nations list
    nation_colors = {}
    nations = []
    for owner_color in map(tuple, map(lambda x: map(int, x), owner_colors)):
        if owner_color not in nation_colors:
            nation_name = color_to_nation.get(owner_color, f"nation_{len(nation_colors)+1:04d}")
            nation_colors[owner_color] = nation_name
            nations.append({"name": nation_name, "color": owner_color})

    # Build province_data dict, now with 'nation' field
    province_data = {}
    for province_id, mask_color, owner_color in zip(province_ids, unique_colors, owner_colors):
        mask_color_tuple = tuple(map(int, mask_color))
        owner_color_tuple = tuple(map(int, owner_color))
        nation_name = nation_colors[owner_color_tuple]
        province_data[province_id] = {
            "mask_color": mask_color_tuple,
            "owner_color": owner_color_tuple,
            "nation": nation_name
        }

    print(f"Found {len(province_data)} provinces and {len(nations)} nations.")

    # Output JSON
    print("Writing output...")
    output = {
        "provinces": province_data,
        "nations": nations
    }
    with open(output_json_path, "w") as f:
        json.dump(convert_np(output), f, indent=2)

    end_time = time.time()
    print(f"Output written to {output_json_path}")
    print(f"Total processing time: {end_time - start_time:.2f} seconds")
    print(f"Processed {len(province_data)} provinces and {len(nations)} nations")

if __name__ == "__main__":
    # Use the blazing fast version by default for best performance
    blazing_fast_province_data_generation()