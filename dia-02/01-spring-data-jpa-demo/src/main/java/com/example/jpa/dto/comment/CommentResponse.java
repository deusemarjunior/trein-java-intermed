package com.example.jpa.dto.comment;

import com.example.jpa.model.Comment;
import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    String text,
    String author,
    Long postId,
    String postTitle,
    LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
            comment.getId(),
            comment.getText(),
            comment.getAuthor(),
            comment.getPost() != null ? comment.getPost().getId() : null,
            comment.getPost() != null ? comment.getPost().getTitle() : null,
            comment.getCreatedAt()
        );
    }
}
