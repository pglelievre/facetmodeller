# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
### Changed
- Added radio button to 2D GUI for showing/hiding regions (was already in 3D GUI).
- New radio button for drawing facet normal vectors (3D).
- New menu item for adding nodes on corners of current section.

### Bugs squashed
- Bug fix for merging groups that was deleting nodes/facets/regions in the merged group.

## 2018-01-011
### Changed
- Added better cross-platform support (newline characters for non *nix platforms).

## 2018-01-05
### Bugs squashed
- Bug fix where node group ID's were not being written out correctly when only writing out displayed or selected groups.
- Bug fix where the menu items for exporting were available without calibration.
- Bug fix for shifting of off-section points in 2D viewing panel.
- Bug fix for loading session files with image and space origin as null.
