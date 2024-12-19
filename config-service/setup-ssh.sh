#!/bin/bash

# .env 파일에서 PRIVATE KEY를 읽어옵니다.
PRIVATE_KEY=${GIT_PRIVATE_KEY}

# 줄바꿈(\n)을 개행 문자로 변경
# echo -e -> 문자열에 작성된 \n, \t같은 탈출 코드를 실제로 반영해 줌.
# .env에는 줄개행을 할 수 없었기 때문에 \n을 썼는데, 원본 키 파일은 줄개행이 반영이 되어야 하기 때문에
# 변환 과정을 거친다.
FORMATTED_KEY=$(echo -e "$PRIVATE_KEY")

# SSH 키 파일 생성
# <<EOF ~~~ EOF: 여러 줄의 텍스트를 파일에 작성할 수 있는 방법.
mkdir -p /root/.ssh
cat <<EOF > /root/.ssh/id_ecdsa
$FORMATTED_KEY
EOF
chmod 600 /root/.ssh/id_ecdsa

# GitHub의 호스트 키를 known_hosts에 추가
ssh-keyscan -H github.com >> /root/.ssh/known_hosts

# 로그 출력 (선택 사항, 디버깅 용도)
echo "SSH key and known_hosts configured successfully."