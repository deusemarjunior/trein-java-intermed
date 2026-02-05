package com.example.jpa.dto.post;

import com.example.jpa.model.Post;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponse(
    Long id,
    String title,
    String content,
    String author,
    Boolean published,
    Integer commentCount,
    Set<String> tags,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getAuthor(),
            post.getPublished(),
            post.getComments() != null ? post.getComments().size() : 0,
            post.getTags() != null ? 
                post.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet()) : 
                Set.of(),
            post.getCreatedAt(),
            post.getUpdatedAt()
        );
    }
}
