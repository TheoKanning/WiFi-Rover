#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "SerialBuffer.h"

#define FRONT_LEFT_MOTOR_NUMBER   2
#define BACK_LEFT_MOTOR_NUMBER    3
#define BACK_RIGHT_MOTOR_NUMBER   4
#define FRONT_RIGHT_MOTOR_NUMBER  1

#define PWM_MIN -255
#define PWM_MAX  255
#define MIN_SPEED 50
#define TIMEOUT 150

#define COMMAND_START '('
#define COMMAND_END ')'
#define COMMAND_SEPARATOR ','

Adafruit_MotorShield motorManager;
Adafruit_DCMotor *motorFrontLeft;
Adafruit_DCMotor *motorBackLeft;
Adafruit_DCMotor *motorBackRight;
Adafruit_DCMotor *motorFrontRight;

int rightMotorSpeed;
int leftMotorSpeed;
long lastUpdateTimeMs = millis();

SerialBuffer *serialBuffer = new SerialBuffer();
SerialMessage *message = new SerialMessage();

void setup() {
  Serial.begin(115200);
  Serial.println("Starting setup");
  initMotors();
}

/*
* Loads serial data if available, releases motors after 1 second with data
*/
void loop() {
  readSerialDataIntoBuffer();
  if(serialBuffer->hasCommand()){
    serialBuffer->getCommand(message);
    performCommand(message);
    lastUpdateTimeMs = millis();
    setMotorSpeeds();
  } else if(millis() - lastUpdateTimeMs > TIMEOUT) {
    //Serial.println("Timed out");
    lastUpdateTimeMs = millis();
    resetSpeeds();
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

void readSerialDataIntoBuffer(){
  while(Serial.available()){
    char next = (char) Serial.read();
    serialBuffer->addToBuffer(next);
  }
}

void performCommand(SerialMessage* message){
  String contents = message->_contents;
  switch(message->_command){
    case RIGHT_MOTOR_COMMAND:
      setRightMotorSpeed(contents.toInt());
      break;
    case LEFT_MOTOR_COMMAND:
      setLeftMotorSpeed(contents.toInt());
      break;
  }
}

void setRightMotorSpeed(int motorSpeed){
  rightMotorSpeed = constrain(motorSpeed, PWM_MIN, PWM_MAX);
}

void setLeftMotorSpeed(int motorSpeed){
  leftMotorSpeed = constrain(motorSpeed, PWM_MIN, PWM_MAX);
}

void setMotorSpeeds(){
  //both right motors set to same speed and vice versa
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

void resetSpeeds(){
  rightMotorSpeed = 0;
  leftMotorSpeed = 0;
}

void releaseMotors(){
  motorFrontLeft->run(RELEASE);
  motorBackLeft->run(RELEASE);
  motorBackRight->run(RELEASE);
  motorFrontRight->run(RELEASE);
}
