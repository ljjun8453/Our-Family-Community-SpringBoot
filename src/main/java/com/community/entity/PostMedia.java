package com.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_media")
public class PostMedia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @Enumerated(EnumType.STRING)
//    private MediaType mediaType;

    @Column
    private String storedName;

    @Column
    private String originalName;

    @Column
    private String url;

    @Column
    private Long size;

//    @Column
//    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void updatePostMedia(String originalName, String storedName, String url, Long size) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
        this.size = size;
    }
}
