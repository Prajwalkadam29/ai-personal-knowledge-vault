package com.vault.ai.features.knowledge.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteNode {

    @Id
    private Long noteId; // Same ID as PostgreSQL for mapping

    private String title;

    // The 'tags' will be separate nodes connected to this note
    @Builder.Default
    @Relationship(type = "HAS_TAG", direction = Relationship.Direction.OUTGOING)
    private Set<TagNode> tags = new HashSet<>();
}