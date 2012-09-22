package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import logging.FileLogger;
import model.DirectionalNetwork;
import model.EdgeInterface;
import model.Graph;
import model.GraphParser;
import model.Link;
import model.Network;
import model.NetworkType;
import model.Node;
import model.OmnidirectionalNetwork;
import model.Vertex;
import model.WeightedGraph;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2011-09-10
 */
public class NetworkGUI extends JPanel implements ActionListener {

	// Set logging true if this is a compiled class, false if the class is
	// in a jar file.
	private static boolean logging = !NetworkGUI.class.getProtectionDomain()
			.getCodeSource().getLocation().toString().contains("jar");

	private DecimalFormat numFormatter = new DecimalFormat("###,###,##0.00");
	private static final long serialVersionUID = 978834603780916726L;

	// Draw Network Control Group (allow initial/easier selection).
	JRadioButton drawDirGraph;
	JRadioButton drawOmniGraph;

	JRadioButton drawPhysical;
	JRadioButton drawLogical;
	JRadioButton drawSameRange;
	JRadioButton drawDiffRange;

	// Setup Control Group
	private JTextField rangeUpdateTextField;

	// Performance Control Group
	private JTextField pathFromTextField;
	private JTextField pathToTextField;
	private JTextField pathLengthTextField;
	private JTextField pathLengthHopsTextField;

	// Statistics Control Group
	private JTextField averageAngleTextField;
	private JTextField averageRangeTextField;
	private JTextField averageSPLTextField;
	private JTextField averageSPLHopsTextField;
	private JTextField graphDiameterTextField;
	private JTextField graphDiameterHopsTextField;
	private JTextField totalEnergyUseTextField;

	// Class members.
	private JCanvas canvas;
	private DirectionalNetwork dirNet = null;
	private OmnidirectionalNetwork omniNet = null;
	private NetworkType selectedNetwork = null;

	// Keep the vertex type generic so we can draw both the physical network
	// (Node) and a logical network (Sensor).
	private WeightedGraph<? extends Vertex, Link> currentGraph = null;

