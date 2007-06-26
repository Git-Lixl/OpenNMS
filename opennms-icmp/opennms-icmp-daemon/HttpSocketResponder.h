#ifndef __HTTPSERVER_H__
#define __HTTPSERVER_H__

#include <QtNetwork/QTcpSocket>
#include <QtNetwork/QAbstractSocket>
#include <QtCore/QString>
#include <QtCore/QThread>
#include "qplatformdefs.h"

class HttpServer : public QThread {
	Q_OBJECT
	
	public:
		HttpServer( int socketDescriptor, QObject *parent = 0 );
		void run();
		
	signals:
	    void error(QTcpSocket::SocketError socketError);
	
	private:
		int socketDescriptor;
};

#endif
