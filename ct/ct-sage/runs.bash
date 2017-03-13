 #!/bin/bash

for iLoop in `seq 1 10000`;
do
   if [ -f "error.txt" ]
   then
      rm error.txt
   fi

   vertices=`awk -v min=100 -v max=300 'BEGIN {srand(); print int(min + rand() * (max - min + 1))}'`
   sage 4ct-normal-selection.py -r $vertices

   if [ -f "error.txt" ]
   then
      mv debug.input_planar_g_faces.serialized input_planar_g_faces.serialized.$iLoop
      mv debug.input_planar_g_faces.embedding_list input_planar_g_faces.embedding_list.$iLoop
      mv debug.really_bad_case.edgelist debug_really_bad_case.edgelist.$iLoop
      mv debug.really_bad_case.dot debug_really_bad_case.dot.$iLoop
   fi
done  
