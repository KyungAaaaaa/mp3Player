# MP3 Player
안드로이드 MP3 Player 앱 구현 프로젝트

## 개요
- 심플한 테마와 음악플레이어의 핵심기능을 구현한 안드로이드 음악플레이어

### 개발일정
- 2020/07/26 ~ 2020/08/02

### 개발환경
- 운영체제 : Windows 10 64bit
- 언어 : Java - jdk1.8.0_251
- 개발툴 : Andorid Studio 3.5.1
- DB : SQLite3
- 테스트환경 : SANSUNG 갤럭시A5 (SM-A500L API 23)

### 주요기능
1. 재생상태를 플레이 화면과 음악 리스트 위젯 동시 확인가능
2. 좋아요 기능
3. 플레이 리스트기능

### 프로그램 소개

#### 음악 리스트 화면
- 앱 구동시 첫 메인화면
- 외장메모리의 Music폴더의 mp3파일 리스트를 RecyclerView로 표시  
![image](https://user-images.githubusercontent.com/63944004/94948019-96f6a580-0519-11eb-9143-a54794288906.png)

#### 음악 재생
- 재생할 노래를 선택시 메인화면 하단에 위젯 출력
- 일시정지, 음악 정지를 할 수 있음
- 하단 플레이 화면을 클릭시 재생화면으로 전환  
![image](https://user-images.githubusercontent.com/63944004/94948047-a544c180-0519-11eb-989c-324a5b8ebde5.png)
![image](https://user-images.githubusercontent.com/63944004/94948037-9fe77700-0519-11eb-8bbc-11ad575b7905.png)  


#### 재생 화면
- 프로그레스바로 음악 재생구간을 지정할 수 있음
- 한곡 반복 재생, 순차 재생, 랜덤재생 설정 기능
- 일시정지, 이전곡, 다음곡 전환 기능 - 좋아요 기능  
![image](https://user-images.githubusercontent.com/63944004/94949222-8b0be300-051b-11eb-9ee4-22eb6a5b4b73.png)  


#### 옵션메뉴
- 전체 노래 목록 리스트를 표시 (메인화면)
- 보관함
    a. 좋아요 리스트 : 좋아요 표시한 곡 리스트를 표시
    b. 재생 목록 : 생성된 재생목록 리스트를 표시  
![image](https://user-images.githubusercontent.com/63944004/94949422-d9b97d00-051b-11eb-8729-ba296fcf9cc6.png)
![image](https://user-images.githubusercontent.com/63944004/94949517-01104a00-051c-11eb-9c39-79ef2f891cf9.png)  

#### 좋아요 
- 좋아요 리스트에서 확인 가능
- 꽉 찬 하트를 누르면 좋아요 취소  
![image](https://user-images.githubusercontent.com/63944004/94948088-b2fa4700-0519-11eb-993b-48046c87afd0.png)
![image](https://user-images.githubusercontent.com/63944004/94948104-b8f02800-0519-11eb-85c1-7b562f67dc87.png)  


#### 재생목록
- 재생목록을 생성하는 기능 - 노래를 롱 클릭시 생성된 재생목록 리스트가 표시
- 추가할 재생목록을 선택 후 추가할 수 있음  
![image](https://user-images.githubusercontent.com/63944004/94948207-eccb4d80-0519-11eb-916c-5d0ae6812fd4.png)
![image](https://user-images.githubusercontent.com/63944004/94948224-f5238880-0519-11eb-8cea-843ebd459a17.png)  


