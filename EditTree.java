package editortrees;

import java.util.Stack;

/**
 * Height balanced tree.
 *
 * @author Cambron Johnson, Benjamin Hall, Sophie Brusniak.
 *         Created Apr 19, 2017.
 */

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	private int rotationCount;
	
	/**
	 * MILESTONE 1
	 * Construct an empty tree
	 */
	public EditTree() {
		this.root = Node.NULL_NODE;
		this.rotationCount = 0;
	}

	/**
	 * MILESTONE 1
	 * Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
		this.rotationCount = 0;
	}

	/**
	 * MILESTONE 2
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		if(e.root != Node.NULL_NODE) {
			this.root = new Node(e.root.element);
			this.root.balance = e.root.balance;
			this.root.rank = e.root.rank;
			this.rotationCount = e.rotationCount;
			this.root.copyHelper(e.root);
		} else {
			this.root = Node.NULL_NODE;
			this.rotationCount = 0;
		}
	}
	
	/**
	 * MILESTONE 3
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		int indexOfRoot = s.length()/2;
		this.root = new Node(s.charAt(indexOfRoot));
		
		String leftString = s.substring(0, indexOfRoot);
		String rightString = s.substring(indexOfRoot + 1);
		
		root.rank = leftString.length();
		
		if(Math.floor(Math.log(leftString.length())/Math.log(2)) > Math.floor(Math.log(rightString.length())/Math.log(2))){
			//height of left side is bigger
			this.root.balance = Node.Code.LEFT;
		}
		else if(Math.floor(Math.log(leftString.length())/Math.log(2)) < Math.floor(Math.log(rightString.length())/Math.log(2))){
			//height of right side is bigger
			this.root.balance = Node.Code.RIGHT;
		}
		else{
			this.root.balance = Node.Code.SAME;
		}
		this.root.left = new Node();
		this.root.right = new Node();
		root.left.stringConstructorHelper(leftString);
		root.right.stringConstructorHelper(rightString);
	}
	
	/**
	 * MILESTONE 1
	 * returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since tree was created.
	 */
	public int totalRotationCount() {
		return this.rotationCount; // replace by a real calculation.
	}

	/**
	 * MILESTONE 1
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		return this.root.toString(); // replace by a real calculation.

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
		if (this.root == Node.NULL_NODE)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(this.root.toDebugString());
		String s = sb.substring(0, sb.length() - 2);
		sb = new StringBuilder();
		sb.append(s + "]");
		return sb.toString();
	}

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.
		if (this.root == Node.NULL_NODE) 
			this.root = new Node(ch);
		else
			addHelper(this.root, ch);
	}
	
	public boolean addHelper(Node node, char c){ //boolean to check when balance codes are updated
		if (node == Node.NULL_NODE)
			return false;
		boolean balanced = false;
		//before recursive call, check if we're at the place we want to add stuff
		if (node.right == Node.NULL_NODE) { //we have found the end and will add
			node.right = new Node(c);
			switch (node.balance) {
			case LEFT :
				node.balance = Node.Code.SAME;
				return true;
			case RIGHT :
				//should never happen
				break;
			case SAME :
				node.balance = Node.Code.RIGHT;
				break;
			default:
				break;
			}
			return false;
		} 
		//recursive call
		balanced = addHelper(node.right, c);
		//after recursive call update balance codes
		if(!balanced) {
			switch (node.balance) {
			case LEFT :
				node.balance = Node.Code.SAME;
				return true;
			case RIGHT :
				//rotate
				this.rotationCount++;
				node.srl();
				return true;
			case SAME :
				node.balance = Node.Code.RIGHT;
				return false;
			default:
				return false;
			}
		}
		return true;
	}
	

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos > this.root.size())
			throw new IndexOutOfBoundsException();
		if (this.root == Node.NULL_NODE)
			this.root = new Node(ch);
		else
			addIndexHelper (ch, pos, this.root);
	}
	
	public boolean addIndexHelper(char c, int pos, Node node) {
		if (node == Node.NULL_NODE)
			return false;
		boolean balanced = false;
		//before recursive call, check if we're at the place we want to add stuff
		if (pos <= node.rank) {
			node.rank++;//going left, so rank will increase.
			if (node.left == Node.NULL_NODE) {
				node.left = new Node(c);
				switch (node.balance) {
				case LEFT :
					//should never happen
					break;
				case RIGHT :
					node.balance = Node.Code.SAME;
					return true;
				case SAME :
					node.balance = Node.Code.LEFT;
					break;
				default:
					break;
				}
				return false;
			}
			balanced = addIndexHelper(c, pos, node.left);
			
			//after recursive call update balance codes
			if(!balanced) {
				switch (node.balance) {
				case LEFT :
					//rotate
					if (node.left.balance == Node.Code.RIGHT) {
						this.rotationCount++;
						node.left.srl();
					}
					this.rotationCount++;
					node.srr();
					return true;
				case RIGHT :
					node.balance = Node.Code.SAME;
					return true;
				case SAME :
					node.balance = Node.Code.LEFT;
					return false;
				default:
					return false;
				}
			}
		} else {
			if (node.right == Node.NULL_NODE) {
				node.right = new Node(c);
				switch (node.balance) {
				case LEFT :
					node.balance = Node.Code.SAME;
					return true;
				case RIGHT :
					//should never happen
					break;
				case SAME :
					node.balance = Node.Code.RIGHT;
					break;
				default:
					break;
				}
				return false;
			}
			balanced = addIndexHelper(c, pos - (node.rank + 1), node.right);
			
			//after recursive call update balance codes
			if(!balanced) {
				switch (node.balance) {
				case LEFT :
					node.balance = Node.Code.SAME;
					return true;
				case RIGHT :
					//rotate
					if (node.right.balance == Node.Code.LEFT) {
						this.rotationCount++;
						node.right.srr();
					}
					this.rotationCount++;
					node.srl();
					return true;
				case SAME :
					node.balance = Node.Code.RIGHT;
					return false;
				default:
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * MILESTONE 1
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos >= this.root.size())
			throw new IndexOutOfBoundsException();
		if (this.root == Node.NULL_NODE)
			throw new IndexOutOfBoundsException();
		return getHelper(pos, this.root);
	}
	
	public char getHelper(int pos, Node node) {
		if (pos == node.rank) {
			return node.element;
		}
		if (pos < node.rank) {
			return getHelper(pos, node.left);
		}
		if (pos > node.rank) {
			return getHelper(pos - (node.rank + 1), node.right);
		}
		return '%';//should never reach this line
	}

	/**
	 * MILESTONE 1
	 * @return the height of this tree CHANGEME
	 */
	public int height() {
		if (this.root == Node.NULL_NODE) {
			return -1;
		}
		return this.root.height(); // replace by a real calculation.
	}

	/**
	 * MILESTONE 2
	 * @return the number of nodes in this tree 
	 */
	public int size() {
		return this.root.size(); // replace by a real calculation.
	}
	
	
	/**
	 * MILESTONE 2
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		
		if (pos < 0 || pos >= this.root.size()) // makes sure pos parameter is valid
			throw new IndexOutOfBoundsException();
		
		if (this.root.right == Node.NULL_NODE && this.root.left != Node.NULL_NODE) {
			if (pos == 0) {
				char deleted = this.root.left.element;
				this.root.left = Node.NULL_NODE;
				return deleted;
			} 
			char deleted = this.root.element;
			this.root = this.root.left;
			return deleted;
		} else if (this.root.isLeaf()) {
			char deleted = this.root.element;
			this.root = Node.NULL_NODE;
			return deleted;
		}
		
		Node.DeletionWrapper wrapper = root.deleteNode(pos);
		this.rotationCount += wrapper.rotCount;
		return wrapper.deleted;
	}
	

	/**
	 * MILESTONE 3, EASY
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		if (pos > this.size() - 1 || pos < 0 || pos + length > this.size()) {
			throw new IndexOutOfBoundsException();
		}
		StringBuilder sb = new StringBuilder();
		for(int i = pos; i < pos + length; i++) {
			sb.append(this.root.getNode(i).element);
		}
		return sb.toString();
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE PAPER REFERENCED IN SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		char toReplace = '\0';
		if(this == other){
			throw new IllegalArgumentException();
		}
		if (other.root == Node.NULL_NODE) {
			return;
		}
		if (this.root == Node.NULL_NODE) {
			this.root = other.root;
			other.root = Node.NULL_NODE;
			return;
		}
		if (other.root.isLeaf()) {
			this.add(other.root.element);
			other.root = Node.NULL_NODE;
			return;
		}
		if (this.height() >= other.height()) {
			toReplace = other.delete(0);
		} else {
			toReplace = this.delete(this.size() - 1);
		}
		paste(this, new Node(toReplace), other);
		other.root = Node.NULL_NODE;
	}
	
	public void paste(EditTree t, Node q, EditTree v){
		Node p = t.root;
		int tHeight = t.height();
		int vHeight = v.height();
		int pHeight;
		Node parent = Node.NULL_NODE;
		
		if(tHeight >= vHeight){
			p = t.root;
			pHeight = tHeight;
			while(pHeight - vHeight >= 1){
				if(p.balance == Node.Code.LEFT){
					pHeight =- 2;
				}
				else{
					pHeight =- 1;
				}
				parent = p;
				p = p.right;
			}
			q.left = p;
			q.right = v.root;
			if(pHeight == vHeight){
				q.balance = Node.Code.SAME;
			}
			else{
				q.balance = Node.Code.LEFT;
			}
			if(parent != Node.NULL_NODE){
				parent.right = q;
			} else {
				t.root = q;
			}
			q.rank = p.size();
			t.root.rebalanceAfterConcatenation(false, q, v.size());
		}
		else{
			p = v.root;
			pHeight = vHeight;
			while(pHeight - tHeight >= 1){
				if(p.balance == Node.Code.RIGHT){
					pHeight =- 2;
				}
				else{
					pHeight =- 1;
				}
				parent = p;
				p = p.left;
			}
			q.right = p;
			q.left = t.root;
			if(pHeight == vHeight){
				q.balance = Node.Code.SAME;
			}
			else{
				q.balance = Node.Code.RIGHT;
			}
			if(parent != Node.NULL_NODE){
				parent.left = q;
			}
			t.root = v.root;
			t.root.rebalanceAfterConcatenation(true, q, v.size());
		}
	}
	
	/**
	 * MILESTONE 3: DIFFICULT
	 * This operation must be done in time proportional to the height of this
	 * tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		if (pos == 0) {
			EditTree toReturn = new EditTree();
			toReturn.root = this.root;
			this.root = Node.NULL_NODE;
			return toReturn;
		}
		
		pos--;
		
		Stack<Node> nodeStack = new Stack<Node>(); //path of nodes to node at pos
		EditTree returnTree = new EditTree();
		
		Node currentNode = this.root;
		int currentPos = this.root.rank + 1;
		while(true){
			if(currentNode.rank < pos){
				nodeStack.push(currentNode);
				pos -= (currentNode.rank + 1);
				currentNode = currentNode.right;
			}
			else if(currentNode.rank > pos){
				nodeStack.push(currentNode);
				currentNode = currentNode.left;
			}
			else{
				nodeStack.push(currentNode);
				break;
			}
		}
		returnTree = splitTree(nodeStack);
		
		return returnTree; // replace by a real calculation.
	}
	
	public EditTree splitTree(Stack<Node> nodeStack){
		Node currentNode = nodeStack.pop();
		Node s = currentNode.left;
		Node t = currentNode.right;
		Node child;
		EditTree tTree = new EditTree();
		EditTree sTree = new EditTree();
		tTree.root = t;
		sTree.root = s;
		EditTree nullTree = new EditTree();
		sTree.add(currentNode.element);
		while(!nodeStack.isEmpty()){
			child = currentNode;
			currentNode = nodeStack.pop();
			if (child == currentNode.right) {
				EditTree left = new EditTree();
				left.root = currentNode.left;
				paste(left, currentNode, sTree);
				sTree = left;
			} else {
				EditTree right = new EditTree();
				right.root = currentNode.right;
				paste(tTree, currentNode, right);
			}
		}
		this.root = sTree.root;
		return tTree;
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length)
			throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete"
							: "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3
	 * Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return find(s, 0);
	}

	/**
	 * MILESTONE 3
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		if (s.equals("")) {
			return 0;
		}
		String searchString = this.toString();
		char[] charArray = searchString.toCharArray();
		int currIndex = 0;
		for(int i = pos; i < charArray.length; i++) {
			if (s.charAt(currIndex) == charArray[i]) {
				 currIndex++;
			} else {
				i -= currIndex;
				currIndex = 0;
			}
			if (currIndex == s.length()) {
				return i - (currIndex - 1);
			}
		}
		return -1;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}
}
