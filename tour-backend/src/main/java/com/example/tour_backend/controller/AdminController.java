package com.example.tour_backend.controller;

import com.example.tour_backend.dto.StatisticsResponseDto;
import com.example.tour_backend.dto.user.UserResponseDto;
import com.example.tour_backend.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    /** ROLE_ADMIN 전용 통계 API */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponseDto> getStatistics() {
        return ResponseEntity.ok(adminService.getStatistics());
    }

    /** ROLE_ADMIN 전용 유저 리스트 조회 API */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "createDate") String sortBy
    ) {
        List<UserResponseDto> list = adminService.getUsers(searchType, keyword, sortBy);
        return ResponseEntity.ok(list);
    }

    /** ROLE_ADMIN 전용 유저 삭제 API */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.err.println("❌ 관리자 사용자 삭제 실패: " + e.getMessage());
            // 에러 메시지를 클라이언트에 전달하기 위해 500 에러 반환
            return ResponseEntity.status(500).build();
        }
    }
}
