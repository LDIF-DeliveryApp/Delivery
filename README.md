# 🛵 Delivery 프로젝트

## 📑 목차
1. [📢 프로젝트 개요](#1-프로젝트-개요)
2. [🎬 주요 기능](#2-주요-기능)
3. [⚙️ 프로젝트 정보 및 개발 환경](#3-프로젝트-정보-및-개발-환경)
4. [🛠 기술 스택](#4-기술-스택)
5. [🏗 아키텍처](#5-아키텍처)
6. [📂 패키지 구조](#6-패키지-구조)
7. [🚀 실행 방법](#7-실행-방법)
8. [🌐 배포 환경](#8-배포-환경)
9. [🔗 링크 (팀 노션)](#9-링크-팀-노션)
10. [👥 팀 구성](#10-팀-구성)

---

## 1️⃣ 프로젝트 개요
> Delivery 프로젝트는 기존의 전화 주문 방식에서 벗어나 고객과 가게 간의 주문 과정을 온라인으로 처리할 수 있도록 만든 배달 주문 관리 서비스입니다.
고객은 가게와 메뉴를 확인하고 원하는 주문을 생성할 수 있으며 가게 주인은 들어온 주문을 확인하고 상태를 변경하면서 주문을 관리할 수 있습니다.
또한, 관리자 계정을 통해 전체 가게와 주문을 관리할 수 있도록 구성하였습니다. 이번 프로젝트에서는 실제 배달 서비스의 기본 흐름을 단순화하여 구현하는 데 집중하였고 Spring Boot 기반의 모놀리식 구조에서 사용자, 가게, 메뉴, 주문 간의 관계를 직접 설계하고 API로 연결해보았습니다.
이 과정을 통해 서비스 흐름을 이해하고 API 설계와 데이터 구조를 어떻게 잡아야 하는지 경험하는 것을 목표로 하였습니다.

## 2️⃣ 주요 기능
#### 🔐 인증
- 회원가입: 사용자 계정을 생성하는 기능
- 로그인: 인증 정보를 기반으로 사용자 로그인 처리
- 로그아웃: 로그인된 사용자 세션을 종료하는 기능
#### 👤 사용자
- 회원 정보 조회: 사용자 정보 확인 기능
- 회원 목록 조회: 전체 사용자 목록 조회 기능
- 회원 정보 수정: 사용자 정보 수정 기능
- 비밀번호 변경: 계정 보안을 위한 비밀번호 변경 기능
- 회원 탈퇴: 사용자 계정 삭제 기능
- 사용자 권한 변경: 사용자 역할(권한) 변경 기능
- 아이디 중복 확인: 회원가입 시 아이디 중복 여부 확인
#### 📍 주소 / 지역
- 주소 관리: 사용자 배송지 등록, 수정, 삭제 및 조회 기능
- 대표 주소 설정: 기본 배송지 설정 및 조회 기능
- 지역 관리: 서비스 제공 지역 등록, 조회, 수정, 삭제 기능
#### 🏪 가게 / 메뉴
- 가게 관리: 음식점 등록, 조회, 수정, 삭제 기능
- 메뉴 관리: 가게별 음식 메뉴 등록, 조회, 수정, 삭제 기능
#### 📦 주문
- 음식 주문: 사용자가 메뉴를 선택하여 주문을 생성하는 기능
- 주문 내역 조회: 사용자의 주문 이력을 조회하는 기능
- 주문 상세 조회: 특정 주문의 상세 정보 확인 기능
- 주문 수정 및 삭제: 주문 정보 변경 및 삭제 기능
- 주문 상태 관리: 가게 주인이 주문 진행 상태를 변경하는 기능
- 주문 취소: 일정 시간(5분) 내 주문 취소 기능
#### 💳 결제
- 결제 처리: 주문에 대한 결제를 수행하는 기능
- 결제 상태 조회: 결제 진행 상태 확인 기능
- 결제 내역 조회: 결제 기록 조회 기능
- 결제 취소: 결제 취소 처리 기능
#### ⭐ 리뷰 / 평점
- 리뷰 관리: 리뷰 작성, 조회, 수정, 삭제 기능
- 평점 등록: 음식점에 대한 평점 등록 기능
#### 🤖 AI 기능
- 음식/상품 추천 및 설명 생성: 사용자 입력을 기반으로 메뉴를 추천하고 설명을 생성하는 기능

## 3️⃣ 개발 환경
- **IDE:** [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- **Language:** Java 17 (OpenJDK)
- **Framework:** Spring Boot 3.5.13
- **Database:** PostgreSQL
- **Build Tool:** Gradle 8.14.4
### 📝 Workflow & Communication
- <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white"> : 회의록, API명세서, ERD, 진행상황, 규칙, 일정
- <img src="https://img.shields.io/badge/github-000000?style=for-the-badge&logo=github&logoColor=white"> : 코드 관리 및 이슈 트래킹
    - **Branch 전략:** `main` (배포), `dev` (통합), `feature/*` (기능 개발)
    - **Issue & PR:** GitHub Issue를 생성 후, 해당 이슈 번호를 포함한 PR을 작성
- 💬 **Communication:** Slack
### ⚙️ Environment Variables
- **환경별 설정 파일(`application-*.properties`)을 통해 관리**
    - **application.properties** : 환경 지정
    - **application-local.properties** : 로컬 개발 환경
    - **application-prod.properties** : 운영 환경
    - **application-test.properties** : 테스트 코드 환경
### 🚀 Deployment
- **Server:** AWS EC2

## 4️⃣ 기술 스택

| Category        | Tech                          | Version / Config        | Notes                          |
|----------------|-------------------------------|--------------------------|--------------------------------|
| Language       | Java                          | 17 (LTS)                |                                |
| Framework      | Spring Boot                   | 3.5.13                     |                                |
| Security       | Spring Security + JWT         | jjwt                    | 매 요청 DB 권한 재검증         |
| Validation     | Spring Validation             | -                       | DTO 유효성 검사                |
| ORM            | JPA / Hibernate               | -                       |                                |
| Query          | QueryDSL JPA                  | -                       | 복합 검색 구현 (도전)          |
| Database       | PostgreSQL                    | 15                     | EC2 직접 설치                  |
| Build Tool     | Gradle                        | 8.x                     |                                |
| API Docs       | springdoc-openapi (Swagger)   | -                       | 또는 Spring REST Docs          |
| AI Integration | Google Gemini 1.5 Flash       | -                       | REST 호출                      |
| HTTP Client    | RestTemplate / WebClient      | -                       | AI API 호출용, 비교 후 선택    |
| Logging        | Logback                       | -                       | 로깅 처리 (도전)               |
| Server         | AWS EC2 (t2.micro)            | Ubuntu 22.04            |                                |

## 5️⃣ 아키텍처 
### 🧩 시스템 아키텍처
![시스템 아키텍처](https://hackmd.io/_uploads/BJeLLJ10Wl.jpg)

### ☁️ 배포 아키텍처
![배포 아키텍처](https://hackmd.io/_uploads/SJ9VPKOpWl.png)


## 6️⃣ 패키지 구조
```text
com.ldif.delivery
├── ai # AI 기능 (상품 설명 생성)
│ ├── application.service
│ ├── domain
│ ├── infrastructure.api.gemini
│ └── presentation
│
├── area # 지역 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── auth # 인증 / 인가 (JWT)
│ ├── application.service
│ └── presentation
│
├── category # 카테고리 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── menu # 메뉴 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── order # 주문 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── review # 리뷰 / 평점 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── store # 가게 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
├── user # 사용자 관리
│ ├── application.service
│ ├── domain
│ └── presentation
│
└── global.infrastructure # 공통 인프라 영역
├── config # 설정 (Security, JWT 등)
├── entity # 공통 엔티티 (BaseEntity 등)
└── presentation # 공통 응답 / 예외 처리
```
### 🔹 계층 설명
- **presentation**: 클라이언트의 요청을 처리하는 계층으로, Controller와 DTO를 포함합니다.
- **application.service**: 핵심 비즈니스 로직을 담당하는 계층으로, 서비스 로직을 처리합니다.
- **domain**: 도메인의 핵심 모델을 정의하는 계층으로, Entity 및 Repository 인터페이스를 포함합니다.
- **infrastructure**: 외부 API 연동(Gemini), DB 접근 등 기술적인 구현을 담당하는 계층입니다.
- **global.infrastructure**: 공통 설정, 보안, 예외 처리 등 전역적으로 사용하는 코드를 관리합니다.

## 7️⃣ 실행 방법

- 본 프로젝트는 **Gradle Wrapper**를 사용하여 별도의 Gradle 설치 없이 빌드 및 실행이 가능합니다. 실행 전 루트 디렉토리에 환경 변수 설정을 위한 `.env` 파일이 반드시 존재해야 합니다.

### 📋 사전 요구 사항 (Prerequisites)
* **.env 파일**: 프로젝트 루트(`root/`)에 생성 (보안을 위해 `.gitignore`에 등록 필수)
  ```properties
  JWT_SECRET_KEY=your_secret_key_at_least_32_characters
  GEMINI_API_KEY=your_gemini_api_key
  ```

### 💻 로컬 개발 환경 (Local Environment)

로컬에서는 `./gradlew dev` 태스크를 통해 `.env` 파일을 자동으로 로드하여 실행할 수 있습니다.

1. **실행 권한 부여 (최초 1회)**
   ```bash
   chmod +x gradlew
   ```

2. **애플리케이션 실행**
   ```bash
   # .env 환경변수를 자동으로 로드하여 서버를 구동합니다.
   ./gradlew dev
   ```


### ☁️ 운영 환경 배포 (AWS EC2 Production)

운영 환경에서는 JAR 파일을 빌드하여 전송한 후, 배포 스크립트(`deploy.sh`)를 사용하여 안전하게 재시작합니다.

#### **1단계: 로컬에서 빌드 (JAR 생성)**
```bash
# 테스트 단계를 제외하고 빠르게 실행 가능한 JAR 파일을 생성합니다.
./gradlew clean build -x test
```
* 생성 파일: `build/libs/[프로젝트명]-0.0.1-SNAPSHOT.jar`

#### **2단계: EC2로 파일 전송**
```bash
# JAR 파일과 .env 파일을 EC2 서버로 전송합니다.
scp -i <key-pair>.pem build/libs/*.jar ubuntu@<EC2-IP>:/home/ubuntu/
scp -i <key-pair>.pem .env ubuntu@<EC2-IP>:/home/ubuntu/
```

#### **3단계: 배포 스크립트 실행**
EC2 서버 내에서 기존 프로세스를 종료하고 서버를 재시작합니다.
```bash
# 배포 스크립트 실행 권한 부여
chmod +x deploy.sh

# 서버 재배포 및 실행 (기존 프로세스 종료 포함)
./deploy.sh
```

### ☁️ 배포 방식
본 프로젝트는 EC2 환경에서 효율적인 배포를 위해 전용 스크립트를 사용합니다.
* **스크립트 파일**: `deploy.sh`
* **주요 역할**: 
    1. 기존 실행 중인 서버 프로세스(PID) 자동 종료
    2. `.env` 환경 변수 최신화 및 로드
    3. `nohup`을 이용한 백그라운드 서버 실행 및 로그 기록

### 🛠️ 주요 명령어 요약
| 명령어 | 설명 |
| :--- | :--- |
| `./gradlew dev` | 로컬에서 `.env` 로드 후 서버 즉시 실행 (개발용) |
| `./gradlew build` | 전체 프로젝트 빌드 및 JAR 파일 생성 (테스트 포함) |
| `./gradlew clean` | 기존 빌드 결과물(`build/` 폴더) 삭제 |


---

## 8️⃣ 배포 환경
![스크린샷 2026-04-24 120746](https://hackmd.io/_uploads/rJO2CUdpZe.png)

### 시스템 아키텍처
> 클라이언트 → EC2(Spring Boot) → PostgreSQL / Google Gemini API
### 기술 스택

| 항목 | 내용 |
|---|---|
| 클라우드 | AWS EC2 t2.micro (Ubuntu 22.04, 프리티어) |
| 애플리케이션 | Spring Boot :8080 (JWT + Spring Security) |
| 데이터베이스 | PostgreSQL :5432 |
| 외부 API | Google Gemini API |
| 컨테이너 | Docker |
| CI/CD | GitHub Actions |
| 네트워크 | AWS VPC, Security Group (포트 22, 8080) |

### CI/CD 파이프라인
```
GitHub Push
    ↓
GitHub Actions (빌드 + 테스트)
    ↓
Docker 이미지 빌드
    ↓
EC2 배포 (Docker 컨테이너 실행)
```

### 환경 변수
`.env.example` 파일을 복사 후 값을 채워주세요.
```
DB_URL=jdbc:postgresql://localhost:5432/배달앱_db명
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret_key
GEMINI_API_KEY=your_gemini_api_key
```

### 로컬 실행 방법
```bash
git clone https://github.com/your-org/your-repo.git
cd your-repo
cp .env.example .env
# .env 파일에 값 입력 후
docker-compose up -d
```


## 9️⃣ 링크 (팀 노션) 
- 🔗 https://www.notion.so/teamsparta/1-3432dc3ef514804ea78eec43b8be938f 

## 1️⃣0️⃣ 팀 구성 
| 이름 | GitHub |  담당 기능 |
|------|--------|-----------|
| 강다연 | [@181108-cherry](https://github.com/181108-cherry) |  가게, 결제 |
| 이다혜 | [@dahye1111](https://github.com/dahye1111) |   주문 |
| 노동완 | [@Wansix](https://github.com/Wansix/) |  인증/인가, 사용자, 리뷰 |
| 김신영 | [@tlsdud135](https://github.com/tlsdud135) |   상품, 지역, AI |
| 강동민 | [@DONGMIN-777](http://github.com/DONGMIN-777) | 주소 |
| 김우범 | [@woobeomkim](https://github.com/woobeomkim) | 카테고리 |
