## 두번째 개인프로젝트
- 2025년 8월 16일 00시 01분 ~ 2025년 08월 30일 23시 59분
- Spring Boot 3.5.5, Spring Security 6, MySQL 8.4, Ubuntu Server 24.04 LTS, Nginx, Thymeleaf 사용
- 기존 Flask로 구현한 것을 Spring Boot와 Spring Security를 이용하여 다시 구현

---

## 🚀 Deployment & Infrastructure

- **OS**: Ubuntu Server 24.04 LTS
- **Process Management**: systemd  
  - Spring Boot 애플리케이션을 `community.service` 유닛으로 등록  
  - 서버 재부팅 시 자동 실행 및 장애 발생 시 자동 재시작
- **Web Server (Reverse Proxy)**: Nginx  
  - SSL Termination (Let's Encrypt / Certbot)  
  - 정적 리소스 캐싱 및 HTTPS 리다이렉트 처리
- **Application Server (WAS)**: Spring Boot 3.5.5 (Embedded Tomcat, port 8080)
- **Database**: MySQL 8.4 (SSD 저장소)
- **Media Storage**: HDD1 (`/mnt/storage1/upload`)
- **Domain**:
  - A 레코드 → 서버 IP 매핑  
  - HTTPS 적용 (자동 인증서 갱신)

---

## 🔒 Security & Network

- **Firewall**: UFW (Uncomplicated Firewall)  
  - 허용 포트: `22 (SSH), 80 (HTTP), 443 (HTTPS)`  
  - 기본 정책: `deny incoming`, `allow outgoing`

- **Intrusion Prevention**: Fail2ban  
  - SSH 및 Nginx 로그인 시도 모니터링  
  - 비정상적인 로그인 시도 감지 시 자동 IP 차단  
  - Brute-force 공격 방어 정책 적용

- **Additional Hardening**  
  - SSH root 로그인 비활성화  
  - sudo 권한 최소 계정만 허용  
  - system logs 모니터링 (`journalctl`, `fail2ban-client status`)

---

## 💾 Backup Strategy

- **Backup Target**
  - `/mnt/storage1/upload` (업로드된 미디어 파일)
  - MySQL 데이터베이스 (`community`)
  - Spring Boot 애플리케이션 JAR (`community-0.0.1-SNAPSHOT.jar`)
- **Backup Destination**: HDD2 (`/mnt/storage2`)
- **Backup Type**: 스냅샷 기반 (날짜별 폴더 생성)

### How it works
  1. **Auto Mount**
     - 지정된 시간에 HDD2 자동 마운트 (cron job)
  2. **Rsync 백업**
     - 업로드된 미디어 파일 → `/mnt/storage2/snapshots/YYYYMMDD_HHMMSS/upload/`
  3. **DB 백업**
     - `mysqldump`으로 SQL 덤프 생성 → `community_backup.sql`
  4. **애플리케이션 JAR 백업**
     - 실행 중인 JAR 파일 복사 → `community-0.0.1-SNAPSHOT.jar`
  5. **Auto Unmount**
     - 백업 완료 후 HDD2 자동 언마운트
  7. **Result**
     - 특정 시점의 업로드된 미디어 파일 + DB덤프 + 애플리케이션 실행 파일이 한 세트로 보관됨

### Example
  /mnt/storage2/snapshots/20250302_030000/<br>
  &emsp;&emsp;├── upload/ # 업로드된 미디어 파일<br>
  &emsp;&emsp;├── community_backup.sql # MySQL 데이터베이스 백업<br>
  &emsp;&emsp;└── community-0.0.1-SNAPSHOT.jar # 애플리케이션 실행 파일
