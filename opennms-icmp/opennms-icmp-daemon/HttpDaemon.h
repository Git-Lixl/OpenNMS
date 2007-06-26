#ifndef __HTTPDAEMON_H__
#define __HTTPDAEMON_H__

#include <QtNetwork/QTcpServer>

class HttpDaemon : public QTcpServer
{
	Q_OBJECT
	
	public:
		HttpDaemon( QObject *parent = 0 );
		
	protected:
		void incomingConnection( int socketDescriptor );
};

#endif
