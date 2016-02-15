#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "SerialBuffer.h"
#include "SerialMessage.h"

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

int rightMotorSpeed;
int leftMotorSpeed;
long lastUpdateTimeMs = millis();

String serialBuffer = "";
SerialBuffer *serialBufferTest = new SerialBuffer();

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
  if(serialBufferTest->hasCommand()){
    SerialMessage* command = serialBufferTest->getCommand();
    Serial.println(command->getCommand());
    Serial.println(command->getContents());
    performCommand(command);
    lastUpdateTimeMs = millis();
    setMotorSpeeds();
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



void readSerialDataIntoBuffer(){
  while(Serial.available()){
    char next = (char) Serial.read();
    serialBuffer += next;
    serialBufferTest->addToBuffer(next);
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

void performCommand(SerialMessage* message){
  String contents = message->getContents();
  switch(message->getCommand()){
    case RIGHT_MOTOR_COMMAND:
      setRightMotorSpeed(contents.toInt());
      break;
    case LEFT_MOTOR_COMMAND:
      setLeftMotorSpeed(contents.toInt());
      break;
  }
}

boolean readCommandsFromBuffer(){
  int startIndex = serialBuffer.indexOf(COMMAND_START);
  int endIndex = serialBuffer.indexOf(COMMAND_END);

  String command = serialBuffer.substring(startIndex+1, endIndex);
  serialBuffer = serialBuffer.substring(endIndex); //remove current command
  
  int separatorIndex = command.indexOf(COMMAND_SEPARATOR);
  String right = command.substring(0, separatorIndex);
  int rightCommand = right.toInt();
  

  String left = command.substring(separatorIndex + 1);
  int leftCommand = left.toInt();
  leftMotorSpeed = constrain(leftCommand, PWM_MIN, PWM_MAX);
        
  //Serial.print("Right:"); Serial.print(rightMotorSpeed); 
  //Serial.print(" Left:"); Serial.println(leftMotorSpeed);
}

void setRightMotorSpeed(int motorSpeed){
  rightMotorSpeed = constrain(motorSpeed, PWM_MIN, PWM_MAX);
  Serial.print("Setting right side speed to "); Serial.println(rightMotorSpeed);
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

void releaseMotors(){
  motorFrontLeft->run(RELEASE);
  motorBackLeft->run(RELEASE);
  motorBackRight->run(RELEASE);
  motorFrontRight->run(RELEASE);
}
