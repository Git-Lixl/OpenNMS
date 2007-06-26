#include <QtCore>
#include <QtCore/QCoreApplication>
#include <QtNetwork/QHostAddress>
#include <QtNetwork/QTcpSocket>
#include <QtNetwork/QTcpServer>

#include "HttpDaemon.h"

int main(int argc, char *argv[])
{
	QCoreApplication app( argc, argv );

	HttpDaemon daemon;

	qDebug() << "The server should now be listening on localhost:8080.";
	qDebug() << "Hit CTRL-C to quit.";
	
	return app.exec();
}
