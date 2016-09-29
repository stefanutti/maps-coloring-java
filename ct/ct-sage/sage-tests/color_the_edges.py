#!/usr/bin/env sage

import sys
from sage.all import *
from sage.graphs.graph_plot import GraphPlot

print ("Create a graph")
G = Graph(sparse=True)
G.allow_multiple_edges(True)
G.add_edge(1,2,"blue")
G.add_edge(2,3,"green")
G.add_edge(3,1,"red")
G.add_edge(1,4,"green")
G.add_edge(2,4,"red")
G.add_edge(3,4,"blue")

G.show()
G.faces()

G.set_planar_positions(test=True,set_embedding=True)

G.show()
G.faces()
print (G.edges())

G.graphplot(color_by_label={'green':'green','blue':'blue','red':'red'}).show()
print (G.edges())
