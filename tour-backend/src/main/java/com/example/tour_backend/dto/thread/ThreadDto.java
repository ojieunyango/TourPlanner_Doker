package com.example.tour_backend.dto.thread;

import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.dto.comment.CommentDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadDto {
    private Long threadId;
    private Long userId;
    private String title;
    private String content;
    private String author;
    private int count;
    private int heart;

    private List<String> filePaths; // ✅ 여러 파일 경로 지원


    private int commentCount;
    private String area;

    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    // 프론트에서 버튼 상태 유지에 사용할 필드 7/1
    private boolean likedByCurrentUser;
    // 댓글 리스트 추가 7/2
    private List<CommentDto> comments;

    // 마이페이지 좋아요 누른 게시글 확인 7/14
    public static ThreadDto from(Thread thread) {
        return ThreadDto.builder()
                .threadId(thread.getThreadId())
                .userId(thread.getUser().getUserId())
                .title(thread.getTitle())
                .content(thread.getContent())
                .author(thread.getAuthor())
                .count(thread.getCount())
                .heart(thread.getHeart())
                .filePaths(thread.getFilePaths()) // ✅ 여기서 세팅
                .commentCount(thread.getCommentCount())
                .area(thread.getArea())
                .createDate(thread.getCreateDate())
                .modifiedDate(thread.getModifiedDate())
                .likedByCurrentUser(false) // (이건 서비스에서 나중에 추가로 처리)
                .comments(null) // (이것도 필요하면 나중에)

                .build();
    }
}