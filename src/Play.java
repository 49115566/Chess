
public class Play {

	//main method
	public static void main(String[] args) {
		//graphics prep
		Space s[][] = new Space[8][8];
		int[] input = new int[2];
		input[0] = input[1] = -1;
		boolean[] checking = new boolean[1];
		checking[0] = false;
		
		Chess game = new Chess();
		
		//GUI
		GUI frame = new GUI(s, input, game, checking);
		frame.setVisible(true);
		
		//Game
		game.printBoard(s);
		game.takeTurn(s, input, checking);
		while(!game.gameOver()) {
			game.printBoard(s);
			game.takeTurn(s, input, checking);
		}
		game.printBoard(s);
		game.printArchive();
		System.out.println("Game Over!");
	}
}