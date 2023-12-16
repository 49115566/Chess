/*
 * This class creates the piece for the Chess class
 */

public class Piece {
	private char type;
	private Color team;
	private int[] pos;
	private int tn;
	private int timesMoved;
	private int turnLastMoved;
	private boolean captured;
	private ChessBoard board;
	
	public Piece(char t, Color w, int x, int y, ChessBoard b) {
		//initializes the piece's info
		type = t;
		team = w;
		pos = new int[2];
		pos[0] = x;
		pos[1] = y;
		tn = -1;
		timesMoved = 0;
		turnLastMoved = -1;
		captured = false;
		board = b;
	}
	public Piece(Piece p) {
		//initializes the piece's info
		type = p.type;
		team = p.team;
		pos = new int[2];
		pos[0] = p.pos[0];
		pos[1] = p.pos[1];
		tn = p.tn;
		timesMoved = p.timesMoved;
		turnLastMoved = p.turnLastMoved;
		captured = p.captured;
		board = p.board;
	}
	
	
	/*
	 * Provides the type of piece
	 */
	public char getType() {
		return type;
	}
	/*
	 * Provides the team of the piece
	 */
	public Color getTeam() {
		return team;
	}
	/*
	 * Provides the times the piece has moved
	 */
	public int getTimesMoved() {
		return timesMoved;
	}
	/*
	 * Privides the turn number of the last turn taken
	 */
	public int getTurnLastMoved() {
		return turnLastMoved;
	}
	/*
	 * Provides whether the piece is captured
	 */
	public boolean isCaptured() {
		return captured;
	}
	/*
	 * Provides the x location of the piece
	 */
	public int getX() {
		return pos[0];
	}
	/*
	 * Provides the y location of the piece
	 */
	public int getY() {
		return pos[1];
	}
	
	/*
	 * Sets the x and y positions of the piece
	 */
	public void setPos(int x, int y) {
		//sets the position
		pos[0] = x;
		pos[1] = y;
		//updates other piece info
		//if-else statement used to allow ChessBoard::undoLastMove()
		timesMoved++;
		turnLastMoved = board.getTurnsTaken();
	}
	/*
	 * Stores the index of itself from the array in which it is stored by an instance of Team
	 */
	public void setTN(int n) {
		tn = n;
	}
	/*
	 * Sets captured - weak protection
	 */
	public void toggleCapture() {
		captured = !captured;
	}
	/*
	 * Decrements times moved twice
	 */
	public void undoTimesMoved() {
		timesMoved -= 2;
	}
	/*
	 * Upgrades the piece if it is a pawn when it is time
	 */
	public void upgrade(Space[][] tiles, int[] input, boolean[] querry) {
		if(type == 'p' && (pos[1] == 0 || pos[1] == 7)) {
			String str = "UPGRADE:";
			System.out.println(str);
			System.out.print("Select the desired piece (Q, B, R, or N): ");
			for(int i = 0; i < tiles[0].length; i++)
				tiles[i][7].setText("" + str.charAt(i));
			tiles[0][6].setText("Q");
			tiles[1][6].setText("B");
			tiles[2][6].setText("R");
			tiles[3][6].setText("N");
			for(int i = 4; i < tiles[1].length; i++)
				tiles[i][6].setText("");
			for(int i = 2; i < tiles.length; i++)
				for(int j = 0; j < tiles[i].length; j++)
					tiles[j][7 - i].setText("");
			
			int x = -1, y = -1;
			querry[0] = true;
			while(y != 6 || x > 4) {
				System.out.print("");
				x = input[0];
				y = input[1];
				//input flips depending on the turn, this takes it into account
				if(team == Color.white) {
					x = 7 - x;
					y = 7 - y;
				}
			}
			querry[0] = false;
			input[0] = input[1] = -1;
			System.out.println(tiles[x][y].getText());
			type = tiles[x][y].getText().toLowerCase().charAt(0);
			board.getTeam(team).upgraded(tn);
		}
	}
	
	
	/*
	 * Checks the piece's ability to move
	 */
	public boolean canMove(int[][] s) {
		if(captured) return false;
		//spreads the problem
		switch (type) {
		case 'p':
			return canMoveP(s);
		case 'n':
			return canMoveN(s);
		case 'r':
			return canMoveR(s);
		case 'b':
			return canMoveB(s);
		case 'q':
			return canMoveQ(s);
		case 'k':
			return canMoveK(s);
		default:
			return false;
		}
	}
	
