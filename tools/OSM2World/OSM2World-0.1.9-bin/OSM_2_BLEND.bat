ECHO Preparing OSM2WORLD convertion to .obj
PAUSE
java -Djava.library.path="lib\jogl\windows-amd64" -Xmx1G -jar OSM2World.jar --config configNew.txt -i example.osm -o image.obj 


ECHO OSM2WORLD process is done. Now preparing Blender convertion to .blend
PAUSE
..\blender --background --python ..\convert_blend_to_obj.py 

PAUSE