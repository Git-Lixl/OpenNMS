#include <QtNetwork/QTcpSocket>
#include <QtCore/QDateTime>
#include <QtCore/QRegExp>
#include <QtCore/QStringList>
#include <QtCore/QThread>
#include <QtCore/QUrl>
#include <unistd.h>
#include <stdlib.h>

#include "HttpSocketResponder.h"
#include "DefaultCommand.h"
#include "PingCommand.h"

static QString trim( const QString & s )
{
  	if ( s.endsWith( "\r\n" ) )
    	return s.left( s.length() - 2 );
  	if ( s.endsWith( "\r" ) || s.endsWith( "\n" ) )
    	return s.left( s.length() - 1 );
  	return s;
}

HttpServer::HttpServer( int socketDescriptor, QObject *parent ) : QThread ( parent ), socketDescriptor( socketDescriptor )
{
}

void HttpServer::run()
{
	QTcpSocket tcpSocket;
	if (!tcpSocket.setSocketDescriptor(socketDescriptor)) {
		emit error(tcpSocket.error());
		return;
	}

	tcpSocket.waitForReadyRead();
	QString line;

	bool inHeaders = true;

	line = trim(tcpSocket.readLine());
	QStringList httpRequest = line.split(QRegExp("\\s+"));
	QUrl *URL = new QUrl(httpRequest[1]);
	QStringList arguments;

	QString path = URL->path();
	path.remove(QRegExp("^/+"));
	arguments = path.split("/");
	QString commandText = arguments.takeFirst().toLower();

	DefaultCommand *command = new DefaultCommand( arguments );
	
	if (httpRequest[0].toLower() == "get")
	{
		/* only command we know for now ;) */
		if (commandText == "ping")
		{
			command = new PingCommand( arguments );
		}
	}

	command->execute();
	
	while ( tcpSocket.canReadLine() )
	{
		line = trim(tcpSocket.readLine());
		if (inHeaders)
		{
			if (line.isEmpty())
			{
				inHeaders = false;

				QTextStream s(&tcpSocket);

				s << "HTTP/1.0 " << command->responseCode() << " " << command->responseCodeText() << "\n";
				if (!command->responseHeaders().isEmpty())
				{
					QString headers = command->responseHeaders();
					headers.remove(QRegExp("[\\r\\n\\s]+$"));
					s << headers << "\n";
				}
				s << "Connection: close\n";
				s << "Content-type: " << command->responseContentType() << "\n";
				s << "\n";
				s << command->responseText();

				s.flush();
			}
		}
		else
		{
			qDebug() << "spurious data after headers have completed: " << line;
		}
	}

	tcpSocket.disconnectFromHost();
	tcpSocket.waitForDisconnected();
	
	exit();
}
