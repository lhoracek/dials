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

int odo = 0;
int revs = 0;
int gear = 0;
float voltage = 0;
float oitTemp = 0;
int speed = 0;
float temp = 0;
float fuel = 0;
boolean turnlight = false;
boolean neutral = false;
boolean engine = false;
boolean lowBeam = false;
boolean highBeam = false;



void stateValue(char* name, int value, boolean last){
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
  stateValue("revs", revs, false);
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

int base = 400;
int step = base;

void loop() 
{
  delay(50);

  if(revs > 13000)  step = -base;
  if(revs <= 0) step = base;
  revs = revs + step;
  sendState();

  // do your job
  if (bluetooth.available())
  {
    a=(bluetooth.read());
  }
}
