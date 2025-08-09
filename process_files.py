import json

def build_structured_file_tree(paths):
    """
    Converts a flat list of file paths into a nested dictionary with an
    explicit structure for files and subdirectories.

    Args:
        paths: A list of strings, where each string is a file path.

    Returns:
        A nested dictionary representing the file and folder structure.
        Example: {"files": ["f1.html"], "subdirs": {"sub1": { ... }}}
    """
    # This is the root of our file tree, representing the base directory.
    root_tree = {"files": [], "subdirs": {}}
    
    for path in paths:
        parts = path.strip().split('/')
        current_level = root_tree

        # Iterate through the parts to build the directory structure.
        # We go up to the second-to-last part (the directories).
        for part in parts[:-1]:
            # Navigate into the subdirectories dictionary
            subdirs = current_level["subdirs"]
            
            # If the subdirectory doesn't exist, create it with the standard structure
            if part not in subdirs:
                subdirs[part] = {"files": [], "subdirs": {}}
            
            # Move down to the next level
            current_level = subdirs[part]
            
        # The last part is the file name. Add it to the 'files' list
        # at the current level.
        file_name = parts[-1]
        if file_name:  # Ensure the file name is not empty
            current_level["files"].append(file_name)
            
    return root_tree

# --- Main script execution ---

# 1. Read the list of files from your text file
try:
    with open("html_files_list.txt", 'r') as f:
        paths_data = f.read()
except FileNotFoundError:
    print("Error: 'html_files_list.txt' not found. Please ensure the file is in the same directory.")
    exit()

# 2. Convert the raw text into a list of file paths
file_paths = paths_data.strip().split('\n')

# 3. Build the new hierarchical tree
structured_tree = build_structured_file_tree(file_paths)

# 4. Wrap the entire structure inside a root object as requested
final_output = {
    "/": structured_tree
}

# 5. Print and save the result to a new JSON file
print("------ New Structured JSON Output ------")
# Printing with indent for readability
print(json.dumps(final_output, indent=2))

with open("maps_of_html_files_structured.json", 'w') as f:
    # Save the final JSON to a file. 
    # Using indent=2 makes the file readable. For minimum file size in production,
    # you can remove the indent argument.
    json.dump(final_output, f, indent=2)

print("\nSuccessfully saved the new structure to 'maps_of_html_files_structured.json'")