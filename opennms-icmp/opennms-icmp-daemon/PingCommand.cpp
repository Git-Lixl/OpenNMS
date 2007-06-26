#include "PingCommand.h"
#include <QtCore/QDebug>

PingCommand::PingCommand( QStringList arguments ) : DefaultCommand( arguments ), responseCodeValue(500), responseCodeTextValue("Internal Server Error")
{
	qDebug() << "PingCommand instantiated";
}

void PingCommand::execute()
{
}

int PingCommand::responseCode()
{
	return responseCodeValue;
}

QString PingCommand::responseCodeText()
{
	return responseCodeTextValue;
}

QString PingCommand::responseText( )
{
	return QString("I like dongs!");
}