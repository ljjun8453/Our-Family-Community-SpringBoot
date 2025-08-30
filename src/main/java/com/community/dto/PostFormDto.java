package com.community.dto;

import com.community.constant.BoardType;
import com.community.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PostFormDto {

    private Long id;

    private BoardType boardType;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 100, message = "제목은 최대 30자까지 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private String ipAddress;

    private String author;

    private String authorId;

    private LocalDateTime createTime;

    private Long views;

    private Long likes;

    private Long comments;

    private List<PostMediaDto> postMediaDtoList = new ArrayList<>();

    private List<Long> postMediaIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Post createPost() {
        return modelMapper.map(this, Post.class);
    }

    public static PostFormDto of(Post post) {
        return modelMapper.map(post, PostFormDto.class);
    }
}
