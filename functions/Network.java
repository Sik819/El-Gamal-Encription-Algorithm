package functions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.math.*;

/*
 * 
 * For Distinction-level tasks only
 * 
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
*/



public class Network {


	Map<Integer,Node> lookup;
	MessageTrackCheck track;
	
	
	
	public Network() {
		lookup = new HashMap<Integer,Node>();
	}
	
	public Vector<Vector<Integer> > readNodesFromFile(String fInName) throws IOException {
		// Reads list of node definitions from a file, four ints per line 
		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<Vector<Integer> > nodeDefs = new Vector<Vector<Integer> >();
		
		while ((s = fIn.readLine()) != null) {
			Vector<Integer> nodeDef = new Vector<Integer>();
			java.util.StringTokenizer line = new java.util.StringTokenizer(s);
			while (line.hasMoreTokens()) {
				nodeDef.add(Integer.parseInt(line.nextToken()));
			}
			nodeDefs.add(nodeDef);
		}
		fIn.close();
		
		return nodeDefs;
	}

	public Vector<String> readMessagesFromFile(String fInName) throws IOException {
		// Reads list of node definitions from a file, four ints per line 
		BufferedReader fIn = new BufferedReader(
							 new FileReader(fInName));
		String s;
		Vector<String> msgs = new Vector<String>();
		
		while ((s = fIn.readLine()) != null) {
			msgs.add(s);
		}
		fIn.close();
		
		return msgs;
	}


	

	public static void main(String[] args) {
		Network net = new Network();
		net.track = new MessageTrackCheck(3);
		String nodeFileInName = "/home/madras/teaching/19comp225/ass/data/nodedef1.in";
		String msgFileInName = "/home/madras/teaching/19comp225/ass/data/msg0.in";
		
	}
}
