#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_PWMServoDriver.h"

#define FRONT_LEFT_MOTOR_NUMBER   4
#define BACK_LEFT_MOTOR_NUMBER    1
#define BACK_RIGHT_MOTOR_NUMBER   2
#define FRONT_RIGHT_MOTOR_NUMBER  3

#define PWM_MIN -255
#define PWM_MAX  255
#define MIN_SPEED 50
#define TIMEOUT 100

#define COMMAND_START '('
#define COMMAND_END ')'
#define COMMAND_SEPARATOR ','

Adafruit_MotorShield motorManager;
Adafruit_DCMotor *motorFrontLeft;
Adafruit_DCMotor *motorBackLeft;
Adafruit_DCMotor *motorBackRight;
Adafruit_DCMotor *motorFrontRight;

int rightSideMotorSpeedCommand;
int leftSideMotorSpeedCommand;
long lastUpdateTimeMs = millis();

String serialBuffer = "";


void setup() {
  Serial.begin(115200);
  Serial.print("Starting setup");
  initMotors();
}
/*
* Loads serial data if available, releases motors after 1 second with data
*/
void loop() {
  readSerialDataIntoBuffer();
  if(commandAvailable()){
    readCommandsFromBuffer();
    lastUpdateTimeMs = millis();
    setMotorSpeeds(rightSideMotorSpeedCommand, leftSideMotorSpeedCommand);
  } else if(millis() - lastUpdateTimeMs > TIMEOUT) {
    //Serial.println("Timed out");
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

void readSerialDataIntoBuffer(){
  while(Serial.available()){
    serialBuffer += (char)Serial.read();
  }
}

//Return true if buffer contains command start and stop
boolean commandAvailable() {
  int startIndex = serialBuffer.indexOf(COMMAND_START);
  int endIndex = serialBuffer.indexOf(COMMAND_END);

  if (startIndex == -1 || endIndex == -1){
    return false;
  }

  //if end character is before start, remove everything until the start
  if(startIndex > endIndex) {
    serialBuffer = serialBuffer.substring(startIndex);
    return false;
  }
  
  return true;
}

boolean readCommandsFromBuffer(){
  int startIndex = serialBuffer.indexOf(COMMAND_START);
  int endIndex = serialBuffer.indexOf(COMMAND_END);

  String command = serialBuffer.substring(startIndex+1, endIndex);
  serialBuffer = serialBuffer.substring(endIndex); //remove current command
  
  int separatorIndex = command.indexOf(COMMAND_SEPARATOR);
  String right = command.substring(0, separatorIndex);
  int rightCommand = right.toInt();
  rightSideMotorSpeedCommand = constrain(rightCommand, PWM_MIN, PWM_MAX);

  String left = command.substring(separatorIndex + 1);
  int leftCommand = left.toInt();
  leftSideMotorSpeedCommand = constrain(leftCommand, PWM_MIN, PWM_MAX);
        
  //Serial.print("Right:"); Serial.print(rightSideMotorSpeedCommand); 
  //Serial.print(" Left:"); Serial.println(leftSideMotorSpeedCommand);
}
