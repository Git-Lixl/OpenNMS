#ifndef __HTTPDAEMON_H__
#define __HTTPDAEMON_H__

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
