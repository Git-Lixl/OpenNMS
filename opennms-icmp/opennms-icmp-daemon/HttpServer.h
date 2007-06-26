#include <QtNetwork/QTcpSocket>
#include <QtNetwork/QAbstractSocket>
#include <QtCore/QString>

class HttpServer : public QObject {
	Q_OBJECT
	
	public:
		HttpServer( QTcpSocket *socket, QObject *parent = 0 );
		~HttpServer();
		
	public Q_SLOTS:
		/* void slotSendResponse(); */
		void slotDisplayClient( const QString &s );
		void slotDisplayServer( const QString &s );
		void slotDisplayMeta( const QString &s );
		void slotReadyRead();
		void slotError( QAbstractSocket::SocketError error );
		void slotConnectionClosed();
		void slotCloseConnection();
		
	private:
		QTcpSocket *mSocket;
		QString *mLine;

};