	/*
	 * Checks the piece's ability to move if it's a pawn
	 */
	private boolean canMoveP(int[][] spaces) {
		int dy;
		//Determines direction of pawn's motion
		if(team == Color.white) dy = 1;
		else dy = -1;
		if(board.getCheckKings() == null) {
			if(	isValidMove(pos[0], pos[1] + dy) ||		//Checks basic forward move
				isValidMove(pos[0] + 1, pos[1] + dy) ||	//Checks capture to the east
				isValidMove(pos[0] - 1, pos[1] + dy))	//Checks capture to the west
				return true;
		}
		else {
			System.out.println("Array length: " + spaces.length);
			if(spaces.length != 0) System.out.println("Internal array length: " + spaces[0].length);
			if(Math.abs(spaces[0][0] - pos[0]) == 1 && spaces[0][1] - pos[1] == dy)
				if(isValidMove(spaces[0][0], spaces[0][1])) return true;
			for(int i = 1; i < spaces.length; i++)
				if(spaces[i][0] - pos[0] == 0 && spaces[i][1] - pos[1] == dy)
					if(isValidMove(spaces[i][0], spaces[i][1])) return true;
		}
		//If it makes it here, the pawn cannot move
		System.out.println("The pawn at " + (char)(pos[0] + 'A') + " " + (pos[1] + 1) + " cannot move.");
		return false;
	}
	/*
	 * Checks the piece's ability to move if it's a knight
	 */
	private boolean canMoveN(int[][] spaces) {
		//Checks the possible valid moves
		if(board.getCheckKings() == null) {
			if(	isValidMove(pos[0] + 1, pos[1] + 2) ||
				isValidMove(pos[0] + 2, pos[1] + 1) ||
				isValidMove(pos[0] + 2, pos[1] - 1) ||
				isValidMove(pos[0] + 1, pos[1] - 2) ||
				isValidMove(pos[0] - 1, pos[1] - 2) ||
				isValidMove(pos[0] - 2, pos[1] - 1) ||
				isValidMove(pos[0] - 2, pos[1] + 1) ||
				isValidMove(pos[0] - 1, pos[1] + 2))
				return true;
		}
		else {
			for(int i = 0; i < spaces.length; i++)
				if(	(Math.abs(spaces[i][0] - pos[0]) == 2 && Math.abs(spaces[i][1] - pos[1]) == 1) ||
					(Math.abs(spaces[i][0] - pos[0]) == 1 && Math.abs(spaces[i][1] - pos[1]) == 2))
						if(isValidMove(spaces[i][0], spaces[i][1])) return true;
		}
		//If it makes it here, the knight cannot move
		System.out.println("The knight at " + (char)(pos[0] + 'A') + " " + (pos[1] + 1) + " cannot move.");
		return false;
	}
	/*
	 * Checks the piece's ability to move if it's a rook
	 */
	private boolean canMoveR(int[][] spaces) {
		//Checks the moves adjacent to the rook's position
		//If any position cannot be moved, no positions an that direction can be moved
		//Theory in line above is incorrect if the rook can take the king out of check
		if(board.getCheckKings() == null) {
			if(	isValidMove(pos[0], pos[1] + 1) ||
				isValidMove(pos[0] + 1, pos[1]) ||
				isValidMove(pos[0], pos[1] - 1) ||
				isValidMove(pos[0] - 1, pos[1]))
				return true;
		}
		else {
			for(int i = 0; i < spaces.length; i++)
				if(spaces[i][0] == pos[0] || spaces[i][1] == pos[1])
					if(isValidMove(spaces[i][0], spaces[i][1])) return true;
		}
		//If it makes it here, the rook cannot move under normal circumstances
		System.out.println("The rook at " + (char)(pos[0] + 'A') + " " + (pos[1] + 1) + " cannot move.");
		return false;
	}
	/*
	 * Checks the piece's ability to move if it's a bishop
	 */
	private boolean canMoveB(int[][] spaces) {
		//See comments for canMoveR()
		if(board.getCheckKings() == null) {
			if(	isValidMove(pos[0] + 1, pos[1] + 1) ||
				isValidMove(pos[0] + 1, pos[1] - 1) ||
				isValidMove(pos[0] - 1, pos[1] - 1) ||
				isValidMove(pos[0] - 1, pos[1] + 1))
				return true;
		}
		else {
			for(int i = 0; i < spaces.length; i++)
				if(Math.abs(spaces[i][0] - pos[0]) == Math.abs(spaces[i][1] - pos[1]))
					if(isValidMove(spaces[i][0], spaces[i][1])) return true;
		}
		//If it makes it here, the bishop cannot move under normal circumstances
		System.out.println("The bishop at " + (char)(pos[0] + 'A') + " " + (pos[1] + 1) + " cannot move.");
		return false;
	}
	/*
	 * Checks the piece's ability to move if it's a queen
	 */
	private boolean canMoveQ(int[][] spaces) {
		//See comments for canMoveR()
		if(board.getCheckKings() == null) {
			if(	isValidMove(pos[0]    , pos[1] + 1) ||
				isValidMove(pos[0] + 1, pos[1] + 1) ||
				isValidMove(pos[0] + 1, pos[1]    ) ||
				isValidMove(pos[0] + 1, pos[1] - 1) ||
				isValidMove(pos[0]    , pos[1] - 1) ||
				isValidMove(pos[0] - 1, pos[1] - 1) ||
				isValidMove(pos[0] - 1, pos[1]    ) ||
				isValidMove(pos[0] - 1, pos[1] + 1))
					return true;
		}
		else {
			for(int i = 0; i < spaces.length; i++)
				if(spaces[i][0] == pos[0] || spaces[i][1] == pos[1] || Math.abs(spaces[i][0] - pos[0]) == Math.abs(spaces[i][1] - pos[1]))
					if(isValidMove(spaces[i][0], spaces[i][1])) return true;
		}
		//If it makes it here, the queen cannot move under normal circumstances
		System.out.println("The queen at " + (char)(pos[0] + 'A') + " " + (pos[1] + 1) + " cannot move.");
		return false;
	}
	/*
	 * Checks the piece's ability to move if it's a king
	 */
	private boolean canMoveK(int [][] spaces) {
		//Checks each spot the king could move
		if(	isValidMove(pos[0], pos[1] + 1) ||
			isValidMove(pos[0] + 1, pos[1]) ||
			isValidMove(pos[0], pos[1] - 1) ||
			isValidMove(pos[0] - 1, pos[1]) ||
			isValidMove(pos[0] + 1, pos[1] + 1) ||
			isValidMove(pos[0] + 1, pos[1] - 1) ||
			isValidMove(pos[0] - 1, pos[1] - 1) ||
			isValidMove(pos[0] - 1, pos[1] + 1))
				return true;
		//If it makes it this far, the king cannot move
		System.out.println("The " + team + " king cannot move.");
		return false;
	}
	
