ACLOCAL_AMFLAGS = -I .
AM_CPPFLAGS = $(LIBDAEMON_CFLAGS) $(all_includes)
distdir = $(PACKAGE)-$(VERSION)-$(RELEASE)

bin_PROGRAMS = icmpd
icmpd_SOURCES = icmpd.c
icmpd_LDFLAGS = $(LIBDAEMON_LDFLAGS)
icmpd_LDADD = $(LIBDAEMON_LIBS)

MAINTAINERCLEANFILES = aclocal.m4 config.guess config.h.in config.sub configure depcomp install-sh ltmain.sh Makefile.in missing *~

if MAKE_RPMS
rpm: @RPM_TARGET@

.PHONY: rpm

EXTRA_DIST = icmpd.spec

$(RPM_TARGET): $(DISTFILES)
	${MAKE} dist
	-mkdir -p $(RPM_DIR)/SRPMS
	-mkdir -p `dirname $(RPM_TARGET)`
	$(RPMBUILD_PROG) $(RPM_ARGS) $(RPM_TARBALL)
	@echo "$(RPM_TARGET) created"
endif
