
public class ChessBoard {
	private Piece[][] board;
	private Team[] teams;
	private Piece checkKings;
	private MoveInfoArchive archive;
	private boolean wWasCheck;
	private boolean bWasCheck;
	private int turnsTaken;
	private int turnsSinceCapture;
	private int side;
	/*
	 * Constructor, basic board
	 */
	public ChessBoard() {
		side = 8;		//Length of a chess board side
		turnsTaken = 0;	//Counts the number of turns taken thus far
		turnsSinceCapture = 0;	//Stores time since last piece was captured
		archive = new MoveInfoArchive(this);
		wWasCheck = false;	//Stores whether the white king has been in check
		bWasCheck = false;	//Stores whether the black king has been in check
		
		//All 2-dimensional arrays representing the board are indexed [x][y]
		//Allocate memory
		board = new Piece[side][side];
		//Initialize 1st row
		board[0][0] = new Piece('r', Color.white, 0, 0, this);
		board[1][0] = new Piece('n', Color.white, 1, 0, this);
		board[2][0] = new Piece('b', Color.white, 2, 0, this);
		board[3][0] = new Piece('q', Color.white, 3, 0, this);
		board[4][0] = new Piece('k', Color.white, 4, 0, this);
		board[5][0] = new Piece('b', Color.white, 5, 0, this);
		board[6][0] = new Piece('n', Color.white, 6, 0, this);
		board[7][0] = new Piece('r', Color.white, 7, 0, this);
		//Initialize 2nd row
		for(int i = 0; i < side; i++) board[i][1] = new Piece('p', Color.white, i, 1, this);
		//Fill empty spaces
		for(int i = 0; i < side; i++)
			for(int j = 2; j < side - 2; j++)
				board[i][j] = new Piece(' ', Color.neither, i, j, this);
		//Initialize last two rows
		for(int i = 0; i < side; i++)
			for(int j = 0; j < 2; j++)
				board[i][7 - j] = new Piece(board[i][j].getType(), Color.black, i, 7 - j, this);
		
		//Store shortcut for the teams
		//Allocate memory
		teams = new Team[2];
		teams[0] = new Team(this, Color.white);
		teams[1] = new Team(this, Color.black);
		
		//Store a piece that puts a king in check; only one king can be in check at once
		checkKings = null;
	}
	
	/*
	 * Provides the piece putting the king in check
	 */
	public Piece getCheckKings() {
		return checkKings;
	}
	
	/*
	 * Privides piece in array location
	 */
	public Piece getPiece(int x, int y) {
		//Return desired piece
		if(x >= 0 && x < 8 && y >= 0 && y < 8)
			return board[x][y];
		//Return blank piece
		return new Piece(' ', Color.neither, x, y, this);
	}
	/*
	 * Returns the king of a certain color
	 */
	public Piece getKing(Color c) {
		if(c == Color.white) return teams[0].getKing();
		if(c == Color.black) return teams[1].getKing();
		return null;
	}
	public Team getTeam(Color c) {
		if(c == Color.white) return teams[0];
		if(c == Color.black) return teams[1];
		return null;
	}
	/*
	 * Returns whether a king of a certain color has been in check
	 */
	public boolean getCheck(Color c) {
		if(c == Color.white) return wWasCheck;
		if(c == Color.black) return bWasCheck;
		return false;
	}
	/*
	 * Returns the number of turns taken
	 */
	public int getTurnsTaken() {
		return turnsTaken;
	}
	/*
	 * Returns the number of turns since a piece was captured
	 */
	public int getTurnsSinceCapture() {
		return turnsSinceCapture;
	}
	/*
	 * Returns whether a team can move
	 */
	public boolean teamCanMove(Color t) {
		if(t == Color.white) return teams[0].canMove();
		return teams[1].canMove();
	}
	/*
	 * Returns the value stored by side
	 */
	public int getSide() {
		return side;
	}
	
