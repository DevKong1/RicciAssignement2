package vertxImpl;

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
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import io.vertx.core.eventbus.EventBus;
import io.vertx.rxjava.core.Vertx;

public final class Viewer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VisualiserPanel panel;
	private Container mainContainer;
	private JPanel componentPane;
	private JPanel buttonPane;
	private JLabel labelUrl;
	private JLabel labelDepth;
	private JTextField urlText;
	private JTextField depthText;
	private JButton run;
	private JFrame myFrame;
	private List<String> nodes;
	@SuppressWarnings("unused")
	private EventBus eb;

	/**
	 * Creates a view of the specified size (in pixels)
	 * 
	 * @param w
	 * @param h
	 */
	public Viewer(int w, int h,EventBus eb) {
		setTitle("Graph Simulation");
		setSize(w, h);
		setResizable(false);
		myFrame = new JFrame();
		myFrame.setSize(new Dimension(w, h + 100));
		myFrame.setLayout(new BorderLayout());

		panel = new VisualiserPanel(w, h);
		mainContainer = getContentPane();
		componentPane = new JPanel();
		buttonPane = new JPanel();
		urlText = new JTextField();
		urlText.setMaximumSize(new Dimension(150, 30));
		depthText = new JTextField("1");
		depthText.setMaximumSize(new Dimension(150, 30));
		run = new JButton("Run");
		nodes = new LinkedList<String>();
		this.eb = eb;
		run.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isNumeric(depthText.getText())) {
					JOptionPane.showMessageDialog(myFrame, "Insert a valid number");
				} else {
						if (isURL(urlText.getText())) {
							String url = getContent(urlText.getText());
							int dept = Integer.parseInt(depthText.getText());
							String message = url + ":"+dept;
							eb.send("init",message);
						} else {
							JOptionPane.showMessageDialog(myFrame, "Error 404, insert a valid URL");
						}
				}
			}
		});

		labelUrl = new JLabel("Insert a URL");
		labelDepth = new JLabel("Insert a Depth");

		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		componentPane.setLayout(new BoxLayout(componentPane, BoxLayout.X_AXIS));
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.Y_AXIS));
		buttonPane.setSize(componentPane.getSize());
		componentPane.add(labelUrl);
		componentPane.add(urlText);
		componentPane.add(labelDepth);
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
	}

	private static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	private static String getContent(final String link) {
		String content = link.substring(30);
		//System.out.println(basicUrl+"w/api.php?action=parse&page=" + content + "&format=json&section=0&prop=links");
		// System.out.println(basicUrl);
		//System.out.println(content);
		return content;
	}
	private boolean isURL(String url) {
		try {
			(new URL(url)).openStream().close();
			return true;
		} catch (Exception ex) {
		}
		return false;
	}

	public void display() {
		myFrame.setVisible(true);
	}

	public void updateView(final List<NodeTuple> values) {
		for(int i=0;i<values.size();i++) {
			for(int j=0;j<nodes.size();j++) {
				if(values.get(i).equals(nodes.get(j))) {
					/**
					 * That voice is already in the graph somewhere, just
					 * place an arch between i's father and j (if there isn't already).
					 * if(NoArch(values.get(i).getFather,nodes.get(j){
					 * 	PlaceArch(values.get(i).getFather,nodes.get(j);
					 * }
					 */
				}else {
					/**
					 * This voice wasn't already in the graph, connect it to its father.
					 */
					nodes.add(values.get(i).getValue());
				}
			}
		}
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
