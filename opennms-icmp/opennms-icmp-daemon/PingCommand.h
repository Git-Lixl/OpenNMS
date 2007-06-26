#ifndef __PINGCOMMAND_H__
#define __PINGCOMMAND_H__

#include "DefaultCommand.h"

class PingCommand : public DefaultCommand {
	Q_OBJECT
	
public:
	PingCommand( QStringList arguments );
	void execute();
	int responseCode();
	QString responseCodeText();
	QString responseText();

protected:
	int responseCodeValue;
	QString responseCodeTextValue;	
};

#endif
