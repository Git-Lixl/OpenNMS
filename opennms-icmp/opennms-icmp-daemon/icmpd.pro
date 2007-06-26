#HEADERS       = HttpDaemon.h
#SOURCES       = HttpDaemon.cpp \
#                main.cpp
HEADERS       = HttpDaemon.h \
				HttpServer.h
SOURCES       = HttpDaemon.cpp \
				HttpServer.cpp \
				main.cpp
QT           += network
QT           -= gui
CONFIG       -= app_bundle
