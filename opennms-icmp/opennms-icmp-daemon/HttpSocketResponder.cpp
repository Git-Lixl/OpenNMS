#include <QtNetwork/QTcpSocket>
#include <QtCore/QDateTime>
#include <QtCore/QThread>
#include <unistd.h>
#include <stdlib.h>

#include "HttpSocketResponder.h"

static QString err2str( QAbstractSocket::SocketError error )
{
  	switch ( error ) {
    	case QAbstractSocket::ConnectionRefusedError: return "Connection refused";
    	case QAbstractSocket::HostNotFoundError: return "Host not found";
    	default: return "Unknown error";
  	}
}

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
	qDebug() << socketDescriptor << ": new instance of HttpServer thread";
}

void HttpServer::run()
{
	QTcpSocket tcpSocket;
	if (!tcpSocket.setSocketDescriptor(socketDescriptor)) {
		emit error(tcpSocket.error());
		return;
	}

	qDebug() << socketDescriptor << ": thread started";
	tcpSocket.waitForReadyRead();
	qDebug() << socketDescriptor << ": ready to read";
	QString line;
	bool inHeaders = true;

	while ( tcpSocket.canReadLine() )
	{
		line = trim(tcpSocket.readLine());
		if (inHeaders)
		{
			if (line.isEmpty())
			{
				inHeaders = false;

				tcpSocket.write("HTTP/1.0 200 OK\n");
				tcpSocket.write("Connection: close\n");
				tcpSocket.write("Content-type: text/plain\n");
				tcpSocket.write("\n");

				tcpSocket.write("This is a test.\n");

				QString now = QDateTime::currentDateTime().toString();
				tcpSocket.write("The current date and time is: ");
				tcpSocket.write(now.toLocal8Bit());
				tcpSocket.write("\n");

				tcpSocket.write("My current thread ID is: ");
				tcpSocket.write(QString((const char*)QThread::currentThreadId()).toLocal8Bit());
				tcpSocket.write("\n");

				tcpSocket.disconnectFromHost();
				tcpSocket.waitForDisconnected();
			}
		}
		else
		{
			qDebug() << "spurious data after headers have completed: " << line;
		}
	}
	exit();
}
