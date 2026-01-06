package com.vault.ai.config;

import org.neo4j.driver.Driver;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class Neo4jConfig {

    @Bean
    @Primary
    public VectorStore customVectorStore(Driver driver, EmbeddingModel embeddingModel) {
        return Neo4jVectorStore.builder(driver, embeddingModel)
                .indexName("note-embeddings")     // Index name in Neo4j
                .label("Note")                    // Node label
                .embeddingProperty("embedding")   // Property for vector
                .initializeSchema(true)           // Automatically create index if it doesn't exist
                .build();
    }

    // THIS BEAN: Explicitly defines the transaction manager for Neo4j
    @Bean(name = "neo4jTransactionManager")
    public PlatformTransactionManager neo4jTransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }
}