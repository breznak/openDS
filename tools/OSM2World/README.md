This folder contains experimental automatic conversion of OpenStreetMap file (.osm) into .blend file loadable with jMonkeyEngine.
To be able to run this script, you must first install Blender onto your computer and copy all content of this folder to your
Blender folder (except README.txt which is not important for running)

Convertor OSM2World-0.1.9 is part of this folder and you don't have to download it.
For more information about this program, please visit http://osm2world.org/
All credits for this convertor goes to Tobias Knerr and other contributors 

After you have copied content of this folder to your blender folder, go to Blender\OSM2World-0.1.9-bin
Then run OSM_2_BLEND.bat. This will create new folder in here called img.obj which contains your .blend file

Now you can run your jMonkeyEngine SDK (https://github.com/jMonkeyEngine/jmonkeyengine) and import this file as object
Save it as .j3o scene after manual editing of desired properties.



Warning: .j3o file can be run in OpenDS, but if the object is set to solid, the roads will act like steem slopes and car
won't be able to hold on them. This might be an issue caused by normal vectors of the object. 
Solution can be leaving object without solid physics.

OpenDS team warned us to the creator of so called "Titan" which will act as openstreetmap convertor directly to object usable in OpenDS
For more information, please visit https://www.opends.eu/