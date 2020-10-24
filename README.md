# JJeuxDeMots
A Java API for accessing JeuxDeMots's graph and lexical models. 
The API offers a low level access abstraction that supports loading JDM from the dump files (high memory requirement) or from an SQL database (see instruction to load JDM into MySQL [here]( doc/load_jdm_mysql.md)).

The Graph API let's one manipulate nodes and edges (the basic graph model of JDM) and the LexicalAspect allows access to the graph at a higher-level from the perspective of lexical semantics (like Diko), following the LMF/Ontolex model. 

## Loading JDM

### From dump (! 32GB+ RAM)

```java
JDMLoader loader = new JDMLoaderFromDump(new FileInputStream("/path/to/jdm.txt"));

final JeuxDeMots jeuxDeMots = loader.load();
```

### From jdbc

```java
final String jdbcUrl = "jdbc://...";
final Connection connection = DriverManager.getConnection(jdbcUrl);
final Connection streamConnection = DriverManager.getConnection(jdbcUrl);
final JeuxDeMots jeuxDeMots = new SQLJeuxDeMots(connection, streamConnection);
```

## Using the Graph-level API

Here are some example of how to use the graph level API:

```java
Optional<JDMNode> voiture = jeuxDeMots.getNode("voiture");
Optional<JDMRelationType> synonym = jeuxDeMots.findType("r_syn");
if (voiture.isPresent() && synonym.isPresent()){
	Collection<JDMRelation> synonyms = jeuxDeMots.getOutgoingRelations(synonym.get(), voiture.get());
	for (JDMRelation relation : synonyms){
    	int target = relation.getTarget();
    	Optional<JDMNode> targetNode = jeuxDeMots.getNode(target);
    	tagetNode.ifPresent(n -> System.out.println("SYN:"+n.getName());
	}

	Collection<JDMRelation> isSynonymOf =  jeuxDeMots.getIncomingRelations(voiture.get(), synonym.get());

	for (JDMRelation relation : isSynonymOf){
    	int source = relation.getSource();
    	Optional<JDMNode> sourceNode = jeuxDeMots.getNode(source);
    	sourceNode.ifPresent(n -> System.out.println("SYN OF:"+n.getName());
	}
}

JDMRelationType raff = jeuxDeMots.findType("r_raff_sem");

Collection<JDMRelation> senseRelations = jeuxDeMots.getOutgoingRelations(voiture, raff);

System.out.println("Senses of "+voiture.getName())

for (JDMRelation relation : senseRelations){
    int target = relation.getTarget();
    JDMNode senseNode = jeuxDeMots.getNode(target);
    System.out.println("\t>SENSE :"+senseNode.getName())
}
```



### Using the Lexical aspect

```JAVA
final JDMLexicalAspect lexicalAspect = new DefaultJDMLexicalAspect(jeuxDeMots);

```

+

