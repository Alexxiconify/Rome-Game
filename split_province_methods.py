#!/usr/bin/env python3

def split_province_methods():
    """Split the large initializeAutoProvinces2 method into smaller methods"""
    
    # Read the current file
    with open('src/com/romagame/map/WorldMap.java', 'r') as f:
        content = f.read()
    
    # Find the start and end of initializeAutoProvinces2
    start_marker = "    private void initializeAutoProvinces2() {"
    end_marker = "    private void initializeSeaZones() {"
    
    start_idx = content.find(start_marker)
    end_idx = content.find(end_marker)
    
    if start_idx == -1 or end_idx == -1:
        print("Could not find method markers")
        return
    
    # Extract the method content
    method_start = start_idx + len(start_marker)
    method_content = content[method_start:end_idx].strip()
    
    # Split into chunks of ~1000 lines each
    lines = method_content.split('\n')
    chunk_size = 1000
    chunks = []
    
    for i in range(0, len(lines), chunk_size):
        chunk = lines[i:i + chunk_size]
        chunks.append('\n'.join(chunk))
    
    # Create the new method calls
    new_method_calls = []
    for i, chunk in enumerate(chunks):
        method_name = f"initializeAutoProvinces2_{i+1}"
        new_method_calls.append(f"        {method_name}();")
    
    # Create the new method definitions
    new_methods = []
    for i, chunk in enumerate(chunks):
        method_name = f"initializeAutoProvinces2_{i+1}"
        method_def = f"""    private void {method_name}() {{
{chunk}
    }}"""
        new_methods.append(method_def)
    
    # Replace the original method
    replacement = f"""    private void initializeAutoProvinces2() {{
{chr(10).join(new_method_calls)}
    }}

{chr(10).join(new_methods)}"""
    
    # Apply the replacement
    new_content = content[:start_idx] + replacement + content[end_idx:]
    
    # Write the updated file
    with open('src/com/romagame/map/WorldMap.java', 'w') as f:
        f.write(new_content)
    
    print(f"Split initializeAutoProvinces2 into {len(chunks)} smaller methods")
    print(f"Original method had {len(lines)} lines")
    print(f"Each new method has approximately {chunk_size} lines")

if __name__ == "__main__":
    split_province_methods() 