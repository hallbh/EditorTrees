package editortrees;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

/**
 * Node class for an editor tree.
 *
 * @author Cambron Johnson, Benjamin Hall, Sophie Brusniak.
 *         Created Apr 19, 2017.
 */

public class Node {
	
	public final static Node NULL_NODE = new Node();
	private boolean secondDelete;
	
	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
				case LEFT:
					return "/";
				case SAME:
					return "=";
				case RIGHT:
					return "\\";
				default:
					throw new IllegalStateException();
			}
		}
	}
	
	public Node() {
		this.left = NULL_NODE;
		this.right = NULL_NODE;
	}
	
	public Node(char ch) {
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.balance = Code.SAME;
		this.rank = 0;
		this.element = ch;
		this.secondDelete = false;
	}
	
	// The fields would normally be private, but for the purposes of this class, 
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects
	
	char element;            
	Node left, right; // subtrees
	int rank;         // inorder position of this node within its own subtree.
	Code balance; 
	// Node parent;  // You may want this field.
	// Feel free to add other fields that you find useful

	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they work correctly
	
	public int height() {
		if (this == NULL_NODE)
			return -1;
		return logHeight();
	}
	
	/*
	 * recurse ---------
	 * if LEFT : Go left
	 * if SAME : Doesn't matter :p
	 * if RIGHT: Go right 
	 * however, this relies on the nodes having
	 * correct balance codes, which we are trying
	 * to fix using our height method
	 */
	private int logHeight() {
		if (this == NULL_NODE || isLeaf())
			return 0;
		if (this.balance == Node.Code.LEFT) 
			return 1 + this.left.logHeight();
		return 1 + this.right.logHeight();
	}

	public boolean isLeaf() {
		return this.right == NULL_NODE && this.left == NULL_NODE;
	}
	
	public class DeletionWrapper {
		
		public boolean balanced;
		public char deleted;
		public int rotCount;
		
		public DeletionWrapper() {
			this.balanced = false;
			this.deleted = '\0';
			this.rotCount = 0;
		}
		
	}
	
	public class StringWrapper {
	
		public int numCalls;
		
		public StringWrapper() {
			this.numCalls = 0;
		}
		
	}
	
	public DeletionWrapper deleteNode(int pos) {
		DeletionWrapper toReturn = new DeletionWrapper();
		boolean toLeft = false;
		// Go left
		if (pos < rank) {
			this.rank--;
			toReturn = left.deleteNode(pos);
			// if we deleted the left child and that child had a null child, fix pointers and return
			if (left.element == toReturn.deleted) {
				if (left.left == NULL_NODE || left.right == NULL_NODE) {
					if (left.isLeaf()) {
						left = NULL_NODE;
					} else if (left.right == NULL_NODE){
						left = left.left;
					} else if (left.left == NULL_NODE) {
						left = left.right;
					}
				}
			}
			if (!toReturn.balanced && !this.secondDelete) {
				toReturn.balanced = this.fixBalanceAfterDeletion(true, toReturn);
			}
			this.secondDelete = false;
		// Go right
		} else if (pos > rank) {
			toReturn = right.deleteNode(pos - (1 + rank));
			//does right have a null child?
			if (right.element == toReturn.deleted) {
				if (right.left == NULL_NODE || right.right == NULL_NODE) {
					if (right.isLeaf()) {
						right = NULL_NODE;
					} else if (right.right == NULL_NODE){
						right = right.left;
					} else if (right.left == NULL_NODE) {
						right = right.right;
					}
				}
			} 
			if (!toReturn.balanced && !this.secondDelete) {
				toReturn.balanced = this.fixBalanceAfterDeletion(false, toReturn);
			}
			this.secondDelete = false;
		} else if (this.rank == pos) {
			toReturn.deleted = this.element;
			if (this.right != NULL_NODE) {
				this.secondDelete = true;
				DeletionWrapper tempWrapper = this.deleteNode(pos+1);
				this.element = tempWrapper.deleted;
				toReturn.rotCount = tempWrapper.rotCount;
				toReturn.balanced = tempWrapper.balanced;
				if (!toReturn.balanced && !this.secondDelete) {
					toReturn.balanced = this.fixBalanceAfterDeletion(false, toReturn);
				}
			}
		}
		return toReturn;
	}
	
	/**
	 * 
	 * This method fixes the balance codes of nodes after deletion and handles rotations
	 *
	 * @param deleteLeft
	 * @return true if the tree is balanced
	 */
	private boolean fixBalanceAfterDeletion(boolean deleteLeft, DeletionWrapper rapper) {
		if (deleteLeft) {	
			switch(this.balance) {
			case LEFT:
				this.balance = Code.SAME;
				return false;
			case RIGHT:
				if (this.right.balance == Code.LEFT) {
					rapper.rotCount++;
					this.right.srr();
				}
				rapper.rotCount++;
				this.srl();
				int leftHeight = this.left.height();
				int rightHeight = this.right.height();
				
				if (leftHeight > rightHeight) {
					this.balance = Node.Code.LEFT;
					return true;
				}
				if (leftHeight < rightHeight) {
					this.balance = Node.Code.RIGHT;
					return true;
				}
				if (leftHeight == rightHeight)
					this.balance = Node.Code.SAME;
				return false;
			case SAME:
				this.balance = Code.RIGHT;
				return true;					
			}
		} else {
			switch(this.balance) {
			case LEFT:
				if (this.left.balance == Code.RIGHT) {
					rapper.rotCount++;
					this.left.srl();
				}
				rapper.rotCount++;
				this.srr();
				int leftHeight = this.left.height();
				int rightHeight = this.right.height();
				
				if (leftHeight > rightHeight) {
					this.balance = Node.Code.LEFT;
					return true;
				}
				if (leftHeight < rightHeight) {
					this.balance = Node.Code.RIGHT;
					return true;
				}
				if (leftHeight == rightHeight)
					this.balance = Node.Code.SAME;
				return false;
			case RIGHT:
				this.balance = Code.SAME;
				return false;
			case SAME:
				this.balance = Code.LEFT;
				return true;
			}
		}
		return false;//should never reach this line
	}
	
	//single rotate right
    public void srr() {
        //move head's data out
        char temp = this.element;
        //replace head's data with it's left child's
        this.element = this.left.element;
        //make the new right node for the head
        Node newRight = new Node(temp);
        //update ranks
        newRight.rank = this.rank - (this.left.rank + 1);
        this.rank = this.left.rank;
        //set the newRight's subtrees
        newRight.right = this.right;
        newRight.left = this.left.right;
        //set it as the right of the head
        this.right = newRight;
        //throw out the old left
        this.left = this.left.left;
        //update balance codes
        if(this.left != Node.NULL_NODE){
	        if(this.left.left.height() > this.left.right.height())
	        	this.left.balance = Node.Code.LEFT;
	        else if(this.left.left.height() < this.left.right.height())
	        	this.left.balance = Node.Code.RIGHT;
	        else if(this.left.left.height() == this.left.right.height())
	        	this.left.balance = Node.Code.SAME;
        }
        this.balance = Code.SAME;
        if(this.right != Node.NULL_NODE){
			if(this.right.left.height() > this.right.right.height())
	        	this.right.balance = Node.Code.LEFT;
	        else if(this.right.left.height() < this.right.right.height())
	        	this.right.balance = Node.Code.RIGHT;
	        else//(this.right.left.height() == this.right.right.height())
	        	this.right.balance = Node.Code.SAME;
		}
    }
	
	//single rotate left
	public void srl() {
		//move head's data out
		char temp = this.element;
		//replace head's data with it's right child's
		this.element = this.right.element;
		//make the new left node for the head
		Node newLeft = new Node(temp);
		//update the ranks
		newLeft.rank = this.rank;
		this.rank = this.rank + this.right.rank + 1;
		//set the newLeft's subtrees
		newLeft.left = this.left;
		newLeft.right = this.right.left;
		//set it as the left of the head
		this.left = newLeft;
		//throw out the old right
		this.right = this.right.right;
		//update balance codes
		if(this.right != Node.NULL_NODE){
			if(this.right.left.height() > this.right.right.height())
	        	this.right.balance = Node.Code.LEFT;
	        else if(this.right.left.height() < this.right.right.height())
	        	this.right.balance = Node.Code.RIGHT;
	        else//(this.right.left.height() == this.right.right.height())
	        	this.right.balance = Node.Code.SAME;
		}

	    	this.balance = Code.SAME;
	    if(this.left != Node.NULL_NODE){
	        if(this.left.left.height() > this.left.right.height())
	        	this.left.balance = Node.Code.LEFT;
	        else if(this.left.left.height() < this.left.right.height())
	        	this.left.balance = Node.Code.RIGHT;
	        else if(this.left.left.height() == this.left.right.height())
	        	this.left.balance = Node.Code.SAME;
        }
	}
	
	public int size() {
		if (this == NULL_NODE)
			return 0;
		return 1 + this.rank + this.right.size();
	}
	
	@Override
	public String toString() {
		if (this == NULL_NODE)
			return "";
		StringBuilder s = new StringBuilder();
		s.append(this.left.toString());
		s.append(this.element);
		s.append(this.right.toString());
		return s.toString();
	}
	
	//it nodes who it is
	public void copyHelper(Node e) {
		if (e.left != NULL_NODE) {
			this.left = new Node(e.left.element);
			this.left.balance = e.left.balance;
			this.left.rank = e.left.rank;
			this.left.copyHelper(e.left);
		}
		if (e.right != NULL_NODE) {
			this.right = new Node(e.right.element);
			this.right.balance = e.right.balance;
			this.right.rank = e.right.rank;
			this.right.copyHelper(e.right);
		}
	}
	/**
	 * MILESTONE 1
	 * This one asks for more info from each node. You can write it like 
	 * the arraylist-based toString() method from the
	 * BST assignment. However, the output isn't just the elements, but the
	 * elements, ranks, and balance codes. Former CSSE230 students recommended
	 * that this method, while making it harder to pass tests initially, saves
	 * them time later since it catches weird errors that occur when you don't
	 * update ranks and balance codes correctly.
	 * For the tree with node b and children a and c, it should return the string:
	 * [b1=, a0=, c0=]
	 * There are many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in
	 *         a pre-order traversal of the tree.
	 */
	public String toDebugString() {
		if (this == NULL_NODE)
			return "";
		StringBuilder s = new StringBuilder();
		s.append(this.element + "" + this.rank + "" + this.balance + ", ");
		s.append(this.left.toDebugString());
		s.append(this.right.toDebugString());
		return s.toString();
	}

	public Node getNode(int pos) {
		if (this.rank == pos) {
			return this;
		}
		if (this.rank < pos) {
			return this.right.getNode(pos - (rank+1));
		}
		return this.left.getNode(pos);
	}

	public void stringConstructorHelper(String s) {
		// TODO Auto-generated method stub.
		int indexOfRoot = s.length()/2;
		
		// Check if node is a leaf
		if (s.length() == 1) {
			this.element = s.charAt(0);
			this.balance = Node.Code.SAME;
			this.rank = 0;
			return;
		}
		
		this.element = s.charAt(indexOfRoot);
		
		String leftString = s.substring(0, indexOfRoot);
		String rightString = s.substring(indexOfRoot + 1);
		
		this.rank = leftString.length();
		
		if(Math.floor(Math.log(leftString.length())/Math.log(2)) > Math.floor(Math.log(rightString.length())/Math.log(2))){
			//height of left side is bigger
			this.balance = Node.Code.LEFT;
		}
		else if(Math.floor(Math.log(leftString.length())/Math.log(2)) < Math.floor(Math.log(rightString.length())/Math.log(2))){
			//height of right side is bigger
			this.balance = Node.Code.RIGHT;
		}
		else{
			this.balance = Node.Code.SAME;
		}
		
		if (leftString.length() != 0) {
			this.left = new Node();
			this.left.stringConstructorHelper(leftString);
		}
		if (rightString.length() != 0) {
			this.right = new Node();
			this.right.stringConstructorHelper(rightString);
		}
		
	}

	public boolean rebalanceAfterConcatenation(boolean isLeft, Node q, int vSize) {
		if(this == q){
			this.rank = this.left.size();
			return false;
		}
		if(isLeft){
			this.rank += vSize + 1;
			boolean balanced = this.left.rebalanceAfterConcatenation(isLeft, q, vSize);
			if(!balanced){
				switch(this.balance){
				case LEFT:
					this.srr();
					return true;
				case RIGHT:
					this.balance = Code.SAME;
					return true;
				case SAME:
					this.balance = Code.LEFT;
					return false;
				default:
					break;
				}
			}
		}
		else{
			boolean balanced = this.right.rebalanceAfterConcatenation(isLeft, q, vSize);
			if(!balanced){
				switch(this.balance){
				case LEFT:
					this.balance = Code.SAME;
					return true;
				case RIGHT:
					this.srl();
					return true;
				case SAME:
					this.balance = Code.RIGHT;
					return false;
				default:
					break;
				}
			}
		}
		return false;
	}
	
}