package functions;

import java.util.*;
import java.io.IOException;
import java.math.*;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;



public class Node {
	private final int ID;
	private int fExp;
	private int gExp;
	private int divisor;
	private boolean encrypt;
	private boolean useBI;
	private Map<Integer,Node> Nodes;
	protected MessageTrackCheck message;
	private String augmentedMessage;
	protected NodeTransitionFunction fFunction;
	private NodeTransitionFunction gFunction;
	private boolean hasTransmitted;
	private boolean isCorrupt;
	private Integer lastSenderID;
	private boolean isGuessed;
	private boolean encrypted;
	private Key publicKey;
	private KeyPair encryptedKey;

	public Node(Integer n, Integer e, Integer d, Integer K, Boolean encrypt, Boolean useBI, Map<Integer,Node> m, MessageTrackCheck t) {
		// CONSTRUCTOR: 
		//      n is node ID,
		//      e is the exponent for the function f()
		//      d is the exponent for the function g()
		//      K is the divisor in f() and g()
		//      encrypt is true if messages are encrypted, false otherwise
		//      useBI is true if BigInteger should be used for NodeTransitionFunction, false otherwise
		//      m is a non-null map of node IDs to node objects
		//      t is an instance of MessageTrackCheck

		this.ID = n;
		this.fExp = e;
		this.gExp = d;
		this.divisor = K;
		this.encrypt = encrypt;
		this.useBI = useBI;
		this.Nodes = m;
		this.message = t;
		this.gFunction = new NodeTransitionFunction(gExp,divisor);
		this.isCorrupt = false;
		this.isGuessed = false;
		this.lastSenderID = null;
		this.encrypted =encrypt;
		if(this.encrypted)
			try {
				Security.addProvider(new BouncyCastleProvider());
				KeyPairGenerator generator = KeyPairGenerator.getInstance("ElGamal","BC");
				SecureRandom sec = new SecureRandom();
				generator.initialize(256,sec);
				this.encryptedKey = generator.generateKeyPair();
			}	catch(Exception exc) {
				exc.printStackTrace();
			}
			
		}
	
	public Boolean hasMsgEncryption() {
		// PRE: -
		// POST: Returns true if messages are encrypted, false otherwise

		// TODO
		
		return encryptedKey!=null;
	}
	
	public Boolean isDestinationNode(String msg) {
		// PRE: msg is an augmented message (i.e. containing 3 characters at the end indicating destination node)
		// POST: Returns true if this is the destination node, false otherwise
		//       E.g. For node 6, will return true for "hello006"

		// TODO
		String msgID = msg.substring(msg.length()-3,msg.length());
		int id = Integer.valueOf(msgID);
		return id==this.ID;
	}


	public Boolean transmittedMessage() {
		// PRE: -
		// POST: Return true if this node has transmitted a message, false otherwise

		// TODO
		
		return hasTransmitted;
	}

	public void sendMsgToNode(Node n, String msg, Integer r, NodeTransitionFunction f) {
		// PRE: n is a non-null node,
		//      msg is a message,
		//      r is the current value of r from the forward transition function.
		//      f is the forward transition function
		// POST: invokes receiveMsgFromNode on node n

		// TODO
		this.hasTransmitted = true;
		n.receiveMsgFromNode(msg,this.getID(),r,f);
	}
	
	public void sendMsgToNode(Node n, String msg, BigInteger r, NodeTransitionFunction f) {
		// PRE: n is a non-null node,
		//      msg is a message,
		//      r is the current value of r from the forward transition function.
		//      f is the forward transition function
		// POST: invokes receiveMsgFromNode on node n

		this.hasTransmitted = true;
		n.receiveMsgFromNode(msg,this.getID(),r,f);
	}
	
	public void receiveMsgFromNode(String msg, Integer id, Integer r, NodeTransitionFunction f) {
		// PRE: msg is a message, 
		//      id is the ID of the sending node,
		//      r is the current value of r from the forward transition function,
		//      f is the forward transition function
		// POST: If this is the destination node, stop;
		//       otherwise, send the message onwards.
		//       Add ID of current (receiving) node to local MessageTrackCheck

		// TODO
		fFunction = f;
		this.message.add(this.ID);
		
		this.lastSenderID = id;
		if(this.isDestinationNode(msg)) {
			augmentedMessage = msg;
			return;
		}
		else {
			int nextNodeID = this.fFunction.apply(r) % Nodes.size();    //  v(i+1) = r(i+1) mod N  = f(r) mod N
			Node n = Nodes.get(nextNodeID);
			this.sendMsgToNode(n,msg,this.fFunction.apply(r),this.fFunction);
		}
	}

