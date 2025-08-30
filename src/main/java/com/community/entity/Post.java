package com.community.entity;

import com.community.constant.BoardType;
import com.community.dto.PostFormDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Column(nullable = false, length = 255)
    private String title;

//    @Column(nullable = false, length = 100)
//    private String author;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_id", nullable = false)
//    private Member authorId;

    @Lob
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

//    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @CreationTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long views = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long likes = 0L;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long comments = 0L;

    @Column
    private Boolean deleted = false;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> mediaList = new ArrayList<>();

    public void updatePost(PostFormDto postFormDto) {
        this.title = postFormDto.getTitle();
        this.content = postFormDto.getContent();
        this.ipAddress = postFormDto.getIpAddress();
    }
}
