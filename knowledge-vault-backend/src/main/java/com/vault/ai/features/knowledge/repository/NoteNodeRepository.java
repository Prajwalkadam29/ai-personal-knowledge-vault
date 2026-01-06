package com.vault.ai.features.knowledge.repository;

import com.vault.ai.features.knowledge.model.NoteNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface NoteNodeRepository extends Neo4jRepository<NoteNode, Long> {
}