	/*
	 * Moves the piece both on the board and in itself
	 * Captures any pieces
	 */
	public void move(Piece p, int newX, int newY) {
		//Something went seriously wrong
		if(p != board[p.getX()][p.getY()]) {
			System.out.println("The piece is not on this board or was previously improperly manipulated.");
			return;
		}
		//System.out.println("MOVE");
		
		//move rook with castle
		if(p.getType() == 'k') {
			if(newX - p.getX() > 1 && board[7][newY].getType() == 'r' && board[7][newY].getTeam() == p.getTeam()) {
				board[7][newY].setPos(newX - 1, newY);							//Move rook internal position
				board[newX - 1][newY] = board[7][newY];							//Move board's location for rook
				board[7][newY] = new Piece(' ', Color.neither, 7, newY, this);	//Empty board's old rook space
			}
			if(newX - p.getX() < -1 && board[0][newY].getType() == 'r' && board[0][newY].getTeam() == p.getTeam()) {
				board[0][newY].setPos(newX + 1, newY);							//Move rook internal position
				board[newX + 1][newY] = board[0][newY];							//Move board's location for rook
				board[0][newY] = new Piece(' ', Color.neither, 0, newY, this);	//Empty board's old rook space
			}
		}
		
		//puts a king in check and stores it
		check(spaceInDanger(getKing(p.getTeam()).getX(), getKing(p.getTeam()).getY(), p.getTeam()));
		
		//store move info
		archive.addNode(new MoveInfo(p.getX(), p.getY(), newX, newY, board[newX][newY], checkKings, turnsSinceCapture,
						((p.getTeam() == Color.white && bWasCheck) || (p.getTeam() == Color.black && wWasCheck))));
		archive.moveToNext();
		
		//capture with en passant
		if(p.getType() == 'p' && board[newX][newY].getTeam() == Color.neither && p.getX() != newX) {
			//change the archive
			archive.moveToPrevious();
			archive.addNode(new MoveInfo(p.getX(), p.getY(), newX, newY, board[newX][p.getY()], checkKings, turnsSinceCapture,
					(p.getTeam() == Color.white && bWasCheck) || (p.getTeam() == Color.black && wWasCheck)));
			archive.moveToNext();
			//capture the piece on the board
			board[newX][p.getY()] = new Piece(' ', Color.neither, newX, p.getY(), this);
		}
		
		//empty old position
		board[p.getX()][p.getY()] = new Piece(' ', Color.neither, p.getX(), p.getY(), this);
		//move piece's internal position
		p.setPos(newX, newY);
		//move board's position of piece, capturing any other piece
		board[newX][newY] = p;
		//internally capture the captured piece
		archive.getCurrent().getCapture().toggleCapture();
		//increase the turn count
		turnsTaken++;
		//increase the turns since the last capture
		turnsSinceCapture++;
		//reset the turns since the last capture
		if(archive.getCurrent().getCapture().getTeam() != Color.neither)
			turnsSinceCapture = 0;
	}
	
