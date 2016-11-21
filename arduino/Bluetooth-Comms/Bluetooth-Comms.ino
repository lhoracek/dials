#include <SoftwareSerial.h>
SoftwareSerial bluetooth(10, 11);

const byte rpmPin = 2;
const byte speedPin = 3;
const byte statusPin = 13;

const byte turnPin = 9;
const byte neutralPin = 8;
const byte enginePin = 7;
const byte lowBeamPin = 6;
const byte highBeamPin = 5;

unsigned int odo = 0;
unsigned int rpm = 0;
unsigned int gear = 0;
float voltage = 0;
float oitTemp = 0;
unsigned int speed = 0;
float temp = 0;
float fuel = 0;
boolean turnlight = false;
boolean neutral = false;
boolean engine = false;
boolean lowBeam = false;
boolean highBeam = false;

const int smoothSizeRpm = 100;
const int smoothSizeSpeed = 10;
int lastRpms[smoothSizeRpm];
int lastSpeeds[smoothSizeSpeed];
int readIndexRpm = 0;
int readIndexSpeed = 0;
unsigned long lastRpm;
unsigned long lastSpeed;

char a; // stores incoming character from other device
unsigned long lastIn; //

void setup()
{
  // set communication preferences
  Serial.begin(115200);
  delay(10);
  bluetooth.begin(115200);
  delay(10);
  pinMode(statusPin, OUTPUT); // light

  // digital input pins
  pinMode(turnPin,  INPUT_PULLUP);
  pinMode(neutralPin,  INPUT_PULLUP);
  pinMode(enginePin,  INPUT_PULLUP);
  pinMode(lowBeamPin,  INPUT_PULLUP);
  pinMode(highBeamPin,  INPUT_PULLUP);

  // prepare interrupts for frequency measuring
  pinMode(speedPin,  INPUT); // light
  pinMode(rpmPin,  INPUT); // light
  delay(10);
  attachInterrupt(digitalPinToInterrupt(speedPin), triggerSpeed, RISING);
  delay(10);
  attachInterrupt(digitalPinToInterrupt(rpmPin), triggerRpm, RISING);
  delay(10);
}

void triggerRpm() {
  int lastIndex = readIndexRpm;
  readIndexRpm = (readIndexRpm + 1) % smoothSizeRpm;
  lastRpms[readIndexRpm] = (micros() - lastRpms[lastIndex]);
  lastRpm = micros();
}

void triggerSpeed() {

  speed = (1000000 / (micros() - lastSpeed)) / 100;
  lastSpeed = micros();
  readIndexSpeed++;
}

void stateValue(char* name, unsigned int value, boolean last) {
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(last ? "" : ", ");
}

void stateValue(char* name, float value, boolean last) {
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(last ? "" : ", ");
}

void stateValue(char* name, boolean value, boolean last) {
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value ? "true" : "false");
  bluetooth.print(last ? "" : ", ");
}

void updateState{
  // read digital values (using grounding with pullup now)
  turnlight = 1 - digitalRead(turnPin);
  neutral = 1 - digitalRead(neutralPin);
  engine = 1 - digitalRead(enginePin);
  lowBeam = 1 - digitalRead(lowBeamPin);
  hightBeam = 1 - digitalRead(highBeamPin);

  // read analog values


  // smooth last pulse values
  int sum = 0;
  for (int i = 0; i < smoothSizeRpm; i++) {
    sum += lastRpms[i];
  }
  rpm = 1000000 / (sum / smoothSizeRpm);

  sum = 0;
  for (int i = 0; i < smoothSizeSpeed; i++) {
    sum += lastSpeeds[i];
  }
  speed = 1000000 / (sum / smoothSizeSpeed);
}

void sendState() {
  // TODO construct the json content
  bluetooth.print("{");
  stateValue("rpm", rpm, false);
  stateValue("odo", odo, false);
  stateValue("gear", gear, false);
  stateValue("speed", speed, false);
  stateValue("voltage", voltage, false);
  stateValue("oilTemp", oitTemp, false);
  stateValue("turnlight", turnlight, false);
  stateValue("neutral", neutral, false);
  stateValue("engine", engine, false);
  stateValue("lowBeam", lowBeam, false);
  stateValue("highBeam", highBeam, false);
  stateValue("temp", temp, false);
  stateValue("fuel", fuel, true);
  bluetooth.print("}");
  bluetooth.print("\n");
}

unsigned long time;

void loop()
{
  updateState();

  time = micros();
  noInterrupts();
  sendState(); // send state through to bluetooth

  // check for incoming bytes
  if (bluetooth.available())
  {
    // TODO copy to char* and check for \n
    lastIn = millis();
    a = (bluetooth.read());
  }
  interrupts();
  time = micros() -  time;
  
  int diff = millis() - lastIn;
  digitalWrite(statusPin, (diff) > 1000 ? 0 : 1);

  delay( 40 - (time / 1000));
}
