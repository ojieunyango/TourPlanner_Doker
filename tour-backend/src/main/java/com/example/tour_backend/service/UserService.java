package com.example.tour_backend.service;

import com.example.tour_backend.config.JwtTokenProvider;
import com.example.tour_backend.domain.Role;
import com.example.tour_backend.domain.thread.Thread;
import com.example.tour_backend.domain.thread.ThreadLikeRepository;
import com.example.tour_backend.domain.user.User;
import com.example.tour_backend.domain.user.UserRepository;
import com.example.tour_backend.dto.thread.ThreadDto;
import com.example.tour_backend.dto.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;      // 비밀번호 암호화 검증용
    private final JwtTokenProvider jwtTokenProvider;    // JWT 토큰 생성용 클래스 (직접 구현 필요)
    private final ThreadLikeRepository threadLikeRepository;

    /**
     * 로그인 처리
     */
    public JwtResponse login(LoginRequestDto loginRequestDto) {
        // 1) 사용자 조회 (아이디 기준)
        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2) 비밀번호 검증
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3) JWT 토큰 생성 (Role도 Claim에 포함)
        String token = jwtTokenProvider.createToken(user.getUsername(), user.getRole().name());

        // 4) 응답 반환 (role 포함)
        return new JwtResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getRole() // ✅ 추가
        );
    }

    /**
     * 회원가입 처리
     */
    @Transactional
    public UserResponseDto registerUser(UserRequestDto requestDto) {
        // 이메일 중복 체크
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        // 사용자명 중복 체크
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }
        // 닉네임 중복 체크
        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = User.builder()
                .username(requestDto.getUsername())
                .password(encodedPassword)
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .phone(requestDto.getPhone())
                .nickname(requestDto.getNickname())
                .role(Role.USER) // ✅ 기본 Role USER
                .build();

        userRepository.save(user);

        // 응답 DTO 생성 및 반환
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setCreateDate(user.getCreateDate());
        dto.setModifiedDate(user.getModifiedDate());
        dto.setRole(user.getRole()); // ✅ role 추가
        return dto;
    }

    /**
     * 회원정보 수정
     */
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setNickname(user.getNickname());
        dto.setCreateDate(user.getCreateDate());
        dto.setModifiedDate(user.getModifiedDate());
        dto.setRole(user.getRole()); // ✅ role 추가
        return dto;
    }

    /**
     * 단일 회원 조회
     */
    public Optional<UserResponseDto> getUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserResponseDto dto = new UserResponseDto();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setNickname(user.getNickname());
                    dto.setCreateDate(user.getCreateDate());
                    dto.setModifiedDate(user.getModifiedDate());
                    dto.setRole(user.getRole()); // ✅ role 추가
                    return dto;
                });
    }

    /**
     * 모든 회원 조회
     */
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponseDto dto = new UserResponseDto();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setNickname(user.getNickname());
                    dto.setCreateDate(user.getCreateDate());
                    dto.setModifiedDate(user.getModifiedDate());
                    dto.setRole(user.getRole()); // ✅ role 추가
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * username으로 userId 조회
     */
    public Long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user.getUserId();
    }


    //관리자가 유저 삭제 위함
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("관리자는 삭제할 수 없습니다.");
        }

        // ✅ 연관 데이터 전부 삭제 (Cascade 설정 덕분에 JPA가 알아서 처리)
        userRepository.delete(user);
    }
    public List<ThreadDto> getLikedThreadsByUser(Long userId) {
        List<Thread> threads = threadLikeRepository.findThreadsByUserId(userId);
        return threads.stream().map(ThreadDto::from).collect(Collectors.toList());
    }

}


