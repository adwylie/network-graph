package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import logging.FileLogger;
import ui.NetworkGUI;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-09
 */
public class GraphParser {

	// Set logging true if this is a compiled class, false if the class is
	// in a jar file.
	private static boolean logging = !NetworkGUI.class.getProtectionDomain()
			.getCodeSource().getLocation().toString().contains("jar");

	public static WeightedGraph<Node, Link> parse(File file) {
		// Tokens
		String NODE = "NODE";
		String EDGE = "EDGE";

		String OPEN_PARENTH = "(";
		String CLOSE_PARENTH = ")";
		String COMMA = ",";
		String WHITESPACE = "\\s";

		// Read the file.
		String fileText = "";
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));

			String line = null;
			while ((line = reader.readLine()) != null) {
				fileText = fileText.concat(line).concat("\n");
			}

		} catch (IOException e) {

			if (logging) {
				// LOGGING
				FileLogger.log(Level.SEVERE, NetworkGUI.class.getName()
						+ ": parseGraph; Error reading file.");
			}
		}

		if (fileText.equals("")) {
			if (logging) {
				// LOGGING
				FileLogger.log(Level.WARNING, NetworkGUI.class.getName()
						+ ": parseGraph; Empty file loaded.");
			}

			return null;
		}

		// Tokenize the file content.
		String[] tokens = fileText.split("(" + WHITESPACE + "+|\\" + COMMA
				+ "|\\" + OPEN_PARENTH + "|\\" + CLOSE_PARENTH + ")+");

		// Parse & Create the representation.
		WeightedGraph<Node, Link> pn = new WeightedGraph<Node, Link>();
		Hashtable<String, Node> nodes = new Hashtable<String, Node>();
		List<String> tokensList = Arrays.asList(tokens);
		Iterator<String> tokensIter = tokensList.iterator();

		String currentObj = "";
		String nodeName = "";
		String nodeX = "";
		String nodeY = "";
		String edgeFrom = "";
		String edgeTo = "";

		while (tokensIter.hasNext()) {
			currentObj = tokensIter.next();

			// We will have either a node or an edge.
			// If not throw away tokens until we are back in our grammar.
			if (!currentObj.equals(NODE) && !currentObj.equals(EDGE)) {
				if (logging) {
					// LOGGING
					FileLogger.log(Level.WARNING, NetworkGUI.class.getName()
							+ ": parseGraph; Error finding object, discarding"
							+ " token and continuing parsing.");
				}

				continue;
			}

			// After we know the current object get its properties.
			if (currentObj.equals(NODE)) {

				if (tokensIter.hasNext()) {
					nodeName = tokensIter.next();
				}
				if (tokensIter.hasNext()) {
					nodeX = tokensIter.next();
				}
				if (tokensIter.hasNext()) {
					nodeY = tokensIter.next();
				} else {
					if (logging) {
						// LOGGING
						FileLogger.log(Level.WARNING,
								NetworkGUI.class.getName()
										+ ": parseGraph; Error adding node."
										+ " No more tokens.");
					}

					continue;
				}

				if (logging) {
					// LOGGING
					FileLogger.log(Level.INFO, NetworkGUI.class.getName()
							+ ": parseGraph; Added node(" + nodeName + ", "
							+ nodeX + ", " + nodeY + ").");
				}

				Node n = new Node(nodeName, Float.parseFloat(nodeX),
						Float.parseFloat(nodeY));

				pn.insertVertex(n);
				nodes.put(nodeName, n);
			}

			if (currentObj.equals(EDGE)) {

				if (tokensIter.hasNext()) {
					edgeFrom = tokensIter.next();
				}
				if (tokensIter.hasNext()) {
					edgeTo = tokensIter.next();
				} else {
					if (logging) {
						// LOGGING
						FileLogger.log(Level.WARNING,
								NetworkGUI.class.getName()
										+ ": parseGraph; Error adding edge."
										+ " No more tokens.");
					}

					continue;
				}

				Node from = nodes.get(edgeFrom);
				Node to = nodes.get(edgeTo);

				// Trying to add an edge to node(s) which don't exist.
				if (from == null || to == null) {
					if (logging) {
						// LOGGING
						FileLogger
								.log(Level.WARNING,
										NetworkGUI.class.getName()
												+ ": parseGraph; Error adding edge. Some or all"
												+ " of the nodes to connect do not exist.");
					}

					continue;
				}

				if (logging) {
					// LOGGING
					FileLogger.log(Level.INFO, NetworkGUI.class.getName()
							+ ": parseGraph; Added edge(" + edgeFrom + ", "
							+ edgeTo + ").");
				}

				pn.insertEdge(from, to, new Link(from.getName() + to.getName()));
			}

		}

		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (logging) {
			// LOGGING
			FileLogger.log(Level.INFO, NetworkGUI.class.getName()
					+ ": parseGraph; Parsing Completed. Network Loaded.");
		}

		return pn;
	}
}
