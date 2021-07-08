/*
 * NeoPixel : 15 -> D8
 * GasSensor : A0 -> A0
 * DHT : 12 -> D6
 * Buzzer : 14 -> D5
 * DHT PIN과 Buzzer PIN을 잘못 연결한 코드임 ==> 코드 올리기 전에 꼭 확인
 * 와이파이는 휴대폰 핫스팟 이용(POCO F1)
 * 
 */

#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include<avr/power.h>
#endif
Adafruit_NeoPixel strip = Adafruit_NeoPixel(12, 15, NEO_GRB + NEO_KHZ800);

#include<DHT.h>
DHT dht(12, DHT11);

#define FIREBASE_HOST "iotcontest-e8046.firebaseio.com"
#define FIREBASE_AUTH ""
#define WIFI_SSID "IoT_Smart_Home"
#define WIFI_PASSWORD "e8046iot"

float colorR = 255.0;
float colorG = 255.0;
float colorB = 255.0;


void setup() {
  pinMode(A0, INPUT);
  // 와이파이 연결
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
  }
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  strip.begin();
  dht.begin();
}

void loop()
{
  // 온도센서 값 조사 + Firebase 서버에 업로드
 float t = dht.readTemperature();
 float h = dht.readHumidity();
 Firebase.setFloat("aee8q/Temp/", t);
 Firebase.setFloat("aee8q/Hum/", h);
 // 가스센서 값 조사 + Firebase 서버에 업로드
 int gas = analogRead(A0);
 Firebase.setFloat("aee8q/GasSensor/", gas);
  // NeoPixel(LED)의 색을 Firebase 서버와 동기화
  colorR = Firebase.getFloat("aee8q/Color/Color_R/");
  colorG = Firebase.getFloat("aee8q/Color/Color_G/");
  colorB = Firebase.getFloat("aee8q/Color/Color_B/");
  float brightness = Firebase.getFloat("aee8q/brightness/");
  strip.setBrightness(brightness);
  strip.show();

  
  if(Firebase.getFloat("aee8q/PowerOn/") == 0)
  {
      Firebase.setFloat("aee8q/brightness/", 0.0); // 밝기를 0으로(전원 끄기)
  }
  else if(brightness == 0)
  {
    Firebase.setFloat("aee8q/brightness/", 100.0);
  }
  
  
      for(int i = 0; i<strip.numPixels(); i++)
     {
        strip.setPixelColor(i, strip.Color(colorR, colorG, colorB));
        strip.show();
        delay(100);
      }
  


  //화재 발생 시


  if(t > 40.0 || gas > 150 || Firebase.getFloat("FireDetected") == 1)
  {
    Firebase.setFloat("aee8q/Color/Color_R/", 255.0);
    Firebase.setFloat("aee8q/Color/Color_G/", 0.0);
    Firebase.setFloat("aee8q/Color/Color_B/", 0.0); // 색을 빨간색으로
    Firebase.setFloat("aee8q/brightness/", 100.0);
    Firebase.setFloat("FireDetected", 1);
    tone(14, 300, 1800);
   
  }
  else
  {
    if(colorR > (5 * colorB) && colorR > (5 * colorG) )
    {
    Firebase.setFloat("aee8q/Color/Color_R/", 255.0);
    Firebase.setFloat("aee8q/Color/Color_G/", 255.0);
    Firebase.setFloat("aee8q/Color/Color_B/", 255.0); 
    }
  }


  

  
}

