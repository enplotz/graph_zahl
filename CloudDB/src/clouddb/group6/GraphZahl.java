package clouddb.group6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infinitegraph.AccessMode;
import com.infinitegraph.BaseEdge;
import com.infinitegraph.BaseVertex;
import com.infinitegraph.ConfigurationException;
import com.infinitegraph.Edge;
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
import com.infinitegraph.navigation.GraphView;
import com.infinitegraph.navigation.Guide;
import com.infinitegraph.navigation.NavigationResultHandler;
import com.infinitegraph.navigation.Navigator;
import com.infinitegraph.navigation.Path;
import com.infinitegraph.navigation.Qualifier;

public class GraphZahl {

	final static Logger logger = LoggerFactory.getLogger(GraphZahl.class);

	private static GraphDatabase graphDB;
	private static final String graphDbName = "GraphZahl";
	private static final String propFile = "config/CloudDB.properties";

	private static boolean setUpDB() {
		try {
			// try{
			// // this is broken for some reason b/c our license seems to be
			// wrong!?
			// GraphFactory.delete(graphDbName, propFile);
			// } catch (final StorageException e) {
			// logger.info(e.getMessage());
			// }

			try {
				logger.info("> Creating graph database ...");
				GraphFactory.create(graphDbName, propFile);
			} catch (final StorageException e) {
				logger.info(e.getMessage());
			}

			// Open graph database
			logger.info("> Opening graph database ...");
			graphDB = GraphFactory.open(graphDbName, propFile);

			ensureGraphIndex("personGraphIndex", Person.class.getName(),
					new String[] { "id" }, true);

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
	 * @param graphIndexName
	 *            the name of the GraphIndex
	 * @param indexedClassName
	 *            the indexed classes name
	 * @param indexedFields
	 *            the indexed fields of the class
	 * @param isUnique
	 *            if the index allows indexing different values for same key
	 * @throws IndexException
	 */
	private static void ensureGraphIndex(final String graphIndexName,
			final String indexedClassName, final String[] indexedFields,
			final boolean isUnique) throws IndexException {
		final IndexDescription[] indices = IndexManager.listAllGraphIndexes();
		boolean exists = false;
		// scan all graph indices for matching index
		// shallow-compare given arrays
		for (final IndexDescription id : indices) {
			if (id.getIndexName().equals(graphIndexName)
					&& id.getIndexedClass().equals(indexedClassName)
					&& Arrays.equals(id.getIndexedFields(), indexedFields)) {
				exists = true;
				break;
			}
		}
		if (!exists) {
			Transaction tx = null;
			try {
				tx = graphDB.beginTransaction(AccessMode.READ_WRITE);
				IndexManager.addGraphIndex(graphIndexName, indexedClassName,
						indexedFields, isUnique);
				logger.info("Creating index: " + graphIndexName);
				tx.commit();
			} catch (final Exception e) {
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
		try {
			logger.info("> Starting sample data write transaction ...");
			tx = graphDB.beginTransaction(AccessMode.READ_WRITE);

			final Person p1 = new Person(1, "George Bluth, Sr.", true,
					"Fugitive");
			final Person p2 = new Person(2, "Oscar Bluth", true, "");
			final Person p3 = new Person(3, "George Oscar \"GOB\" Bluth", true,
					"Magician");
			final Person p4 = new Person(4, "Tobias Fünke", true,
					"Unemployed Actor");
			final Person p5 = new Person(5, "Lindsay Fünke", false, "Activist");
			final Person p6 = new Person(6, "Lucille Bluth", false, "");
			final Person p7 = new Person(7, "Byron \"Buster\" Bluth", true,
					"Seal Victim");
			final Person p8 = new Person(8, "Michael Bluth", true,
					"Bluth Company CEO");
			final Person p9 = new Person(9, "George Michael Bluth", true,
					"Student");
			final Person p10 = new Person(10, "Tracy Bluth", false, "");
			final Person p11 = new Person(11, "Mae \"Maeby\" Fünke", false,
					"Student");
			final Person p12 = new Person(12, "Surely Woolfbeek", false,
					"Imaginary Character");
			final Person p13 = new Person(13, "Steve Holt", true, "Student");
			final Person p14 = new Person(14, "Eve Holt", false, "");

			graphDB.addVertex(p1);
			logger.info(">> Added " + p1.getId());
			graphDB.addVertex(p2);
			logger.info(">> Added " + p2.getId());
			graphDB.addVertex(p3);
			logger.info(">> Added " + p3.getId());
			graphDB.addVertex(p4);
			logger.info(">> Added " + p4.getId());
			graphDB.addVertex(p5);
			logger.info(">> Added " + p5.getId());
			graphDB.addVertex(p6);
			logger.info(">> Added " + p6.getId());
			graphDB.addVertex(p7);
			logger.info(">> Added " + p7.getId());
			graphDB.addVertex(p8);
			logger.info(">> Added " + p8.getId());
			graphDB.addVertex(p9);
			logger.info(">> Added " + p9.getId());
			graphDB.addVertex(p10);
			logger.info(">> Added " + p10.getId());
			graphDB.addVertex(p11);
			logger.info(">> Added " + p11.getId());
			graphDB.addVertex(p12);
			logger.info(">> Added " + p12.getId());
			graphDB.addVertex(p13);
			logger.info(">> Added " + p13.getId());
			graphDB.addVertex(p14);
			logger.info(">> Added " + p14.getId());

			final Relationship r12 = new Relationship(12, "Brother");
			final Relationship r21 = new Relationship(21, "Brother");
			final Relationship r16 = new Relationship(16, "Wife");
			final Relationship r61 = new Relationship(61, "Husband");
			final Relationship r23 = new Relationship(23, "Uncle");
			final Relationship r32 = new Relationship(32, "Nephew");
			final Relationship r45 = new Relationship(45, "Husband");
			final Relationship r54 = new Relationship(54, "Wife");
			final Relationship r67 = new Relationship(67, "Mother");
			final Relationship r76 = new Relationship(76, "Son");
			final Relationship r38 = new Relationship(38, "Brother");
			final Relationship r83 = new Relationship(83, "Brother");
			final Relationship r58 = new Relationship(58, "Sister");
			final Relationship r85 = new Relationship(85, "Sister");
			final Relationship r78 = new Relationship(78, "Brother");
			final Relationship r87 = new Relationship(87, "Brother");
			final Relationship r89 = new Relationship(89, "Father");
			final Relationship r98 = new Relationship(98, "Son");
			final Relationship r910 = new Relationship(910, "Son");
			final Relationship r109 = new Relationship(109, "Mother");
			final Relationship r911 = new Relationship(911, "Cousin, maybe");
			final Relationship r119 = new Relationship(119, "Cousin, maybe");
			final Relationship r1112 = new Relationship(1112, "Alter Ego");
			final Relationship r14 = new Relationship(14, "Father-in-Law");
			final Relationship r41 = new Relationship(41, "Son-in-Law");
			final Relationship r1314 = new Relationship(1314, "Son");
			final Relationship r1413 = new Relationship(1413, "Mother");

			p1.addEdge(r12, p2, EdgeKind.OUTGOING, (short) 0.15);
			p2.addEdge(r21, p1, EdgeKind.OUTGOING, (short) 0.15);
			p1.addEdge(r16, p6, EdgeKind.OUTGOING, (short) 0.48);
			p6.addEdge(r61, p1, EdgeKind.OUTGOING, (short) 0.48);
			p2.addEdge(r23, p3, EdgeKind.OUTGOING, (short) 0.5);
			p3.addEdge(r32, p2, EdgeKind.OUTGOING, (short) 0.5);
			p4.addEdge(r45, p5, EdgeKind.OUTGOING, (short) 0.03);
			p5.addEdge(r54, p4, EdgeKind.OUTGOING, (short) 0.03);
			p6.addEdge(r67, p7, EdgeKind.OUTGOING, (short) 0.86);
			p7.addEdge(r76, p6, EdgeKind.OUTGOING, (short) 0.86);
			p3.addEdge(r38, p8, EdgeKind.OUTGOING, (short) 0.9);
			p8.addEdge(r83, p3, EdgeKind.OUTGOING, (short) 0.9);
			p5.addEdge(r58, p8, EdgeKind.OUTGOING, (short) 0.65);
			p8.addEdge(r85, p5, EdgeKind.OUTGOING, (short) 0.65);
			p7.addEdge(r78, p8, EdgeKind.OUTGOING, (short) 0.27);
			p8.addEdge(r87, p7, EdgeKind.OUTGOING, (short) 0.27);
			p8.addEdge(r89, p9, EdgeKind.OUTGOING, (short) 0.54);
			p9.addEdge(r98, p8, EdgeKind.OUTGOING, (short) 0.54);
			p9.addEdge(r910, p10, EdgeKind.OUTGOING, (short) 0.94);
			p10.addEdge(r109, p9, EdgeKind.OUTGOING, (short) 0.94);
			p9.addEdge(r911, p11, EdgeKind.OUTGOING, (short) 0.87);
			p11.addEdge(r119, p9, EdgeKind.OUTGOING, (short) 0.87);
			p11.addEdge(r1112, p12, EdgeKind.OUTGOING, (short) 0.19);
			p1.addEdge(r14, p4, EdgeKind.OUTGOING, (short) 0.72);
			p4.addEdge(r41, p1, EdgeKind.OUTGOING, (short) 0.72);
			p13.addEdge(r1314, p14, EdgeKind.OUTGOING, (short) 0.57);
			p14.addEdge(r1413, p13, EdgeKind.OUTGOING, (short) 0.57);

			logger.info("> Committing sample data ...");
			tx.commit();
		} catch (final Exception e) {
			e.printStackTrace();
			tx.rollback();
		} finally {
			tx.complete();
		}
	}

	public static Person queryPersonByName(final String name) {
		Person result = null;
		final Query<Person> res = graphDB.createQuery(Person.class.getName(),
				"name='" + name + "'");
		try {
			final Iterator<Person> iter = res.execute();
			while (iter.hasNext()) {
				final Person p = iter.next();
				logger.info(">> found: Person{" + p.getId() + "," + p.name
						+ ", " + p.id + "}");
				result = p;
			}
		} catch (final GraphException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Relationship queryRelationshipByName(final String name) {
		Transaction tx = null;
		Relationship result = null;
		try {
			tx = graphDB.beginTransaction(AccessMode.READ);
			final Query<Relationship> res = graphDB
					.createQuery(Relationship.class.getName(), "relationship='"
							+ name + "'");
			try {
				final Iterator<Relationship> iter = res.execute();
				while (iter.hasNext()) {
					final Relationship p = iter.next();
					logger.info(">> found: Relation{" + p.getId() + ","
							+ p.getRelationship() + "}");
					result = p;
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
		return result;
	}

	public static boolean isBridge(final Relationship r) {

		final Person source = (Person) r.getOrigin().getVertex();
		final Person target = (Person) r.getTarget().getVertex();

		final GraphView view = new GraphView();
		view.excludeClass(graphDB.getTypeId(Relationship.class.getName()),
				"identifier ==" + r.getIdentifier());

		final Set<Person> fromSource = transitivelyReachable(view, source);
		final Set<Person> fromTarget = transitivelyReachable(view, target);
		if (fromSource.contains(target)) {
			return false;
		}
		if (fromTarget.contains(source)) {
			return false;
		}
		return true;
	}

	public static Set<Person> transitivelyReachable(final Person person) {
		return transitivelyReachable(null, person);
	}

	public static Set<Person> transitivelyReachable(final GraphView view,
			final Person person) {
		final Set<Person> result = new HashSet<Person>();

		final Navigator navi = person.navigate(view, Guide.SIMPLE_DEPTH_FIRST,
				Qualifier.FOREVER, Qualifier.ANY, null,
				new NavigationResultHandler() {

					@Override
					public void handleResultPath(final Path p, final Navigator n) {
						// needed to load data ?!
						p.getFinalHop().getVertex().toString();
						result.add((Person) p.getFinalHop().getVertex());
					}

					@Override
					public void handleNavigatorFinished(final Navigator n) {
						// no-op
					}
				});

		navi.start();
		navi.stop();

		return result;
	}

	public static List<Person> friendsOfFriends(final Person person) {
		final List<Person> result = new ArrayList<Person>();

		final Navigator navi = person.navigate(Guide.SIMPLE_BREADTH_FIRST,
				new Qualifier() {

					@Override
					public boolean qualify(final Path p) {
						return p.size() < 4;
					}
				}, new Qualifier() {

					@Override
					public boolean qualify(final Path p) {
						return p.size() == 3;
					}
				}, new NavigationResultHandler() {

					@Override
					public void handleResultPath(final Path p, final Navigator n) {
						// needed to load data ?!
						p.getFinalHop().getVertex().toString();
						result.add((Person) p.getFinalHop().getVertex());
					}

					@Override
					public void handleNavigatorFinished(final Navigator n) {
						logger.info("Friends of friends done!");
					}
				});

		navi.start();
		navi.stop();

		return result;
	}

	public static void main(final String[] args) {
		// wether the db was set up/connected properly
		final boolean setup = setUpDB();
		if (!setup)
			return;

		final boolean sampleLoaded = testSampleGraphImported();
		if (!sampleLoaded)
			loadSampleGraph();

		final Transaction tx = graphDB.beginTransaction(AccessMode.READ);

		final Person oscar = queryPersonByName("Oscar Bluth");
		logger.info("Friends of friends of Oscar Bluth");
		final List<Person> fof = friendsOfFriends(oscar);
		for (final Person p : fof) {
			logger.info(String.valueOf(p.id));
		}

		logger.info("Transitively reachable from Oscar Bluth");
		final Set<Person> res = transitivelyReachable(oscar);
		for (final Person p : res) {
			logger.info(String.valueOf(p.id));
		}

		logger.info("Find all Bridges in the graph:");
		final Iterable<Edge> edges = graphDB.getEdges();

		for (final Edge e : edges) {
			final Relationship r = (Relationship) e;
			if (isBridge(r)) {
				logger.info("Bridge: " + r.getIdentifier());
			}
		}

		tx.commit();
		tx.complete();

		closeDB();
	}

	private static boolean testSampleGraphImported() {
		Transaction tx = null;
		boolean imported = false;
		try {
			tx = graphDB.beginTransaction(AccessMode.READ);
			final Query<Person> res = graphDB.createQuery(
					Person.class.getName(), "");
			try {
				final Iterator<Person> iter = res.execute();
				// if we got any person node, we assume for now
				// that all sample data is present
				if (imported = iter.hasNext()) {
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
		if (!imported)
			logger.info(">> Seems like no import has been done yet...");
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

	@Override
	public int hashCode() {
		return 31 * id + 17;
	}
}

class Relationship extends BaseEdge {
	private String relationship;

	private int identifier;

	public Relationship(final int identifier, final String relationship) {
		this.setRelationship(relationship);
		this.setIdentifier(identifier);
	}

	public String getRelationship() {
		fetch();
		return relationship;
	}

	public void setRelationship(final String relationship) {
		markModified();
		this.relationship = relationship;
	}

	public int getIdentifier() {
		fetch();
		return identifier;
	}

	public void setIdentifier(final int identifier) {
		markModified();
		this.identifier = identifier;
	}
}