# 예제 프론트엔드 파일
https://github.com/maybeags/carlist_front.git
에서 .zip 파일을 받습니다.

frontend에 폴더에 집어넣고
local에서 실행시키기 위하여
npm install
npm run dev

window + docker
db를 먼저 켜고
백엔드를 켭니다.

그러면 이제 backend와 frontend가 연결이 되어있는데, 여태까지와의 차이점이 뭐냐면
전에는 backend / frontend가 다 local이었는데
이제는 backend는 docker에서
frontend는 local에서 돌아가고 있다는 점입니다.

이제 다음 단계는 frontend의 배포가 됩니다.
frontend 배포를 github pages를 활용할 때는
repository에 올라간 코드를 기준으로 합니다.

그래서 저희는 현재 예제 프론트엔드 파일을 깃허브에 올릴겁니다.

자 방금까지 github repository에 push 과정 거쳤습니다.
그럼 현재 리포지토리에 올라간 것을 깃허브 페이지가 확인하여 그것을 기준으로 배포가 이루어지게 됩니다.

npm install gh-pages

전체 패키지 목록 출력하는 명령어
npm list

특정 패키지 목록을 가져오는 명령어
npm list gh-pages

사전 설정을 제가 해둔 상태기 때문에 파일들을 좀 참조하겠습니다.

```ts
// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  base: '/korit_3_front_deployment_example/',
  // base는 리포지토리명과 동일하게 가져갑니다.
  plugins: [react()],
});
```

```json
//package.json
"scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "preview": "vite preview",
    "predeploy": "npm run build", // 배포 전에 알아서 build하라고 미리 설정해둔 명령어
    "deploy": "gh-pages -d dist"
  }
```

gh-pages까지 push 하신 분들은
npm run deploy를 입력하면 되는데, 그러면 deploy하기 전에 predeploy가 실행된다.

즉
npm run deploy 엔터치면
npm run build가 먼저 실행되고 그 다음에
npm run deploy가 실행됨.

Published까지 확인을 하셨으면 성공적으로 배포가 완료된것입니다.

https://여러분깃허브아이디.github.io/korit_3_frontend_deployment/
https://maybeags.github.io/korit_3_frontend_deployment/
로 들어가시면 됩니다.

혹시 안된다
다시 push하고 npm run deploy를 실행해보세요.

docker로 db 이미지를 감싸버렸기 때문에 heidiSQL에서 다이렉트로 데이터 유무를 확인할 수 없습니다.
그래서 docker desktop에서 확인하는 법
좌측에 Containers 클릭 -> cardb 선택(DB 선택) -> Exec 선택
저희는 마리아db를 사용 중이기 때문에
mariadb -u root -p 선택하면 비밀번호 물어봅니다. 우리는 1234입니다.(복사 붙여넣기 할 때 터미널에서처럼 shift + ins)

그 다음에
SHOW databases;

USE cardb;
이후에는 sql문으로 데이터 CRUD가 가능합니다.