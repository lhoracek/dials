#ifndef Type_h
#define Types_h

typedef struct {
  int rpm;
  int gear;
  int odo;
  float voltage;
  int fuel;
  int temp;
  int speed;
  boolean oilPressureLow;
  boolean sidestand;
  boolean turnRight;
  boolean turnLeft;
  boolean neutral;
  boolean ignition;
  boolean lowBeam;
  boolean highBeam;
} State;

#endif


