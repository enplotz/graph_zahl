package com.infinitegraph.samples.hellograph;

/*
 COPYRIGHT AND DISCLAIMER NOTICE
 =========================================

 The following copyright and disclaimer notice applies to all files
 included in this application

 Objectivity, Inc. grants you a nonexclusive copyright license to use all
 programming code examples from which you can generate similar function
 tailored to your own specific needs.

 All sample code is provided by Objectivity, Inc. for illustrative
 purposes only. These examples have not been thoroughly tested under all
 conditions. Objectivity, Inc., therefore, cannot guarantee or imply
 reliability, serviceability, or function of these programs.

 All programs contained herein are provided to you "AS IS" without any
 warranties or indemnities of any kind. The implied warranties of
 non-infringement, merchantability and fitness for a particular purpose
 are expressly disclaimed.
 */

// Import all InfiniteGraph packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.BaseEdge;
import com.infinitegraph.BaseVertex;
import com.infinitegraph.ConfigurationException;
import com.infinitegraph.EdgeKind;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;

// Import SLF4J logging packages

public class HelloGraph {

	public static void main(final String[] args) {
		// Set up logging for the HelloGraph class
		final Logger logger = LoggerFactory.getLogger(HelloGraph.class);

		// Create null transaction, null graph database instance
		Transaction tx = null;
		GraphDatabase helloGraphDB = null;

		// Name for graph database
		final String graphDbName = "HelloGraph";

		try {
			try {
				// Delete graph database if it already exists
				GraphFactory.delete(graphDbName);
			} catch (final StorageException sE) {
				logger.info(sE.getMessage());
			}

			// HINT: Add code to create graph database and its contents
			// Create graph database
			logger.info("> Creating graph database ...");
			GraphFactory.create(graphDbName);

			// Open graph database
			logger.info("> Opening graph database ...");
			helloGraphDB = GraphFactory.open(graphDbName);

			// Begin transaction
			logger.info("> Starting a read/write transaction ...");
			tx = helloGraphDB.beginTransaction(AccessMode.READ_WRITE);

			// Create two Person instances, adding them to the graph
			// HINT: Add code to create vertices and edges here.
			logger.info("> Creating Person vertices ...");
			final Person john = new Person("John", "Hello ");
			helloGraphDB.addVertex(john);
			final Person dana = new Person("Dana", "Database!");
			helloGraphDB.addVertex(dana);

			// Create a Meeting instance
			logger.info("> Creating Meeting edge ...");
			final Meeting meeting1 = new Meeting("NY", "Graph");

			// Connect john to dana across the meeting1 edge
			logger.info("> Connecting vertices ...");
			john.addEdge(meeting1, dana, EdgeKind.BIDIRECTIONAL, (short) 0);

			// Specify john as a named root for traversal
			logger.info("> Naming a root vertex ...");
			helloGraphDB.nameVertex("John", john);

			// Commit to save your changes to the graph database
			logger.info("> Committing changes ...");
			tx.commit();
		}

		catch (final ConfigurationException cE) {
			logger.warn("> Configuration Exception was thrown ... ");
			logger.error(cE.getMessage());
		}

		finally {
			// If the transaction was not committed, complete
			// will roll it back
			if (tx != null)
				tx.complete();
			if (helloGraphDB != null) {
				helloGraphDB.close();
				logger.info("> On Exit: Closed graph database");
			}
		}
	}
}

// HINT: Add class definitions for Person and Meeting classes here.

class Person extends BaseVertex {
	private String name;
	private String slogan;

	public Person(final String name, final String slogan) {
		setName(name);
		setSlogan(slogan);
	}

	public void setName(final String name) {
		markModified();
		this.name = name;
	}

	public String getName() {
		fetch();
		return this.name;
	}

	public void setSlogan(final String slogan) {
		markModified();
		this.slogan = slogan;
	}

	@Override
	public String toString() {
		fetch();
		return this.slogan;
	}
}

class Meeting extends BaseEdge {
	private String location;
	private String message;

	public Meeting(final String location, final String message) {
		setLocation(location);
		setMessage(message);
	}

	public void setLocation(final String location) {
		markModified();
		this.location = location;
	}

	public String getLocation() {
		fetch();
		return this.location;
	}

	public void setMessage(final String message) {
		markModified();
		this.message = message;
	}

	@Override
	public String toString() {
		fetch();
		return this.message;
	}
}
