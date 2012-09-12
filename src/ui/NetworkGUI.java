package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import model.GraphParser;
import model.Link;
import model.NetworkType;
import model.Node;
import model.OmnidirectionalNetwork;
import model.Sensor;
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

	private static final long serialVersionUID = 1L;

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

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NetworkGUI.initializeGUI();
			}
		});
	}

	private static void initializeGUI() {

		// Set the system look and feel.
		try {
			// Set System L&F
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
		// ///////////// Draw Network Control Group ////////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// controls to draw a network graph

		// Create the buttons for displaying the graph.
		JRadioButton drawDirButton = new JRadioButton("Directional");
		JRadioButton drawOmniButton = new JRadioButton("Omnidirectional");

		// Create a button group for the buttons.
		ButtonGroup drawNetworkButtonGroup = new ButtonGroup();
		drawNetworkButtonGroup.add(drawDirButton);
		drawNetworkButtonGroup.add(drawOmniButton);

		drawDirButton.setActionCommand("drawDir");
		drawOmniButton.setActionCommand("drawOmni");
		drawDirButton.addActionListener(this);
		drawOmniButton.addActionListener(this);

		JPanel drawNetworkPanel = new JPanel();

		drawNetworkPanel.setLayout(new GridLayout(0, 1));
		drawNetworkPanel.add(drawDirButton);
		drawNetworkPanel.add(drawOmniButton);
		drawNetworkPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Draw Network"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// /////////////// Setup Control Group ////////////////////////
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
		// ///////// Performance Control Group //////////////////
		// /////////////////////////////////////////////////////////////////////

		// In this group:
		// shortest path (draw) & its length. given a -> b

		// Length of a route

		JButton pathButton = new JButton("Get Path");
		pathButton.setActionCommand("getPath");
		pathButton.addActionListener(this);

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

		performancePanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Performance"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// /////////////////////////////////////////////////////////////////////
		// ///////////////////// Statistics Control Group ///////////////////
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
		// /////////////////// Options Control Group ////////////////////
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
		// ///////////////// OptionList Pane ///////////////////
		// /////////////////////////////////////////////////////////////////////

		// Add all of the components to the main content pane.
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(optionList);
		add(Box.createRigidArea(new Dimension(5, 0)));
		add(canvas);

		optionList.add(drawNetworkPanel);
		optionList.add(setupPanel);
		optionList.add(performancePanel);
		optionList.add(statisticsPanel);
		optionList.add(optionsPanel);
	}

	DecimalFormat numFormatter = new DecimalFormat("###,###,##0.00");

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
				}
			}

			// Draw the network on load if there is one selected.
			if (selectedNetwork != null) {
				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(dirNet.createOptimalNetwork(true));
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(omniNet.createOptimalNetwork(true));
				}
			}
		}

		// Draw a graph on the drawing action commands.
		if ("drawDir".equals(e.getActionCommand())) {
			if (dirNet != null) {
				drawGraph(dirNet.createOptimalNetwork(true));
			}
			// Keep track of which network is visible.
			selectedNetwork = NetworkType.DIRECTIONAL;

		} else if ("drawOmni".equals(e.getActionCommand())) {
			if (omniNet != null) {
				drawGraph(omniNet.createOptimalNetwork(true));
			}
			selectedNetwork = NetworkType.OMNIDIRECTIONAL;
		}

		// Make sure that a graph is loaded before any actual commands are
		// available.
		if (dirNet == null || omniNet == null) {
			JOptionPane.showMessageDialog(this.getRootPane(),
					"You must load a network graph before analyzing it.");
			return;
		}

		// Action event code for the shortest path retrieval.
		if ("getPath".equals(e.getActionCommand())) {

			// There must be a network currently selected.
			if (selectedNetwork == null) {
				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for"
								+ " which to find a path in.");
			} else

			// Code for the shortest specified path button.
			if (pathFromTextField.getText().equals("")
					|| pathToTextField.getText().equals("")) {
				// Create dialog to inform user to enter info.
				JOptionPane.showMessageDialog(this.getRootPane(),
						"Enter sensor names to find"
								+ " the shortest path between them.");
			} else {
				// Set up some temporary variables to prevent code repetition.
				String fromText = pathFromTextField.getText();
				String toText = pathToTextField.getText();
				List<Sensor> sPath = null;
				float sPathLen = 0f;
				int sPathLenHops = 0;

				// Get the route length.
				if (selectedNetwork != null
						&& NetworkType.DIRECTIONAL.equals(selectedNetwork)) {

					sPathLenHops = dirNet.getShortestPathLengthHops(fromText,
							toText);
					sPathLen = dirNet.getShortestPathLength(fromText, toText);
					sPath = dirNet.getShortestPath(fromText, toText);

				} else if (selectedNetwork != null
						&& NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {

					sPathLenHops = omniNet.getShortestPathLengthHops(fromText,
							toText);
					sPathLen = omniNet.getShortestPathLength(fromText, toText);
					sPath = omniNet.getShortestPath(fromText, toText);
				}

				pathLengthTextField.setText(numFormatter.format(sPathLen));
				pathLengthHopsTextField.setText(numFormatter
						.format(sPathLenHops));

				// Get and draw the route itself.
				// Paint over the canvas, but use its transform so we can draw
				// at the correct spot.
				if (sPath != null) {
					// Get the current elements graphics object (main panel).
					Graphics2D g = ((Graphics2D) getGraphics());
					// Redraw it so that any old paths are erased.
					update(g);

					// Apply the transformation to change our basis to the
					// canvas' basis.
					g.transform(canvas.getCanvasTransform());

					// Draw the path.
					for (int i = 0; i < sPath.size() - 1; i++) {
						Sensor v = sPath.get(i);
						Sensor u = sPath.get(i + 1);

						g.setColor(Color.RED);
						g.drawLine((int) v.getX(), (int) v.getY(),
								(int) u.getX(), (int) u.getY());
					}

					// Revert to the original basis.
					g.transform(canvas.getOriginTransform());
				}
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

			} else if (selectedNetwork != null) {

				// Check the value validity.
				if (newRangeText.equals("")) {
					// Create dialog to inform user to enter info.
					JOptionPane.showMessageDialog(this.getRootPane(),
							"Enter sensor range to be set.");
					return;
				}

				float newRange = Float.parseFloat(newRangeText);

				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(dirNet.createNetwork(newRange));
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(omniNet.createNetwork(newRange));
				}
			}

		} else if ("resetSetup".equals(e.getActionCommand())) {
			// There must be a network currently selected.
			if (selectedNetwork == null) {
				JOptionPane.showMessageDialog(this.getRootPane(),
						"A network must be selected for"
								+ " which to reset sensor range.");
			}

			rangeUpdateTextField.setText("");

			if (selectedNetwork != null) {
				if (NetworkType.DIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(dirNet.createOptimalNetwork(true));
				} else if (NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {
					drawGraph(omniNet.createOptimalNetwork(true));
				}
			}
		}

		// On any event we want to...
		if (selectedNetwork != null
				&& NetworkType.DIRECTIONAL.equals(selectedNetwork)) {

			// Update the normal graph statistics.
			String fAvgAngle = numFormatter.format(dirNet.getAverageAngle());
			String fAvgRange = numFormatter.format(dirNet.getAverageRange());
			double totEnergy = dirNet.getTotalEnergyUse() / 1000;
			averageAngleTextField.setText(fAvgAngle);
			averageRangeTextField.setText(fAvgRange);
			totalEnergyUseTextField.setText(numFormatter.format(totEnergy));

			// Update the average shortest path values.
			float ASPL = dirNet.getAverageShortestPathLength();
			float ASPLH = dirNet.getAverageShortestPathLengthHops();
			averageSPLTextField.setText(numFormatter.format(ASPL));
			averageSPLHopsTextField.setText(numFormatter.format(ASPLH));

			// Update the graph diameter.
			String fDiam = numFormatter.format(dirNet.getDiameter());
			String fDiamHops = numFormatter.format(dirNet.getDiameterHops());
			graphDiameterTextField.setText(fDiam);
			graphDiameterHopsTextField.setText(fDiamHops);

		} else if (selectedNetwork != null
				&& NetworkType.OMNIDIRECTIONAL.equals(selectedNetwork)) {

			// Update the normal graph statistics.
			String fAvgAngle = numFormatter.format(omniNet.getAverageAngle());
			String fAvgRange = numFormatter.format(omniNet.getAverageRange());
			double totEnergy = omniNet.getTotalEnergyUse() / 1000;
			averageAngleTextField.setText(fAvgAngle);
			averageRangeTextField.setText(fAvgRange);
			totalEnergyUseTextField.setText(numFormatter.format(totEnergy));

			// Update the average shortest path values.
			float ASPL = omniNet.getAverageShortestPathLength();
			float ASPLH = omniNet.getAverageShortestPathLengthHops();
			averageSPLTextField.setText(numFormatter.format(ASPL));
			averageSPLHopsTextField.setText(numFormatter.format(ASPLH));

			// Update the graph diameter.
			String fDiam = numFormatter.format(omniNet.getDiameter());
			String fDiamHops = numFormatter.format(omniNet.getDiameterHops());
			graphDiameterTextField.setText(fDiam);
			graphDiameterHopsTextField.setText(fDiamHops);
		}

	}

	// Redraw the input graph.
	private void drawGraph(WeightedGraph<Sensor, Link> g) {
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
