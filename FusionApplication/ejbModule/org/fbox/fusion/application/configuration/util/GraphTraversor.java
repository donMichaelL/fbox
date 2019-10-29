package org.fbox.fusion.application.configuration.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GraphTraversor {

    private String START;
	private String END;
	
	private ArrayList<LinkedList<String>> pathsExtracted=new ArrayList<LinkedList<String>>();
	
    public String getSTART() {
		return START;
	}

	public void setSTART(String sTART) {
		START = sTART;
	}

	public String getEND() {
		return END;
	}

	public void setEND(String eND) {
		END = eND;
	}

	

    /*
    public static void main(String[] args) {
        // this graph is directional
        Graph graph = new Graph();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "A");
        graph.addEdge("B", "D");
        graph.addEdge("B", "E"); // this is the only one-way connection
        graph.addEdge("B", "F");
        graph.addEdge("C", "A");
        graph.addEdge("C", "E");
        graph.addEdge("C", "F");
        graph.addEdge("D", "B");
        graph.addEdge("E", "C");
        graph.addEdge("E", "F");
        graph.addEdge("F", "B");
        graph.addEdge("F", "C");
        graph.addEdge("F", "E");
        LinkedList<String> visited = new LinkedList();
        visited.add(START);
        new Search().breadthFirst(graph, visited);
    }*/

	
    public ArrayList<LinkedList<String>> getPathsExtracted() {
		return pathsExtracted;
	}

	public void search(Graph graph) {
       	
        LinkedList<String> visited = new LinkedList<String>();        
        visited.add(START);
    	breadthFirst(graph, visited);  	
    }
    
    public void breadthFirst(Graph graph, LinkedList<String> visited) {
        LinkedList<String> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                storePath(visited);
                visited.removeLast();
                break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (String node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            breadthFirst(graph, visited);
            visited.removeLast();
        }
    }

    private void storePath(LinkedList<String> visited) {
        /*for (String node : visited) {
            System.out.print(node);
            System.out.print(" ");
        }
        System.out.println();*/
        pathsExtracted.add(new LinkedList<>(visited));
    }
    
    
    
}