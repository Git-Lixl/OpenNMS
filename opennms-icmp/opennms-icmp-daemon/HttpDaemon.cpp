#include "HttpDaemon.h"
#include "HttpServer.h"

static const QHostAddress localhost( 0x7f000001 ); // 127.0.0.1

HttpDaemon::HttpDaemon( QObject *parent ) : QTcpServer( parent )
{
	listen( localhost, 8080 );
	setMaxPendingConnections( 1 );
	
	connect(
		this, SIGNAL( newConnection() ),
		this, SLOT( newConnectionAvailable() )
	);
}

HttpDaemon::~HttpDaemon()
{
}

void HttpDaemon::newConnectionAvailable()
{
	HttpServer *d = new HttpServer( nextPendingConnection() );
}