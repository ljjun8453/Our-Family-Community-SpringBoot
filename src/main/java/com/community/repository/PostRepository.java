package com.community.repository;

import com.community.constant.BoardType;
import com.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByBoardTypeAndDeletedFalse(BoardType boardType, Pageable pageable);

    Optional<Post> findByIdAndDeletedFalse(Long id);

    // 내가 쓴 글 최신 50개
    List<Post> findTop50ByCreatedByOrderByCreateTimeDesc(String createdBy);

    @Modifying
    @Query("update Post p set p.deleted = true where p.id = :postId")
    int markDeleted(@Param("postId") Long postId);

    // 조회수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.views = p.views + 1 where p.id = :id")
    int incrementViews(@Param("id") Long id);

    // NATIVE 조회수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE posts SET views = COALESCE(views, 0) + 1 WHERE id = :id", nativeQuery = true)
    int incrementViewsNative(@Param("id") Long id);

    // 댓글수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.comments = p.comments + 1 where p.id = :id")
    int incrementComments(@Param("id") Long id);

    // 댓글수 감소
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.comments = case when p.comments > 0 then p.comments - 1 else 0 end where p.id = :id")
    int decrementComments(@Param("id") Long id);

    // 추천수 증가
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.likes = p.likes + 1 where p.id = :id")
    int incrementLikes(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Post p set p.likes = case when p.likes > 0 then p.likes - 1 else 0 end where p.id = :id")
    int decrementLikes(@Param("id") Long id);

}
