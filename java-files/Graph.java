import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Graph extends JFrame {
    int width;
    int height;

    ArrayList<Node> nodes;
    ArrayList<edge> edges;

    public Graph() { //Constructor
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nodes = new ArrayList<Node>();
        edges = new ArrayList<edge>();
        width = 30;
        height = 30;
    }

    public Graph(String name) { //Construct with label
        this.setTitle(name);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        nodes = new ArrayList<Node>();
        edges = new ArrayList<edge>();
        width = 30;
        height = 30;
    }

    class Node {
        int x, y;
        String name;
        String color;
        
        public Node(String myName, int myX, int myY, String myColor) {
            x = myX;
            y = myY;
            name = myName;
            color = myColor;
        }
    }
    
    class edge {
        int i,j;
        String color;
        
        public edge(int ii, int jj, String myColor) {
            i = ii;
            j = jj;	    
            color = myColor;
        }
    }
    
    public void addNode(String name, int x, int y, String color) { 
        //add a node at pixel (x,y)
        nodes.add(new Node(name, x, y, color));
        this.repaint();
    }
    public void addEdge(int i, int j, String color) {
        //add an edge between nodes i and j
        edges.add(new edge(i,j, color));
        this.repaint();
    }
    
    public void paint(Graphics g)
    { // draw the nodes and edges
        int nodeHeight, nodeWidth;
	    FontMetrics f = g.getFontMetrics();

	    //g.setColor(Color.black);
	    for (edge e : edges) {

            if(e.color.equals("black"))
                g.setColor(Color.black);
            if(e.color.equals("red"))
                g.setColor(Color.red);

	        g.drawLine(nodes.get(e.i).x, nodes.get(e.i).y,
		     nodes.get(e.j).x, nodes.get(e.j).y);
	    }

	    for (Node n : nodes) {
	        
            if(n.color.equals("red"))
            {
                nodeHeight = 50;
                nodeWidth = 48;
                ImageIcon png = new ImageIcon( "star1.png" );
                png.paintIcon(this, g, n.x - nodeWidth/2, n.y -nodeHeight/2);
                g.drawString(n.name, n.x - f.stringWidth(n.name)/2,
                     n.y + nodeWidth);
            }
            else
            {
                nodeHeight = Math.max(height, f.getHeight());
                nodeWidth = Math.max(width, f.stringWidth(n.name)+width/2);

                g.setColor(Color.white);

                g.fillOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
                   nodeWidth, nodeHeight);
                g.setColor(Color.black);
                g.drawOval(n.x-nodeWidth/2, n.y-nodeHeight/2, 
                   nodeWidth, nodeHeight);
                g.drawString(n.name, n.x-f.stringWidth(n.name)/2,
                    n.y+f.getHeight()/2);
            }
	    }
    }
}

