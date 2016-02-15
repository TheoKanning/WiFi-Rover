/**
 * Contains command type and contents
 */
 #ifndef SerialMessage_h
 #define SerialMessage_h

 #include "Arduino.h"

 const char RIGHT_MOTOR_COMMAND = 'R';
 const char LEFT_MOTOR_COMMAND = 'L';
 
 class SerialMessage 
 {
   public:
   SerialMessage(char command, String contents);
   char getCommand();
   String getContents();

   private:
     char _command;
     String _contents;
 };

 #endif

 

