package com.example.tour_backend.controller;

import com.example.tour_backend.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.tour_backend.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // 댓글 목록 조회 (threadId 기반) 6/30
    @GetMapping("/thread/{threadId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long threadId) {
        List<CommentDto> comments = commentService.getComments(threadId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentDto dto) {
        CommentDto result = commentService.addComment(dto);
        return ResponseEntity.ok(result);
    }
    //댓글 수정 6/30
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId, @RequestBody CommentDto dto) {
        CommentDto updated = commentService.updateComment(commentId, dto);
        return ResponseEntity.ok(updated);
    }
    //댓글 삭제 6/30
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long userId) { // ✅ userId 파라미터 추가
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
