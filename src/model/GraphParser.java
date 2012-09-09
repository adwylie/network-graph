package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andrew Wylie <andrew.dale.wylie@gmail.com>
 * @version 1.0
 * @since 2012-09-09
 */
public class GraphParser {

	private String fileText = null;

	public WeightedGraph<Node, Link> parse(File file) {
		// Tokens
		String NODE = "NODE";
		String EDGE = "EDGE";

		String OPEN_PARENTH = "\\(";
		String CLOSE_PARENTH = "\\)";
		String COMMA = "\\,";
		String WHITESPACE = "\\s";

		readFile(file);

		// Tokenize the file content.
		String[] tokens = fileText.split("(" + WHITESPACE + "+|" + COMMA + "|"
				+ OPEN_PARENTH + "|" + CLOSE_PARENTH + ")+");

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
					continue;
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
					continue;
				}

				Node from = nodes.get(edgeFrom);
				Node to = nodes.get(edgeTo);

				// Trying to add an edge to node(s) which don't exist.
				if (from == null || to == null) {
					continue;
				}

				pn.insertEdge(from, to, new Link(from.getName() + to.getName()));
			}
		}

		return pn;
	}

	private void readFile(File file) {

		String text = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				text = text.concat(line).concat("\n");
			}

			reader.close();

		} catch (FileNotFoundException fnfException) {
			// Opening the file failed.
		} catch (IOException ioException) {
			// Reading or closing the file failed.
		}

		fileText = text;
	}

}
