#include <QtNetwork/QTcpSocket>

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

HttpServer::HttpServer( QTcpSocket *socket, QObject *parent ) : QObject( parent ), mSocket( socket )
{
	Q_ASSERT( socket );
	
	connect( socket, SIGNAL(disconnected()), SLOT(slotConnectionClosed()) );
	connect( socket, SIGNAL(error(QAbstractSocket::SocketError)), SLOT(slotError(QAbstractSocket::SocketError)) );
  	connect( socket, SIGNAL(readyRead()), SLOT(slotReadyRead()) );
}

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

/*
void HttpServer::slotSendResponse()
{
	const QString line = QString(mLine);
	mLine = "";
	QTextStream s( mSocket );
	s << line << "\n";
	slotDisplayServer( line );
}
*/

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

void HttpServer::slotReadyRead()
{
	QString line;
	bool inHeaders = true;
	while ( mSocket->canReadLine() )
	{
		line = trim(mSocket->readLine());
		if (line.isEmpty())
		{
			inHeaders = false;
			QTextStream s( mSocket );
			s << "HTTP/1.0 200 OK" << endl << "Connection: close" << endl << "Content-type: text/plain" << endl << endl;
			s << "This is a test." << endl;
			mSocket->close();
		}
		// slotDisplayClient( line );
	}
}

void HttpServer::slotError( QAbstractSocket::SocketError error )
{
	slotDisplayMeta( QString( "E: %1").arg( err2str(error)) );
}

void HttpServer::slotConnectionClosed()
{
	slotDisplayMeta( "Connection closed by peer." );
}

void HttpServer::slotCloseConnection()
{
	mSocket->close();
}