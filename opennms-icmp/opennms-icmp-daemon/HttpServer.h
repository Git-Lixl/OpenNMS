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
		/* HttpServer( QTcpSocket *socket, QObject *parent = 0 ); */
		HttpServer( QT_SOCKLEN_T socket, QObject *parent = 0 );
		~HttpServer();
		void run();
		
	public Q_SLOTS:
		void slotDisplayClient( const QString &s );
		void slotDisplayServer( const QString &s );
		void slotDisplayMeta( const QString &s );
		void slotCloseConnection();
		
	private:
		QTcpSocket *mSocket;
};

#endif
