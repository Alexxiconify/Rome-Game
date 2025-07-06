import numpy as np
from PIL import Image

# Input files
load_path = 'src/resources/load.png'
start1_path = 'src/resources/start1.png'
ocean_path = 'src/resources/ocean.png'
out_path = 'src/resources/ocean_start_mask.png'

def main():
    load_img = Image.open(load_path).convert('RGBA')
    start1_img = Image.open(start1_path).convert('RGBA')
    ocean_img = Image.open(ocean_path).convert('RGBA')

    load_arr = np.array(load_img)
    start1_arr = np.array(start1_img)
    ocean_arr = np.array(ocean_img)

    # Get the ocean color from start1.png (assume top-left pixel is ocean)
    ocean_color = start1_arr[0, 0, :3]

    # Create ocean mask: True where ocean, False where land
    ocean_mask = (
        (ocean_arr[:, :, :3] != [0, 0, 0]).any(axis=2) |
        (start1_arr[:, :, :3] == ocean_color).all(axis=2)
    )

    # Start with province colors, set ocean pixels to black
    result = load_arr.copy()
    result[ocean_mask] = [0, 0, 0, 255]

    Image.fromarray(result, 'RGBA').save(out_path)
    print(f"Wrote {out_path}")

if __name__ == '__main__':
    main() 