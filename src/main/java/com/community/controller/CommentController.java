package com.community.controller;

import com.community.constant.BoardType;
import com.community.dto.CommentDto;
import com.community.dto.PostFormDto;
import com.community.service.CommentService;
import com.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class CommentController {

    private final PostService postService;
    private final CommentService commentService; // ← 추가

    // 1. 댓글 조회
    @GetMapping("/{boardType}/{postId}/comments")
    public String postDtl(@PathVariable BoardType boardType,
                          @PathVariable Long postId,
                          Model model) {
        PostFormDto postFormDto = postService.getPostDtl(postId);
        model.addAttribute("boardType", boardType);
        model.addAttribute("post", postFormDto);
        // 댓글 같이 내려주기
        model.addAttribute("comments", commentService.comments(postId));

        return "board/view";
    }

    // 2. 댓글 생성
    @PostMapping("/{boardType}/{postId}/comments")
    public String commentNew(@PathVariable BoardType boardType,
                                @PathVariable Long postId,
                                @ModelAttribute CommentDto dto,
                                Authentication authentication,
                                RedirectAttributes rttr) {
        // 로그인 사용자 ID 가져오기
        String userId = (authentication != null ? authentication.getName() : null);

        dto.setPostId(postId);

        // 댓글 저장
        commentService.create(postId, dto, userId);
        rttr.addFlashAttribute("alert", "댓글이 등록되었습니다.");

        // 다시 해당 게시글 상세 페이지로 리다이렉트
        return "redirect:/board/" + boardType + "/" + postId;
    }
    
    // 3. 댓글 수정
    @PostMapping("/{boardType}/{postId}/comments/{commentId}/edit")
    public String commentUpdate(@PathVariable BoardType boardType,
                                @PathVariable Long postId,
                                @PathVariable Long commentId,
                                @ModelAttribute CommentDto dto,
                                Authentication authentication,
                                RedirectAttributes rttr) {
        // 로그인 사용자 ID
        String userId = (authentication != null ? authentication.getName() : null);

        // 댓글 수정 (service.update 내부에서 id 검사 + patch)
        commentService.update(commentId, dto);
        rttr.addFlashAttribute("alert", "댓글이 수정되었습니다.");

        // 다시 상세 페이지로 리다이렉트
        return "redirect:/board/" + boardType + "/" + postId;
    }

    // 4. 댓글 삭제
    @PostMapping("/{boardType}/{postId}/comments/{commentId}/delete")
    public String commentDelete(@PathVariable BoardType boardType,
                                @PathVariable Long postId,
                                @PathVariable Long commentId,
                                RedirectAttributes rttr) {
        // 실제 삭제
        commentService.delete(commentId);

        // 알림 메시지
        rttr.addFlashAttribute("alert", "댓글이 삭제되었습니다.");

        // 원글 상세로 리다이렉트
        return "redirect:/board/" + boardType + "/" + postId;
    }
}
