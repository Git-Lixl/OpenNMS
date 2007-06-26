#include <QtNetwork/QTcpSocket>

#include "HttpDaemon.h"
#include "HttpSocketResponder.h"

static const QHostAddress localhost( 0x7f000001 ); // 127.0.0.1

HttpDaemon::HttpDaemon( QObject *parent ) : QTcpServer( parent )
{
	listen( localhost, 8080 );
}

void HttpDaemon::incomingConnection( int socketDescriptor )
{
	HttpServer *thread = new HttpServer( socketDescriptor, this );
	connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
	thread->start();
}
