
# JOIN

<img width="400" src="https://user-images.githubusercontent.com/52390975/63001150-e7dab300-bead-11e9-837c-918a6f33a519.png">



## 서로 걸으면서 움직였던 지도 사진을 공유하는 어플

1. 자신이 걸으면서 시간, 평균속도, 거리, 걸음 수를 측정할 수 있다.

2. 저장된 지도상의 출발지와 목적지등을 팔로워들과 공유, 소통할 수 있다.





## screenshot/GIF
### 1.시작화면

- 앱 실행시 짧은 로딩화면과 함께 로그인 화면이 출력된다.

![](https://media.giphy.com/media/mBSpX90OiVjto0didN/giphy.gif)

### 2.로그인

- 회원가입이 가능하며 회원가입한 이메일과 비밀번호로 로그인 된다.

![](https://media.giphy.com/media/J3GBgYOgtQ3ePFbqXY/giphy.gif)

- 한 번 로그인이 된 상황에서는 앱을 재 시작할 때마다 자동 로그인이 된다.

![](https://media.giphy.com/media/PizkT13Pa5tidimDjh/giphy.gif)

### 3.레코딩
- 시작지점에서 레코딩 할 수 있으며, 정지, 재 시작 기능이 있다.
- 걸어가면서 자신이 걸은 경로를 볼 수 있으며, 총 걸은 시간, 평균 속도, 총 거리, 그리고 걸음 수를 확인 할 수 있다.
  - 분활 화면
<div>
  <img width="200" src="https://user-images.githubusercontent.com/52390975/63000272-b6f97e80-beab-11e9-8c08-ae4904a9b177.png">   <img width="200" src="https://user-images.githubusercontent.com/52390975/63000274-b6f97e80-beab-11e9-8ce9-e82e3fe3d616.png"> 
</div>

  - animated gif

![](https://media.giphy.com/media/ZeR4XemQDEMcfe9J0K/giphy.gif)

### 4. 업로드
- 레코딩 한 기록을 타임라인에 올릴 수 있다.
- 기록일지의 제목과 내용을 함께 올릴 수 있다.


![](https://media.giphy.com/media/MBaYXAcF9uZfuJe7at/giphy.gif)

### 4.타임라인
- 내가 팔로우 한 사람들과 자신의 기록일지(거리, 최고 고도)를 볼 수 있다.

![](https://media.giphy.com/media/YrZfTthvfxE2PH5qN4/giphy.gif)

- 타임라인의 내용을 클릭 시, 상세정보(거리, 이동시간, 평균속도, 걸음 수)를 확인 할 수 있다.

![](https://media.giphy.com/media/LMWd1kVwxwybBMbcjE/giphy.gif)

### 5.팔로우 추가
- 계정 설정란에 들어가서 친구(팔로우) 찾기를 클릭 후, 아이디를 검색하여 친구를 추가 할 수 있다.

![](https://media.giphy.com/media/fAP0xxjWD7frg8LrVQ/giphy.gif)


--------

## Skill 
- Language : Kotlin
- OS : Android
- Database : Firebase
- Library : Google Maps Flatform

## Reference

- Firebase로 안드로이드 SNS 앱 만들기
- 최우진박사님 사이트(https://sites.google.com/site/choeuzin/home)

--------
## 개발일지

1. 1주차 (7.11 ~ 7.12)
7월 11~12일 아이디어 토론. 
Strava란 어플의 토픽을 사용. 운동하기를 도와주는 어플을 실버분들께 적용하기로 생각함.
Join이라는 어플 이름 제작.
  
<img width="400" src="https://user-images.githubusercontent.com/52390975/63145414-56487e00-c032-11e9-92ec-90c5a3d0f043.png">

--------

2. 2주차 (7.15 ~ 7.19)

JOIN어플에 필요한 기능에 대하여 공부하기로 결정. 

심영민 : SNS의 기능을 공부. 
- 파이어베이스 연동 ,Content DTO 구현
- 로그인 페이지
- 사진 업로드 
- 내비게이션 뷰 
- 리사이클러 
- 프래그먼트 액티비티 

박진서 : 구글맵 기능을 공부
- 구글 맵 API키로 구글맵 기능 
- 위치정보 퍼미션확인 기능 
- 카메라 위치, 선 긋기 설정 
- 내 위치, 특정 위치 표시

공통 : 깃헙(GitHub)에 대한 공부 및 이용 시작

--------

3. 3주차 (7.22 ~ 7.26)

심영민 : SNS의 기능 추가
- 폰에 있는 갤러리 사진을 이용해 프로필 이미지 추가
- 친구찾기 기능(파이어 베이스에 연동된 다른 아이디 검색)
- 팔로잉/팔로워 수 확인 기능
- 활동기록 탭 추가

박진서 : 맵 기능에 레코딩 기능 추가

- 버튼리스너를 통해 맵 액티비티 변환
- 위치(위도,경도)를 이동 시 선 긋기 설정
- 위치가 바뀔때마다 위도,경도를 Realm데이터에 저장
- 총 이동경로를 Realm데이터에서 받아 맵 지도에 표시 -> 후에 불필요하다 느껴 사용 X
- 그동안의 맵 기능을 바탕으로 액티비티간 데이터 전송-> 프래그먼트간 데이터 전송으로 새 프로젝트 구현 시작

공통 : 공부한 것을 바탕으로 Merge시작

---------
4. 4주차 (7.29~ 8.2)

 심영민 :
- 지도 업로드 기능과 파이어베이스 연동(DTO) 구현
- 지도 타이머 쓰레드 기능 조정 
- 지도 스냅샷 기능(Google static map의 URI를 이용) -> 어플에 이용 


 박진서 :
- 레코드 디테일(총 시간, 평균 속도, 거리) xml 및 기능 구현
- 프래그먼트 간 애니메이션 기능
- 지도 스냅샷 기능(스크린샷 메소드를 이용)


공통 : 
- 그동안 서로간의 기능들을 Merge함 - > GitHub을 이용
- 맵기능에 초점 , 지도 스냅샷 기능, 출발지&목적지 마커 추가
     
---------
5. 5주차 (8.5~ 8.9)
심화교육(Tensorflow를 이용한 딥 러닝 교육)으로 인해 특별한 기능 구현 x

---------

Record xml 추가

Record의 시간 기능 추가

Map 프래그먼트와 Record 프래그먼트 간 애니메이션 추가.

bound를 이용해 한 화면에 모든 경로를 지난 폴리라인선이 보이게끔 카메라 이동 구현완료. upload버튼 누를시 생김.

upload버튼시 스크린샷되어진 이미지를 sdcard(외부 메모리)에 저장 구현 완료. 1/Aug/2019

14/Aug/2019 핵심기능 구현완료.