	/*
	 * Finds the spaces which can be moved to during check
	 */
	public int[][] rescueSpaces() {
		if(board.getCheckKings() == null) return null;
		System.out.println("RESCUE SPACES:");
		int[][] spaces;
		if(board.getCheckKings().getType() != 'n') {
			int distance = board.getCheckKings().getY() - board.getKing(team).getY();
			if(distance == 0) distance = board.getCheckKings().getX() - board.getKing(team).getX();
			System.out.println("COUNT: " + Math.abs(distance));
			spaces = new int[Math.abs(distance)][2];
		}
		else
			spaces = new int[1][2];
		int dx, dy;
		switch (board.getCheckKings().moveDirection(board.getKing(team).getX(), board.getKing(team).getY())) {
		case north:		dx = 0;		dy = 1;		break;
		case northeast:	dx = 1;		dy = 1;		break;
		case east:		dx = 1;		dy = 0;		break;
		case southeast:	dx = 1;		dy = -1;	break;
		case south:		dx = 0;		dy = -1;	break;
		case southwest:	dx = -1;	dy = -1;	break;
		case west:		dx = -1;	dy = 0;		break;
		case northwest:	dx = -1;	dy = 1;		break;
		default:
			spaces[0][0] = board.getCheckKings().getX();
			spaces[0][1] = board.getCheckKings().getY();
			System.out.println("X: " + (char)(spaces[0][0] + 'A') + ", Y: " + (spaces[0][1] + 1));
			return spaces;
		}
		for(int i = board.getCheckKings().getX(), j = board.getCheckKings().getY(), k = 0;
			k < spaces.length; i += dx, j += dy, k++) {
			spaces[k][0] = i;
			spaces[k][1] = j;
			System.out.println("X: " + (char)(spaces[k][0] + 'A') + ", Y: " + (spaces[k][1] + 1));
		}
		return spaces;
	}
	
	
	/*
	 * Checks the validity of a move
	 */
	public boolean isValidMove(int x, int y) {
		if(captured) return false;
		//out of bounds cannot move
		if(x < 0 || x >= board.getSide() || y < 0 || y >= board.getSide()) return false;
		//cannot capture piece of your own team
		if(board.getPiece(x, y).getTeam() == team) return false;
		//pieceBetween() ignores knights; no other piece can jump another piece
		if(pieceBetween(x, y)) return false;
		//cannot put team's king in check
		if(putsKingInCheck(x, y, team)) return false;
		//divides the problem further
		switch (type) {
		case 'p':
			return isValidP(x, y);
		case 'n':
			return isValidN(x, y);
		case 'r':
			return isValidR(x, y);
		case 'b':
			return isValidB(x, y);
		case 'q':
			return isValidQ(x, y);
		case 'k':
			return isValidK(x, y);
		default:
			return false;
		}
	}
	
