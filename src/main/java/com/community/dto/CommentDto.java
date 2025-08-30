package com.community.dto;

import com.community.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CommentDto {
    private Long id;
    private Long postId;
    private String userId;
    private String name;
    private String content;
    private LocalDateTime createTime;

    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getPost().getId(),
                comment.getMember().getUserId(),
                comment.getMember().getName(),
                comment.getContent(),
                comment.getCreateTime()
        );
    }
}
