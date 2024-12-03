# Space Invaders Server
Space Invaders Server는 클라이언트와 동일한 네트워크(같은 Wi-Fi)에 접속하여 게임을 실행할 수 있도록 지원하는 서버입니다.  
이 서버는 8080 포트를 통해 클라이언트와 통신합니다.


## Client Repository 
클라이언트 리포지토리 주소입니다 : https://github.com/bumsoft/Invaders-SDP

## Set up
- 자바 버전: 17
- DB: Mysql
```
# 아래 명령어로 db생성
create database invaders;
```
- application.properties 파일 수정필요
```
# DB username
spring.datasource.username=사용자이름으로변경할것

# DB password
spring.datasource.password=사용자비밀번호로변경할것
```

- 8080 포트 개방
```
windows방화벽 
-> 고급설정 
-> 인바운드 규칙(새규칙) 추가 
-> 규칙종류:포트 선택 
-> 특정포트 8080입력 
-> 나머지 기본값으로 두고 규칙사용
```
- 서버를 실행한 컴퓨터의 ipv4주소를 공유해주기.
```
cmd실행
->ipconfig 입력
->IPv4 Address 복사
```

- 클라이언트 세팅 관련은 서버 리포지토리 README 참고

## notes
데이터베이스 연결이 안되었을 시 서버 실행에 문제가 발생할 수 있습니다.


# Space Invaders Server
The Space Invaders Server enables the Space Invaders game to run by communicating with the client over the same network (e.g., the same Wi-Fi).
This server uses port 8080 for client-server communication.

## Client Repository
Client repository URL: https://github.com/bumsoft/Invaders-SDP

## Set up
### Requirements:
- Java Version: 17
- Database: MySQL
1. Database Setup
   Run the following command in MySQL to create the database:
```
create database invaders;
```
2. Update application.properties
   Modify the application.properties file to include your database credentials:
```
# DB username
spring.datasource.username=<your-username>

# DB password
spring.datasource.password=<your-password>
```
3. Open Port 8080
   Allow inbound traffic on port 8080:
```
Open Windows Firewall
-> Go to Advanced Settings
-> Add a new Inbound Rule
-> Select Port as the rule type
-> Enter port 8080
-> Leave all other settings as default and enable the rule
```
4. Share IPv4 Address
   Share the IPv4 address of the server machine with the client:
```
Open Command Prompt (cmd)
-> Run the command: ipconfig
-> Copy the IPv4 Address and share it with the client.
```
5. Client Configuration
   Refer to the Client Repository README for client setup instructions.

## Notes
The server may fail to start if the database connection is not properly configured. Ensure that all required database settings are correctly set up before running the server.