#include <QtNetwork/QTcpSocket>

#include "HttpDaemon.h"
#include "HttpSocketResponder.h"

static const QHostAddress localhost( 0x7f000001 ); // 127.0.0.1

HttpDaemon::HttpDaemon( QObject *parent ) : QTcpServer( parent )
{
	listen( localhost, 8080 );

	/*	
	connect(
		this, SIGNAL( newConnection() ),
		this, SLOT( newConnectionAvailable() )
	);
	*/
}

void HttpDaemon::incomingConnection( int socketDescriptor )
{
	qDebug() << socketDescriptor << ": incoming connection";
	HttpServer *thread = new HttpServer( socketDescriptor, this );
	connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
	qDebug() << socketDescriptor << ": starting http server thread";
	thread->start();
}

/*
void HttpDaemon::newConnectionAvailable()
{
	QTcpSocket *socket = nextPendingConnection();
	QT_SOCKLEN_T descriptor = socket->socketDescriptor();
	HttpServer *hts = new HttpServer( descriptor, this );
	hts->start();
}
*/