	/*
	 * Checks to see if a move puts the king into check
	 */
	private boolean putsKingInCheck(int x, int y, Color c) {
		//Color.neither cannot move and has no king
		if(c == Color.neither) return false;
		
		//Returns false by default
		boolean r = false;
		//Checks if the move puts the king in check
		board.move(this, x, y);
		//If this king will be in danger, this should return true
		if(board.spaceInDanger(board.getKing(c).getX(), board.getKing(c).getY(), c) != null) r = true;
		//The move hasn't happened yet, so undo it
		board.undoLastMove();
		//board.print();
		return r;
	}
	
	/*
	 * Checks the validity of a move if the piece is a pawn
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidP(int x, int y) {
		//determine direction
		int dy;
		if(team == Color.white) dy = 1;
		else dy = -1;
		
		//moving to an unoccupied space
		if(board.getPiece(x, y).getTeam() == Color.neither) {
			//basic move; y changes dy
			if(x == pos[0] && y == pos[1] + dy) return true;
			//optional first move; y changes 2 * dy
			if(timesMoved == 0 && x == pos[0] && y == pos[1] + (2*dy)) return true;
			//returns whether it can enPassant, the only other option
			return enPassantPossible(x, y, dy);
		}
		//if it makes it to here, it is moving to an occupied space
		//capture; x changes +/- 1, y changes dy
		return (x == pos[0] + 1 || x == pos[0] - 1) && y == pos[1] + dy;
	}
	/*
	 * Checks the validity of a move if the piece is a knight
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidN(int x, int y) {
		//Returns if whether the move is one of the valid moves
		return ((x == pos[0] + 1 && y == pos[1] + 2) || (x == pos[0] + 2 && y == pos[1] + 1) ||
				(x == pos[0] + 2 && y == pos[1] - 1) || (x == pos[0] + 1 && y == pos[1] - 2) ||
				(x == pos[0] - 1 && y == pos[1] - 2) || (x == pos[0] - 2 && y == pos[1] - 1) ||
				(x == pos[0] - 2 && y == pos[1] + 1) || (x == pos[0] - 1 && y == pos[1] + 2));
	}
	/*
	 * Checks the validity of a move if the piece is a rook
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidR(int x, int y) {
		//Finds direction
		Direction dir = moveDirection(x, y);
		//Returns whether the direction is valid
		return (dir == Direction.north || dir == Direction.east ||
				dir == Direction.south || dir == Direction.west);
	}
	/*
	 * Checks the validity of a move if the piece is a bishop
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidB(int x, int y) {
		//Finds direction
		Direction dir = moveDirection(x, y);
		//Returns whether the direction is valid
		return (dir == Direction.northeast || dir == Direction.southeast ||
				dir == Direction.southwest || dir == Direction.northwest);
	}
	/*
	 * Checks the validity of a move if the piece is a queen
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidQ(int x, int y) {
		//Finds direction
		Direction dir = moveDirection(x, y);
		//Returns whether the piece moves directly in a direction
		return !(dir == Direction.none);
	}
	/*
	 * Checks the validity of a move if the piece is a king
	 * Does not check for things in isValidMove()
	 */
	private boolean isValidK(int x, int y) {
		//Finds change in x and y
		int dx = x - pos[0];
		int dy = y - pos[1];
		//If the change in x or y is in a valid range, things are good
		if((dx >= -1 && dx <= 1) && (dy >= -1 && dy <= 1)) return true;
		//Only other option is castling
		return castlePossible(x, y);
	}
	
