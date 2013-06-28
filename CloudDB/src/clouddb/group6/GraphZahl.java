package clouddb.group6;

import java.util.Arrays;
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
import com.infinitegraph.indexing.IndexDescription;
import com.infinitegraph.indexing.IndexException;
import com.infinitegraph.indexing.IndexManager;

public class GraphZahl {

	final static Logger logger = LoggerFactory.getLogger(GraphZahl.class);

	private static GraphDatabase graphDB;
	private static final String graphDbName = "GraphZahl";
	private static final String propFile = "config/CloudDB.properties";


	private static boolean setUpDB() {
		try {
			//	try{
			//		// this is broken for some reason b/c our license seems to be wrong!?
			//		GraphFactory.delete(graphDbName, propFile);
			//	} catch (final StorageException e) {
			//		logger.info(e.getMessage());
			//	}

			try{
				logger.info("> Creating graph database ...");
				GraphFactory.create(graphDbName,propFile);
			} catch (final StorageException e) {
				logger.info(e.getMessage());
			}

			// Open graph database
			logger.info("> Opening graph database ...");
			graphDB = GraphFactory.open(graphDbName,propFile);

			ensureGraphIndex("personGraphIndex", Person.class.getName(), new String[]{"id"}, true);

			return graphDB != null;
		} catch (final ConfigurationException e) {
			logger.info(e.getMessage());
			return false;
		} catch (final IndexException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Ensures that the given index is set.
	 * 
	 * @param graphIndexName the name of the GraphIndex
	 * @param indexedClassName the indexed classes name
	 * @param indexedFields the indexed fields of the class
	 * @param isUnique if the index allows indexing different values for same key
	 * @throws IndexException
	 */
	private static void ensureGraphIndex(final String graphIndexName, final String indexedClassName,
			final String[] indexedFields, final boolean isUnique) throws IndexException {
		final IndexDescription[] indices = IndexManager.listAllGraphIndexes();
		boolean exists = false;
		// scan all graph indices for matching index
		// shallow-compare given arrays
		for (final IndexDescription id : indices) {
			if(	id.getIndexName().equals(graphIndexName) &&
					id.getIndexedClass().equals(indexedClassName) &&
					Arrays.equals(id.getIndexedFields(), indexedFields)){
				exists = true;
				break;
			}
		}
		if(!exists){
			Transaction tx = null;
			try{
				tx = graphDB.beginTransaction(AccessMode.READ_WRITE);
				IndexManager.addGraphIndex(graphIndexName, indexedClassName, indexedFields, isUnique);
				logger.info("Creating index: " + graphIndexName);
				tx.commit();
			} catch (final Exception e){
				logger.info(e.getLocalizedMessage());
				tx.rollback();
			} finally {
				tx.complete();
			}
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
		try{
			logger.info("> Starting sample data write transaction ...");
			tx = graphDB.beginTransaction(AccessMode.READ_WRITE);

			final Person p1 = new Person(1, "George Bluth, Sr.", true, "Fugitive");
			final Person p2 = new Person(2, "Oscar Bluth", true, "");
			// TODO add more persons


			graphDB.addVertex(p1);
			logger.info(">> Added " + p1.getId());
			graphDB.addVertex(p2);
			logger.info(">> Added " + p2.getId());
			// TODO add more vertices

			final Relationship r12 = new Relationship("Brother");
			final Relationship r21 = new Relationship("Brother");
			// TODO add more relationships

			p1.addEdge(r12, p2, EdgeKind.OUTGOING, (short) 0.15);
			p2.addEdge(r21, p1, EdgeKind.OUTGOING, (short) 0.15);
			// TODO add more edges

			logger.info("> Committing sample data ...");
			tx.commit();
		} catch (final Exception e){
			e.printStackTrace();
			tx.rollback();
		} finally{
			tx.complete();
		}
	}

	public static void query(final String q) {
		Transaction tx = null;
		try{
			tx = graphDB.beginTransaction(AccessMode.READ);
			final Query<Person> res = graphDB
					.createQuery(Person.class.getName(), q);
			try {
				final Iterator<Person> iter = res.execute();
				while (iter.hasNext()) {
					final Person p = iter.next();
					logger.info(">> found: Person{" + p.getId() + "," + p.name + ", " + p.id + "}");
				}
			} catch (final GraphException e) {
				e.printStackTrace();
			}
			tx.commit();
		} catch (final Exception e){
			e.printStackTrace();
			tx.rollback();
		} finally {
			tx.complete();
		}
	}

	public static void main(final String[] args) {
		// wether the db was set up/connected properly
		final boolean setup = setUpDB();
		if(!setup) return;

		final boolean sampleLoaded = testSampleGraphImported();
		if(!sampleLoaded) loadSampleGraph();
		query("name=='Oscar Bluth'");
		closeDB();
	}

	private static boolean testSampleGraphImported() {
		Transaction tx = null;
		boolean imported = false;
		try {
			tx = graphDB.beginTransaction(AccessMode.READ);
			final Query<Person> res = graphDB
					.createQuery(Person.class.getName(), "");
			try {
				final Iterator<Person> iter = res.execute();
				// if we got any person node, we assume for now
				// that all sample data is present
				if(imported = iter.hasNext()){
					logger.info(">> Sample data already imported.");
				}
			} catch (final GraphException e) {
				e.printStackTrace();
			}
			tx.commit();
		} catch (final Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			tx.complete();
		}
		if(!imported) logger.info(">> Seems like no import has been done yet...");
		return imported;
	}

}

class Person extends BaseVertex {
	int id;
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