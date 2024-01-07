/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 **/
public class AutoComplete implements AutoCompleteInterface {

	private DLBNode root;
	private StringBuilder currentPrefix;
	private DLBNode currentNode;
	// TODO: Add more instance variables if you need to
	private boolean newSibling = false;

	public AutoComplete() {
		root = null;
		currentPrefix = new StringBuilder();
		currentNode = null;
	}


	/**
	 * Adds a word to the dictionary in O(word.length()) time
	 *
	 * @param word the String to be added to the dictionary
	 * @return true if add is successful, false if word already exists
	 * @throws IllegalArgumentException if word is the empty string
	 **/
	@Override
	public boolean add(String word) {
		DLBNode tempNode;
		// TODO: implement this
		if (word.isBlank() || word.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (!isWord(word)) {

			currentNode = root; //always make sure current node points to root when first adding word

			for (int i = 0; i < word.length(); i ++) {
				newSibling = false;  ///makes sure sibling is false each time for each new node
				if (i == 0) {
					currentNode = addNode(root, word.charAt(0));
					if (root == null) { root = currentNode; }  ///Linked root to first new currentNode if its null (only runs on first node creation)
				} else {
					tempNode = addNode(currentNode.child, word.charAt(i));
					if (newSibling) {	///If newSibling == true --> sibling was created. s
						currentNode = tempNode;
					} else {	///No newSibling --> tempNode = new Child. Links parent and child
						currentNode.child = tempNode;
						currentNode.child.parent = currentNode;
						currentNode = currentNode.child;
					}
				}

				/*
				*  Checks if at end of word input
				*  True --> set currentNode isWord Flag to true
				*  Otherwise false
				*/
				if (endWord(i, word)) {
					currentNode.isWord = true;
					return true;
				}
			}
		}
		return false;
	}

	/**
	* Private Helper Function
	* Takes Current Node and Character
	* Returns newly created node
	**/
	private DLBNode addNode(DLBNode node, char c) {
		if (node == null) {			///If node = null --> return newly created node
			node = new DLBNode(c);
			node.size++;
			return node;
		} else if (node.data == c) {  ///Increase node size if node is equal to char value
			node.size++;
			return node;
		} else {
			node = createSibling(node, c); ///Creates new sibling node if either are false
			newSibling = true;
			node.size++;
			return node;
		}
	}

	/**
	* Private Helper Function
	* Takes Index of Add and Word
	* Checks length to see if at end of word
	**/
	private boolean endWord(int index, String word) {
		if (index == word.length() - 1) {
			return true;
		}
		return false;
	}

	/**
	* Private Helper Function
	* Takes node and char
	* Creates Sibling Node at that level
	**/
	private DLBNode createSibling(DLBNode node, char c) {
		if (node.data == c) {	/// If node is equal to the char value return that node
			return node;
		} else if (node.nextSibling == null) { /// If nodes next sibling. Make immediate new node for char
				node.nextSibling = new DLBNode(c);
				node.nextSibling.previousSibling = node;
				return node.nextSibling;
		} else {	///Else recurse over the next sibling
				return createSibling(node.nextSibling, c);
		}
	}

	/**
	* Private Helper Function
	* Takes node and char
	* Gets the sibling node for the char at that level
	* Returns null if no sibling node
	**/
	private DLBNode getSibling(DLBNode node, char c) {
		if (node != null) {
			int comp = Character.compare(node.data, c);   ///Compares node to char c
			if (comp != 0) {	////if node not equal zero recurse over next sibling
				return getSibling(node.nextSibling, c);
			} else if (comp == 0) {	 ///if comp == 0, match return said node
				return node;
			}
		}
		return null; ///No matches, return null
	}

	/**
	* Private Helper Function
	* Takes node, word, index
	* Gets the Node for the last letter of the string
	**/
	private DLBNode getWordNode(DLBNode node, String word, int index) {
		if (node != null) {
			if (node.data == word.charAt(index)) {
				if (index == word.length() - 1) {
					return node;
				} else {
					return getWordNode(node.child, word, ++index);
				}
			} else {
				return getWordNode(node.nextSibling, word, index);
			}
		}
		return null;
	}

	/**
	* Private Helper Function
	* Takes word
	* Returns true if word is already in dictionary
	* False if word doesn't exist
	**/
	private boolean isWord(String word) {
		// TODO: implement this method
		if (word == null) throw new IllegalArgumentException("isWord() Fail");
		DLBNode result = getWordNode(root, word, 0);
		if (result == null) { return false; }
		return result.isWord;
	}

	/**
	 * appends the character c to the current prefix in O(1) time. This method
	 * doesn't modify the dictionary.
	 *
	 * @param c: the character to append
	 * @return true if the current prefix after appending c is a prefix to a word in
	 *         the dictionary and false otherwise
	 */
	@Override
	public boolean advance(char c) {
		// TODO: implement this method
		if (currentPrefix.length() == 0) { //If CurrentPrefix is Empty Make Sure Start at Root
			currentPrefix.append(c);
			currentNode = root;
			if (currentNode.data == c) {
				return true;
			} else {
				currentNode = getSibling(currentNode, c); //Make currentNode the sibling of the currentNode at that level
				if (currentNode == null) {
					return false;
				}
				return true;
			}
		} else if (currentNode != null) { //Else currenNode != null meaning prefix is not a word
			currentPrefix.append(c);
			if (currentNode.child != null && currentNode.child.data == c) {
				currentNode = currentNode.child;
				return true;
			} else {
				currentNode = getSibling(currentNode.child, c); //Make currentNode sibling of node at that level for the prefix
				if (currentNode == null) {
					return false;
				}
				return true;
			}
		} else {
            		currentPrefix.append(c); //Since advance doesnt handle removal, this will append the char to keep the prefix
           		return false;  // without attempting to modify the nodes
		}
	}

	/**
	 * removes the last character from the current prefix in O(1) time. This method
	 * doesn't modify the dictionary.
	 *
	 * @throws IllegalStateException if the current prefix is the empty string
	 */
	@Override
	public void retreat() {
		// TODO: implement this method
		if (!(currentPrefix.length() == 0)) {
			currentPrefix.deleteCharAt(currentPrefix.length()-1);
			if (currentPrefix.length() == 0) {
				currentNode = null;
				return;
			}
			currentNode = getWordNode(root, currentPrefix.toString(), 0);  //Attempts to relink prefix with nodes, currentNode = null if currentPrefixx is not in dictionary
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * resets the current prefix
	 * fix to the empty string in O(1) time
	 */
	@Override
	public void reset() {
		// TODO: implement this method
		currentPrefix = new StringBuilder();
	}

	/**
	 * @return true if the current prefix is a word in the dictionary and false
	 *         otherwise
	 */
	@Override
	public boolean isWord() {
		// TODO: implement this method
		if (currentPrefix == null) throw new IllegalArgumentException("isWord() Fail");
		String preString = currentPrefix.toString();

		DLBNode result = getWordNode(root, preString, 0); //Set result to the last node of the current hall
		if (result == null) {
			return false;
		}
		return result.isWord;
	}

	/**
	 * adds the current prefix as a word to the dictionary (if not already a word)
	 * The running time is O(length of the current prefix).
	 */
	@Override
	public void add() {
		// TODO: implement this method
		if (!isWord()) {
			add(currentPrefix.toString());
		}
	}

	/**
	 * @return the number of words in the dictionary that start with the current
	 *         prefix (including the current prefix if it is a word). The running
	 *         time is O(1).
	 */
	@Override
	public int getNumberOfPredictions() {
		// TODO: implement this method
		if (currentNode != null) {
			return currentNode.size;
		}
		return 0;
	}

	/**
	 * retrieves one word prediction for the current prefix. The running time is
	 * O(prediction.length()-current prefix.length())
	 *
	 * @return a String or null if no predictions exist for the current prefix
	 */
	@Override
	public String retrievePrediction() {
		// TODO: implement this method
		if (currentNode != null) {
			String predictions = currentPrefix.toString();
			StringBuilder prediction = new StringBuilder(predictions);

			while (currentNode.child != null) {
				prediction.append(currentNode.child.data);
				if (currentNode.child.isWord) {
					currentNode = getWordNode(root, currentPrefix.toString(), 0);
					return prediction.toString(); //Returns the closest prediction to prefix
				}
				currentNode = currentNode.child;
			}
		}
		return null;
	}

	/*
	 * ============================== Helper methods for debugging.
	 * ==============================
	 */

	// print the subtrie rooted at the node at the end of the start String
	public void printTrie(String start) {
		System.out.println("==================== START: DLB Trie Starting from \"" + start + "\" ====================");
		if (start.equals("")) {
			printTrie(root, 0);
		} else {
			DLBNode startNode = getNode(root, start, 0);
			if (startNode != null) {
				printTrie(startNode.child, 0);
			}
		}

		System.out.println("==================== END: DLB Trie Starting from \"" + start + "\" ====================");
	}

	// a helper method for printTrie
	private void printTrie(DLBNode node, int depth) {
		if (node != null) {
			for (int i = 0; i < depth; i++) {
				System.out.print(" ");
			}
			System.out.print(node.data);
			if (node.isWord) {
				System.out.print(" *");
			}
			System.out.println(" (" + node.size + ")");
			printTrie(node.child, depth + 1);
			printTrie(node.nextSibling, depth);
		}
	}

	// return a pointer to the node at the end of the start String.
	private DLBNode getNode(DLBNode node, String start, int index) {
		if (start.length() == 0) {
			return node;
		}
		DLBNode result = node;
		if (node != null) {
			if ((index < start.length() - 1) && (node.data == start.charAt(index))) {
				result = getNode(node.child, start, index + 1);
			} else if ((index == start.length() - 1) && (node.data == start.charAt(index))) {
				result = node;
			} else {
				result = getNode(node.nextSibling, start, index);
			}
		}
		return result;
	}

	// The DLB node class
	private class DLBNode {
		private char data;
		private int size;
		private boolean isWord;
		private DLBNode nextSibling;
		private DLBNode previousSibling;
		private DLBNode child;
		private DLBNode parent;

		private DLBNode(char data) {
			this.data = data;
			size = 0;
			isWord = false;
			nextSibling = previousSibling = child = parent = null;
		}
	}

}
