#include <QtCore>
#include <QtCore/QCoreApplication>
#include <QtNetwork/QHostAddress>
#include <QtNetwork/QTcpSocket>
#include <QtNetwork/QTcpServer>

#include "HttpServer.h"
#include "HttpDaemon.h"

int main(int argc, char *argv[])
{
	QCoreApplication app( argc, argv );

	HttpDaemon daemon;

	qDebug() << "The server should now be listening on localhost:8080." << endl;
	qDebug() << "Hit CTRL-C to quit." << endl;
	
	return app.exec();
}
