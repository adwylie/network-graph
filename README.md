# Network-Graph

## Overview

This project concerns the connectivity of wireless sensor networks using omnidirectional and directional antenna. It covers simulation of omnidirectional and directional networks using directed graphs as a basis, and calculation of the graph's antenna angle, range, and direction given an undirected weighted graph as input. It was a term project for one of my courses in university.

The motivation for the project was as follows:
> "Consider a set *S* of *n* points in the plane that can be identified with sensors having a range r > 0. For a given angle 0 &le; &#981; &le; 2&pi; each sensor is allowed to use at most one directional antenna of angle at most &#981;. How dod we rotate the antenna and at what range *r* (same for all sensors) so that by a directed, strongly connected network on *S* is formed."

The simulation presents an algorithm which contructs a strongly connected network on the input graph using directional (or omnidirectional) antenna. From the input graph, a minimum spanning tree is first found using Prim's algorithm. Following this, the graph is converted into a logical network where the vertices contain the sensors whose properties are set by the orientation algorithm. Using the user interface, statistics which were gathered during the antenna orientation process can be inspected, and shortest paths between each of the contained sensors can be found. With regards to implementation, Dijkstra's single source shortest path algorithm was used to find shortest paths, and the other gathered statistics provided comprise shortest paths, average length of routes, diameter of the network, and averages for sensor angles and ranges. Separate logical graph types can be built, including ones having either identical or independant ranges.

## Usage

###Graph Creation

Input graphs are created by the import of a text file containing the graph information. The format is as follows:

    // create a vertex/node at position (x, y)
    NODE(*name*, *x*, *y*)
    // create an edge connecting the named vertices
    EDGE(*name*, *name*)

A vertex/node is given an x position and y position in the plane, along with a name. An edge is created by identifying two vertices which the edge connects to. In this case, the input graph is undirected, so order does not matter. The file is parsed without lookback, so any vertices named by an edge must be created before the edge.

### User Interface

The user interface allows for either an omnidirectional or directional network to be drawn, with respect to certain specified parameters. All of the input graph, the intermediate minimum spanning tree, and oriented graphs wherein all sensors have either an identical range, or individual ranges can be selected to be examined. Additional controls exist to view the shortest path from any vertex to another, along with controls to manually set the sensor ranges when viewing an oriented graph with identical sensor range. For each graph, statistics are also shown.

## Setup

The Java Runtime Environment is required (1.6 or higher). After this is done, the program can be executed either by cloning the repository and building from source, or by grabbing the newest version available on the project's downloads page. The version in the downloads page includes a sample input graph, and generated Javadoc documentation for the code.
