import csv
import math

INPUT_CSV = "province_ownership_report.csv"
OUTPUT_JAVA = "ProvinceCreationSnippets.java"
METHOD_LINES_LIMIT = 2000  # Safe limit per method

def read_provinces(csv_path):
    provinces = []
    with open(csv_path, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            if row["owner"] in ("REMOVE_FROM_MAP", "BORDER"):
                continue
            provinces.append(row)
    return provinces

def write_java(provinces, output_path, method_lines_limit):
    num_methods = math.ceil(len(provinces) / method_lines_limit)
    with open(output_path, "w") as f:
        f.write("// Auto-generated province creation code\n")
        f.write("public class ProvinceCreationSnippets {\n")
        for m in range(num_methods):
            start = m * method_lines_limit
            end = min((m+1) * method_lines_limit, len(provinces))
            f.write(f"    public static void createProvincesPart{m+1}(WorldMap map) {{\n")
            for prov in provinces[start:end]:
                pid = prov["province_id"]
                owner = prov["owner"]
                r, g, b = prov["r"], prov["g"], prov["b"]
                f.write(f'        map.createProvince("{pid}", "{owner}", {r}, {g}, {b});\n')
            f.write("    }\n\n")
        # Write a helper to call all
        f.write("    public static void createAllProvinces(WorldMap map) {\n")
        for m in range(num_methods):
            f.write(f"        createProvincesPart{m+1}(map);\n")
        f.write("    }\n")
        f.write("}\n")

if __name__ == "__main__":
    provinces = read_provinces(INPUT_CSV)
    write_java(provinces, OUTPUT_JAVA, METHOD_LINES_LIMIT)
    print(f"Java code written to {OUTPUT_JAVA} with {len(provinces)} provinces.")