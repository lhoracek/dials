#include <SoftwareSerial.h>
SoftwareSerial bluetooth(10, 11); 

const byte rpmPin = 2;
const byte speedPin = 3;

unsigned long lastRpm;
unsigned long lastSpeed;

const byte statusPin = 13;

const byte turnPin = 9;
const byte neutralPin = 8;
const byte enginePin = 7;
const byte lowBeamPin = 6;

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

char a; // stores incoming character from other device
unsigned long lastIn; //

void setup()  
{
  Serial.begin(115200);
  delay(10);
  bluetooth.begin(115200);
  delay(10);
  pinMode(statusPin, OUTPUT); // light
  pinMode(speedPin,  INPUT); // light
  pinMode(rpmPin,  INPUT); // light

  pinMode(turnPin,  INPUT_PULLUP);
  pinMode(neutralPin,  INPUT_PULLUP);
  pinMode(enginePin,  INPUT_PULLUP);
  pinMode(lowBeamPin,  INPUT_PULLUP);
  
  delay(10);
  attachInterrupt(digitalPinToInterrupt(speedPin), triggerSpeed, RISING);
  delay(10);
  attachInterrupt(digitalPinToInterrupt(rpmPin), triggerRpm, RISING);
  delay(10);
  
}

void triggerRpm() {
  long delta = micros() - lastRpm;
  if(delta < 80) delta = 80;

  rpm = 1000000 / delta;
  lastRpm = micros();
  
}

void triggerSpeed() {
  long delta = micros() - lastSpeed;
  if(delta < 80) delta = 80;
  speed = (1000000 / delta) / 100;
  lastSpeed = micros();
}

void stateValue(char* name, unsigned int value, boolean last){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(last ? "" : ", ");
}

void stateValue(char* name, float value, boolean last){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(last ? "" : ", ");
}

void stateValue(char* name, boolean value, boolean last){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value ? "true" : "false");
  bluetooth.print(last ? "" : ", ");
}

void sendState(){
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

  turnlight = 1-digitalRead(turnPin);
  neutral = 1-digitalRead(neutralPin);
  engine = 1-digitalRead(enginePin);
  lowBeam = 1-digitalRead(lowBeamPin);
  
  noInterrupts();
  time = micros();
  sendState();
  Serial.print(rpm);
  Serial.print(" : ");
  Serial.print(speed);
  Serial.println();
  // do your job
  if (bluetooth.available())
  {
    lastIn = millis();
    a=(bluetooth.read());
  }
  time = micros() -  time;
  interrupts();

  int diff = millis() - lastIn;
  digitalWrite(statusPin, (diff) > 1000 ? 0 : 1);
  
  delay( 40 -(time / 1000));
}
