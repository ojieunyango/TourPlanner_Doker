package com.example.tour_backend.controller;

import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadRepository;
import com.example.tour_backend.dto.thread.ThreadDto;
import com.example.tour_backend.dto.thread.ThreadUpdateRequestDto;
import com.example.tour_backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.tour_backend.service.ThreadService;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
public class ThreadController {
    private final ThreadService threadService;
    private final FileUploadService fileUploadService; // 파일 업로드
    private final ThreadRepository threadRepository;

    @PostMapping //게시글 생성
    public ResponseEntity<ThreadDto> createThread(@RequestBody ThreadUpdateRequestDto requestDto) {
        ThreadDto created = threadService.createThread(requestDto); // 파일 업로드
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}") //특정 게시글 조회(조회수 증가 포함) 수정함
    public ResponseEntity<ThreadDto> getThread(@PathVariable Long id) {
        try {
            ThreadDto threadDto = threadService.getThreadDetail(id);  // 조회수 증가 로직 포함된 메서드
            return ResponseEntity.ok(threadDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping //모든 게시글 목록 조회
    public ResponseEntity<List<ThreadDto>> getAllThreads() {
        List<ThreadDto> threads = threadService.getAllThreads();
        return ResponseEntity.ok(threads);

    }
    @DeleteMapping("/{id}") // 게시글 삭제 (추추추가)
    public ResponseEntity<Void> deleteThread(@PathVariable Long id) {
        threadService.deleteThread(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}") // 게시물 수정(PUT /api/thread/{id} 엔드포인트 추가) 추추가
    public ResponseEntity<ThreadDto> updateThread(
            @PathVariable Long id,
            @RequestBody ThreadUpdateRequestDto requestDto){
            // 파일 업로드


        // 수정된 게시글 받아오기
        Thread updated = threadService.updateThread(id, requestDto);

        // 응답은 ThreadDto로 변환해서 줘도 되고, entity 자체를 줘도 됨
        ThreadDto responseDto = new ThreadDto();
        responseDto.setThreadId(updated.getThreadId());
        responseDto.setUserId(updated.getUser().getUserId());
        responseDto.setTitle(updated.getTitle());
        responseDto.setContent(updated.getContent());
        responseDto.setAuthor(updated.getAuthor());

        responseDto.setFilePaths(updated.getFilePaths()); // ✅ 여러 파일 경로 세팅
        responseDto.setArea(updated.getArea());
        responseDto.setCreateDate(updated.getCreateDate());
        responseDto.setModifiedDate(updated.getModifiedDate());
        responseDto.setCount(updated.getCount());
        responseDto.setHeart(updated.getHeart());
        responseDto.setCommentCount(updated.getCommentCount());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/search") // 게시글 검색 기능 (수정함) 추추추추가
    public ResponseEntity<List<ThreadDto>> searchThreads(@RequestParam String keyword,
                                                         @RequestParam String searchType,
                                                         @RequestParam(required = false, defaultValue = "createDate") String sortBy) {


        List<ThreadDto> results = threadService.searchThreads(keyword, searchType, sortBy);
        return ResponseEntity.ok(results);
    }
    @PostMapping("/{id}/like") // 좋아요 기능 추가 (수정함)
    public ResponseEntity<ThreadDto> likeThread(@PathVariable Long id, @RequestParam Long userId) {
        try {
            ThreadDto updated = threadService.toggleLike(id, userId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 게시물에 현재 좋아요 누른 사용자 호출 7/1
    @GetMapping("/{id}/like-status")
    public ResponseEntity<ThreadDto> getThreadWithLikeStatus( // 메서드 이름 중복이어서 이름만 바꿈 7/2
                                                              @PathVariable Long id,
                                                              @RequestParam Long userId
    ) {
        ThreadDto dto = threadService.getThreadById(id, userId);
        return ResponseEntity.ok(dto);
    }
    // 인증없이 간단하게 구현하기위해 userId를 @RequestParam 또는 @RequestBody 로 받는 방식 사용
    // 추후 보안상의 이유로 JwtAuthenticationFilter 구현 필요하다면 (JWT)
    // - 필터에서 CustomUserDetails 생성 → SecurityContextHolder에 저장하는 방식으로 바꾸어야함
    @PostMapping("/upload") // 파일 업로드
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String filePath = fileUploadService.saveFile(file);
        return ResponseEntity.ok(filePath); // 클라이언트에 저장 경로 응답
    }

    // 나의 게시글 모두 보기
    @GetMapping("/user/{userId}/threads")
    public ResponseEntity<List<ThreadDto>> getThreadsByUserId(@PathVariable Long userId) {
        List<Thread> threads = threadRepository.findByUser_UserId(userId);
        List<ThreadDto> response = threads.stream()
                .map(ThreadDto::from) // ✅ 기존에 있는 정적 메서드 사용!
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

}