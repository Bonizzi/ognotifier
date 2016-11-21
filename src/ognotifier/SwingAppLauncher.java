package ognotifier;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SwingAppLauncher {
	public static JFrame frame = new JFrame("OGNotifier: PrjHub Issues Notifications");
	public static JLabel label3 = new JLabel();
	public static JPanel pane2 = new JPanel();

	public static void createAndShowGUI() throws Exception {

		try {
			frame.setIconImage(ImageIO.read(new File("images/orientdb_64x64.png")));
		} catch (IOException exc) {
			exc.printStackTrace();
		}

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(300, 150);
		JPanel pane1 = new JPanel();
		pane1.setSize(30, 30);
		frame.add(pane1, BorderLayout.NORTH);

		JPanel pane3 = new JPanel();
		pane3.setSize(300, 30);
		frame.add(pane3, BorderLayout.SOUTH);

		Icon icon = new ImageIcon("images/loading.gif");
		JLabel label2 = new JLabel(icon, SwingConstants.CENTER);
		pane1.add(label2);

		pane2.setSize(300, 50);
		label3.setText("<html>Issues totali: <font color='red'><strong>" + 0
				+ "</strong></font>. <br /> Updating every minute.</html>");

		pane2.add(label3);
		frame.add(pane2);

		JLabel label1 = new JLabel("<html>Scanning... please be patient. <br /> Takes aprox. 30 seconds.</html>",
				SwingConstants.CENTER);
		pane1.add(label1);

		JLabel label4 = new JLabel(
				"<html>Sorint.Lab S.p.A. © 2016 - <a href='mailto:ogubchenko@sorint.it'>ogubchenko@sorint.it</a></html>",
				SwingConstants.LEFT);
		pane3.add(label4);

		frame.setVisible(true);

		// Display the window.

		PrjHub_Parser login = new PrjHub_Parser();
		login.execute();

		/*
		 * try { Thread.sleep(5000); } catch (InterruptedException e) {
		 * e.printStackTrace(); }
		 * 
		 * CallBack callBack = new CallBackImpl();
		 * 
		 * int i=1; while(callBack.methodToCallBack(i)){
		 * label3.setText(String.valueOf(i)); i++; }
		 */

	}

	public static void doAlarm() throws Exception {
		String soundName = "images/alarm.wav";
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile());
		Clip clip = AudioSystem.getClip();
		try {
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JButton button = new JButton();
		button.setText("New ticket detected!");
		button.setVisible(false);
		frame.add(button);

		button.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JOptionPane.showConfirmDialog(frame, "New ticket detected!");
			}
		});
		button.doClick();
		clip.stop();

	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}