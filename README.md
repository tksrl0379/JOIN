
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

https://media.giphy.com/media/PizkT13Pa5tidimDjh/giphy.gif

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



## Skill
- Language : Kotlin
- OS : Android
- Database : Firebase
- Library : Google Maps Flatform


## 개발일지

7월 11~12일 아이디어 토론. Join에 대한 주제로 좁혀짐.




Record xml 추가

Record의 시간 기능 추가

Map 프래그먼트와 Record 프래그먼트 간 애니메이션 추가.

bound를 이용해 한 화면에 모든 경로를 지난 폴리라인선이 보이게끔 카메라 이동 구현완료. upload버튼 누를시 생김.

upload버튼시 스크린샷되어진 이미지를 sdcard(외부 메모리)에 저장 구현 완료. 1/Aug/2019

14/Aug/2019 핵심기능 구현완료.