	/*
	 * Finds the direction of a move
	 */
	public Direction moveDirection(int x, int y) {
		//finds change in x and y
		int dx = x - pos[0];
		int dy = y - pos[1];
		if(x == pos[0] && y > pos[1]) return Direction.north;					//Checks for due north
		if(dx == dy && x > pos[0] && y > pos[1]) return Direction.northeast;	//Checks for due northeast
		if(x > pos[0] && y == pos[1]) return Direction.east;					//Checks for due east
		if(dx == -dy && x > pos[0] && y < pos[1]) return Direction.southeast;	//Checks for due southeast
		if(x == pos[0] && y < pos[1]) return Direction.south;					//Checks for due south
		if(dx == dy && x < pos[0] && y < pos[1]) return Direction.southwest;	//Checks for due southwest
		if(x < pos[0] && y == pos[1]) return Direction.west;					//Checks for due west
		if(dx == -dy && x < pos[0] && y > pos[1]) return Direction.northwest;	//Checks for due northwest
		return Direction.none;													//Otherwise it is not directly any direction
	}
	
	/*
	 * Determines whether there is a piece between the current and desired positions for valid non-knight moves
	 */
	private boolean pieceBetween(int x, int y) {
		int dx, dy;
		//Finds rate of change in x and y relative to each other
		//If the piece is a rook, it'll return false
		switch (moveDirection(x, y)) {
		case north:		dx = 0;		dy = 1;		break;
		case northeast:	dx = 1;		dy = 1;		break;
		case east:		dx = 1;		dy = 0;		break;
		case southeast:	dx = 1;		dy = -1;	break;
		case south:		dx = 0;		dy = -1;	break;
		case southwest:	dx = -1;	dy = -1;	break;
		case west:		dx = -1;	dy = 0;		break;
		case northwest:	dx = -1;	dy = 1;		break;
		default:		return false;
		}
		//Checks for a piece between until desired position
		for(int i = pos[0] + dx, j = pos[1] + dy; i != x || j != y; i += dx, j += dy)
			if(board.getPiece(i, j).getTeam() != Color.neither)	//If a piece is found, it returns true
				return true;
		//Otherwise, it returns false
		return false;
	}
	
	/*
	 * Determines whether the selected king can castle
	 */
	private boolean castlePossible(int x, int y) {
		int rX;
		//Finds rook
		if(x <= 4) rX = 0;
		else rX = 7;
		//special castle rules; if any line is not the case, it returns false
		return (y == pos[1] &&														//y must remain constant
				(x == pos[0] - 2 || x == pos[0] + 2) &&								//x must change 2
				timesMoved == 0 &&													//Must be king's first move
				board.getPiece(rX, y).getType() == 'r' &&							//Rook must be present
				board.getPiece(rX, y).getTimesMoved() == 0 &&						//Rook cannot have moved	
				!board.getCheck(team) &&											//King cannot have been in check
				board.spaceInDanger((x - pos[0])/2 + pos[0], y, team) == null &&	//King cannot move through check
				!pieceBetween(rX, y));												//No piece can be between rook and king
	}
	/*
	 * Determines whether the selected pawn can en passantE
	 */
	private boolean enPassantPossible(int x, int y, int dy) {
		//Return test results
				//pawn must move diagonally forward
		return (x == pos[0] + 1 || x == pos[0] - 1) && y == pos[1] + dy &&
				//there must be a pawn in the space above the move
				(board.getPiece(x, y - dy).getType() == 'p' && board.getPiece(x, y - dy).getTimesMoved() == 1) &&
				//said pawn must be of the opposing team
				(board.getPiece(x, y - dy).getTeam() != Color.neither && board.getPiece(x, y - dy).getTeam() != team) &&
				//said pawn must have moved last turn
				(board.getPiece(x, y - dy).getTurnLastMoved() == board.getTurnsTaken() - 1) &&
				//this test will ensure that the opposing team's pawn has moved only once
				((team == Color.white && y == 5) || (team == Color.black && y == 2));
	}
	
	/*
	 * Checks if pieces are the same ignoring team and capture status
	 */
	public boolean isITC(Piece p) {
		return (type == p.type && pos[0] == p.pos[0] && pos[1] == p.pos[1]);
	}
	
	/*
	 * Prints the details of the piece
	 */
	public void print() {
		System.out.print(type + ", " + team + ", " + tn + ", " + (char)(pos[0] + 'A') + "" + (pos[1] + 1));
		if(captured) System.out.print(", captured");
	}
}
