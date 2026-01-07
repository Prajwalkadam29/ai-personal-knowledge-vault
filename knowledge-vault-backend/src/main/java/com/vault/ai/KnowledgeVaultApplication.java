package com.vault.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
// 1. Tell Spring to look here for PostgreSQL Repositories
@EnableJpaRepositories(basePackages = {
		"com.vault.ai.features.note.repository",
		"com.vault.ai.features.auth.repository"  // ADD THIS LINE
})
// 2. Tell Spring to look here for Neo4j Repositories
@EnableNeo4jRepositories(basePackages = "com.vault.ai.features.knowledge.repository")
public class KnowledgeVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(KnowledgeVaultApplication.class, args);
	}

}
