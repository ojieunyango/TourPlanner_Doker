package com.example.tour_backend.service;

import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadLike;
import com.example.tour_backend.domain.thread.ThreadLikeRepository;
import com.example.tour_backend.domain.thread.ThreadRepository;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.comment.CommentDto;
import com.example.tour_backend.dto.thread.ThreadDto;
import com.example.tour_backend.dto.thread.ThreadUpdateRequestDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ThreadService {
    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;
    private final ThreadLikeRepository threadLikeRepository; //(수정함)

    @Transactional //게시글 생성 파일 업로드 추가
    public ThreadDto createThread(ThreadUpdateRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Thread thread = Thread.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .author(requestDto.getAuthor())
                .filePaths(requestDto.getFilePaths())  // 파일업로드
                .area(requestDto.getArea())
                .build();

        threadRepository.save(thread);



        return convertToDto(thread);
    }

    public Optional<ThreadDto> getThread(Long threadId) { //게시글 하나 조회
        return threadRepository.findById(threadId)

                .map(this::convertToDto); // ✅ 중복 제거
    }

    public List<ThreadDto> getAllThreads() { //모든 게시글 목록 조회
        return threadRepository.findAll().stream()
                .map(this::convertToDto) // 공통 변환 메서드 재사용하도록 수정
                .collect(Collectors.toList());

    }

    @Transactional // 게시글 삭제 (추추추가)
    public void deleteThread(Long id) {
        Thread thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        threadRepository.delete(thread);
    }

    @Transactional // 게시글 수정
    // 추추가 (메서드 게시글 수정위해 추가
    public Thread updateThread(Long id, ThreadUpdateRequestDto dto) {
        Thread thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 수정 가능 필드만 변경
        thread.setTitle(dto.getTitle());
        thread.setContent(dto.getContent());
        thread.setAuthor(dto.getAuthor());
        thread.setFilePaths(dto.getFilePaths());// 파일 업로드
        thread.setArea(dto.getArea());
        thread.setModifiedDate(LocalDateTime.now());

        return threadRepository.save(thread);
    }
    // 게시물 검색 기능 추추추추가 (수정함)
    @Transactional(readOnly = true)
    public List<ThreadDto> searchThreads(String keyword, String searchType, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate"); // 기본 최신순
        if ("views".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "count"); // 조회수 내림차순
        } else if ("likes".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "heart"); // 좋아요 내림차순
        } // 최신순은 기본값

        List<Thread> threads;
        // 검색 기준에 따라 쿼리 분기
        if ("author".equals(searchType)) {
            // 작성자 기준 검색
            threads = threadRepository.findByAuthorContaining(keyword, sort);
        } else {
            // 기본값: 제목 + 내용 기준 검색
            threads = threadRepository.findByTitleContainingOrContentContaining(keyword, keyword, sort);
        }
        // 검색된 Thread 목록을 DTO로 변환하여 반환
        return threads.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Transactional // 게시물 조회수 증가 및 상세 조회
    public ThreadDto getThreadDetail(Long threadId) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 조회수 증가
        thread.setCount(thread.getCount() + 1);


        // 변경사항 저장
        threadRepository.save(thread);

        // DTO 변환 후 반환
        return convertToDto(thread);
    }
    // DTO 변환 메서드 추가
    private ThreadDto convertToDto(Thread thread) {
        ThreadDto dto = new ThreadDto();
        dto.setThreadId(thread.getThreadId());
        dto.setUserId(thread.getUser().getUserId());
        dto.setTitle(thread.getTitle());
        dto.setContent(thread.getContent());
        dto.setAuthor(thread.getAuthor());
        dto.setCount(thread.getCount());
        dto.setHeart(thread.getHeart());

        dto.setFilePaths(thread.getFilePaths()); // 파일 업로드
        dto.setCommentCount(thread.getCommentCount());
        dto.setArea(thread.getArea());
        dto.setCreateDate(thread.getCreateDate());
        dto.setModifiedDate(thread.getModifiedDate());

        //  댓글 리스트를 CommentDto 리스트로 변환 7/2 파일업로드 커맨트 null추가
        List<CommentDto> commentDtos = thread.getComments() != null
                ? thread.getComments() .stream().map(comment -> {
            CommentDto commentDto = new CommentDto();
            commentDto.setCommentId(comment.getCommentId());
            commentDto.setThreadId(thread.getThreadId());
            commentDto.setComment(comment.getComment());
            commentDto.setAuthor(comment.getAuthor());
            commentDto.setCreateDate(comment.getCreateDate());
            commentDto.setModifiedDate(comment.getModifiedDate());
            return commentDto;
        }).toList()
        : List.of();


        dto.setComments(commentDtos);



        return dto;
    }
    @Transactional // 좋아요 토글 메서드 추가 7/1
    public ThreadDto toggleLike(Long threadId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
// 7/1
        Optional<ThreadLike> existingLike = threadLikeRepository.findByUserAndThread(user, thread);

        boolean liked;

        if (existingLike.isPresent()) {
            // 이미 좋아요 누른 상태 → 취소
            threadLikeRepository.delete(existingLike.get());
            thread.setHeart(thread.getHeart() - 1);
            liked = false;
        } else {
            // 좋아요 추가
            ThreadLike like = new ThreadLike();
            like.setUser(user);
            like.setThread(thread);
            threadLikeRepository.save(like);
            thread.setHeart(thread.getHeart() + 1);
            liked = true;
        }

        threadRepository.save(thread);


        return ThreadDto.builder()
                .threadId(thread.getThreadId())
                .userId(thread.getUser().getUserId())
                .title(thread.getTitle())
                .content(thread.getContent())
                .author(thread.getAuthor())
                .count(thread.getCount())
                .heart(thread.getHeart())
                .filePaths(thread.getFilePaths()) // 파일 업로드
                .commentCount(thread.getCommentCount())
                .area(thread.getArea())
                .createDate(thread.getCreateDate())
                .modifiedDate(thread.getModifiedDate())
                .likedByCurrentUser(liked) // 토글 후 상태를 명확히 반환
                .build();
    }
    // 게시글에 현재 좋아요 누른 사용자 저장 7/1
    public ThreadDto getThreadById(Long threadId, Long userId) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("게시글이 없습니다."));
        // ✅ 조회수 증가
        thread.setCount(thread.getCount() + 1);
        threadRepository.save(thread);


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        //  좋아요 눌렀는지 여부 계산
        boolean liked = threadLikeRepository.existsByUserAndThread(user, thread);

        //  응답 DTO 생성 시 liked 상태도 포함
        return ThreadDto.builder()
                .threadId(thread.getThreadId())
                .userId(thread.getUser().getUserId())
                .title(thread.getTitle())
                .content(thread.getContent())
                .author(thread.getAuthor())
                .count(thread.getCount())
                .heart(thread.getHeart())

                .filePaths(thread.getFilePaths()) // ✅ 여러 개 파일 경로
                .commentCount(thread.getCommentCount())
                .area(thread.getArea())
                .createDate(thread.getCreateDate())
                .modifiedDate(thread.getModifiedDate())
                .likedByCurrentUser(liked)
                .build();
    }


}
