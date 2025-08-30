package com.community.service;

import com.community.dto.CommentDto;
import com.community.entity.Comment;
import com.community.entity.Member;
import com.community.entity.Post;
import com.community.repository.CommentRepository;
import com.community.repository.MemberRepository;
import com.community.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    public List<CommentDto> comments(Long postId) {
        // 1. 댓글 조회
        List<Comment> comments = commentRepository.findByPostId(postId);
        // 2. 엔티티 -> DTO 변환
        List<CommentDto> dtos = new ArrayList<CommentDto>();
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            CommentDto dto = CommentDto.createCommentDto(c);
            dtos.add(dto);
        }
        // 3. 결과 반환
        return dtos;
    }


    // 댓글 생성
    @Transactional
    public CommentDto create(Long postId, CommentDto dto, String userId) {
        // 1. 게시글 조회 및 예외 발생
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! 대상 게시글이 없습니다."));
        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("댓글 작성자 정보를 찾을 수 없습니다: " + userId));
        // 2. 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto, post, member);
        // 3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);
        // 댓글 수 +1
        postRepository.incrementComments(post.getId());
        // 4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);
    }


    // 댓글 수정
    @Transactional
    public CommentDto update(Long id, CommentDto dto){
        Comment target = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패! 대상 댓글이 없습ㄴ디ㅏ."));
        target.patch(dto);
        Comment updated = commentRepository.save(target);
        return CommentDto.createCommentDto(updated);
    }


    // 댓글 삭제
    @Transactional
    public void  delete(Long id){
        Comment target = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패! 대상이 없습니다."));
        Long postId = target.getPost().getId();
        commentRepository.delete(target);
        // 댓글 수 -1
        postRepository.decrementComments(postId);
    }
}