import {
  Box,
  Typography,
  Button,
  TextField,
  IconButton,
  Stack,
  Chip,
} from "@mui/material";
import FavoriteIcon from "@mui/icons-material/Favorite";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import { useEffect, useState, useContext } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  getThreadWithLikeStatus,
  deleteThread,
  likeThread,
  updateThread,
  uploadFile,
} from "../../services/threadApi";
import { Thread, ThreadRequest } from "../../types/thread";
import { AuthContext } from "../../context/AuthContext";
import Comments from "../Comments/Comments";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const ThreadDetail = () => {
  // ---------------------- [상태 및 훅 설정] ----------------------
  const { threadId } = useParams<{ threadId: string }>(); // URL에서 threadId 추출
  const [thread, setThread] = useState<Thread | null>(null); // 게시글 데이터 상태
  const { user } = useContext(AuthContext); // 현재 로그인한 사용자
  const navigate = useNavigate(); // 페이지 이동을 위한 훅
  const [liked, setLiked] = useState(false);

  // 수정추가: 수정 모드 여부 상태
  const [isEditing, setIsEditing] = useState(false);

  // 수정추가: 수정 입력 폼 상태 (초기값은 비어 있음)
  const [editForm, setEditForm] = useState<Omit<ThreadRequest, "userId">>({
    title: "",
    content: "",
    author: "",
    filePaths: [], // ✅ 빈 배열로 초기화
    area: "",
  });

  // 📌  파일 업로드 핸들러
  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      const uploadedUrl = await uploadFile(file);
      setEditForm((prev) => ({ ...prev, filePath: uploadedUrl }));
    } catch (error) {
      console.error("파일 업로드 실패:", error);
      alert("파일 업로드 중 오류가 발생했습니다.");
    }
  };

  // ---------------------- [게시글 상세 조회] ----------------------
  useEffect(() => {
    //7/2
    if (!threadId || !user) return;

    getThreadWithLikeStatus(Number(threadId), user.userId) // threadId 기반으로 게시글 조회
      .then((data) => {
        setThread(data); // 수정 추가: 수정 폼도 초기화
        setLiked(data.likedByCurrentUser); // 초기 상태 설정!
        setEditForm({
          title: data.title,
          content: data.content,
          author: data.author,
          filePaths: data.filePaths,
          area: data.area,
        });
        // TODO: 여기서 좋아요 여부 API 호출해서 liked 상태 업데이트 가능
        // 임시로 false로 세팅
        //setLiked(false);
      })

      // 성공 시 상태에 저장
      .catch((err) => {
        console.error("게시글 상세 조회 실패:", err);
        alert("게시글을 불러오는 데 실패했습니다.");
      });
  }, [threadId, user]);

  // ---------------------- [게시글 삭제 기능] ----------------------
  const handleDelete = async () => {
    if (!thread) return;

    // 작성자가 아니면 삭제 불가
    if (!user || user.userId !== thread.userId) {
      alert("본인 게시글만 삭제할 수 있습니다.");
      return;
    }

    // 사용자 확인 후 삭제 API 호출
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await deleteThread(thread.threadId);
      alert("게시글이 삭제되었습니다.");
      navigate("/thread"); // 삭제 후 게시글 목록 페이지로 이동
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("게시글 삭제 중 오류가 발생했습니다.");
    }
  };

  // ---------------------- [좋아요 기능] ----------------------
  const handleLike = async () => {
    if (!thread) return;

    console.log(user);

    if (!user) {
      alert("로그인 후 좋아요를 누를 수 있습니다.");
      return;
    }

    try {
      const updatedThread = await likeThread(thread.threadId, user.userId); // 좋아요 처리
      setThread(updatedThread); // 좋아요 수 반영
      setLiked((prev) => !prev); // 토글 상태 반전
    } catch (error) {
      console.error("좋아요 처리 실패:", error);
      alert("좋아요 처리 중 오류가 발생했습니다.");
    }
  };
  // ---------------------- [수정 기능: 추가] ----------------------

  // 수정 폼 입력 변경 핸들러 (입력 필드가 변경될 때마다 editForm 상태 업데이트)
  const handleEditChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setEditForm((prev) => ({ ...prev, [name]: value }));
  };

  // 수정 저장 핸들러(수정 완료 버튼 클릭 시 호출)
  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!thread || !user) return;

    try {
      // 수정 API 호출 (userId와 username은 context user에서 따로 넣어줌)
      const updated = await updateThread(thread.threadId, {
        ...editForm,
        userId: user.userId,
        author: user.username,
      });
      setThread(updated); // 수정 후 상태 업데이트 (화면에 반영)
      setIsEditing(false); // 수정 모드 종료 (상세보기로 돌아감)
      alert("게시글이 수정되었습니다.");
    } catch (err) {
      console.error("수정 실패:", err);
      alert("게시글 수정 중 오류가 발생했습니다.");
    }
  };

  // ---------------------- [로딩 처리] ----------------------
  if (!thread) {
    return <div>로딩중...</div>;
  }

  // ---------------------- [렌더링 영역] ----------------------
  return (
    <Box
      sx={{
        width: "100%",
        maxWidth: "900px",
        mx: "auto",
        mt: 7,
        mb: 7,
        p: 5,
        bgcolor: "white",
        borderRadius: 4,
        boxShadow: 5,
      }}
    >
      {!isEditing ? (
        <>
          <Typography
            variant="h3"
            fontWeight={700}
            color="primary.main"
            gutterBottom
          >
            {thread.title}
          </Typography>
          <Typography color="text.secondary" gutterBottom fontSize={18}>
            작성자: {thread.author} | 작성일:{" "}
            {new Date(thread.createDate).toLocaleDateString()}
          </Typography>
          <Typography color="text.secondary" sx={{ mb: 2 }} fontSize={17}>
            조회수: {thread.count}
          </Typography>
          <Typography
            sx={{ whiteSpace: "pre-wrap", mb: 3, fontSize: "1.05rem" }}
          >
            {thread.content}
          </Typography>
          {thread.filePaths &&
            thread.filePaths.map((path, index) => (
              <Box key={index} mb={2}>
                {path.match(/\.(jpg|jpeg|png|gif)$/i) ? (
                  <img
                    //src={`http://localhost:8080${path}`}
                    src={`${API_BASE_URL}${path}`}
                    alt={`첨부 이미지 ${index + 1}`}
                    style={{
                      maxWidth: "100%",
                      height: "auto",
                      marginTop: 8,
                      borderRadius: 4,
                    }}
                  />
                ) : (
                  <a
                    //href={`http://localhost:8080${path}`}
                    href={`${API_BASE_URL}${path}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{
                      color: "#1976d2",
                      textDecoration: "underline",
                      display: "inline-block",
                      marginTop: "8px",
                    }}
                  >
                    📄 PDF 파일 {index + 1} 열기
                  </a>
                )}
              </Box>
            ))}

          {thread.area && (
            <Chip
              label={`여행 지역: ${thread.area}`}
              variant="outlined"
              sx={{ mb: 2, fontSize: "0.95rem" }}
            />
          )}

          <Stack direction="row" alignItems="center" spacing={1} mb={2}>
            <Typography>좋아요: {thread.heart}개</Typography>
            <IconButton
              onClick={handleLike}
              color={liked ? "error" : "default"}
            >
              {liked ? <FavoriteIcon /> : <FavoriteBorderIcon />}
            </IconButton>
          </Stack>

          {user && user.userId === thread.userId && (
            <Stack direction="row" spacing={2}>
              <Button
                variant="contained"
                color="primary"
                onClick={() => setIsEditing(true)}
                sx={{ borderRadius: 8, px: 4, py: 1.5, fontSize: "1rem" }}
              >
                ✏️ 수정
              </Button>
              <Button
                variant="outlined"
                color="error"
                onClick={handleDelete}
                sx={{ borderRadius: 8, px: 4, py: 1.5, fontSize: "1rem" }}
              >
                🗑 삭제
              </Button>
            </Stack>
          )}
        </>
      ) : (
        <Box component="form" onSubmit={handleEditSubmit}>
          <Typography variant="h4" fontWeight={600} gutterBottom>
            게시글 수정
          </Typography>
          <Stack spacing={2}>
            <TextField
              label="제목"
              name="title"
              value={editForm.title}
              onChange={handleEditChange}
              fullWidth
              required
              size="medium"
            />
            <TextField
              label="내용"
              name="content"
              value={editForm.content}
              onChange={handleEditChange}
              fullWidth
              required
              multiline
              rows={8}
              size="medium"
            />
            <TextField
              label="PDF 경로"
              name="filePath"
              value={editForm.filePaths}
              onChange={handleEditChange}
              fullWidth
              size="medium"
            />
            {/* 📌  실제 파일 선택 input */}
            <input
              type="file"
              accept="application/pdf"
              onChange={handleFileChange}
            />
            <TextField
              label="여행 지역"
              name="area"
              value={editForm.area}
              onChange={handleEditChange}
              fullWidth
              size="medium"
            />
            <Stack direction="row" spacing={2}>
              <Button
                type="submit"
                variant="contained"
                color="success"
                sx={{ borderRadius: 8, px: 4, py: 1.5, fontSize: "1rem" }}
              >
                ✅ 저장
              </Button>
              <Button
                type="button"
                variant="outlined"
                color="secondary"
                onClick={() => setIsEditing(false)}
                sx={{ borderRadius: 8, px: 4, py: 1.5, fontSize: "1rem" }}
              >
                ❌ 취소
              </Button>
            </Stack>
          </Stack>
        </Box>
      )}

      {/* 댓글 기능 추가 */}
      <Comments threadId={thread.threadId} />
    </Box>
  );
};

export default ThreadDetail;
