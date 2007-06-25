AC_INIT([icmpd],[1.0.5])
RELEASE=1

AC_CONFIG_MACRO_DIR([.])
AC_CONFIG_HEADERS([config.h])

AM_INIT_AUTOMAKE([1.8 foreign])

AC_ENABLE_SHARED([yes])
AC_ENABLE_STATIC([no])

AC_HEADER_STDC

AC_PROG_CC
AC_PROG_LN_S
AC_PROG_LIBTOOL

AC_SUBST(RELEASE)
AM_RPM_INIT
AM_CONDITIONAL(MAKE_RPMS, test x$make_rpms = xtrue)

# Check for pkg-config manually first, as if its not installed the
# PKG_PROG_PKG_CONFIG macro won't be defined.
AC_CHECK_PROG(have_pkg_config, pkg-config, yes, no)

if test x"$have_pkg_config" == xno; then
	AC_MSG_ERROR(pkg-config is required to install this program)
fi

PKG_PROG_PKG_CONFIG
PKG_CHECK_MODULES([LIBDAEMON], [libdaemon])

AC_CONFIG_FILES([Makefile icmpd.spec])

AC_OUTPUT