	/*
	 * Undoes the last move
	 */
	public boolean undoLastMove() {
		//catch error
		if(!archive.isCurrent()) {
			System.out.println("Cannot access history");
			return false;
		}
		//System.out.println("UNDO");
		
		//decrease the turn count
		turnsTaken--;
		//undo change in turnsSinceCapture
		turnsSinceCapture = archive.getCurrent().getTSC();
		//undo piece putting king in check
		uncheck();
		
		//regular undo
		//create shortcut for the piece being unmoved
		Piece p = board[archive.getCurrent().getNewX()][archive.getCurrent().getNewY()];
		//refill old position
		board[archive.getCurrent().getOldX()][archive.getCurrent().getOldY()] = p;
		//unmove piece's internal position
		p.setPos(archive.getCurrent().getOldX(), archive.getCurrent().getOldY());
		//undo the increase in times moved from the various setPos()'s.
		p.undoTimesMoved();
		//uncapture the old piece/clear the space's storage of the piece being moved
		board[archive.getCurrent().getCapture().getX()][archive.getCurrent().getCapture().getY()] = archive.getCurrent().getCapture();
		//internally uncapture the captured piece
		archive.getCurrent().getCapture().toggleCapture();
		
		//enPassant changes
		//if enPassant occurred, the piece's old position was not cleared; this will do so
		if(board[archive.getCurrent().getNewX()][archive.getCurrent().getNewY()] == p)
			board[archive.getCurrent().getNewX()][archive.getCurrent().getNewY()] =
					new Piece(' ', Color.neither, archive.getCurrent().getNewX(), archive.getCurrent().getNewY(), this);
		
		//castling changes
		//undo potential castling
		if(p.getType() == 'k' && archive.getCurrent().getNewY() == archive.getCurrent().getOldY()) {
			Piece r;
			if(archive.getCurrent().getNewX() - archive.getCurrent().getOldX() > 1) {
				r = board[archive.getCurrent().getOldX() + 1][archive.getCurrent().getOldY()];	//store information (for the sake of less typing)
				r.setPos(7, archive.getCurrent().getOldY());									//change rook's internal position
				r.undoTimesMoved();																//undo change in timesMoved on rook
				board[7][archive.getCurrent().getOldY()] = r;									//change rook's position on board
				board[archive.getCurrent().getOldX() + 1][archive.getCurrent().getOldY()] =		//clear old position on board
						new Piece(' ', Color.neither, archive.getCurrent().getOldX() + 1, archive.getCurrent().getOldY(), this);
			}
			if(archive.getCurrent().getNewX() - archive.getCurrent().getOldX() < -1) {
				r = board[archive.getCurrent().getOldX() - 1][archive.getCurrent().getOldY()];	//store information (for the sake of less typing)
				r.setPos(0, archive.getCurrent().getOldY());									//change rook's internal position
				r.undoTimesMoved();																//undo change in timesMoved on rook
				board[0][archive.getCurrent().getOldY()] = r;									//change rook's position on board
				board[archive.getCurrent().getOldX() - 1][archive.getCurrent().getOldY()] =		//clear old position on board
						new Piece(' ', Color.neither, archive.getCurrent().getOldX() - 1, archive.getCurrent().getOldY(), this);
			}
		}
		
		archive.moveToPrevious();
		return true;
	}
	
	/*
	 * Redoes undone last move
	 */
	public boolean redoMove() {
		if(!archive.isNext()) {
			//System.out.println("All Moves Redone");
			return false;
		}
		//System.out.println("REDO");
		archive.moveToNext();
		Piece p = board[archive.getCurrent().getOldX()][archive.getCurrent().getOldY()];
		int newX = archive.getCurrent().getNewX(), newY = archive.getCurrent().getNewY();
		//Something went seriously wrong
		if(p != board[p.getX()][p.getY()]) {
			System.out.println("The piece is not on this board or was previously improperly manipulated.");
			archive.moveToPrevious();
			return false;
		}
		
		//move rook with castle
		if(p.getType() == 'k') {
			if(newX - p.getX() > 1 && board[7][newY].getType() == 'r' && board[7][newY].getTeam() == p.getTeam()) {
				board[7][newY].setPos(newX - 1, newY);							//Move rook internal position
				board[newX - 1][newY] = board[7][newY];							//Move board's location for rook
				board[7][newY] = new Piece(' ', Color.neither, 7, newY, this);	//Empty board's old rook space
			}
			if(newX - p.getX() < -1 && board[0][newY].getType() == 'r' && board[0][newY].getTeam() == p.getTeam()) {
				board[0][newY].setPos(newX + 1, newY);							//Move rook internal position
				board[newX + 1][newY] = board[0][newY];							//Move board's location for rook
				board[0][newY] = new Piece(' ', Color.neither, 0, newY, this);	//Empty board's old rook space
			}
		}
		

		//puts a king in check and stores it
		check(spaceInDanger(getKing(p.getTeam()).getX(), getKing(p.getTeam()).getY(), p.getTeam()));
		
		//capture with en passant
		if(p.getType() == 'p' && board[newX][newY].getTeam() == Color.neither && p.getX() != newX) {
			archive.moveToPrevious();
			board[newX][p.getY()] = new Piece(' ', Color.neither, newX, p.getY(), this);
		}
		
		//empty old position
		board[p.getX()][p.getY()] = new Piece(' ', Color.neither, p.getX(), p.getY(), this);
		//move piece's internal position
		p.setPos(newX, newY);
		//move board's position of piece, capturing any other piece
		board[newX][newY] = p;
		//internally capture the captured piece
		archive.getCurrent().getCapture().toggleCapture();
		//increase the turn count
		turnsTaken++;
		//increase the turns since the last capture
		turnsSinceCapture++;
		//reset the turns since the last capture
		if(archive.getCurrent().getCapture().getTeam() != Color.neither)
			turnsSinceCapture = 0;
		
		return true;
	}
	
