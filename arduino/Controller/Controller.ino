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
#define RPM_PIN 32
#define SPEED_PIN 33
// ANALOG
#define VOLTAGE_PIN 36
#define TEMP_PIN 37
#define FUEL_PIN 38

// DIGITAL
#define OIL_PRESSURE_PIN 34
#define SIDESTAND_PIN 35
#define TURNLIGHT_PIN 25
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

long rpmMicros;
long speedMicros;
ulong speedPips;

long lastRpmMicros;
void IRAM_ATTR handleRpmInterrupt() {
  long mics= micros();
  portENTER_CRITICAL_ISR(&rpmMux);
    // TODO smoothing
    rpmMicros = mics - lastRpmMicros;
  portEXIT_CRITICAL_ISR(&rpmMux);
  lastRpmMicros = mics;
}

long lastSpeedMicros;
void IRAM_ATTR handleSpeedInterrupt() {
  long mics= micros();
  portENTER_CRITICAL_ISR(&speedMux);
    speedMicros = mics - lastSpeedMicros;
    speedPips++;
  portEXIT_CRITICAL_ISR(&speedMux);
  lastSpeedMicros = mics;
}

long btLastTime = 0;
long displayLastTime = 0;
long eepromLastTime = 0;

void setup() {

  Serial.begin(115200);
  SerialBT.begin(BT_NAME);
  Serial.println("The device started, now you can pair it with bluetooth!");
  pinMode (LED_PIN, OUTPUT);

  pinMode(RPM_PIN, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(RPM_PIN), handleRpmInterrupt, RISING);
  pinMode(SPEED_PIN, INPUT_PULLUP);
  attachInterrupt(digitalPinToInterrupt(SPEED_PIN), handleSpeedInterrupt, RISING);

  pinMode(OIL_PRESSURE_PIN, INPUT_PULLUP);
  pinMode(SIDESTAND_PIN, INPUT_PULLUP);
  pinMode(TURNLIGHT_PIN, INPUT_PULLUP);
  pinMode(NEUTRAL_PIN, INPUT_PULLUP);
  pinMode(IGNITION_PIN, INPUT_PULLUP);
  pinMode(LOWBEAM_PIN, INPUT_PULLUP);
  pinMode(HIGHBEAM_PIN, INPUT_PULLUP);

  display.begin();  
  display.set_contrast(255);
}

void loop() {
  speedPips = millis();

  if(timePassed(&btLastTime,BT_MILLIS)){
    long rpmMics;
    long speedMics;
    long speedPs;

    portENTER_CRITICAL(&rpmMux);
      rpmMics = rpmMicros;
    portEXIT_CRITICAL(&rpmMux);
    portENTER_CRITICAL(&speedMux);
      speedMics = speedMicros;
      speedPs = speedPips++;
    portEXIT_CRITICAL(&speedMux);

    //sampleData(&state, speedPs, rpmMics, speedMics);
    mockData(&state, speedPips);
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
  Serial.println(speedPips);
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


