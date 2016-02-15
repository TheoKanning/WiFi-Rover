/**
 * Class for managing buffer and parsing commands
 */

#ifndef SerialBuffer_h
#define SerialBuffer_h

 #include "Arduino.h"
 #include "SerialMessage.h"

 const unsigned char START_CHAR = '(';
 const unsigned char END_CHAR = ')';

 
 class SerialBuffer {
   public:
     SerialBuffer();
     boolean hasCommand();
     void addToBuffer(char next);
     SerialMessage* getCommand();
     
   private:
     String _buffer;
     String popFirstCommandString();
 };

#endif
