import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
//import javax.swing.Icon;
//import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Space extends JButton {
	private static final long serialVersionUID = 1L;
	//private Graphics g;
	private int wx;
	private int bx;
	private int wy;
	private int by;
	private Image img;
	private GUI g;
	
	public Space(String str, GUI gui, int x, int y) {
		super(str);
		wx = x;
		bx = 7 - x;
		wy = y;
		by = 7 - y;
		g = gui;
	}
	
	public int getX(boolean whiteTurn) {
		if(whiteTurn)
			return wx;
		return bx;
	}
	public int getY(boolean whiteTurn) {
		if(whiteTurn)
			return wy;
		return by;
	}
	
	public void setImage(char type) {
		img = g.getImage(type);
	    //super.setIcon(new ImageIcon(p.getScaledInstance(20, -1, Image.SCALE_SMOOTH)));
	}
	
	public void paintComponent(Graphics g){
	    super.paintComponent(g);
	    g.drawImage(img, 0, 0, this);
	  }
}
