/* 
 *  lhoracek @ 2019
 */
#include "Types.h"
#include "BluetoothSerial.h"
#include "EEPROM.h"
#include <oled.h>

#define BT_NAME "Viffer 750"
#define LED_PIN 2
#define BT_MILLIS 40
#define DISPLAY_MILLIS 50
#define EEPROM_MILLIS 10000
#define VOLTAGE_MAX 16.5f
#define TEMP_MAX 150.0f;


// FRQUENCY
#define RPM_PIN 15
#define SPEED_PIN 13
// ANALOG
#define VOLTAGE_PIN 36
#define TEMP_PIN 37
#define FUEL_PIN 38

// DIGITAL
#define OIL_PRESSURE_PIN 34
#define SIDESTAND_PIN 35
#define TURN_LEFT_PIN 25
#define TURN_RIGHT_PIN 25
#define NEUTRAL_PIN 26
#define IGNITION_PIN 27
#define LOWBEAM_PIN 14
#define HIGHBEAM_PIN 12

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

portMUX_TYPE rpmMux = portMUX_INITIALIZER_UNLOCKED;
portMUX_TYPE speedMux = portMUX_INITIALIZER_UNLOCKED;

BluetoothSerial SerialBT;
State state;
OLED display = OLED(5, 4, 1, 0x3C, 128, 64);

long rpmMicros = LONG_MAX;
long speedMicros = LONG_MAX;
ulong speedPips = 0;

ulong lastRpmMicros;
void IRAM_ATTR handleRpmInterrupt() {
  ulong mics= micros();
  portENTER_CRITICAL_ISR(&rpmMux);
    // TODO smoothing
    rpmMicros = mics - lastRpmMicros;
    lastRpmMicros = mics;
  portEXIT_CRITICAL_ISR(&rpmMux);
}

ulong lastSpeedMicros;
void IRAM_ATTR handleSpeedInterrupt() {
  ulong mics= micros();
  portENTER_CRITICAL_ISR(&speedMux);
    speedMicros = mics - lastSpeedMicros;
    speedPips++;
    lastSpeedMicros = mics;
  portEXIT_CRITICAL_ISR(&speedMux);
}

long btLastTime = 0;
long displayLastTime = 0;
long eepromLastTime = 0;

void setup() {

  pinMode(RPM_PIN, INPUT);
  attachInterrupt(digitalPinToInterrupt(RPM_PIN), handleRpmInterrupt, RISING);
  pinMode(SPEED_PIN, INPUT);
  attachInterrupt(digitalPinToInterrupt(SPEED_PIN), handleSpeedInterrupt, RISING);

  pinMode(TURN_LEFT_PIN, INPUT);
  pinMode(TURN_RIGHT_PIN, INPUT);
  pinMode(NEUTRAL_PIN, INPUT);
  pinMode(OIL_PRESSURE_PIN, INPUT);
  pinMode(SIDESTAND_PIN, INPUT);
  pinMode(IGNITION_PIN, INPUT);
  pinMode(LOWBEAM_PIN, INPUT);
  pinMode(HIGHBEAM_PIN, INPUT);

  Serial.begin(115200);
  SerialBT.begin(BT_NAME);
  Serial.println("The device started, now you can pair it with bluetooth!");
  pinMode (LED_PIN, OUTPUT);

 

  display.begin();  
  display.set_contrast(255);
}

void loop() {
  speedPips = millis();

  if(timePassed(&btLastTime,BT_MILLIS)){
    long rpmMics;
    long speedMics;
    long speedPs;

    ulong now = micros();
    portENTER_CRITICAL(&rpmMux);
      ulong lastRpm = now - lastRpmMicros;
      rpmMics = lastRpm > rpmMicros ? lastRpm : rpmMicros;
    portEXIT_CRITICAL(&rpmMux);
    portENTER_CRITICAL(&speedMux);
      ulong lastSpeed = now - lastSpeedMicros;
      speedMics = lastSpeed > speedMicros ? lastSpeed : speedMicros;
      speedPs = speedPips++;
    portEXIT_CRITICAL(&speedMux);
    
    sampleData(&state, speedPs, rpmMics, speedMics);
    //mockData(&state, speedPips);
    sendData(&state, SerialBT);
  }

  if(timePassed(&displayLastTime,DISPLAY_MILLIS)){
    drawDisplay(&state, display, SerialBT);
  }

  if(timePassed(&eepromLastTime,EEPROM_MILLIS)){
    // TODO write eeprom
  }
  
  // no led, only display info
  // digitalWrite (LED_PIN, SerialBT.hasClient() ? LOW : HIGH);

  // not needed anymore
  delay(1);
}

boolean timePassed(long *lastTime, int threshold){
  long mills = millis();
  int sinceLast = mills - *lastTime;
  if(sinceLast >= threshold){
    *lastTime = mills;
    return true;
  }
  return false;
}


