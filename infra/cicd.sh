#!/bin/bash

KEY_PATH="~/.ssh/key-dompoo.pem"
USER_HOST="ubuntu@ec2-3-34-186-106.ap-northeast-2.compute.amazonaws.com"
REMOTE_SCRIPT_PATH="./deploy.sh"

# (1) 원격 EC2에 배포 스크립트 업로드
scp -i $KEY_PATH ./deploy.sh $USER_HOST:$REMOTE_SCRIPT_PATH

# (2) 원격 EC2에서 배포 스크립트 실행
ssh -i $KEY_PATH $USER_HOST "bash $REMOTE_SCRIPT_PATH"
