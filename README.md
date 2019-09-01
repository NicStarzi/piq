# piq
An object oriented GUI framework similar to JavaFX, AWT/Swing and others. The greatest feature of piq is its true platform independence. This independence is achieved by decoupling the window creation, rendering and input querying from the layout engine and the component definitions. As such the core library alone is not enough to actually display a GUI. Instead, a so called *root implementation* is needed. 

As a result of this design a piq UI can be embedded within existing applications and even other UI frameworks with relative ease. The user must *only* create a root implementation within the target platform.


This repository does already contain two example root implementations, one based on AWT/Swing, the other using LWJGL3, GLFW and OpenGL. Users can use these implementation as the basis for their own implementations.
