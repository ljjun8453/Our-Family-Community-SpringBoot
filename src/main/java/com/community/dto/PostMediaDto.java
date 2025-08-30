package com.community.dto;

import com.community.entity.PostMedia;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class PostMediaDto {

    private Long id;

    private String storedName;

    private String originalName;

    private String url;

    private Long size;

    private static ModelMapper modelMapper = new ModelMapper();

    public static PostMediaDto of(PostMedia postMedia) {
        return modelMapper.map(postMedia, PostMediaDto.class);
    }
}
