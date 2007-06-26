#include "DefaultCommand.h"
#include <QtCore/QDebug>

DefaultCommand::DefaultCommand( QStringList arguments ) : arguments(arguments)
{
	qDebug() << "DefaultCommand instantiated";
}

void DefaultCommand::execute()
{
}

int DefaultCommand::responseCode()
{
	return 500;
}

QString DefaultCommand::responseCodeText()
{
	return "Internal Server Error";
}

QString DefaultCommand::responseHeaders()
{
	return QString();
}

QString DefaultCommand::responseText()
{
	return QString();
}

QString DefaultCommand::responseContentType()
{
	return "text/plain";
}