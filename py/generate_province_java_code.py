# generate_province_java_code.py

csv_path = "resources/province_color_map.csv"
java_path = "province_creation_snippet.java"

with open(csv_path, "r") as f:
    lines = f.readlines()

with open(java_path, "w") as out:
    out.write("// Auto-generated province creation code\n")
    out.write("// Example: createProvince(String id, ...);\n")
    for line in lines[1:]:  # skip header
        parts = line.strip().split(",")
        if len(parts) >= 3:
            province_id = parts[2]
            out.write(f'createProvince("{province_id}", ...);\n')

print(f"Java code written to {java_path}")