package org.neo4j.contrib.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.pattern.SimplePatternSplitTokenizerFactory;
import org.neo4j.annotations.service.ServiceProvider;
import org.neo4j.graphdb.schema.AnalyzerProvider;

import java.io.IOException;

@ServiceProvider
public class DoublePipeAnalyzerProvider extends AnalyzerProvider {

    public static final String DESCRIPTION = "tokenize by double pipe (`||`)";
    public static final String ANALYZER_NAME = "doublepipe";

    public DoublePipeAnalyzerProvider() {
        super(ANALYZER_NAME);
    }

    public Analyzer createAnalyzer() {
        try {
            return CustomAnalyzer.builder()
                    .withTokenizer(SimplePatternSplitTokenizerFactory.class, "pattern", "\\|\\|")
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }
}
