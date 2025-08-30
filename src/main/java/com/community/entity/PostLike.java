package com.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "post_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_post_like_post_member",
                        columnNames = { "post_id", "member_id" }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLike extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }
}
