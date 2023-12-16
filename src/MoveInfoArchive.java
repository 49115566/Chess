/*
 * This keeps track of information from all the previous moves.
 * It is used to create Chess::threefoldRepetition.
 * It may also be used to create an undo feature.
 */
public class MoveInfoArchive {
	private MoveInfoNode head;
	private MoveInfoNode current;
	private MoveInfoNode foot;
	private ChessBoard board;
	
	public MoveInfoArchive(ChessBoard b) {
		head = new MoveInfoNode(null, null, null);
		foot = new MoveInfoNode(null, null, head);
		head.setPrevious(foot);
		current = foot;
		board = b;
	}
	
	
	public MoveInfo getCurrent() {
		return current.getInfo();
	}
	
	public void moveToHead() {
		current = head;
	}
	public void moveToFoot() {
		current = foot;
	}
	public void moveToPrevious() {
		current = current.getPrevious();
	}
	public void moveToNext() {
		current = current.getNext();
	}
	
	public boolean isPrevious() {
		return current.getPrevious() != foot;
	}
	public boolean isCurrent() {
		return current.getInfo() != null;
	}
	public boolean isNext() {
		return current.getNext() != head;
	}
	
	
	public void addNode(MoveInfo m) {
		//Java automatically deletes all info above current
		MoveInfoNode temp = new MoveInfoNode(m, current, head);
		current.setNext(temp);
		head.setPrevious(temp);
	}
	
	
	public boolean hasCapture() {
		return current.getInfo().getCapture().getType() != ' ';
	}
	public boolean movedPawn() {
		return board.getPiece(current.getInfo().getNewX(), current.getInfo().getNewY()).getType() == 'p';
	}
	
	public void print() {
		MoveInfoNode temp = current;
		System.out.print("FOOT");
		moveToFoot();
		while(isNext()) {
			System.out.print("->");
			moveToNext();
			current.getInfo().print();
		}
		System.out.println("->HEAD");
		System.out.println();
		current = temp;
	}
}
