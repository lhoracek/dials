void sampleData(State* state, ulong speed_pips, float rpm_micros, float speed_micros){
  // TODO compute ODO from pips
  state->odo = 0;
  // TODO compute gear from rpm and 
  state->gear = 0;

  // TODO correct computing
  state->speed = (1000000/(float)speed_micros);
  state->rpm = ((1000000/(float)rpm_micros)/4)*60;
  
  state->voltage = (analogRead(VOLTAGE_PIN)/(float)255) * VOLTAGE_MAX ;
  state->fuel = (analogRead(VOLTAGE_PIN)/(float)255) * VOLTAGE_MAX;
  state->temp = (analogRead(TEMP_PIN)/(float)255) * TEMP_MAX;

  state->turnLeft = digitalRead(TURN_LEFT_PIN);
  state->turnRight = digitalRead(TURN_RIGHT_PIN);
  state->neutral = digitalRead(NEUTRAL_PIN);
  state->ignition = digitalRead(IGNITION_PIN);
  state->lowBeam = digitalRead(LOWBEAM_PIN);
  state->highBeam = digitalRead(HIGHBEAM_PIN);
  state->sidestand = digitalRead(SIDESTAND_PIN);
  state->oilPressureLow = digitalRead(OIL_PRESSURE_PIN);
}

void sendData(State* state, BluetoothSerial &s){   
  if (s.hasClient()) {
    String payload = (String) "#{"
    +"\"gear\":"+ (state->gear > 0 ? (String)state->gear : "null")
    +",\"odo\":"+state->odo
    +",\"rpm\":" + state->rpm 
    +",\"voltage\":"+state->voltage
    +",\"fuel\":"+state->fuel
    +",\"temp\":"+state->temp
    +",\"speed\":"+state->speed
    +",\"turnLeft\":"+ (state->turnLeft ? "true":"false")
    +",\"turnRight\":"+ (state->turnRight ? "true":"false")
    +",\"neutral\":"+ (state->neutral ? "true":"false")
    +",\"ignition\":"+ (state->ignition ? "true":"false")
    +",\"lowBeam\":"+ (state->lowBeam ? "true":"false")
    +",\"highBeam\":"+ (state->highBeam ? "true":"false")
    +",\"sidestand\":"+ (state->sidestand ? "true":"false")
    +",\"oilPressureLow\":"+ (state->oilPressureLow ? "true":"false")
    +"}#";
    s.print(payload);
  }
}

void printSerialTable(State* state, HardwareSerial &s){
  s.println("\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n");
  s.println("****************************************************");
  s.println("*\tData\t\t\t\t*");
  s.println("****************************************************");
  s.println("*\t\t\t\t\t*");
  s.println("*\tRPM\tSpeed\tOdo\tFuel\t*"); 
  s.print("*\t");  
  s.print(state->rpm);      
  s.print("\t"); 
  s.print(state->speed);    
  s.print("\t"); 
  s.print(state->odo);    
  s.print("\t"); 
  s.print(state->fuel);    
  s.println("\t*");
  s.println("*\t\t\t\t\t*");
  s.println("*\tGear\tTemp\tVoltage\tOil\t*"); 
  s.print("*\t"); 
  s.print(state->gear);    
  s.print("\t"); 
  s.print(state->temp);    
  s.print("\t"); 
  s.print(state->voltage);    
  s.print("\t"); 
  s.println("\t*");
  s.println("*\t\t\t\t\t*");
  s.println("****************************************************");
}