	public static void main(String[] args) {
		// Schedule a job for the event-dispatch thread to create & show the ui.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NetworkGUI.initializeGUI();
			}
		});
	}

	private static void initializeGUI() {

		// Set the system look and feel.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			if (logging) {
				// LOGGING
				FileLogger.log(Level.WARNING, NetworkGUI.class.getName()
						+ ": initializeGUI; Error loading System Look & Feel.");
			}
		}

		// Create and set up the main window.
		JFrame rootFrame = new JFrame("NetworkDemo: Andrew Wylie");
		rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		rootFrame.setSize(800, 600);
		rootFrame.setResizable(false);

		NetworkGUI content = new NetworkGUI();
		content.setOpaque(true);
		rootFrame.setContentPane(content);

		// Display the window.
		rootFrame.pack();
		rootFrame.setVisible(true);
	}

	public NetworkGUI() {

		// Set the layout of the main (this) panel.
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		GridBagConstraints c;

		// Create the optionList JPanel. It will hold all of the buttons and
		// controls vertically on the left side of the page.
		JPanel optionList = new JPanel();
		optionList.setLayout(new BoxLayout(optionList, BoxLayout.PAGE_AXIS));
		optionList.setAlignmentY(Component.TOP_ALIGNMENT);

		// Create the canvas panel. The networks will be drawn on it.
		canvas = new JCanvas();
		canvas.setPreferredSize(new Dimension(600, 600));
		canvas.setTransform(AffineTransform.getTranslateInstance(300, 300));
		canvas.setAlignmentY(Component.TOP_ALIGNMENT);
		canvas.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Network"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// ///////////// Draw Network Control Group ////////////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// controls to draw a network graph

		JLabel networkTypeLabel = new JLabel("Network Type");
		JLabel graphTypeLabel = new JLabel("Graph Type");

		// Create the buttons for choosing the type of graph to display.
		drawDirGraph = new JRadioButton("Directional Network");
		drawOmniGraph = new JRadioButton("Omnidirectional Network");

		// Create the buttons for displaying a graph.
		drawPhysical = new JRadioButton("Input Graph");
		drawLogical = new JRadioButton("Logical Network");
		drawSameRange = new JRadioButton(
				"Oriented Logical Network, Homogeneous Range");
		drawDiffRange = new JRadioButton(
				"Oriented Logical Network, Heterogeneous Range");

		// Create button groups for the buttons.
		ButtonGroup selectNetworkButtonGroup = new ButtonGroup();
		selectNetworkButtonGroup.add(drawDirGraph);
		selectNetworkButtonGroup.add(drawOmniGraph);

		ButtonGroup drawNetworkButtonGroup = new ButtonGroup();
		drawNetworkButtonGroup.add(drawPhysical);
		drawNetworkButtonGroup.add(drawLogical);
		drawNetworkButtonGroup.add(drawSameRange);
		drawNetworkButtonGroup.add(drawDiffRange);

		// Add action listeners to the buttons.
		drawDirGraph.addActionListener(this);
		drawOmniGraph.addActionListener(this);

		drawPhysical.addActionListener(this);
		drawLogical.addActionListener(this);
		drawSameRange.addActionListener(this);
		drawDiffRange.addActionListener(this);

		// Add the controls to the host panel.
		JPanel drawNetworkPanel = new JPanel(new GridLayout(0, 1));

		drawNetworkPanel.add(networkTypeLabel);
		drawNetworkPanel.add(drawDirGraph);
		drawNetworkPanel.add(drawOmniGraph);

		drawNetworkPanel.add(graphTypeLabel);
		drawNetworkPanel.add(drawPhysical);
		drawNetworkPanel.add(drawLogical);
		drawNetworkPanel.add(drawSameRange);
		drawNetworkPanel.add(drawDiffRange);

		drawNetworkPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Draw Network"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Set default selection for the button groups.
		drawDirGraph.setSelected(true);
		drawPhysical.setSelected(true);

		// /////////////////////////////////////////////////////////////////////
		// /////////////// Setup Control Group /////////////////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// controls to update range

		JLabel rangeUpdateLabel = new JLabel("Set Sensor Range:");
		rangeUpdateTextField = new JTextField(4);
		JButton applySetupButton = new JButton("Apply Changes");
		JButton resetSetupButton = new JButton("Reset");

		rangeUpdateTextField.setHorizontalAlignment(JTextField.RIGHT);

		applySetupButton.setActionCommand("applySetup");
		resetSetupButton.setActionCommand("resetSetup");
		applySetupButton.addActionListener(this);
		resetSetupButton.addActionListener(this);

		// Create the layout constraints object.
		c = new GridBagConstraints();

		// Set initial properties for the constraints.
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 0, 5);

		JPanel setupPanel = new JPanel();
		setupPanel.setLayout(new GridBagLayout());

		setupPanel.add(rangeUpdateLabel, c);
		c.gridx += 1;
		setupPanel.add(rangeUpdateTextField, c);
		c.gridx -= 1;
		c.gridy += 1;
		c.gridwidth = 2;
		setupPanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);
		c.gridwidth = 1;
		c.gridy += 1;
		setupPanel.add(applySetupButton, c);
		c.gridx += 1;
		setupPanel.add(resetSetupButton, c);

		setupPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Setup"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// ///////// Performance Control Group /////////////////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// shortest path (draw) & its length. given a -> b
		// Length of a route

		JButton pathButton = new JButton("Get Path");
		pathButton.setActionCommand("getPath");
		pathButton.addActionListener(this);

		JButton resetPathButton = new JButton("Reset");
		resetPathButton.setActionCommand("resetPath");
		resetPathButton.addActionListener(this);

		JLabel pathLabel = new JLabel("Get Shortest Path:");

		JLabel pathFromLabel = new JLabel("From Node (name):");
		pathFromLabel.setFont(new Font(getFont().getName(), 0, 11));
		pathFromTextField = new JTextField(2);
		pathFromTextField.setHorizontalAlignment(JTextField.RIGHT);

		JLabel pathToLabel = new JLabel("To Node (name):");
		pathToLabel.setFont(new Font(getFont().getName(), 0, 11));
		pathToTextField = new JTextField(2);
		pathToTextField.setHorizontalAlignment(JTextField.RIGHT);

		// Length
		JLabel pathLengthLabel = new JLabel("Length:");
		pathLengthLabel.setFont(new Font(getFont().getName(), 0, 11));
		pathLengthTextField = new JTextField(6);
		pathLengthTextField.setHorizontalAlignment(JTextField.RIGHT);
		pathLengthTextField.setEditable(false);

		// Length (Hops)
		JLabel pathLengthHopsLabel = new JLabel("Length (Hops):");
		pathLengthHopsLabel.setFont(new Font(getFont().getName(), 0, 11));
		pathLengthHopsTextField = new JTextField(6);
		pathLengthHopsTextField.setHorizontalAlignment(JTextField.RIGHT);
		pathLengthHopsTextField.setEditable(false);

		// Create the layout constraints object.
		c = new GridBagConstraints();

		// Set initial properties for the constraints.
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 0, 5);

		JPanel performancePanel = new JPanel();
		performancePanel.setLayout(new GridBagLayout());

		c.gridwidth = 2;
		performancePanel.add(pathLabel);

		c.gridwidth = 1;
		c.gridy += 1;
		performancePanel.add(pathFromLabel, c);
		c.gridx += 1;
		performancePanel.add(pathFromTextField, c);

		c.gridx -= 1;
		c.gridy += 1;
		performancePanel.add(pathToLabel, c);
		c.gridx += 1;
		performancePanel.add(pathToTextField, c);

		c.gridx -= 1;
		c.gridy += 1;
		performancePanel.add(pathLengthLabel, c);
		c.gridx += 1;
		performancePanel.add(pathLengthTextField, c);

		c.gridx -= 1;
		c.gridy += 1;
		performancePanel.add(pathLengthHopsLabel, c);
		c.gridx += 1;
		performancePanel.add(pathLengthHopsTextField, c);

		c.gridwidth = 2;
		c.gridy++;
		c.gridx -= 1;
		performancePanel.add(new JSeparator(SwingConstants.HORIZONTAL), c);
		c.gridwidth = 1;
		c.gridy += 1;
		performancePanel.add(pathButton, c);
		c.gridx += 1;
		performancePanel.add(resetPathButton, c);

		performancePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Performance"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// ///////////////////// Statistics Control Group //////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// average sensor range
		// average sensor angle
		// total energy use
		// graph diameter

		// Create text & text boxes to display the averages.

		// Average angle
		JLabel averageAngleLabel = new JLabel("Average Angle:");
		averageAngleLabel.setFont(new Font(getFont().getName(), 0, 11));
		averageAngleTextField = new JTextField(7);
		averageAngleTextField.setHorizontalAlignment(JTextField.RIGHT);
		averageAngleTextField.setEditable(false);

		// Average Range
		JLabel averageRangeLabel = new JLabel("Average Range:");
		averageRangeLabel.setFont(new Font(getFont().getName(), 0, 11));
		averageRangeTextField = new JTextField(7);
		averageRangeTextField.setHorizontalAlignment(JTextField.RIGHT);
		averageRangeTextField.setEditable(false);

		// Average Shortest Path Length
		JLabel averageSPLLabel = new JLabel("Avg. Shortest Path Length:");
		averageSPLLabel.setFont(new Font(getFont().getName(), 0, 11));
		averageSPLTextField = new JTextField(5);
		averageSPLTextField.setHorizontalAlignment(JTextField.RIGHT);
		averageSPLTextField.setEditable(false);

		// Average Shortest Path Length (Hops)
		JLabel averageSPLHopsLabel = new JLabel(
				"Avg. Shortest Path Length (Hops):");
		averageSPLHopsLabel.setFont(new Font(getFont().getName(), 0, 11));
		averageSPLHopsTextField = new JTextField(5);
		averageSPLHopsTextField.setHorizontalAlignment(JTextField.RIGHT);
		averageSPLHopsTextField.setEditable(false);

		// Graph Diameter
		JLabel graphDiameterLabel = new JLabel("Graph Diameter:");
		graphDiameterLabel.setFont(new Font(getFont().getName(), 0, 11));
		graphDiameterTextField = new JTextField(7);
		graphDiameterTextField.setHorizontalAlignment(JTextField.RIGHT);
		graphDiameterTextField.setEditable(false);

		// Graph Diameter (Hops)
		JLabel graphDiameterHopsLabel = new JLabel("Graph Diameter (Hops):");
		graphDiameterHopsLabel.setFont(new Font(getFont().getName(), 0, 11));
		graphDiameterHopsTextField = new JTextField(7);
		graphDiameterHopsTextField.setHorizontalAlignment(JTextField.RIGHT);
		graphDiameterHopsTextField.setEditable(false);

		// Total Energy Use
		JLabel totalEnergyUseLabel = new JLabel("Total Energy Use (10^3):");
		totalEnergyUseLabel.setFont(new Font(getFont().getName(), 0, 11));
		totalEnergyUseTextField = new JTextField(7);
		totalEnergyUseTextField.setHorizontalAlignment(JTextField.RIGHT);
		totalEnergyUseTextField.setEditable(false);

		// Create the layout constraints object.
		c = new GridBagConstraints();

		// Set initial properties for the constraints.
		c.gridheight = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 0, 5);

		JPanel statisticsPanel = new JPanel();
		statisticsPanel.setLayout(new GridBagLayout());

		statisticsPanel.add(averageAngleLabel, c);
		c.gridx += 1;
		statisticsPanel.add(averageAngleTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(averageRangeLabel, c);
		c.gridx += 1;
		statisticsPanel.add(averageRangeTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(averageSPLLabel, c);
		c.gridx += 1;
		statisticsPanel.add(averageSPLTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(averageSPLHopsLabel, c);
		c.gridx += 1;
		statisticsPanel.add(averageSPLHopsTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(graphDiameterLabel, c);
		c.gridx += 1;
		statisticsPanel.add(graphDiameterTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(graphDiameterHopsLabel, c);
		c.gridx += 1;
		statisticsPanel.add(graphDiameterHopsTextField, c);

		c.gridy += 1;
		c.gridx -= 1;
		statisticsPanel.add(totalEnergyUseLabel, c);
		c.gridx += 1;
		statisticsPanel.add(totalEnergyUseTextField, c);

		statisticsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Statistics"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// /////////////////// Options Control Group ///////////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// load physical network from file

		// File open button
		JButton loadGraphButton = new JButton("Load Graph");
		loadGraphButton.setActionCommand("loadGraph");
		loadGraphButton.addActionListener(this);

		JPanel optionsPanel = new JPanel();

		optionsPanel.add(loadGraphButton);

		optionsPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Options"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// ///////////////// OptionList Pane ///////////////////////////////////
		// /////////////////////////////////////////////////////////////////////

		// Add all of the components to the main content pane.
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(optionList);
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(canvas);
		add(Box.createRigidArea(new Dimension(5, 0)));

		optionList.add(drawNetworkPanel);
		optionList.add(setupPanel);
		optionList.add(performancePanel);
		optionList.add(statisticsPanel);
		optionList.add(optionsPanel);
	}

	public void actionPerformed(ActionEvent e) {

		// Action code for the options menu.
		if ("loadGraph".equals(e.getActionCommand())) {

			final JFileChooser fileChooser = new JFileChooser();
			int retVal = fileChooser.showOpenDialog(this);

			if (retVal == JFileChooser.APPROVE_OPTION) {
				// Get the file.
				File file = fileChooser.getSelectedFile();

				// Parse the file for the physical network.
				WeightedGraph<Node, Link> pn = parseGraph(file);

				// Run the orientation algorithm.
				if (pn != null) {
					dirNet = new DirectionalNetwork(pn);
					omniNet = new OmnidirectionalNetwork(pn);

					JOptionPane.showMessageDialog(this.getRootPane(),
							"Network loaded!");

					// Apply default selection for the button groups.
					// TODO: check if they only change one initial selection
					if (drawDirGraph.isSelected() && drawPhysical.isSelected()) {

						drawDirGraph.doClick();
						drawPhysical.doClick();
					}
				}
			}
		}

		// A graph should be loaded before any actual commands are available.
		if (dirNet == null || omniNet == null) {
			JOptionPane.showMessageDialog(this.getRootPane(),
					"You must load a network graph before analyzing it.");
			return;
		}

		if (drawDirGraph.isSelected()) {
			selectedNetwork = NetworkType.DIRECTIONAL;

		} else if (drawOmniGraph.isSelected()) {
			selectedNetwork = NetworkType.OMNIDIRECTIONAL;
		}

		// TODO: extract complexity via subclass; wirelessnetwork has
		// createOptimalNetwork(boolean)
		if (drawPhysical.isSelected()) {

			if (selectedNetwork.equals(NetworkType.DIRECTIONAL)) {
				currentGraph = dirNet.getPhysicalNetwork();
			} else if (selectedNetwork.equals(NetworkType.OMNIDIRECTIONAL)) {
				currentGraph = omniNet.getPhysicalNetwork();
			}

		} else if (drawLogical.isSelected()) {

			if (selectedNetwork.equals(NetworkType.DIRECTIONAL)) {
				currentGraph = dirNet.getPhysicalNetworkMst();
			} else if (selectedNetwork.equals(NetworkType.OMNIDIRECTIONAL)) {
				currentGraph = omniNet.getPhysicalNetworkMst();
			}

		} else if (drawSameRange.isSelected()) {

			if (selectedNetwork.equals(NetworkType.DIRECTIONAL)) {
				currentGraph = dirNet.createOptimalNetwork(true);
			} else if (selectedNetwork.equals(NetworkType.OMNIDIRECTIONAL)) {
				currentGraph = omniNet.createOptimalNetwork(true);
			}

		} else if (drawDiffRange.isSelected()) {

			if (selectedNetwork.equals(NetworkType.DIRECTIONAL)) {
				currentGraph = dirNet.createOptimalNetwork(false);
			} else if (selectedNetwork.equals(NetworkType.OMNIDIRECTIONAL)) {
				currentGraph = omniNet.createOptimalNetwork(false);
			}
		}

		// Make sure that we have a selection on both button groups, if so then
		// draw the network graph.
		if (selectedNetwork != null && currentGraph != null) {
			drawGraph(currentGraph);
		} else {
			return;
		}

		// TODO: disable sensor range setting on optimal diff range
		// TODO: parser rework

		// Action event code for the shortest path retrieval.
		if ("getPath".equals(e.getActionCommand())) {

			// There must be a network currently selected.
			if (selectedNetwork == null) {
				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for"
								+ " which to find a path in.");
				return;
			}

			// Code for the shortest specified path button.
			if (pathFromTextField.getText().equals("")
					|| pathToTextField.getText().equals("")) {
				// Create dialog to inform user to enter info.
				JOptionPane.showMessageDialog(this.getRootPane(),
						"Enter sensor names to find"
								+ " the shortest path between them.");
				return;
			}

			// Reset any path currently drawn.
			drawGraph(currentGraph);

			// Set up some temporary variables to prevent code repetition.
			String from = pathFromTextField.getText();
			String to = pathToTextField.getText();
			int splh = 0;
			float spl = 0f;
			List<? extends Vertex> sp = null;

			// Get the route length.
			if (selectedNetwork != null) {
				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {

					splh = currentGraph.getShortestPathLengthHops(from, to);
					spl = currentGraph.getShortestPathLength(from, to);
					sp = currentGraph.getShortestPath(from, to);

				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {

					splh = currentGraph.getShortestPathLengthHops(from, to);
					spl = currentGraph.getShortestPathLength(from, to);
					sp = currentGraph.getShortestPath(from, to);
				}
			}

			pathLengthHopsTextField.setText(numFormatter.format(splh));
			pathLengthTextField.setText(numFormatter.format(spl));

			// Create a drawable object to add to the canvas object.
			if (sp != null) {

				Polyline polyline = new Polyline();
				polyline.setColor(Color.red);

				// Draw the path.
				for (int i = 0; i < sp.size(); i++) {
					Vertex v = sp.get(i);
					Point vPoint = new Point((int) v.getX(), (int) v.getY());
					polyline.add(vPoint);
				}

				canvas.add(polyline);
			}
		}

		// Action event code for the sensor range updating.
		if ("applySetup".equals(e.getActionCommand())) {

			// Update Range.
			String newRangeText = rangeUpdateTextField.getText();

			// There must be a network currently selected.
			if (selectedNetwork == null) {

				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for"
								+ " which to update sensor range.");
				return;
			}

			if (selectedNetwork != null) {

				// Check the value validity.
				if (newRangeText.equals("")) {
					// Create dialog to inform user to enter info.
					JOptionPane.showMessageDialog(this.getRootPane(),
							"Enter sensor range to be set.");
					return;
				}

				// TODO exception handling
				float newRange = Float.parseFloat(newRangeText);

				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = dirNet.createNetwork(newRange);
					drawGraph(currentGraph);
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = omniNet.createNetwork(newRange);
					drawGraph(currentGraph);
				}
			}

		} else if ("resetSetup".equals(e.getActionCommand())) {

			// There must be a network currently selected.
			if (selectedNetwork == null) {
				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for"
								+ " which to reset sensor range.");
				return;
			}

			rangeUpdateTextField.setText("");

			if (selectedNetwork != null) {
				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = dirNet.createOptimalNetwork(true);
					drawGraph(currentGraph);
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = omniNet.createOptimalNetwork(true);
					drawGraph(currentGraph);
				}
			}
		} else if ("resetPath".equals(e.getActionCommand())) {

			// There must be a network currently selected.
			if (selectedNetwork == null) {
				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for a path to be found.");
				return;
			}

			pathFromTextField.setText("");
			pathToTextField.setText("");
			pathLengthTextField.setText("");
			pathLengthHopsTextField.setText("");

			if (selectedNetwork != null) {
				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = dirNet.createOptimalNetwork(true);
					drawGraph(currentGraph);
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					currentGraph = omniNet.createOptimalNetwork(true);
					drawGraph(currentGraph);
				}
			}
		}

		// On any event we want to update the network statistics.
		if (selectedNetwork != null) {
			if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
				updateUiStatistics(dirNet, currentGraph);
			} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
				updateUiStatistics(omniNet, currentGraph);
			}
		}

	}

	// populate the ui fields with information about the current network
	private void updateUiStatistics(Network net,
			WeightedGraph<? extends Vertex, Link> wg) {

		// Update the normal graph statistics.
		String fAvgAngle = numFormatter.format(net.getAverageAngle());
		String fAvgRange = numFormatter.format(net.getAverageRange());
		double totEnergy = net.getTotalEnergyUse() / 1000;
		averageAngleTextField.setText(fAvgAngle);
		averageRangeTextField.setText(fAvgRange);
		totalEnergyUseTextField.setText(numFormatter.format(totEnergy));

		// Update the average shortest path values.
		float ASPL = wg.getAverageShortestPathLength();
		float ASPLH = wg.getAverageShortestPathLengthHops();
		averageSPLTextField.setText(numFormatter.format(ASPL));
		averageSPLHopsTextField.setText(numFormatter.format(ASPLH));

		// Update the graph diameter.
		String fDiam = numFormatter.format(wg.getDiameter());
		String fDiamHops = numFormatter.format(wg.getDiameterHops());
		graphDiameterTextField.setText(fDiam);
		graphDiameterHopsTextField.setText(fDiamHops);
	}

	// Redraw the input graph.
	private void drawGraph(Graph<? extends Vertex, ? extends EdgeInterface> g) {
		canvas.clear();
		canvas.add(g);
	}

	// This function loads a physical network from a file. It handles the
	// tokenization, parsing, and object creation.
	private WeightedGraph<Node, Link> parseGraph(File file) {
		GraphParser graphParser = new GraphParser();
		return graphParser.parse(file);
	}

}
