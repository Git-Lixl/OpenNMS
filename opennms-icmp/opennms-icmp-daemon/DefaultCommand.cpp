#include "DefaultCommand.h"
#include <QtCore/QDebug>

DefaultCommand::DefaultCommand( QStringList arguments ) : arguments(arguments), responseCodeValue(501), responseCodeTextValue("Not Implemented")
{
}

void DefaultCommand::execute()
{
}

int DefaultCommand::responseCode()
{
	return responseCodeValue;
}

QString DefaultCommand::responseCodeText()
{
	return responseCodeTextValue;
}

QString DefaultCommand::responseHeaders()
{
	return QString();
}

QString DefaultCommand::responseText()
{
	return "I like cheese!\n";
}

QString DefaultCommand::responseContentType()
{
	return "text/plain";
}