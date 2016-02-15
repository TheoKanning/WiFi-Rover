#include "SerialMessage.h"

SerialMessage::SerialMessage(char command, String contents)
{
  _command = command;
  _contents = contents;
}

char SerialMessage::getCommand()
{
  return _command;
}

String SerialMessage::getContents()
{
  return _contents;
}

