#include "PingCommand.h"
#include <QtCore/QDebug>

#include <sys/types.h>
#include <sys/socket.h>
#if defined(__SOLARIS__) || defined (__FreeBSD__)
# include <netinet/in_systm.h>
#endif
#if defined(__DARWIN__) 
#include <stdint.h>
# include <netinet/in_systm.h>
# include <AvailabilityMacros.h>
# ifndef MAC_OS_X_VERSION_10_3
#  define socklen_t int
# endif
#endif
#include <netinet/in.h>
#include <netinet/ip.h>
#include <netinet/ip_icmp.h> 
#include <netdb.h>

#if defined(__DARWIN__) || defined(__SOLARIS__) || defined (__FreeBSD__)
typedef struct ip iphdr_t;
typedef struct icmp icmphdr_t;
#define ihl ip_hl
#else
typedef struct iphdr iphdr_t;
typedef struct icmphdr icmphdr_t;
#endif

#if defined(__FreeBSD__)
#include "byteswap.h"
#endif

#if defined(__DARWIN__)
#include <architecture/byte_order.h>
#endif

/**
 * This macro is used to recover the current time
 * in milliseconds.
 */
#ifndef CURRENTTIMEMILLIS
#define CURRENTTIMEMILLIS(_dst_) \
{                               \
        struct timeval tv;      \
        gettimeofday(&tv,NULL); \
        _dst_ = (uint64_t)tv.tv_sec * 1000UL + (uint64_t)tv.tv_usec / 1000UL; \
}
#endif

/** 
 * This macro is used to recover the current time
 * in microseconds
 */
#ifndef CURRENTTIMEMICROS
#define CURRENTTIMEMICROS(_dst_) \
{                               \
        struct timeval tv;      \
        gettimeofday(&tv,NULL); \
        _dst_ = (uint64_t)tv.tv_sec * 1000000UL + (uint64_t)tv.tv_usec; \
}
#endif

/**
 * converts microseconds to milliseconds
 */
#ifndef MICROS_TO_MILLIS
# define MICROS_TO_MILLIS(_val_) ((_val_) / 1000UL)
#endif

/**
 * convert milliseconds to microseconds.
 */
#ifndef MILLIS_TO_MICROS
# define MILLIS_TO_MICROS(_val_) ((_val_) * 1000UL)
#endif

/**
 * This constant specifies the length of a 
 * time field in the buffer
 */
#ifndef TIME_LENGTH
# define TIME_LENGTH sizeof(uint64_t)
#endif

/**
 * Specifies the header offset and length
 */
#ifndef ICMP_HEADER_OFFSET
# define ICMP_HEADER_OFFSET 0
# define ICMP_HEADER_LENGTH 8
#endif

/** 
 * specifies the offset of the sent time.
 */
#ifndef SENTTIME_OFFSET
# define SENTTIME_OFFSET (ICMP_HEADER_OFFSET + ICMP_HEADER_LENGTH)
#endif

/**
 * Sepcifies the offset of the received time.
 */
#ifndef RECVTIME_OFFSET
# define RECVTIME_OFFSET (SENTTIME_OFFSET + TIME_LENGTH)
#endif

/**
 * Specifies the offset of the thread identifer
 */
#ifndef THREADID_OFFSET
# define THREADID_OFFSET (RECVTIME_OFFSET + TIME_LENGTH)
#endif

/**
 * Specifies the offset of the round trip time
 */
#ifndef RTT_OFFSET
# define RTT_OFFSET (THREADID_OFFSET + TIME_LENGTH)
#endif

/**
 * specifies the magic tag and the offset/length of
 * the tag in the header.
 */
#ifndef OPENNMS_TAG
# define OPENNMS_TAG "OpenNMS!"
# define OPENNMS_TAG_LEN 8
# define OPENNMS_TAG_OFFSET (RTT_OFFSET + TIME_LENGTH)
#endif

/**
 * Macros for doing byte swapping
 */

#ifndef ntohll
# if defined(__DARWIN__)
#  define ntohll(_x_) NXSwapBigLongLongToHost(_x_)
# elif defined(__SOLARIS__)
#  if defined(_LITTLE_ENDIAN)
#   define ntohll(_x_) ((((uint64_t)ntohl((_x_) >> 32)) & 0xffffffff) | (((uint64_t)ntohl(_x_)) << 32))
#   define htonll(x) ntohll(x)
#  else
#   define ntohll(_x_) (_x_)
#  endif
# elif defined(__FreeBSD__)
#  define  ntohll(_x_) __bswap_64(_x_)
# else
#  define ntohll(_x_) __bswap_64(_x_)
# endif
#endif
#ifndef htonll
# if defined(__DARWIN__)
#  define htonll(_x_) NXSwapHostLongLongToBig(_x_)
# elif defined(__SOLARIS__)
#  if defined(_LITTLE_ENDIAN)
#   define htonll(_x_) ((htonl((_x_ >> 32) & 0xffffffff) | ((uint64_t) (htonl(_x_ & 0xffffffff)) << 32)))
#  else
#   define htonll(_x_) (_x_)
#  endif
# elif defined(__FreeBSD__)
#  define  htonll(_x_) __bswap_64(_x_)
# else
#  define htonll(_x_) __bswap_64(_x_)
# endif
#endif

/**
 * This routine is used to quickly compute the
 * checksum for a particular buffer. The checksum
 * is done with 16-bit quantities and padded with
 * zero if the buffer is not aligned on a 16-bit
 * boundry.
 *
 */
static
unsigned short checksum(unsigned short *p, int sz)
{
        unsigned long sum = 0; // need a 32-bit quantity

        /*
         * interate over the 16-bit values and 
         * accumulate a sum.
         */
        while(sz > 1)
        {
                sum += *p++;
                sz  -= 2;
        }

        if(sz == 1) /* handle the odd byte out */
        {
                /*
                 * cast the pointer to an unsigned char pointer,
                 * dereference and premote to an unsigned short.
                 * Shift in 8 zero bits and whalla the value
                 * is padded!
                 */
                sum += ((unsigned short) *((unsigned char *)p)) << 8;
        }

        /*
         * Add back the bits that may have overflowed the 
         * "16-bit" sum. First add high order 16 to low
         * order 16, then repeat
         */
        while(sum >> 16)
                sum = (sum >> 16) + (sum & 0xffffUL);

        sum = ~sum & 0xffffUL; 
        return sum;
}

PingCommand::PingCommand( QStringList arguments ) : DefaultCommand( arguments )
{
	responseCodeValue = 500;
	responseCodeTextValue = "Internal Server Error";
	QString hostname = arguments.takeFirst();
	if (!hostname.isEmpty())
	{
		address.setAddress(hostname);
	}
}

void PingCommand::execute()
{
	struct protoent *proto;
	proto = getprotobyname("icmp");
	if (proto == (struct protoent *) NULL)
	{
		// unable to get icmp proto
		return;
	}
	
	int icmpfd = socket(AF_INET, SOCK_RAW, proto->p_proto);
	if(icmpfd < 0)
	{
		// unable to get file descriptor
		return;
	}

	qDebug() << "pinging " << address;
	
	responseCodeValue = 200;
	responseCodeTextValue = "OK";
}

QString PingCommand::responseText( )
{
	return "I *hate* cheese!\n";
}