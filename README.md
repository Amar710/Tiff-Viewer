﻿# Tiff-Viewer
TIFF Viewer Application 
The TIFF Viewer reads and displays uncompressed TIFF image files. It uses Java Swing GUI for 
users to interact with and perform image processing operations on 24-bit RGB full-color TIFF 
images.
Libraries and Tools Used
The application is written in Java and utilizes the following libraries:
1. Java AWT (Abstract Window Toolkit): A set of APIs used for creating window-based 
applications. It provides components for building GUIs such as buttons, menus, and 
windows.
2. Swing: Built on top of AWT, Swing provides a richer set of components than AWT. It is 
used to create more sophisticated GUI elements that are platform-independent.
3. ImageIO: Part of the Java standard library, it is used for reading and writing images in 
various formats including TIFF, which is crucial for the application's core functionality.
4. Java Logging API: Utilized for logging errors and other information, which is essential for 
debugging and maintaining the application

Graphical User Interface
The GUI is the main interface between the user and the application. It consists of:
• A main window (JFrame) that houses all other components.
• A menu bar (JMenuBar) with a "File" menu that includes "Open File" and "Exit" items for 
file operations.
• A panel (JPanel) that contains two labels (JLabel) to display the original and processed 
images side by side.
• A "Next" button (JButton) allowing users to cycle through the image processing 
operations.
Application Workflow
Upon launching, the application presents a window where users can choose to open a TIFF 
image file. Once an image is selected, a variety of sequence of operations can be performed
