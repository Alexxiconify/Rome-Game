import pandas as pd
import json

# Load data
owner_df = pd.read_csv('owner_color_name.csv')
prov_df = pd.read_csv('province_owners.csv')

# Clean color strings to tuple
owner_df['owner_color'] = owner_df['owner_color'].apply(lambda x: tuple(int(i) for i in x.strip('()').replace(' ', '').split(',')))

# Alphabetize nations
nations = owner_df[['owner_name', 'owner_color']].drop_duplicates().sort_values('owner_name')
nations_list = [
    {'name': row.owner_name, 'color': row.owner_color}
    for row in nations.itertuples()
]

# Provinces list
provinces_list = [
    {'province_id': row.province_id, 'owner_name': row.owner_name}
    for row in prov_df.itertuples()
]

# Output JSON
out = {
    'nations': nations_list,
    'provinces': provinces_list
}
with open('nations_and_provinces.json', 'w') as f:
    json.dump(out, f, indent=2)
print('Wrote nations_and_provinces.json') 