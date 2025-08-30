package com.community.controller;

import com.community.constant.BoardType;
import com.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final PostService postService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        // 서비스 호출해서 글 10개만 가져오기
        var notice = postService.listTopN(BoardType.NOTICE, 4);
        var free   = postService.listTopN(BoardType.FREE, 10);
        var album    = postService.listTopN(BoardType.ALBUM, 10);

        model.addAttribute("noticePosts", notice);
        model.addAttribute("freePosts", free);
        model.addAttribute("albumPosts", album);

        return "home";
    }

}
