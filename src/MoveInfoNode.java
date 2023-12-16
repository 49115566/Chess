/*
 * This class is used to create the MoveInfoArchive
 */
public class MoveInfoNode {
	private MoveInfo info;
	private MoveInfoNode previous;
	private MoveInfoNode next;
	
	public MoveInfoNode(MoveInfo m, MoveInfoNode p, MoveInfoNode n) {
		info = m;
		previous = p;
		next = n;
	}
	
	public MoveInfo getInfo() {
		return info;
	}
	public MoveInfoNode getPrevious() {
		return previous;
	}
	public MoveInfoNode getNext() {
		return next;
	}
	public void setNext(MoveInfoNode n) {
		next = n;
	}
	public void setPrevious(MoveInfoNode p) {
		previous = p;
	}
}
