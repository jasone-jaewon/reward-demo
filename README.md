# reward-demo
## 리눅스 서버에 설치 방법
### 필수 설치 요소
* git
### 설치 절차
1. 리눅스 서버에 소스 코드 clone 받을 디렉토리를 생성.
2. reward-demo 저장소 url(https://github.com/jasone-jaewon/reward-demo.git) 을 복사하여 clone
  * git clone https://github.com/jasone-jaewon/reward-demo.git
3. clone받은 디렉토리로 이동하여 gradlew 의 실행 권한을 변경해준다.
  * chmod 755 gradlew
4. gradle로 빌드
  * ./gradlew build

### 실행
1. 빌드 후 build/libs 디렉토리로 이동한다.
2. 생성된 jar 파일을 실행한다.
  * java -jar reward-demo-0.0.1-SNAPSHOT.jar
