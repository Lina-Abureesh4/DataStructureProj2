package application;

public class Cursor<T> {

	private CNode<T>[] cursorArray;
	private int size;

	// arg_Construtor, takes a size as an argument and creates a cursor array with
	// the specified size
	public Cursor(int size) {
		super();
		this.cursorArray = new CNode[size];
		this.size = size;
		initialization();
	}

	// initialize all nodes with setting data of each to null
	public void initialization() {
		for (int i = 0; i < cursorArray.length - 1; i++)
			cursorArray[i] = new CNode<>(null, i + 1);
		cursorArray[cursorArray.length - 1] = new CNode<>(null, 0);
	}

	// memory allocation (get a node off the free list and return its index)
	public int malloc() {
		int p = cursorArray[0].getNext();
		cursorArray[0].setNext(cursorArray[p].getNext());
		return p;
	}

	// free method (returns a node back to the free list)
	public void free(int p) {
		cursorArray[p] = new CNode<>(null, cursorArray[0].getNext());
		cursorArray[0].setNext(p);
	}

	// this method returns a boolean value indicating whether a specific node has
	// null data or not
	public boolean isNull(int l) {
		return cursorArray[l].getData() == null;
	}

	// this method returns whether a list is empty or not
	public boolean isEmpty(int l) {
		return cursorArray[l].getNext() == 0;
	}

	// this returns whether a specific node is last in a list or not
	public boolean isLast(int p) {
		return cursorArray[p].getNext() == 0;
	}

	// create a new list in the cursor array only if there is enough space in the
	// array, i.e, there still nodes in the free list other than the zero node,
	// otherwise, print a sorry message
	public int createList() {
		int l = malloc();
		if (l == 0)
			System.out.println("Error: Out of space!!!");
		else
			cursorArray[l] = new CNode("-", 0);
		return l;
	}

	// insert a new data at the first of a list
	public void insertAtHead(T data, int l) {
		if (isNull(l)) // list not created
			return;
		int p = malloc();
		if (p != 0) {
			cursorArray[p] = new CNode<>(data, cursorArray[l].getNext());
			cursorArray[l].setNext(p);
		} else
			System.out.println("Error: Out of space!!!");
	}

	// delete the first element from a list
	public CNode<T> deleteFromHead(int l) {
		if (!isNull(l) && !isEmpty(l)) {
			int p = cursorArray[l].getNext();
			CNode<T> node = cursorArray[p];
			cursorArray[l].setNext(cursorArray[p].getNext());
			free(p);
			return node;
		}
		return null;
	}

	// get the first element in the list
	public T getHead(int l) {
		int next = cursorArray[l].getNext();
		return cursorArray[next].getData();
	}
}