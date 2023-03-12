# Project

AI search algorithms

## Description

AI search algorithms for the marine navigation planning problem.

Informed and uninformed search algorithms: depth-first search (DFS), breadth-first search (BFS), best-first search (BestF), A*(AStar) search and bidirectional search.

The output is as follows.

1. The coordinates in the nodes and the costs of the frontier at each step before polling a node
2. The path represented as a sequence of (r, c) coordinates with no spaces
3. Thepathrepresentedasasequenceofstringsseparatedbyaspace(e.g.RightRightLeft) 
4. The path cost (double)
5. The number of nodes visited (int)

<img width="503" alt="Screenshot 2023-02-15 at 15 44 06" src="https://user-images.githubusercontent.com/118636537/219078910-ae917256-35f5-4a72-9cab-b6a8af78a351.png">

## Getting Started

### Dependencies

* Java Version: openjdk 19.0.1

### Running program

* Please run the following command
```
cd AISearchAlgorithm/src
javac *.java
java Main <DFS|BFS|AStar|BestF|Bidirectional|> <ConfID>
```