	public void receiveMsgFromNode(String msg, Integer id, BigInteger r, NodeTransitionFunction f) {
		// PRE: msg is a message, 
		//      id is the ID of the sending node,
		//      r is the current value of r from the forward transition function,
		//      f is the forward transition function
		// POST: If this is the destination node, stop;
		//       otherwise, send the message onwards.
		//       Add ID of current (receiving) node to local MessageTrackCheck

		// TODO
		fFunction = f;
		this.message.add(this.ID);
		this.lastSenderID = id;
		if(this.isDestinationNode(msg)) {
			augmentedMessage = msg;
			return;
		}
		else {
			BigInteger nextNodeID = this.fFunction.apply(r).mod(BigInteger.valueOf(Nodes.size()));    //  v(i+1) = r(i+1) mod N  = f(r) mod N
			Node n = Nodes.get(nextNodeID.intValue());
			this.sendMsgToNode(n,msg,this.fFunction.apply(r),this.fFunction);
		}
	}
	
	public String getMsg() {
		// PRE: -
		// POST: Returns the current received (non-augmented) message, null if no received message

		// TODO

		return augmentedMessage.substring(0,augmentedMessage.length()-3);
	}

	/*
	*/
	

	/*
	 * Initiator 
	 */
	
	public NodeTransitionFunction createForwardNodeTransitionFunction() {
		// PRE: -
		// POST: Creates a NodeTransitionFunction using this node's public function f() with parameters e, K
		
		// TODO
		NodeTransitionFunction temp = new NodeTransitionFunction(fExp,divisor);
		fFunction = temp;
		return fFunction;
	}
	
	public String addDestIDToMsg(String msg, Integer v) {
		// PRE: msg is a message, v is a node ID
		// POST: Returns a string that concatenates v as a 3-character string to the end of msg.
		//       E.g. for msg="hello", v=6, returns "hello006"
		
		// TODO
		String temp = Integer.toString(v);
		if(temp.length() == 1)
			return msg + "00" + (Integer.toString(v));
		if(temp.length()==2)
			return msg + "0" + (Integer.toString(v));
		return msg + (Integer.toString(v));
	}

	public Integer firstRForInitiatingMessage(Integer k, Integer v) {
		// PRE: v is destination node ID, k is number of steps
		// POST: Uses the trapdoor function inverse, applied to destination node v with number of steps k, to calculate the node path;
		//       returns value of r that determines first step on node path

		// TODO
		this.createForwardNodeTransitionFunction();
		int temp = v;
		for(int i = 0; i<k-1;i++) {
			temp = gFunction.apply(temp);
		}
		return temp;
		
	}

	public BigInteger firstRForInitiatingMessage(Integer k, BigInteger v) {
		// PRE: v is destination node ID, k is number of steps as a BigInteger
		// POST: Uses the trapdoor function inverse, applied to destination node v with number of steps k, to calculate the node path;
		//       returns value of r that determines first step on node path

		// TODO

		this.createForwardNodeTransitionFunction();
		BigInteger temp = v;
		for(int i = 0; i<k-1;i++) {
			temp = gFunction.apply(temp);
		}
		return temp;
		}
	

	public void initiateMessage(String msg, Integer k, Integer v) {
		// PRE: msg is an original message, v is destination node ID, k is number of steps
		// POST: Adds destination ID to msg; 
		//       sends augmented msg to the next node, as determined by firstRForInitiatingMessage(k, v), 
		//       along with forward transition function
		String message = addDestIDToMsg(msg,v);
		int nextNodeID = firstRForInitiatingMessage(k,v)%Nodes.size();;
		Node n = Nodes.get(nextNodeID);	
		this.message.add(this.ID);
		if(!useBI) sendMsgToNode(n,message,fFunction.apply(fFunction.apply(v)),fFunction);
		else sendMsgToNode(n,message,BigInteger.valueOf(fFunction.apply(fFunction.apply(v))),fFunction);;
	}
	
	private void setGuess() {
		this.isGuessed = true;
	}
	
	public Integer getID() {
		// PRE: -
		// POST: Returns node ID

		// TODO
		
		return this.ID;
	}
	
	public Integer getE() {
		// PRE: -
		// POST: Returns value of e in this node's function f()
		
		// TODO

		return this.fExp;
	}
	
	public Integer getK() {
		// PRE: -
		// POST: Returns value of K in this node's function f()
		
		// TODO

		return this.divisor;
	}
	

	// guess initiator
	
