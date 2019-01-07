// TODO to be removed and replaced with actual sampling
void mockData(State* state, long pips) {
  state->rpm = abs(((pips*10)%26000)-13000);
  state->odo = (pips) % 20000;
  state->voltage = abs(((pips/10) % 100)-50) / 10.0f + 11.0f ;
  state->gear = ((pips / 100) % 7);
  state->fuel = abs( ((pips / 8) + 50) % 200-100);
  state->temp = (abs((((pips / 8) % 200)-100)) / 2.0f) + 60;
  state->speed = (pips/5) % 250;
  state->turnLeft = ((pips / 200) % 2) > 0;
  state->turnRight = ((pips / 200) % 2) < 1;
  state->neutral = ((pips / 200) % 2) < 1;
  state->ignition = ((pips / 200) % 2) > 0;
  state->lowBeam = ((pips / 200) % 2) <1 ;
  state->highBeam = ((pips / 200) % 2) > 0;
  state->oilPressureLow = ((pips / 200) % 2) <1 ;
  state->sidestand = ((pips / 200) % 2) > 0;
}

