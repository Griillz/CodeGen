package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

	
	public class Node implements Iterable<Node> {
		
		private ArrayList<Node> children = new ArrayList<Node>();
		private String data;

		
		public Node() {
			this(null);
		}
		
		public Node(String d) {
			this.data = d;
		}
		
		public void addChild(Node add) {
			this.children.add(add);

		}
		
		public Node getChild(int i) {
			return this.children.get(i);
		}
		
		public ArrayList<Node> getChildren() {
			return this.children;
		}
		
		public int getSize() {
			return this.children.size();
		}
		
		public String getData() {
			return this.data;
		}
		
		public boolean hasChildren() {
			return getSize() != 0;
		}

		@Override
		public Iterator<Node> iterator() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public void printChildren(Node node) {
			for(Node get : node.children) { 
				System.out.println(get.data);
				printChildren(get);
			}
		}
		
		
		
		
		
	}