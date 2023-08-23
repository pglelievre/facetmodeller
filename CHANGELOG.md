# Changelog
All notable changes to this project will be documented in this file. Dates are in format YYYY-MM-DD.

## [Unreleased]
### Changed
- Added radio button to turn on or off plotting of calibration points.
### Bugs squashed
- The "copy section" menu command did not work unless a .node and .ele file had been loaded in the selected section.
- Tool panel and 3D viewing panel were not being shown/hidden based on information in loaded session file.
- After saving the session from the menu, pressing the save button on the tool bar behaved like you hadn't already saved.

## 2022-06-13
### Changed
- Added menu item for exporting of a 3D node file for a 2D model, which is also exported with "all files" with suffix "\_3D".
- See bug squashed below: increased maximum number of facet groups split to 16 (can increase further in future if required).
### Bugs squashed
- When loading .node/.ele files, splitting facets into different groups based on attributes in the file was not working.

## 2021-12-06
### Changed
- When a section image file is missing, the program now looks for it in the most recent directory from which a file was opened.
- New menu option to copy the current section and its nodes (the copied nodes are not added to any facets).
- Allowed user to optionally change the name of an image section to something different than the related file name (to clear that optional name, change it again and specify the string "null").
- Provided option to change 2D pixel coordinates of an on-section node.
- New menu option to change VOI to current model extents.

### Bugs squashed
- Error was occurring when trying to paint the 3D scene when there were only nodes on uncalibrated sections.

## 2021-01-05
### Bugs squashed
- Boundary marker information was not being displayed for facets in 2D models.
- Radio buttons for colouring nodes and facets were not enabled when loading 2D model.

## 2020-12-01
### Changed
- Added option to show/hide the outline of the image in the 2D panel.
- Allowed users to control the zooming factor on the 2D and 3D panels.
- New menu items to assign a boundary marker value to all nodes or facets of the current group.
- Added menu item for exporting region information to a .vtu file (in addition to the existing option of exporting to a .node file).

### Bugs squashed
- Boundary markers were not being saved to the session files.
- Facet information line in exported poly file was not correctly indicating boundary markers.

## 2020-07-13
### Changed
- Added support for boundary markers on nodes and facets:
  - New clickmodes provided for toggling node or facet boundary markers on mouse click, or setting them to either true or false on mouse click/drag.
  - The boundary marker state (0/1) is indicated in the information text bar along with the other node/facet information.
  - There are new radio button options for colouring the nodes and facets based on their boundary marker value.
  - Consequently, there was some reorganization of the radio button panel so things may look a little different.
  - The colours used when colouring the nodes/facets based on the their boundary marker value can be altered in the display menu.
  - Boundary marker information is only written to output files if any of the boundary markers have been set to 1.
- Added support for reading .node/.ele files with indexing starting from 0. If reading a pair of .node/.ele files then indexing must be consistent.
- Similarly, indexing in output .node/.ele/.poly files can now start from 0 or 1. Access this option in the menu item File > Export options.
- The precision used for coordinates when writing files can now be changed (also in menu item File > Export options). The default is 6 decimal places.

### Bugs squashed
- If the image file for a section was not found then the model was not being painted in the 2D panel.
- Added support for older session files that used the obsolete TopoSection class (replaced with NoImageDepthSection class).
- Variable-facet .ele files were not being read correctly.
- Length of normal vectors was not allowed to be < 1 (now <= 0).
- Facet vectors were sometimes plotted in opposite direction for non-triangular facets.

## 2019-05-02
### Changed
- Added cancel buttons to dialogs during section calibration.
- When moving a node, the location of the cursor is now shown properly after the first click.
- Added a dialog to indicate when a session has been saved successfully.
- Added radio buttons to plot heads/tails on facet normal vectors.
- Normal vectors can now have their colour changed through the display menu.
- Normal vectors and facet edges can be drawn thick in 3D (with aliasing) through the display menu.
- Added new click mode to propagate the facet node ordering, and therefore normal vector direction, within a facet group.
### Bugs squashed
- Viewing panels and menu wasn't being updated if all sections were removed.
- Painting the 3D view failed when there was a node on an uncalibrated section.
- Bug in change node coordinates click mode (the one where you manually enter the coordinates in a text field).
- The display option to change the edge colour was not being included in the 2D version.

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
