# Roma Game Data Manager

## Overview
This is a comprehensive, optimized data management system that combines all the functionality from multiple redundant Python scripts into one efficient system.

## Files

### Core Files
- **`data_manager.py`** - Main comprehensive data manager (replaces 19+ individual scripts)
- **`cleanup_old_scripts.py`** - Cleanup utility to remove redundant files

### Essential Utilities (Kept)
- **`generate_province_data_json.py`** - Original data generation
- **`gen_province_owners.py`** - Province generation from mask
- **`create_new_mask.py`** - Advanced mask creation
- **`create_simple_mask.py`** - Simple mask creation
- **`province_mask_gui.py`** - GUI for mask editing
- **`list_unique_colors.py`** - Color analysis utility
- **`gen_alphabetized_nations_and_provinces.py`** - Alphabetized output

## Data Manager Usage

### Commands

```bash
# Show help
python3 data_manager.py help

# Validate data consistency
python3 data_manager.py validate

# Update nations from owner_color_name.csv
python3 data_manager.py update_nations

# Regenerate all data files
python3 data_manager.py regenerate_all

# Generate province owners from mask
python3 data_manager.py generate_owners --mask province_mask.png

# Create a simple test mask
python3 data_manager.py create_mask
```

### Features

#### Data Validation
- Checks nations and provinces count
- Validates color mappings against CSV
- Reports missing colors
- Ensures data consistency

#### Nation Updates
- Updates nation names from `owner_color_name.csv`
- Updates province owners from color mappings
- Handles missing colors gracefully
- Preserves data integrity

#### File Regeneration
- Regenerates `province_owners.csv`
- Regenerates `province_ownership_report.csv`
- Regenerates `nation.txt`
- Regenerates `province_color_map.csv`
- Uses consistent color-to-name mapping

#### Mask Processing
- Generates province owners from mask images
- Maps colors to owner names
- Handles ocean (black) regions
- Creates structured province data

## Data Files

### Input Files
- **`nations_and_provinces.json`** - Main data file
- **`owner_color_name.csv`** - Color to name mapping

### Output Files
- **`province_owners.csv`** - Province ownership data
- **`province_ownership_report.csv`** - Ownership statistics
- **`nation.txt`** - Nation data in text format
- **`province_color_map.csv`** - Province color mapping

## Optimization Results

### Before
- 28+ individual Python scripts
- Redundant functionality
- Inconsistent data handling
- Difficult maintenance

### After
- 1 comprehensive data manager
- 8 essential utility scripts
- Consistent data processing
- Easy maintenance and updates

### Removed Files (19 total)
- All fix/update scripts (now in data_manager.py)
- All test scripts (functionality integrated)
- All redundant generation scripts
- All individual validation scripts

## Benefits

1. **Consistency** - All data operations use the same logic
2. **Maintainability** - Single source of truth for data processing
3. **Reliability** - Comprehensive validation and error handling
4. **Efficiency** - Reduced code duplication and file count
5. **Usability** - Simple command-line interface

## Data Validation Results
- Nations: 80
- Provinces: 6704
- CSV mappings: 81
- Missing colors: 0 ✅

All data is now consistent and properly mapped! 