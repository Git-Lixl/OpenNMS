#ifndef HTTPDAEMON_H
#define HTTPDAEMON_H

#include <QtNetwork/QTcpServer>

class HttpDaemon : public QTcpServer
{
	Q_OBJECT
	
	public:
		HttpDaemon( QObject *parent = 0 );
		~HttpDaemon();
		
	private Q_SLOTS:
		void newConnectionAvailable();

};

#endif
