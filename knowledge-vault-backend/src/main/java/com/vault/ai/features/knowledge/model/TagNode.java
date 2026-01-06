package com.vault.ai.features.knowledge.model;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagNode {
    @Id
    private String name;
}