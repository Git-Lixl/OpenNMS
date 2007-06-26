#include "PingCommand.h"
#include <QtCore/QDebug>

PingCommand::PingCommand( QStringList arguments ) : DefaultCommand( arguments ), responseCodeValue(500), responseCodeTextValue("Internal Server Error")
{
}

void PingCommand::execute()
{
}

QString PingCommand::responseText( )
{
	return "I *hate* cheese!\n";
}