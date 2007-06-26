#ifndef __DEFAULTCOMMAND_H__
#define __DEFAULTCOMMAND_H__

#include <QtCore/QString>
#include <QtCore/QStringList>

class DefaultCommand : public QObject {
	Q_OBJECT
	
public:
	DefaultCommand( QStringList arguments );
	void execute();
	int responseCode();
	QString responseCodeText();
	QString responseHeaders();
	QString responseText();
	QString responseContentType();

protected:
	QStringList arguments;
};

#endif
