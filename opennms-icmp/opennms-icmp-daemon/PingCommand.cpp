#include "PingCommand.h"
#include <QtCore/QDebug>

PingCommand::PingCommand( QStringList arguments ) : DefaultCommand( arguments )
{
	qDebug() << "PingCommand instantiated";
}

void PingCommand::execute()
{
}

QString PingCommand::responseText( )
{
	return "I like dongs!\n";
}