package org.neo4j.contrib.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Rule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.schema.AnalyzerProvider;
import org.neo4j.harness.junit.rule.Neo4jRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.neo4j.contrib.analyzers.DoublePipeAnalyzerProvider.ANALYZER_NAME;
import static org.neo4j.contrib.analyzers.DoublePipeAnalyzerProvider.DESCRIPTION;
import static org.neo4j.contrib.analyzers.TestUtils.assertResultCount;

public class DoublePipeTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule();

    @Test
    public void checkTokenStream() {
        AnalyzerProvider provider = TestUtils.analyzerProviderByName(ANALYZER_NAME);
        assertNotNull(provider);
        assertEquals(DESCRIPTION, provider.description());
        Analyzer analzyer = provider.createAnalyzer();
        assertThat(TestUtils.analyze("AA||BB", analzyer), contains("aa", "bb"));
        assertThat(TestUtils.analyze("AA xX||BB", analzyer), contains("aa xx", "bb"));
        assertThat(TestUtils.analyze("AA|BB", analzyer), contains("aa|bb"));
        assertThat(TestUtils.analyze("AA BB", analzyer), contains("aa bb"));
    }

    @Test
    public void checkAnalyzerIsAvailable() {
        TestUtils.checkForAnalyzer(neo4j.defaultDatabaseService(), ANALYZER_NAME, DESCRIPTION);
    }

    @Test
    public void checkWithDatabase() {
        GraphDatabaseService db = neo4j.defaultDatabaseService();
        db.executeTransactionally("CALL db.index.fulltext.createNodeIndex('myIndex', ['Article'], ['title'], {analyzer: '" + ANALYZER_NAME +"'})");
        db.executeTransactionally( "CREATE (:Article{title:'AA||BB'})");

        assertResultCount(db, "CALL db.index.fulltext.queryNodes('myIndex', 'AA') yield node, score return node.title as text, score", 1);
        assertResultCount(db, "CALL db.index.fulltext.queryNodes('myIndex', 'aa') yield node, score return node.title as text, score", 1);
    }
}
