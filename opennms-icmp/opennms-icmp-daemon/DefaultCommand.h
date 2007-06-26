#ifndef __DEFAULTCOMMAND_H__
#define __DEFAULTCOMMAND_H__

#include <QtCore/QString>
#include <QtCore/QStringList>

class DefaultCommand : public QObject {
	Q_OBJECT
	
public:
	DefaultCommand( QStringList arguments );
	virtual void execute();
	virtual int responseCode();
	virtual QString responseCodeText();
	virtual QString responseHeaders();
	virtual QString responseText();
	virtual QString responseContentType();

protected:
	QStringList arguments;
	int responseCodeValue;
	QString responseCodeTextValue;	
};

#endif
