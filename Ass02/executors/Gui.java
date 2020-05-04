package executors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.*;

import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1L;
	private Container mainContainer;
	private JPanel componentPane;
	private JLabel labelUrl;
	private JLabel labelDepth;
	private JTextField urlText;
	private JTextField depthText;
	private JButton run;
	
	/**
	 * Creates a view of the specified size (in pixels)
	 * 
	 * @param w
	 * @param h
	 */
	public Gui(int w, int h, final SharedContext sharedContext) {
		setTitle("Graph Simulation");
		setSize(w, h);
		setResizable(false);
		JFrame myFrame = new JFrame();
		myFrame.setSize(new Dimension(w, h));
		myFrame.setLayout(new BorderLayout());
		Viewer viewer = new Viewer(sharedContext.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		mainContainer = getContentPane();
		componentPane = new JPanel();
		urlText = new JTextField();
		urlText.setMaximumSize(new Dimension(150,30));
		urlText.setMinimumSize(new Dimension(150,30));
		depthText = new JTextField("1");
		depthText.setMaximumSize(new Dimension(150,30));
		depthText.setMinimumSize(new Dimension(150,30));
		run = new JButton("Run");
		labelUrl = new JLabel("Insert a URL");
		labelDepth = new JLabel("Insert a Depth");
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sharedContext.getGraph().display(true);
				if(!isNumeric(depthText.getText())) {
					JOptionPane.showMessageDialog(myFrame, "Insert a valid number");
				} else {
					if(isURL(urlText.getText())) {
						sharedContext.setDepth(Integer.parseInt(depthText.getText()));
						sharedContext.setInitialUrl(urlText.getText());
						sharedContext.setBasicUrl();
						sharedContext.running(sharedContext);
						run.setEnabled(false);
					} else {
						JOptionPane.showMessageDialog(myFrame, "Error 404, insert a valid URL");
					}
				}
			}
		});
		
		View view = viewer.addDefaultView(false);
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
		componentPane.setLayout(new BoxLayout(componentPane, BoxLayout.X_AXIS));
		componentPane.add(labelUrl);
		componentPane.add(urlText);
		componentPane.add(labelDepth);
		componentPane.add(depthText);
		componentPane.add(run);
		componentPane.add(Box.createRigidArea(new Dimension(200, 0)));
		mainContainer.add(componentPane);
		mainContainer.add(Box.createRigidArea(new Dimension(0, 10)));
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(-1);
			}

			public void windowClosed(WindowEvent ev) {
				System.exit(-1);
			}
		});
		myFrame.add((Component) view);
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
	     (new URL(url)).openStream().close();
	     return true;
	  } catch (Exception ex) { }
	  return false;
	}
}
