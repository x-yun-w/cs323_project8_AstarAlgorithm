import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

class AstarNode {
	protected int config[];
	protected int gstar;
	protected int hstar;
	protected int fstar;
	protected AstarNode next;
	protected AstarNode parent;
	
	public AstarNode(int[] c, int g, int h, int f, AstarNode n, AstarNode p) {
		config = c;
		gstar = g;
		hstar = h;
		fstar = f;
		next = n;
		parent = p;	
	}
	
	public void printNode(AstarNode n, int a, FileWriter out) throws IOException {
		if(n.parent != null) {
			out.write("<" + n.parent.fstar );
			n.parent.printConfig(n.parent, a, out);
			out.write( " ::  " + n.fstar );
			n.printConfig(n, a, out);
			out.write( ">\n");
		}
			else {
			out.write("<NULL [ NULL ]  ::  " + n.fstar );
			n.printConfig(n, a, out);
			out.write( ">\n");
		}
	}
	
	public void printConfig(AstarNode n, int a, FileWriter out) throws IOException {
		if(a == 3)
			for(int i = 0; i<9; i++) {
				if(i+1%3 ==0)
					out.write(n.config[i] +"]\n");
				else if(i+1 % 3 == 1)
					out.write("[" + n.config[i]);
				else
					out.write(n.config[i]);
			}
		else if(a ==9) {
			out.write("[ ");
			for(int i = 0; i<9; i++) {
				out.write(n.config[i] + " ");
			}
			out.write("]");
		}
	}
}
public class WangX_Project8_Main{
	protected static AstarNode start;
	protected static AstarNode open;
	protected static AstarNode childlist;
	protected static int table[][];
	protected static int initconfig[];
	protected static int goalconfig[];
	protected static int dummyconfig[];
	
	public WangX_Project8_Main(){
		
	}
	
	public static int computeHstar(int[] nodec, int[] goalc, FileWriter debug) throws IOException {
		debug.write("Entering computeHstar method");
		int sum = 0;
		int i= 0;
		int p1;
		int j ;
		while(i<9) {
		    p1 = nodec[i];
			 j = 0;
		
			while(j<9) {
				if(goalc[j] == p1) {
					sum+= table[i][j];
					break;
				}
				j++;
			}
			i++;
		}
		debug.write("Leaving computeHstar method: sum = " + sum +"\n");
		return sum;
	}
	
	public static AstarNode expandChildlist(AstarNode currentN, FileWriter debug) throws IOException {
		debug.write("Entering expandChildList method\n");
		debug.write("Printing currentNode: ");
		currentN.printNode(currentN, 9,debug);
		
		AstarNode tmplist = new AstarNode(dummyconfig, 0, 0, 0, null, null);
		int i = 0;
		int zeroPosition = -1;
		while( currentN.config[i] !=0 && i<9) {
			if(currentN.config[i] != 0)
				i++;
		}
		if(i>=9) {
			debug.write("Something is wrong, currentNode does not have a zero in it\n");
			return null;
		}
		else {
			zeroPosition = i;
			debug.write("find the zero position in currentNode at position i = " + i + "\n");
		}
		int j = 0;
		
		while(j <9) {
		    if(table[zeroPosition][j] == 1) {
		        int[] newConfig = Arrays.copyOf(currentN.config, currentN.config.length);
		        AstarNode n = new AstarNode(newConfig, 999, 999, 999, null, currentN);
		        
		        n.config[zeroPosition] = currentN.config[j];
		       n.config[j] = 0;
		        if(checkAncestors(n)) {
		            n.next = tmplist.next ;
		            tmplist.next = n;
		        }
		    }
		    j++;
		}
		debug.write("Leaving expandChildList method and printing tmpList: ");
		printList( tmplist.next, debug);
		return tmplist.next;
	}

	public static void openInsert(AstarNode n, FileWriter debug) throws IOException {
	    AstarNode spot = open;
	    AstarNode prev = null;
	    while (spot != null && n.fstar > spot.fstar) {
	        prev = spot;
	        spot = spot.next;
	    }
	    n.next = spot;
	    if (prev == null) {
	        open.next = n; 
	    } else {
	        prev.next = n; 
	    }
	}

	
	public static AstarNode remove(AstarNode list) {
		AstarNode tmp = list.next;
		if(tmp !=null ) {
			list.next = list.next.next;
			tmp.next = null;
			return tmp;
		}
		else
			return null;
	}
	
