#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

#define FRONT_LEFT_MOTOR_NUMBER   1
#define BACK_LEFT_MOTOR_NUMBER    2
#define BACK_RIGHT_MOTOR_NUMBER   3
#define FRONT_RIGHT_MOTOR_NUMBER  4

#define PWM_MIN -255
#define PWM_MAX  255
#define MIN_SPEED 50
#define TIMEOUT 1000

Adafruit_MotorShield motorManager;
Adafruit_DCMotor *motorFrontLeft;
Adafruit_DCMotor *motorBackLeft;
Adafruit_DCMotor *motorBackRight;
Adafruit_DCMotor *motorFrontRight;

int rightSideMotorSpeedCommand;
int leftSideMotorSpeedCommand;
long lastUpdateTimeMs = millis();


void setup() {
  Serial.begin(115200);
  Serial.println("Starting setup");
  initMotors();
}
/*
* Loads serial data if available, releases motors after 1 second with data
*/
void loop() {
  if(readSerialData()){
    lastUpdateTimeMs = millis();
    setMotorSpeeds(rightSideMotorSpeedCommand, leftSideMotorSpeedCommand);
  } else if(millis() - lastUpdateTimeMs > TIMEOUT) {
    Serial.println("Timed out, releasing motors");
    lastUpdateTimeMs = millis();
    releaseMotors();
  }
}

void initMotors(){
  motorManager = Adafruit_MotorShield();
  motorFrontLeft   = motorManager.getMotor(FRONT_LEFT_MOTOR_NUMBER);
  motorBackLeft    = motorManager.getMotor(BACK_LEFT_MOTOR_NUMBER);
  motorBackRight   = motorManager.getMotor(BACK_RIGHT_MOTOR_NUMBER);
  motorFrontRight  = motorManager.getMotor(FRONT_RIGHT_MOTOR_NUMBER);
  
  motorManager.begin();
  releaseMotors();
}

/*
* Sets all motors speeds, assumes good data
*/
void setMotorSpeeds(int rightMotorSpeed, int leftMotorSpeed){
  setSingleMotorSpeed(motorFrontRight, rightMotorSpeed);
  setSingleMotorSpeed(motorBackRight, rightMotorSpeed);
  setSingleMotorSpeed(motorFrontLeft, leftMotorSpeed);
  setSingleMotorSpeed(motorBackLeft, leftMotorSpeed);
}

/*
* Sets speed of a single motor, assumes good data
*/
void setSingleMotorSpeed(Adafruit_DCMotor *motor, int motorSpeed){
  if(abs(motorSpeed) < MIN_SPEED){
    motor->run(RELEASE);
  } else if (motorSpeed > 0) {
    motor->setSpeed(motorSpeed);
    motor->run(FORWARD);
  } else {
    motor->setSpeed(abs(motorSpeed));
    motor->run(BACKWARD);
  }
}

void releaseMotors(){
  motorFrontLeft->run(RELEASE);
  motorBackLeft->run(RELEASE);
  motorBackRight->run(RELEASE);
  motorFrontRight->run(RELEASE);
}

/* 
* Reads available bluetooth serial data and returns true if a complete set has been read
* Updates rightSideMotorSpeedCommand and leftSideMotorSpeedCommand global variables
*/
boolean readSerialData(){
  const int leftBit = 1;
  const int rightBit = 2;
  const int doneBit = 3;
  
  /* Temporary variable for storing progress, 
  * done == doneBit once right and left each been read at least once
  * Uses OR instead of + to prevent errors if one side is read multiple times
  * Ex. done = 0: done |= rightBit; done |= rightBit; done |= rightBit; done == 1
  * done = 0; done += rightBit; done += rightBit; done += rightBit; done == 3; <-- Considered complete without reading ledt side
  */
  int done = 0; 
  char buffer = '\0';
  
  while(Serial.available() > 0 && done != doneBit){
    buffer = Serial.read();
    switch(buffer){
      case 'R':
        rightSideMotorSpeedCommand = constrain(Serial.parseFloat(), PWM_MIN, PWM_MAX);
        done |= rightBit;
        break;
      case 'L':
        leftSideMotorSpeedCommand = constrain(Serial.parseFloat(), PWM_MIN, PWM_MAX);
        done |= leftBit;
        break;
    }
  } 
  if(done == doneBit) {
    Serial.flush();
    //Serial.print("Right:"); Serial.print(rightSideMotorSpeedCommand); 
    //Serial.print(" Left:"); Serial.println(leftSideMotorSpeedCommand);
    return true;
  } else {
    rightSideMotorSpeedCommand = 0;
    leftSideMotorSpeedCommand = 0; 
    return false;
  }
}
