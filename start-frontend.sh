#!/bin/bash

echo "=========================================="
echo "의약품 허가심사 검토 시스템 - Frontend 시작"
echo "=========================================="
echo ""

# 환경 확인
echo "[1/3] 환경 확인 중..."

# Node.js 버전 확인
if ! command -v node &> /dev/null; then
    echo "❌ Node.js가 설치되어 있지 않습니다. Node.js 18 이상을 설치해주세요."
    exit 1
fi

NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
if [ "$NODE_VERSION" -lt 18 ]; then
    echo "❌ Node.js 18 이상이 필요합니다. 현재 버전: $(node -v)"
    exit 1
fi

echo "✅ Node.js 버전: $(node -v)"
echo "✅ npm 버전: $(npm -v)"

# Frontend 디렉토리로 이동
cd frontend || exit 1

# 의존성 확인
echo ""
echo "[2/3] 의존성 확인 중..."

if [ ! -d "node_modules" ]; then
    echo "의존성을 설치합니다..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ npm install 실패"
        exit 1
    fi
else
    echo "✅ 의존성이 이미 설치되어 있습니다."
fi

# Vite 개발 서버 실행
echo ""
echo "[3/3] Vite 개발 서버 시작 중..."
echo ""
echo "=========================================="
echo "Frontend 실행 중: http://localhost:3000"
echo "=========================================="
echo ""

npm run dev
