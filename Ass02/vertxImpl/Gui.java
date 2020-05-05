package vertxImpl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

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
	private JFrame myFrame;
	private List<String> nodes;
	@SuppressWarnings("unused")
	private EventBus eb;
	private Graph graph;
	
	/**
	 * Creates a view of the specified size (in pixels)
	 * 
	 * @param w
	 * @param h
	 */
	public Gui(int w, int h, EventBus eb) {
		setTitle("Graph Simulation");
		setSize(w, h);
		myFrame = new JFrame();
		myFrame.setSize(new Dimension(w, h));
		graph = new SingleGraph("graph");
		Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		nodes = new LinkedList<String>();
		this.eb = eb;

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
	}
	public void display() {
		myFrame.setVisible(true);
	}
	private static String getContent(final String link) {
		String content = link.substring(30);
		//System.out.println(basicUrl+"w/api.php?action=parse&page=" + content + "&format=json&section=0&prop=links");
		// System.out.println(basicUrl);
		//System.out.println(content);
		return content;
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
