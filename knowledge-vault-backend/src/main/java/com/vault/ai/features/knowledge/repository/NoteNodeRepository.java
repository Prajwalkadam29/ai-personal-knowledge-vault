package com.vault.ai.features.knowledge.repository;

import com.vault.ai.features.knowledge.model.NoteNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface NoteNodeRepository extends Neo4jRepository<NoteNode, Long> {

    @Query("MATCH (n:Note {noteId: $noteId})-[:HAS_TAG]->(t:Tag)<-[:HAS_TAG]-(other:Note) " +
            "MATCH (other)-[:HAS_TAG]->(rec:Tag) " +
            "WHERE NOT (n)-[:HAS_TAG]->(rec) " +
            "RETURN DISTINCT rec.name LIMIT 5")
    List<String> suggestRelatedTopics(Long noteId);
}