void drawDisplay(State* state, OLED &s, BluetoothSerial &bts){
    display.clear();

    drawControl(66,0, state->turnLeft, "<");
    drawControl(82,0, state->neutral, "N");
    drawControl(98,0, state->turnRight, ">");
    drawControl(58,10, state->ignition, "I");
    drawControl(74, 10, bts.hasClient(), "R"); 
    drawControl(90,10, state->highBeam, "S");
    drawControl(66,20, state->highBeam, "O");
    drawControl(82,20, state->lowBeam, "L");
    drawControl(98,20, state->highBeam, "H");

    drawBar(0, 33, 128, state->fuel, 0, 100, "Fuel"); 
    drawBar(0, 44, 37, state->voltage, 11, 16, "Vol");
    drawBar(40, 44, 88, state->temp, 60, 110, "Temp");
    drawBar(0, 55, 128, state-> rpm, 0, 12000, "RPM");
    
    drawValue(0,14, state->speed, "%3d", true);
    display.draw_string(40,11, "km");
    display.draw_line(40,19, 52, 19);
    display.draw_string(41,21, "h");
    drawValue(10,0, state->odo, "%6dkm", false);
    drawValue(113,8, state->gear, "%1d", true);
    display.draw_rectangle(112,5, 127, 24,OLED::HOLLOW);
    
    display.display();
}

void drawValue(int x, int y, int value, char* format, boolean large){ 
  char buffer[9]; 
  sprintf(buffer, format, value); 
  display.draw_string(x,y, buffer, large ? OLED::DOUBLE_SIZE : OLED::NORMAL_SIZE); 
}

void drawBar(int x, int y, int len, float value, int minVal, int maxVal, char* text){
    display.draw_string(x,y+1,text); 
    int off = (strlen(text)* 6) + 4;
    int diff = (maxVal - minVal);
    int scale = (len-(off + 2));
    float coeff = ((float)scale/diff);
    float realVal = (min(max(minVal,value),maxVal) - minVal);
    float scaleVal = coeff * realVal;
    int val = scaleVal ;
    //Serial.println("" + (String)state + " -> " + val + " ["+minVal+","+maxVal+"] : d:" + diff + " s:" + scale + " c:" + coeff + " rv:" + realVal + " sv:" + scaleVal);
    display.draw_rectangle(x+off,y, x+(len-1), y+8,OLED::HOLLOW);
    display.draw_rectangle(x+1+off,y+1, x+off+val, y+7,OLED::SOLID);
}

void drawControl(int x, int y, boolean state, char* text){
    display.draw_circle(x+6,y+5,5, state ? OLED::SOLID : OLED::HOLLOW); 
    display.draw_circle(x+6,y+5,5, state ? OLED::SOLID : OLED::HOLLOW); 
    display.draw_string(x+3,y+2, text, OLED::NORMAL_SIZE, state ? OLED::BLACK : OLED::WHITE); 
}