	public static boolean match(int[] c1, int[] c2) {
		int m = 0;
		for(int i = 0; i<9; i++) {
			if(c1[i] == c2[i])
				m++;
			else
				return false;
		}
		return m == 9;
	}
	
	public static boolean checkAncestors(AstarNode child) {
		AstarNode t = child;
		int[] c = child.config;
		while(t.parent != null) {
			if(match(c, t.parent.config))
				return false;
			else
				t = t.parent;
		}
		return true;
				
	}
	
	public static void printList(AstarNode list, FileWriter out) throws IOException {
		AstarNode tmp = list;
		while(tmp.next != null) {
			tmp.printNode(tmp,9, out);
			tmp = tmp.next;
		}
	}
	
	public static void printSolution(AstarNode n, FileWriter out) throws IOException {
		AstarNode tmp = n;
		while(tmp.next != null) {
			tmp.printNode(tmp,3, out);
			tmp = tmp.next;
		}
	}
	
	public static void main(String[] args) throws IOException {
		File in1 = new File(args[0]);
		File in2 = new File(args[1]);
		File outFile1 = new File(args[2]);
		File outFile2 = new File(args[3]);
		File debugFile = new File(args[4]);
		FileWriter debug = new FileWriter (debugFile);
		FileWriter out1 = new FileWriter (outFile1);
		FileWriter out2 = new FileWriter (outFile2);
		
		Scanner s1 = new Scanner(in1);
		Scanner s2 = new Scanner(in2);
		int i = 0;
		initconfig = new int[9];
		goalconfig = new int[9];
		dummyconfig = new int[9];
		while(s1.hasNextInt() && s2.hasNextInt() && i<9) {
			initconfig[i] = s1.nextInt();
			goalconfig[i] = s2.nextInt();
			dummyconfig[i] = -1;
			i++;
		}
		start = new AstarNode(initconfig, 0, 9999, 9999, null, null);
		open = new AstarNode(dummyconfig, 0, 0, 0, null, null);
		AstarNode close = new AstarNode(dummyconfig, 0, 0, 0, null, null);
		childlist = new AstarNode(dummyconfig, 0, 0, 0, null, null);
		table = new int[][] {
		    {0, 1, 2, 1, 2, 3, 2, 3, 4},
		    {1, 0, 1, 2, 1, 2, 3, 2, 3},
		    {2, 1, 0, 3, 2, 1, 4, 3, 2},
		    {1, 2, 3, 0, 1, 2, 1, 2, 3},
		    {2, 1, 2, 1, 0, 1, 2, 1, 2},
		    {3, 2, 1, 2, 1, 0, 3, 2, 1},
		    {2, 3, 4, 1, 2, 3, 0, 1, 2},
		    {3, 2, 3, 2, 1, 2, 1, 0, 1},
		    {4, 3, 2, 3, 2, 1, 2, 1, 0}
		};

		start.gstar = 0;
		start.hstar = computeHstar(start.config, goalconfig, debug);
		start.fstar = start.gstar +start.hstar;
		
		openInsert(start,debug);
		AstarNode currentnode = remove(open);
		openInsert(start, debug);
		i= 0;
		while(open.next != null) {
			if(match(currentnode.config, goalconfig) == false ) {
		currentnode = remove(open);
		debug.write("this is currentNode: \n");
		currentnode.printNode(currentnode, 9, debug);
		
		if(currentnode != null && match(currentnode.config, goalconfig)) {
			out2.write("A solution is found!!\n");
			printSolution(currentnode, out2);
			System.exit(0);
		}
		childlist = expandChildlist(currentnode, debug);
		while(childlist.next != null && i<50) {
		AstarNode child = remove(childlist);
		debug.write("In main(), remove node from childList, and printing: ");
		child.printNode(child, 9, debug);
		child.gstar = currentnode.gstar +1;
		child.hstar = computeHstar(child.config, goalconfig, debug);
		child.fstar = child.gstar + child.hstar;
		child.parent = currentnode;
		if(checkAncestors(child))
			openInsert(child, debug);
		if(i<100) {
		out1.write("\nBelow is Open list: \n");
		printList(open, out1);
		i++;
		}
		}
	}
		}
		out1.write("*** Message: no solution can be found in the search! ****\n");
		out1.close();
		out2.close();
		debug.close();
	}
}