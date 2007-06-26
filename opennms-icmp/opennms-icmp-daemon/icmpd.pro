#HEADERS       = HttpDaemon.h
#SOURCES       = HttpDaemon.cpp \
#                main.cpp
HEADERS       = HttpDaemon.h \
				HttpSocketResponder.h
SOURCES       = HttpDaemon.cpp \
				HttpSocketResponder.cpp \
				main.cpp
QT           += network
QT           -= gui
CONFIG       -= app_bundle
