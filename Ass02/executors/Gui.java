package executors;

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
	private JPanel p1;
	private JPanel p2;
	private JLabel labelUrl;
	private JLabel labelDepth;
	private JTextField urlText;
	private JTextField depthText;
	private JButton run;
	private JLabel nodeCounts;
	
	/**
	 * Creates a view of the specified size (in pixels)
	 * 
	 * @param w
	 * @param h
	 */
	public Gui(int w, int h, final SharedContext sharedContext) {
		setTitle("Graph Simulation");
		setSize(w, h);
		JFrame myFrame = new JFrame();
		myFrame.setSize(new Dimension(w, h));
		
		Viewer viewer = new Viewer(sharedContext.getGraph(), Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);

		mainContainer = new JPanel();
		componentPane = new JPanel();
		p1 = new JPanel();
		p2 = new JPanel();
		
		SpringLayout layout = new SpringLayout();
		mainContainer.setLayout(layout);
		mainContainer.add(componentPane);
		mainContainer.add(p1);
		mainContainer.add(p2);
		
		nodeCounts = new JLabel("Total nodes: ");
		urlText = new JTextField("");
		urlText.setPreferredSize(new Dimension(150,30));
		depthText = new JTextField("1");
		depthText.setPreferredSize(new Dimension(150,30));
		run = new JButton("Run");
		labelUrl = new JLabel("Insert an URL");
		labelDepth = new JLabel("Insert a Depth");
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isNumeric(depthText.getText())) {
					JOptionPane.showMessageDialog(myFrame, "Insert a valid number");
				} else {
					if(isURL(urlText.getText())) {
						sharedContext.getGraph().display(true);
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
		
		layout.putConstraint(SpringLayout.NORTH, componentPane, 0, SpringLayout.NORTH, mainContainer);
		layout.putConstraint(SpringLayout.WEST, componentPane, 0, SpringLayout.WEST, mainContainer);
		
		layout.putConstraint(SpringLayout.SOUTH, componentPane, 0, SpringLayout.NORTH, p1);
		
		layout.putConstraint(SpringLayout.WEST, p1, 0, SpringLayout.WEST, mainContainer);
		
		layout.putConstraint(SpringLayout.SOUTH, p1, 0, SpringLayout.NORTH, p2);
		
		layout.putConstraint(SpringLayout.WEST, p2, 0, SpringLayout.WEST, mainContainer);
		layout.putConstraint(SpringLayout.SOUTH, p2, 0, SpringLayout.SOUTH, mainContainer);
		
		componentPane.add(labelUrl);
		componentPane.add(urlText);
		componentPane.add(nodeCounts);
		p1.add(labelDepth);
		p1.add(depthText);
		p2.add(run);
		sharedContext.setLabelCount(nodeCounts);
		
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				System.exit(-1);
			}

			public void windowClosed(WindowEvent ev) {
				System.exit(-1);
			}
		});
		myFrame.add((Component) view);
		myFrame.add(mainContainer);
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
