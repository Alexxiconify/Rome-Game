import csv

csv_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_ownership_report.csv"
java_path = "/mnt/c/Users/taylo/Documents/projects/Roma Game/province_creation_snippet.java"

with open(csv_path, "r") as f, open(java_path, "w") as out:
    reader = csv.DictReader(f)
    out.write("// Auto-generated province creation code\n")
    out.write("// Example: createProvince(String id, String owner, int r, int g, int b)\n")
    for row in reader:
        pid = row["provinceID"]
        owner = row["owner_name"]
        r = row["owner_r"]
        g = row["owner_g"]
        b = row["owner_b"]
        out.write(f'createProvince("{pid}", "{owner}", {r}, {g}, {b});\n')

print(f"Java code written to {java_path}") 