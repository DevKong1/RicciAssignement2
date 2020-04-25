package executors;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;

public class Viewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VisualiserPanel panel;
	private Container mainContainer;
	private JPanel componentPane;
	private JPanel buttonPane;
	private JTextField urlText;
	private JTextField depthText;
	private JButton run;
	private HttpConnection httpConnection;
	
	/**
	 * Creates a view of the specified size (in pixels)
	 * 
	 * @param w
	 * @param h
	 */
	public Viewer(int w, int h) {
		setTitle("Graph Simulation");
		setSize(w, h);
		setResizable(false);
		JFrame myFrame = new JFrame();
		myFrame.setSize(new Dimension(w, h + 75));
		myFrame.setLayout(new BorderLayout());

		panel = new VisualiserPanel(w, h);
		mainContainer = getContentPane();
		componentPane = new JPanel();
		buttonPane = new JPanel();
		urlText = new JTextField("c");
		urlText.setMaximumSize(new Dimension(150,30));
		depthText = new JTextField("b");
		depthText.setMaximumSize(new Dimension(150,30));
		run = new JButton("Run");
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isNumeric(depthText.getText())) {
					JOptionPane.showMessageDialog(myFrame, "Insert a valid number");
				} else {
					try {
						if(isURL(urlText.getText())) {
							httpConnection = new HttpConnection(new URL(urlText.getText()), "CIAO.html");
							httpConnection.connect();
						} else {
							JOptionPane.showMessageDialog(myFrame, "Error 404, insert a valid URL");
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		componentPane.setLayout(new BoxLayout(componentPane, BoxLayout.X_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		buttonPane.setSize(componentPane.getSize());
		componentPane.add(urlText);
		componentPane.add(depthText);
		componentPane.add(run);
		componentPane.add(Box.createRigidArea(new Dimension(200, 0)));
		componentPane.add(buttonPane);
		mainContainer.add(componentPane);
		mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));
		mainContainer.add(panel);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(-1);
			}

			public void windowClosed(WindowEvent ev) {
				System.exit(-1);
			}
		});
		myFrame.getContentPane().add(mainContainer);
		myFrame.setVisible(true);
	}
	
	private static boolean isNumeric(String str) { 
	  try {  
	    Integer.parseInt(str);  
	    return true;
	  } catch(NumberFormatException e) {  
	    return false;  
	  }  
	}
	
	private boolean isURL(String url) {
	  try {
	     (new java.net.URL(url)).openStream().close();
	     return true;
	  } catch (Exception ex) { }
	  return false;
	}

	
	public static class VisualiserPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private long dx;
		private long dy;

		public VisualiserPanel(int w, int h) {
			setSize(w, h);
			dx = w / 2 - 20;
			dy = h / 2 - 20;
		}

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.clearRect(0, 0, this.getWidth(), this.getHeight());

			
		}

		public void display(double vt) {
			repaint();
		}
	}
}