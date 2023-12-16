

public class Team {
	private Piece[] pieces;
	private ChessBoard board;
	private int[] last;
	
	public Team(ChessBoard b, Color t) {
		if(t != Color.neither) {
			board = b;
			
			int y = 0, dy = 1;
			if(t == Color.black) {y = 7; dy = -1;}
			pieces = new Piece[16];
			pieces[0] = board.getPiece(4, y);
			pieces[0].setTN(0);
			pieces[1] = board.getPiece(3, y);
			pieces[1].setTN(1);
			for(int i = 0; i < 3; i++) {
				pieces[2 + 2*i] = board.getPiece(i, y);
				pieces[2 + 2*i].setTN(2 + 2*i);
				pieces[3 + 2*i] = board.getPiece(7 - i, y);
				pieces[3 + 2*i].setTN(3 + 2*i);
			}
			for(int i = 0; i < 8; i++) {pieces[8 + i] = board.getPiece(i, y + dy); pieces[8 + i].setTN(8 + i);}
		}
		last = new int[] {1, 3, 5, 7, 15};
	}
	public Team(Team t) {
		board = t.board;
		
		pieces = new Piece[16];
		for(int i = 0; i < 16; i++) pieces[i] = new Piece(t.pieces[i]);
		
		//This could lead to future errors.
		//If I copy a team and undo to the point that the last[] is changed,
		//the change will be permanent
		last = t.last;
	}

	public Piece getKing() {
		return pieces[0];
	}
	public Piece[] getQueens() {
		Piece[] p = new Piece[last[0]];
		for(int i = 0; i < last[0]; i++) p[i] = pieces[last[0] - i];
		return p;
	}
	public Piece[] getRooks() {
		Piece[] p = new Piece[last[1] - last[0]];
		for(int i = 0; i < last[1] - last[0]; i++) p[i] = pieces[last[1] - i];
		return p;
	}
	public Piece[] getKnights() {
		Piece[] p = new Piece[last[2] - last[1]];
		for(int i = 0; i < last[2] - last[1]; i++) p[i] = pieces[last[2] - i];
		return p;
	}
	public Piece[] getBishops() {
		Piece[] p = new Piece[last[3] - last[2]];
		for(int i = 0; i < last[3] - last[2]; i++) p[i] = pieces[last[3] - i];
		return p;
	}
	public Piece[] getPawns() {
		Piece[] p = new Piece[last[4] - last[3]];
		for(int i = 0; i < last[4] - last[3]; i++) p[i] = pieces[last[4] - i];
		return p;
	}
	
	public boolean canMove() {
		int[][] s = pieces[1].rescueSpaces();
		if(pieces[0].canMove(s)) return true;
		for(int i = pieces.length - 1; i > 0; i--)
			if(pieces[i].canMove(s)) return true;
		return false;
	}
	public boolean isIgnorePawns(Team t) {
		for(int i = 7; i >= 0; i--) {
			if(!pieces[i].isITC(t.pieces[i])) return false;
		}
		return true;
	}
	
	public void upgraded(int tn) {
		int i = 4;
		switch(pieces[tn].getType()) {
		case 'q': i = 0; break;
		case 'r': i = 1; break;
		case 'n': i = 2; break;
		case 'b': i = 3; break;
		default: return;
		}
		move(tn, last[i] + 1);
		for(; i < 4; i++) last[i]++;
	}
	private void move(int tn, int p) {
		if(tn == p) return;
		
		Piece temp = pieces[tn];
		
		int d = 1;
		if(tn > p) d = -1;
		while(tn != p) {
			pieces[tn] = pieces[tn + d];
			pieces[tn].setTN(tn);
			tn += d;
		}
		pieces[tn] = temp;
		pieces[tn].setTN(tn);
	}
	
	public void print() {
		if(pieces[0].getTeam() == Color.white) System.out.println("WHITE:");
		else System.out.println("BLACK:");
		System.out.print("King: ");
		pieces[0].print();
		System.out.println();
		System.out.println("Queen(s):");
		Piece[] temp = getQueens();
		for(int i = 0; i < temp.length; i++) {
			temp[i].print();
			System.out.println();
		}
		System.out.println("Rooks(s):");
		temp = getRooks();
		for(int i = 0; i < temp.length; i++) {
			temp[i].print();
			System.out.println();
		}
		System.out.println("Bishops(s):");
		temp = getBishops();
		for(int i = 0; i < temp.length; i++) {
			temp[i].print();
			System.out.println();
		}
		System.out.println("Knights(s):");
		temp = getKnights();
		for(int i = 0; i < temp.length; i++) {
			temp[i].print();
			System.out.println();
		}
		System.out.println("Pawn(s):");
		temp = getPawns();
		for(int i = 0; i < temp.length; i++) {
			temp[i].print();
			System.out.println();
		}
	}
}