	public Integer guessInitiator() {
		// PRE: -
		// POST: Guesses a node to be the initiator if it can track back through corrupted nodes
		//       along two separate paths
		//      returns this node ID, or -1 if no guess

		// TODO

		LinkedList<Node> corruptNodes = new LinkedList();
		Node temp = this;
		while(true) {
			corruptNodes.add(temp);
			if(temp.lastSender() == -1) {
			break;
			}
			temp = Nodes.get(temp.lastSender());
		} 
		List<Node> path = DFS(this.getID(), corruptNodes);
		return path.get(path.size()-1).getID();
	}
	 public List<Node> DFS(int id , List<Node> l){
		 
		 List<Node> resultList = new Vector<Node>();
		 this.setGuess();
		 Iterator<Node> nIt = l.iterator();
		 List<Node> resList = new LinkedList<Node>();
		 while(nIt.hasNext()) {			 
			 Node nextNode = nIt.next();
			 nIt.remove();
			 if(!nextNode.isGuessed) {
				 List<Node> tmpResList= DFS(nextNode.getID(),l);
				 resList.addAll(tmpResList);
			 }
		 }
		 resList.add(0,Nodes.get(id));
		 return resList;
		 }


	
	

	public void setCorrupt() {
		// PRE: -
		// POST: Sets a node to be corrupt
		
		this.isCorrupt = true;
	}
	
	public Integer lastSender() {
		// PRE: -
		// POST: If a node is not corrupt, returns -1;
		//       if a node is corrupt, returns ID of node that last sent it a message,
		//       -1 if it has not been sent any messages

		// TODO

		if(!this.isCorrupt) return -1;
		else return this.lastSenderID;
	}
	

	//
	
	
	public String basicHashFunction (String m) {
		// PRE: -
		// POST: Sums the numeric value of each character, using Character.getNumericValue(), 
		//       takes mod 100 of the total; returns as a 3-char string
		
		// TODO
		
		int sum = 0;
		for(int i = 0 ; i<m.length();i++) {
			sum+=Character.getNumericValue(m.charAt(i));
		}
		sum%=100;
		return Integer.toString(sum);
	}
	
	public Key getPublicKey() {
		// PRE:
		// POST: Returns the node's public key (null if hasMsgEncryption() is false)
		
		// TODO
		if(!this.hasMsgEncryption())
			return null;
		
		this.publicKey = this.encryptedKey.getPublic();
		return this.publicKey;
	}

	public byte[] encryptedMsg(String msg, Key chosenPubKey){
		// PRE: msg is a message, chosenPubKey is a public key
		// POST: Returns msg encrypted with chosenPubKey (null if hasMsgEncryption() is false or chosenPubKey is null)
		
		// TODO

		if(chosenPubKey == null || !hasMsgEncryption()) return null; 
		this.encrypted = true;
		byte[] encryptedMessage = msg.getBytes();
		try {
		Cipher cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
		cipher.init(Cipher.ENCRYPT_MODE,chosenPubKey);
		return cipher.doFinal(encryptedMessage);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public byte[] decryptedMsg(byte[] msg) {
		// PRE: msg is an encrypted message as a byte array
		// POST: Returns msg decrypted using node's private key (null if hasMsgEncryption() is false)

		// TODO

		if(encryptedKey == null) return null;
		try {
		Cipher cipher = Cipher.getInstance("ElGamal/None/NoPadding", "BC");
		cipher.init(Cipher.DECRYPT_MODE,this.encryptedKey.getPrivate());
		return cipher.doFinal(msg);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		}

	public String addCheckToMsg(String msg) {
		// PRE: msg is a message
		// POST: Returns a string that concatenates the basicHashFunction of msg
		//       E.g. for msg="hello", v=6, returns "hello006"
		
		// TODO

		return msg + basicHashFunction(msg);
	}


	public Boolean isDestinationNode(byte[] msg) {
		// PRE: msg is an augmented encrypted message (i.e. containing 3 check digits at the end)
		// POST: Returns true if this is the destination node, false otherwise
		//       Determines if this is the destination by decrypting msg, 
		//       and comparing the hashed decrypted core msg (i.e. up to the last 3 characters) 
		//       against the last 3 chars of the decrypted msg 
		
		// TODO
		String decryptedMessageID = new String(this.decryptedMsg(msg)).substring(new String(this.decryptedMsg(msg)).length() - 3);
		String hashedFunction = basicHashFunction(new String(this.decryptedMsg(msg)).substring(0, new String(this.decryptedMsg(msg)).length() - 3));
		
		return decryptedMessageID.equals(hashedFunction);
	}
	 
	public static void main(String[] args) {
		
		Map<Integer,Node> g = new HashMap();
		for(int i = 0 ; i<20 ; i++) g.put( i, new Node(1, 3, 7, 33, Boolean.FALSE, Boolean.FALSE, g, new MessageTrackCheck(1)));
	
		
		Node n = new Node(1, 3, 7, 33, Boolean.FALSE, Boolean.FALSE, g, new MessageTrackCheck(1));
	}

}
