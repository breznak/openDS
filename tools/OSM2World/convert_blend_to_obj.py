import bpy
import sys

bpy.ops.scene.delete()
bpy.ops.import_scene.obj(filepath=".\image.obj\part0000.obj")
bpy.ops.wm.save_as_mainfile(filepath=".\image.obj\pokus3.blend")