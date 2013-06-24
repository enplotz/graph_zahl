package clouddb.group6;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.BaseEdge;
import com.infinitegraph.BaseVertex;
import com.infinitegraph.ConfigurationException;
import com.infinitegraph.EdgeKind;
import com.infinitegraph.GraphDatabase;
import com.infinitegraph.GraphException;
import com.infinitegraph.GraphFactory;
import com.infinitegraph.Query;
import com.infinitegraph.StorageException;
import com.infinitegraph.Transaction;

public class GraphZahl {

	final static Logger logger = LoggerFactory.getLogger(GraphZahl.class);

	private static GraphDatabase graphDB;

	private static void setUpDB() {
		// Name for graph database
		final String graphDbName = "Graph Zahl";

		try {
			// Delete graph database if it already exists
			// GraphFactory.delete(graphDbName);

			logger.info("> Creating graph database ...");
			GraphFactory.create(graphDbName);

			// Open graph database
			logger.info("> Opening graph database ...");
			graphDB = GraphFactory.open(graphDbName);

			// final Transaction tx = graphDB
			// .beginTransaction(AccessMode.READ_WRITE);
			// try {
			// IndexManager.addGraphIndex("personGraphIndex",
			// Person.class.getName(), new String[] { "id" }, true);
			// } catch (final IndexException e) {
			// e.printStackTrace();
			// }
			// tx.commit();
			// tx.complete();

		} catch (final StorageException e) {
			logger.info(e.getMessage());
		} catch (final ConfigurationException e) {
			logger.info(e.getMessage());
		}
	}

	private static void closeDB() {
		if (graphDB != null) {
			graphDB.close();
			logger.info("> On Exit: Closed graph database");
		}
	}

	private static void loadSampleGraph() {
		Transaction tx = null;
		logger.info("> Starting sample data write transaction ...");
		tx = graphDB.beginTransaction(AccessMode.READ_WRITE);

		final Person p1 = new Person(1, "George Bluth, Sr.", true, "Fugitive");
		final Person p2 = new Person(2, "Oscar Bluth", true, "");

		graphDB.addVertex(p1);
		graphDB.addVertex(p2);

		final Relationship r12 = new Relationship("Brother");
		final Relationship r21 = new Relationship("Brother");

		p1.addEdge(r12, p2, EdgeKind.OUTGOING, (short) 0.15);
		p2.addEdge(r21, p1, EdgeKind.OUTGOING, (short) 0.15);

		logger.info("> Committing sample data ...");
		tx.commit();
		tx.complete();
	}

	public static void query(final String q) {
		Transaction tx = null;
		tx = graphDB.beginTransaction(AccessMode.READ);
		final Query<Person> res = graphDB
				.createQuery(Person.class.getName(), q);
		try {
			final Iterator<Person> iter = res.execute();
			while (iter.hasNext()) {
				final Person p = iter.next();
				System.out.println(p.id);
			}
		} catch (final GraphException e) {
			e.printStackTrace();
		}
		tx.commit();
		tx.complete();
	}

	public static void main(final String[] args) {
		setUpDB();
		loadSampleGraph();
		query("name=\"Oscar Bluth\"");
		closeDB();
	}

}

class Person extends BaseVertex {
	final int id;
	String name;
	boolean gender;
	String occupation;

	public Person(final int id, final String name, final boolean gender,
			final String occupation) {
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.occupation = occupation;
	}
}

class Relationship extends BaseEdge {
	String relationship;

	public Relationship(final String relationship) {
		this.relationship = relationship;
	}
}