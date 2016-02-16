#include "SerialBuffer.h"

SerialBuffer::SerialBuffer()
{
  _buffer = "";
}

boolean SerialBuffer::hasCommand()
{
  int startIndex = _buffer.indexOf(START_CHAR);
  int endIndex = _buffer.indexOf(END_CHAR);

  if (startIndex == -1 || endIndex == -1){
    return false;
  }

  //if end character is before start, remove everything until the start
  if(startIndex > endIndex && endIndex != -1) {
    _buffer = _buffer.substring(startIndex);
    return false;
  }

  return true;
}

void SerialBuffer::addToBuffer(char next)
{
  _buffer += next;
}

SerialMessage* SerialBuffer::getCommand()
{
  String command = popFirstCommandString();
  char commandType = command.charAt(0);
  String commandContents = command.substring(1);
  SerialMessage *message = new SerialMessage(commandType, commandContents);
  return message;
}

String SerialBuffer::popFirstCommandString(){
  int startIndex = _buffer.indexOf(START_CHAR);
  int endIndex = _buffer.indexOf(END_CHAR);

  String command = _buffer.substring(startIndex+1, endIndex);
  _buffer = _buffer.substring(endIndex); //remove current command

  return command;
}

