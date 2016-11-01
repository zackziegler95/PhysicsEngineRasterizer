# PhysicsEngineRasterizer

This project has two aims:

1) To build a physics engine
2) To build a rasterizer, including objects and lighting

This stems from my interest in how physics engines and rasterizers work, and is not by any means meant to be used in a production setting. Currently the engine supports contacts for spheres and planes, and rasterization for arbitrary polygon shapes.

The scene is defined in Window.java. By default it consists of two spheres and an open box. The camera translation is controlled with wasd, and the rotation is controlled with GUI sliders. 


To run the simulation you may run the jar file like normal:
java -jar Rasterization.jar

The jar file can be found in dist/Rasterization.jar
Java JRE 1.8 is required, and it has been tested with 1.8.0_72 on Linux Mint 17.3
The code is developed with Java JDK 1.8.
