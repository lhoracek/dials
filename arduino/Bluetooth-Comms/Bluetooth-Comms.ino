#include <SoftwareSerial.h>
SoftwareSerial bluetooth(10, 11); 

void setup()  
{
  Serial.begin(115200);
  delay(10);
  bluetooth.begin(9600);
  delay(10);
  pinMode(13, OUTPUT); // light
  
}
char a; // stores incoming character from other device

int revs = 5000;
int gear = 3;
float voltage = 13.5;
float oitTemp = 90;
int speed = 123;
float temp = 50;
float fuel = 50;
boolean turnlight = true;
boolean neutral = true;
boolean engine = true;
boolean lowBeam = true;
boolean highBeam = true;


int base = 400;
int step = base;

void stateValue(char* name, int value){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(", ");
}

void stateValue(char* name, float value){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value);
  bluetooth.print(", ");
}

void stateValue(char* name, boolean value){
  bluetooth.print(name);
  bluetooth.print(":");
  bluetooth.print(value ? 1 : 0);
  bluetooth.print(", ");
}

void sendState(){
  // TODO construct the json content
  bluetooth.print("{");
  stateValue("revs", revs);
  stateValue("gear", gear);
  stateValue("speed", speed);
  stateValue("voltage", voltage);
  stateValue("oilTemp", oitTemp);
  stateValue("turnlight", turnlight);
  stateValue("neutral", neutral);
  stateValue("engine", engine);
  stateValue("lowbeam", lowBeam);
  stateValue("highBeam", highBeam);
  stateValue("temp", temp);
  stateValue("fuel", fuel);
  bluetooth.print("}");
  bluetooth.print("\n");
}

void loop() 
{
  delay(100);
  
  sendState();

  // do your job
  if (bluetooth.available())
  {
    a=(bluetooth.read());
  }
}
