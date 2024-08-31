package application;

public class CStack<T> implements Stackable<T> {

	Cursor<T> cursor;
	int list;

	// no_arg constructor
	public CStack() {
		super();
		cursor = new Cursor<>(20);
		list = cursor.createList();
	}

	// an arg_construtor, takes a size as an argument and creates a cursor array
	// with the specified size
	public CStack(int size) {
		super();
		cursor = new Cursor<>(size);
		list = cursor.createList();
	}

	// another arg_constructor, takes a cursor array as an argument and creates a
	// list for the stack
	public CStack(Cursor<T> cursor) {
		super();
		this.cursor = cursor;
		list = cursor.createList();
	}

	// push method, adds a new data at the top of the stack
	@Override
	public void push(T data) {
		cursor.insertAtHead(data, list);

	}

	// pop method, drops the top element from the stack and returns its data
	@Override
	public T pop() {
		if (!isEmpty())
			return cursor.deleteFromHead(list).getData();
		return null;
	}

	// peek method, returns the data at the top of the stack
	@Override
	public T peek() {
		if (!isEmpty())
			return cursor.getHead(list);
		return null;
	}

	// isEmpty method, returns whether the stack is empty or not
	@Override
	public boolean isEmpty() {
		return cursor.isEmpty(list);
	}

	// clear method, empties the stack
	@Override
	public void clear() {
		while (!isEmpty())
			pop();

	}
}
