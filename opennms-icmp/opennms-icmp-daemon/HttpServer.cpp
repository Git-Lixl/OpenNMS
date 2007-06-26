#include <QtNetwork/QTcpSocket>
#include <QtCore/QDateTime>
#include <QtCore/QThread>
#include <unistd.h>
#include <stdlib.h>

#include "HttpServer.h"

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

HttpServer::HttpServer( QT_SOCKLEN_T socket, QObject *parent ) : QThread (parent )
{
	mSocket = new QTcpSocket();
	mSocket->setSocketDescriptor(socket);
}

/*
HttpServer::HttpServer( QTcpSocket *socket, QObject *parent ) : QThread ( parent ), mSocket( socket )
{
}
*/

HttpServer::~HttpServer()
{
	if ( mSocket )
	{
		mSocket->close();
		if ( mSocket->state() == QAbstractSocket::ClosingState )
		{
			connect(
				mSocket, SIGNAL(delayedCloseFinished()),
				mSocket, SLOT(deleteLater())
			);
		}
		else
		{
			mSocket->deleteLater();
		}
		mSocket = 0;
	}
}

void HttpServer::slotDisplayClient( const QString &s )
{
	qDebug() << "C: " << trim(s);
}

void HttpServer::slotDisplayServer( const QString &s )
{
	qDebug() << "S: " << trim(s);
}

void HttpServer::slotDisplayMeta( const QString &s )
{
	qDebug() << "M: " << trim(s);
}

void HttpServer::run()
{
	mSocket->waitForReadyRead();
	QString line;
	bool inHeaders = true;

	while ( mSocket->canReadLine() )
	{
		line = trim(mSocket->readLine());
		if (line.isEmpty())
		{
			inHeaders = false;
//			QTextStream out( mSocket );
			QByteArray out;

			long sleeptime = rand() % 5;
			qDebug() << "sleeping " << sleeptime;
			sleep((int)sleeptime);
			
			mSocket->write("HTTP/1.0 200 OK\n");
			mSocket->write("Connection: close\n");
			mSocket->write("Content-type: text/plain\n");
			mSocket->write("\n");
			
			mSocket->write("This is a test.\n");

			QString now = QDateTime::currentDateTime().toString();
			mSocket->write("The current date and time is: ");
			mSocket->write(now.toLocal8Bit());
			mSocket->write("\n");
			
			mSocket->write("My current thread ID is: ");
			mSocket->write(QString((const char*)QThread::currentThreadId()).toLocal8Bit());
			mSocket->write("\n");

			mSocket->close();
		}
		// slotDisplayClient( line );
	}
	exit();
}

void HttpServer::slotCloseConnection()
{
	mSocket->close();
}