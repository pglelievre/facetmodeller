# Changelog
All notable changes to this project will be documented in this file. Dates are in format YYYY-MM-DD.

## [Unreleased]
### Changed
### Bugs squashed

## 2019-01-21
### Changed
- Nodes can now be read from a .node file and split into the existing defined groups or new groups.
- Added functionality to reduce the size of section images and adjust calibration and node coordinates acoordingly.
- Some changes to dialogs to tell the user when to wait patiently (before large image file read/write operations).
- Allowed reading of non-triangular facets into a 3D model from a .node/.ele pair.
- Decoupled the section-facet linkages which are not useful and only complicate the GUI.
- Removed section status bar, which is fairly useless, to make more room for the cursor status bar, which is very useful.
- Allowed reading of non-triangular facets into a 3D model from a .node/.ele pair.
### Bugs squashed
- Bug fix when changing a node's section that was moving the selected node to the clicked point.
- Minor bug fix that was not correctly managing whether some menu items were enabled or disabled.

## 2018-03-19
### Changed
- Removed some additional dialogs when the user requests that some dialogs are hidden (adding node in facet or node on edge).
- Changed the way that nodes are added within facets (now always added to currently selected group and section).
- New click mode to duplicate nodes.
- New menu item for creation of new node at specified coordinates.
- New click mode for changing node coordinates by editing in a text input dialog.
- An appropriate error dialog is now supplied if a tab character is read in a .node or .ele file.
- New menu item to reverse the group order.
- Changed painting of nodes and facets in the 2D panel in the reverse order that the groups are ordered, so that the groups listed higher in the group selection boxes will overlay those listed lower.
- Minor changes to some tip and title strings on menu items and buttons.
- Added abilility to translate all or current group of 3D nodes by some specified amount.
- Added user control over overlay transparency.
- Added radio button to 2D GUI for showing/hiding regions (was already in 3D GUI).
- New radio button for drawing facet normal vectors (3D).
- New menu item for adding nodes on corners of current section.
### Bugs squashed
- Bug fix when changing a node's section.
- Minor fix to add some click modes that were missing from the menu but which were available on the tool bar.
- Bug fix for merging groups that was deleting nodes/facets/regions in the merged group.

## 2018-01-11
### Changed
- Added better cross-platform support (newline characters for non *nix platforms).

## 2018-01-05
### Bugs squashed
- Bug fix where node group ID's were not being written out correctly when only writing out displayed or selected groups.
- Bug fix where the menu items for exporting were available without calibration.
- Bug fix for shifting of off-section points in 2D viewing panel.
- Bug fix for loading session files with image and space origin as null.
