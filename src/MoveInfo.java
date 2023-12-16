
public class MoveInfo {
	int[] oPos;
	int[] nPos;
	Piece capture;
	Piece cK;
	int tSC;
	boolean wasCheck;
	
	public MoveInfo(int oX, int oY, int nX, int nY, Piece c, Piece k, int t, boolean w) {
		oPos = new int[2];
		nPos = new int[2];
		oPos[0] = oX;
		oPos[1] = oY;
		nPos[0] = nX;
		nPos[1] = nY;
		capture = c;
		cK = k;
		tSC = t;
		wasCheck = w;
	}
	
	public int getOldX() {
		return oPos[0];
	}
	public int getOldY() {
		return oPos[1];
	}
	public int getNewX() {
		return nPos[0];
	}
	public int getNewY() {
		return nPos[1];
	}
	public Piece getCapture() {
		return capture;
	}
	public Piece getCK() {
		return cK;
	}
	public void setCK(Piece c) {
		cK = c;
	}
	public int getTSC() {
		return tSC;
	}
	public boolean getWC() {
		return wasCheck;
	}
	
	public void print() {
		System.out.println((char)(oPos[0] + 'A') + "" + (oPos[1] + 1) + " to " + (char)(nPos[0] + 'A') + "" + (nPos[1] + 1));
		System.out.println("Piece captured: " + capture + ", king in check by: " + cK);
		System.out.println("wasCheck stores " + wasCheck);
	}
}