	/*
	 * Switches whether king of color has been in check
	 */
	public void check(Piece p) {
		checkKings = p;
		if(checkKings == null) return;
		if(p.getTeam() == Color.white) wWasCheck = true;
		if(p.getTeam() == Color.black) bWasCheck = true; 
	}
	public void uncheck() {
		checkKings = archive.getCurrent().getCK();
		if(checkKings == null) return;
		if(checkKings.getTeam() == Color.white) bWasCheck = archive.getCurrent().getWC();
		if(checkKings.getTeam() == Color.black) wWasCheck = archive.getCurrent().getWC(); 
	}
	
	/*
	 * Returns whether a piece in a space is in danger
	 * It evaluates based upon the team of the piece
	 */
	public Piece spaceInDanger(int x, int y, Color thisTeam) {
		//Check if there is a team in consideration
		if(thisTeam == Color.neither) return null;
		//Checks if the space is in danger
		Piece temp = spaceInDangerFromPawn(x, y, thisTeam);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromKnight(x, y, thisTeam);
		if(temp != null) return temp;
		
		return spaceInDangerFromBRQK(x, y, thisTeam);
	}
	/*
	 * Returns whether a pawn endangers a space
	 */
	private Piece spaceInDangerFromPawn(int x, int y, Color team) {
		int pY;
		Color cTeam;
		//Determines whether to check up or down and finds the opposing team
		if(team == Color.white) {
			pY = y + 1;
			cTeam = Color.black;
		}
		else {
			pY = y - 1;
			cTeam = Color.white;
		}
		if(pY < 0 || pY >= 8) return null;	//If pawn cannot exist, stop here
		if(x + 1 < 8 && (board[x + 1][pY].getTeam() == cTeam	//Checks for opposing pawn
				&& board[x + 1][pY].getType() == 'p')) return board[x + 1][pY]; //in positive x diagonal.
		if(x - 1 >= 0 && (board[x - 1][pY].getTeam() == cTeam	//Checks for opposing pawn
				&& board[x - 1][pY].getType() == 'p')) return board[x - 1][pY];	//in negative x diagonal.
		//If it makes it to here, no pawn endangers the space
		return null;
	}
	/*
	 * Returns whether a knight endangers a space
	 */
	private Piece spaceInDangerFromKnight(int x, int y, Color team) {
		//Array of dangerous knight locations
		int[][] cN =   {{x + 1, y + 2}, {x + 2, y + 1},
						{x + 2, y - 1}, {x + 1, y - 2},
						{x - 1, y - 2}, {x - 2, y - 1},
						{x - 2, y + 1}, {x - 1, y + 2}};
		//Checks each position
		for(int i = 0; i < 8; i++) {
			if(	cN[i][0] < 8 && cN[i][1] >= 0 && cN[i][0] < 8 && cN[i][0] >= 0 &&
				getPiece(cN[i][0], cN[i][1]).getType() == 'n' &&
				getPiece(cN[i][0], cN[i][1]).getTeam() != Color.neither &&
				getPiece(cN[i][0], cN[i][1]).getTeam() != team)
					return getPiece(cN[i][0], cN[i][1]);
		}
		//If it makes it to here, no knight endangers the space
		return null;
	}
	/*
	 * Returns whether a bishop, rook, queen, or king threatens a space
	 */
	private Piece spaceInDangerFromBRQK(int x, int y, Color team) {
		//Subdivides the solution
		Piece temp;
		temp = spaceInDangerFromDir(x, y, team, Direction.north);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.northeast);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.east);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.southeast);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.south);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.southwest);
		if(temp != null) return temp;
		
		temp = spaceInDangerFromDir(x, y, team, Direction.west);
		if(temp != null) return temp;
		
		return	spaceInDangerFromDir(x, y, team, Direction.northwest);
	}
	/*
	 * Returns whether any piece along a cardinal direction threatens the space
	 */
	private Piece spaceInDangerFromDir(int x, int y, Color team, Direction dir) {
		//dx and dy will represent the change between individual spaces being checked
		int dx, dy;
		//diag will represent whether or not it's diagonal, determining whether to check rooks or bishops
		boolean diag;
		//initializes dx, dy, and diag based upon direction
		switch(dir) {
		case north:		dx =  0; dy =  1; diag = false;	break;
		case northeast:	dx =  1; dy =  1; diag = true;	break;
		case east:		dx =  1; dy =  0; diag = false;	break;
		case southeast:	dx =  1; dy = -1; diag = true;	break;
		case south:		dx =  0; dy = -1; diag = false;	break;
		case southwest:	dx = -1; dy = -1; diag = true;	break;
		case west:		dx = -1; dy =  0; diag = false;	break;
		case northwest:	dx = -1; dy =  1; diag = true;	break;
		default: return null;	//Any threat outside of the eight directions has its own method
		}
		//stores info about opposing team
		Color cTeam;
		if(team == Color.white) cTeam = Color.black;
		else cTeam = Color.white;
		
		boolean prevOcc = false;
		
		//checks in the direction from the space under examination
		for(int cx = x + dx, cy = y + dy;	//first spaces being checked
				cx >= 0 && cx < 8 && cy >= 0 && cy < 8 && !prevOcc;	//termination conditions
				cx += dx, cy += dy) {		//increment to find next space to check
			if(board[cx][cy].getTeam() != Color.neither) prevOcc = true;
			if(board[cx][cy].getTeam() == cTeam) {
				if(board[cx][cy].getType() == 'q' ||
						(cx == x + dx && cy == y + dy  && board[cx][cy].getType() == 'k'))
					return board[cx][cy];
				if(diag && board[cx][cy].getType() == 'b') return board[cx][cy];
				if(!diag && board[cx][cy].getType() == 'r') return board[cx][cy];
			}
		}
		return null;
	}
	
	/*
	 * Checks if a move has already occurred
	 */
	public boolean threefoldRepetition() {
		//archive search can stop once a piece is uncaptured, a pawn is unmoved, or the repetitions reach 3
		int count = 0;
		Team white = new Team(teams[0]);
		Team black = new Team(teams[1]);
		while(count < 3 && undoLastMove() && archive.isCurrent() && !archive.hasCapture() && !archive.movedPawn())
			if(teams[0].isIgnorePawns(white) && teams[1].isIgnorePawns(black)) count++;
		while(redoMove());
		return count == 3;
	}
	/*
	 * Checks if the board has the pieces for a checkmate
	 */
	public boolean insufficientMaterial() {
		//insufficient material relates to the pieces, not the positions
		//if the game ends due to a timer, further analysis must occur to find if the position is impossible
		//all queens, rooks, and pawns must be captured
		//there can only be one bishop or knight per team
		
		//if a piece was not just captured, this condition will not occur
		if(!archive.hasCapture()) return false;
		
		//Queens, Rooks, and Pawns are "major" pieces
		//the presence of any of them allows for possible checkmate
		//Check Queens
		Piece[] temp = teams[0].getQueens();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		temp = teams[1].getQueens();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		
		//Check Rooks
		temp = teams[0].getRooks();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		temp = teams[1].getRooks();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		
		//Check Pawns
		temp = teams[0].getPawns();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		temp = teams[1].getPawns();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured()) return false;
		
		//Bishops and Knights are "minor" pieces
		//the presence of two of them allows for checkmate
		int p = 0;
		
		//Check Bishops
		temp = teams[0].getBishops();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured() && p++ > 1) return false;
		temp = teams[1].getBishops();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured() && p++ > 1) return false;
		
		//Check Knights
		temp = teams[0].getKnights();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured() && p++ > 1) return false;
		temp = teams[1].getKnights();
		for(int i = 0; i < temp.length; i++) if(!temp[i].isCaptured() && p++ > 1) return false;
		
		return true;
	}
	
	
	/*
	 * Prints board using printW and printB
	 */
	public void print(Space[][] b) {
		if(turnsTaken % 2 == 0) printW(b);
		else printB(b);
		
		if(checkKings != null) {
			System.out.println("CHECK!");
			System.out.println("PIECE DETAILS:");
			System.out.println("PIECE TEAM: " + checkKings.getTeam());
			System.out.println("PIECE TYPE: " + checkKings.getType());
			System.out.println("X: " + (char)(checkKings.getX() + 'A') + ", Y: " + (checkKings.getY() + 1));
			System.out.println();
		}
	}
	
	/*
	 * Prints board on black turn
	 */
	private void printB(Space[][] b) {
		//conversion constants
		int toLetter = 'A' - 1;
		int toUpper = 'A' - 'a';
		int easyReadPawn = 'i' - 'p';
		int conversion = 0;
		
		//Prints board
		int i, j;
		System.out.println();
		for(i = 0; i < side; i++) {
			//Print row number
			System.out.print(" " + (i + 1) + " ");
			for(j = 0; j < side; j++) {
				//Makes 'p' print as 'i'
				if(board[side - 1 - j][i].getType() == 'p') conversion = easyReadPawn;
				else conversion = 0;
				//If white, print in uppercase
				if(board[side - 1 - j][i].getTeam() == Color.white) conversion += toUpper;
				//Print character
				System.out.print(" " + (char)(board[side - 1 - j][i].getType() + conversion));
				if(j != side - 1) System.out.print(" |");
				
				if(board[side - 1 - i][side - 1 - j].getType() == 'p') conversion = easyReadPawn;
				else conversion = 0;
				if(board[side - 1 - i][side - 1 - j].getTeam() == Color.white) conversion += toUpper;
				b[i][j].setImage((char)(board[side - 1 - i][side - 1 - j].getType() + conversion));
				b[i][j].repaint();
			}
			System.out.println();
			System.out.print("   ");
			if(i != side - 1) {
				for(j = 0; j < side; j++) {
					System.out.print("---");
					if(j != side - 1) System.out.print("+");
				}
				System.out.println();
			}
			else {
				for(j = 0; j < side; j++) {
					if(j != 0) System.out.print("  ");
					System.out.print(" " + (char) (side - j + toLetter));
				}
				System.out.println();
			}
		}
		System.out.println("Black Turn!");
		System.out.println();
	}
	/*
	 * Prints board on white turn
	 */
	private void printW(Space[][] b) {
		int toLetter = 'A' - 1;
		int toUpper = 'A' - 'a';
		int easyReadPawn = 'i' - 'p';
		int conversion = 0;
		int i, j;
		
		System.out.println();
		for(i = 0; i < side; i++) {
			System.out.print(" " + (side - i) + " ");
			for(j = 0; j < side; j++) {
				if(board[j][side - 1 - i].getType() == 'p') conversion = easyReadPawn;
				else conversion = 0;
				if(board[j][side - 1 - i].getTeam() == Color.white) conversion += toUpper;
				System.out.print(" " + (char)(board[j][side - 1 - i].getType() + conversion));
				if(j != side - 1) System.out.print(" |");
				
				if(board[i][j].getType() == 'p') conversion = easyReadPawn;
				else conversion = 0;
				if(board[i][j].getTeam() == Color.white) conversion += toUpper;
				b[i][j].setImage((char)(board[i][j].getType() + conversion));
				b[i][j].repaint();
			}
			System.out.println();
			System.out.print("   ");
			if(i != side - 1) {
				for(j = 0; j < side; j++) {
					System.out.print("---");
					if(j != side - 1) System.out.print("+");
				}
				System.out.println();
			}
			else {
				for(j = 0; j < side; j++) {
					if(j != 0) System.out.print("  ");
					System.out.print(" " + (char) (j + 1 + toLetter));
				}
				System.out.println();
			}
		}
		System.out.println("White Turn!");
		System.out.println();
	}
	
	/*
	 * Prints archive using archive.print()
	 */
	public void printArchive() {
		archive.print();
	}
	/*
	 * Prints the teams using Team::print()
	 */
	public void printTeams() {
		teams[0].print();
		teams[1].print();
	}
}