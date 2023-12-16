/*
 * Creates a runnable game of chess
 * @author Craig Truitt
 * Created on Oct 5, 2023
 */

//import java.util.Scanner;
public class Chess {
	private ChessBoard board;
	
	public Chess() {
		board = new ChessBoard();
	}
	
	/*
	 * Returns the number of turns taken so far using ChessBoard::getTurnsTaken()
	 */
	public int getTurnsTaken() {
		return board.getTurnsTaken();
	}
	/*
	 * Prints the board using the method from ChessBoard.java
	 */
	public void printBoard(Space[][] j) {
		board.print(j);
	}
	/*
	 * Prints the archive using the method from ChessBoard.java
	 */
	public void printArchive() {
		board.printArchive();
	}
	
	/*
	 * takes move
	 */
	public synchronized void takeTurn(Space[][] tiles, int[] input, boolean[] checking) {
		//Scanner scan = new Scanner(System.in);
		int x = -1, y = -1;
		Color team;
		//determines whose turn is being taken
		if(board.getTurnsTaken() % 2 == 0) team = Color.white;
		else team = Color.black;
		int[][] s = board.getKing(team).rescueSpaces();
		//finds a valid piece to move
		while(board.getPiece(x, y).getTeam() != team || !board.getPiece(x, y).canMove(s)) {
			System.out.print("Enter the x position of the piece you want to move: ");
			checking[0] = true;
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			x = input[0];
			checking[0] = false;
			System.out.println((char) (x + 'A'));
			System.out.print("Enter the y position of the piece you want to move: ");
			y = input[1];
			System.out.println(y + 1);
			input[0] = input[1] = -1;
		}
		System.out.println();
		//stores the desired piece
		Piece p = board.getPiece(x, y);
		
		x = -1;
		y = -1;
		//finds a valid place to move
		while(!p.isValidMove(x, y)) {
			System.out.print("Enter the x position where you want to move: ");
			checking[0] = true;
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			x = input[0];
			checking[0] = false;
			System.out.println((char) (x + 'A'));
			System.out.print("Enter the y position where you want to move: ");
			y = input[1];
			System.out.println(y + 1);
			input[0] = input[1] = -1;
		}
		System.out.println();
		//moves it
		board.move(p, x, y);
		p.upgrade(tiles, input, checking);
	}
	
	public synchronized void syncNotify() {
		notify();
	}
	
	/*
	 * checks for endgame using stalemate(), insufficientMaterial(), mutualAgreement(),
	 * threefoldRepetition(), fiftyMove(), and checkmate()
	 */
	public boolean gameOver() {
		if(fiftyMove() || threefoldRepetition() || insufficientMaterial()) return true;
		
		Color team = Color.white;
		if(board.getTurnsTaken() % 2 == 1) team = Color.black;
		boolean m = board.teamCanMove(team);
		if(checkmate(m) || stalemate(m)) return true;
		
		return false;
	}
	
	/*
	 * An endgame function; checks for stalemate
	 */
	private boolean stalemate(boolean m) {
		if(!m && board.getCheckKings() == null) {
			System.out.println("GAME OVER - STALEMATE");
			return true;
		}
		return false;
	}
	/*
	 * An endgame function; checks for dead position
	 */
	private boolean insufficientMaterial() {
		return board.insufficientMaterial();
	}
	/*
	 * An endgame function; checks for mutual agreement
	 */
	private boolean mutualAgreement() {
		System.out.println("mutualAgreement() Not Created, returning false");
		return false;
	}
	/*
	 * An endgame function; checks for the arrival to the same spot three times
	 */
	private boolean threefoldRepetition() {
		if(!board.threefoldRepetition()) return false;
		System.out.println("GAME OVER - THIS BOARD POSITION HAS OCCURRED THREE TIMES");
		return true;
	}
	/*
	 * An endgame function; checks for fifty moves without a piece taken
	 */
	private boolean fiftyMove() {
		if(board.getTurnsSinceCapture() == 50) {
			System.out.println("GAME OVER - FIFTY MOVES HAVE OCCURED SINCE THE LAST CAPTURE");
			return true;
		}
		return false;
	}
	/*
	 * An endgame function; checks for checkmate
	 */
	private boolean checkmate(boolean m) {
		if(!m && board.getCheckKings() != null) {
			System.out.println("GAME OVER - CHECKMATE!");
			return true;
		}
		return false;
	}
}