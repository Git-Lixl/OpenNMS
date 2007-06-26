#ifndef __PINGCOMMAND_H__
#define __PINGCOMMAND_H__

#include "DefaultCommand.h"

class PingCommand : public DefaultCommand {
	Q_OBJECT
	
public:
	PingCommand( QStringList arguments );
	virtual void execute();
	virtual QString responseText();

protected:
	int responseCodeValue;
	QString responseCodeTextValue;	
};

#endif
