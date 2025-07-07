import tkinter as tk
from tkinter import filedialog, messagebox
import cv2
import numpy as np
from PIL import Image
import random
import os
import threading

def generate_mask(input_path, output_path):
    img = cv2.imread(input_path)
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    _, binary = cv2.threshold(gray, 240, 255, cv2.THRESH_BINARY)
    num_labels, labels = cv2.connectedComponents(binary)
    output = np.zeros_like(img)
    label_to_color = {}
    for label in range(1, num_labels):
        color = tuple(random.randint(50, 255) for _ in range(3))
        label_to_color[label] = color
        output[labels == label] = color
    output_rgb = cv2.cvtColor(output, cv2.COLOR_BGR2RGB)
    Image.fromarray(output_rgb).save(output_path)
    return num_labels - 1

def select_file():
    file_path = filedialog.askopenfilename(
        title="Select Q-BAM White Map PNG",
        filetypes=[("PNG Images", "*.png")]
    )
    if not file_path:
        return
    folder = os.path.dirname(file_path)
    output_path = os.path.join(folder, "province_mask.png")

    def run_mask():
        try:
            num_provinces = generate_mask(file_path, output_path)
            messagebox.showinfo("Success", f"Province mask saved as:\n{output_path}\nProvinces detected: {num_provinces}")
        except Exception as e:
            messagebox.showerror("Error", f"Failed to generate mask:\n{e}")

    threading.Thread(target=run_mask).start()

root = tk.Tk()
root.title("Q-BAM Province Mask Generator")
root.geometry("400x200")
root.resizable(False, False)

label = tk.Label(root, text="Select your Q-BAM white PNG map to generate a province mask.", wraplength=380, pady=20)
label.pack()

button = tk.Button(root, text="Open PNG File...", command=select_file, font=("Arial", 14), width=20)
button.pack(pady=30)

root.mainloop()