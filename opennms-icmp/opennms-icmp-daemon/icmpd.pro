#HEADERS       = HttpDaemon.h
#SOURCES       = HttpDaemon.cpp \
#                main.cpp
HEADERS       = HttpDaemon.h \
				HttpSocketResponder.h \
				DefaultCommand.h \
				PingCommand.h
SOURCES       = HttpDaemon.cpp \
				HttpSocketResponder.cpp \
				DefaultCommand.cpp \
				PingCommand.cpp \
				main.cpp
QT           += network
QT           -= gui
CONFIG       -= app_bundle
