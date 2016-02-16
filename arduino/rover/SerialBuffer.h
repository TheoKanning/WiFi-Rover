/**
 * Class for managing buffer and parsing commands
 */

#ifndef SerialBuffer_h
#define SerialBuffer_h

 #include "Arduino.h"
 
 const unsigned char START_CHAR = '(';
 const unsigned char END_CHAR = ')';

 const char RIGHT_MOTOR_COMMAND = 'R';
 const char LEFT_MOTOR_COMMAND = 'L';

 class SerialMessage 
 {
   public:
     char _command;
     String _contents;
 };
 
 class SerialBuffer {
   public:
     SerialBuffer();
     boolean hasCommand();
     void addToBuffer(char next);
     void getCommand(SerialMessage *message);
     
   private:
     String _buffer;
     String popFirstCommandString();
 };

 


#endif
