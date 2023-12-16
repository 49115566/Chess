import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Image[] img;

	/**
	 * Create the frame.
	 */
	public GUI(Space[][] jb, int[] click, Chess b, boolean[] bool) {
		Color white = new Color(245, 245, 220);
		Color black = new Color(110, 70, 35);
		//All 2-dimensional arrays representing the board are indexed [x][y].
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				jb[j][7 - i] = new Space("", this, j, 7 - i);
				jb[j][7 - i].setBorder(new EmptyBorder(0, 0, 0, 0));
				if((i + j) % 2 == 0)
					jb[j][7 - i].setBackground(white);
				else
					jb[j][7 - i].setBackground(black);
			}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(8, 8, 0, 0));
		
		for(int i = 0; i < 8; i++)
			for(int j = 0; j < 8; j++) {
				final Space temp = jb[j][7 - i];
				contentPane.add(temp);
				temp.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(bool[0]) {
							click[0] = temp.getX(b.getTurnsTaken() % 2 == 0);
							click[1] = temp.getY(b.getTurnsTaken() % 2 == 0);
							b.syncNotify();
						}
					}
				});
			}

		img = new Image[13];
		try {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		img[0] = ImageIO.read(classLoader.getResourceAsStream("wp.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[1] = ImageIO.read(classLoader.getResourceAsStream("bp.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[2] = ImageIO.read(classLoader.getResourceAsStream("wr.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[3] = ImageIO.read(classLoader.getResourceAsStream("br.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[4] = ImageIO.read(classLoader.getResourceAsStream("wn.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[5] = ImageIO.read(classLoader.getResourceAsStream("bn.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[6] = ImageIO.read(classLoader.getResourceAsStream("wb.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[7] = ImageIO.read(classLoader.getResourceAsStream("bb.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[8] = ImageIO.read(classLoader.getResourceAsStream("wq.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[9] = ImageIO.read(classLoader.getResourceAsStream("bq.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[10] = ImageIO.read(classLoader.getResourceAsStream("wk.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[11] = ImageIO.read(classLoader.getResourceAsStream("bk.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		img[12] = ImageIO.read(classLoader.getResourceAsStream("n.png")).getScaledInstance(40, -1, Image.SCALE_SMOOTH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Image getImage(char type) {
		switch(type) {
		case 'I': return img[0];
		case 'i': return img[1];
		case 'R': return img[2];
		case 'r': return img[3];
		case 'N': return img[4];
		case 'n': return img[5];
		case 'B': return img[6];
		case 'b': return img[7];
		case 'Q': return img[8];
		case 'q': return img[9];
		case 'K': return img[10];
		case 'k': return img[11];
		default: return img[12];
		}
	}
}