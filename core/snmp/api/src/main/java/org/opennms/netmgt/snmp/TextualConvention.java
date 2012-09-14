/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.snmp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennms.core.utils.LogUtils;

/*
 * From https://tools.ietf.org/rfc/rfc2579.txt
 * 
     The five parts of a octet-format specification are:

(1)  the (optional) repeat indicator; if present, this part is a `*',
     and indicates that the current octet of the value is to be used as
     the repeat count.  The repeat count is an unsigned integer (which
     may be zero) which specifies how many times the remainder of this
     octet-format specification should be successively applied.  If the
     repeat indicator is not present, the repeat count is one.

(2)  the octet length: one or more decimal digits specifying the number
     of octets of the value to be used and formatted by this octet-
     specification.  Note that the octet length can be zero.  If less
     than this number of octets remain in the value, then the lesser
     number of octets are used.

(3)  the display format, either:  `x' for hexadecimal, `d' for decimal,
     `o' for octal, `a' for ascii, or `t' for UTF-8.  If the octet
     length part is greater than one, and the display format part refers
     to a numeric format, then network-byte ordering (big-endian
     encoding) is used interpreting the octets in the value.  The octets
     processed by the `t' display format do not necessarily form an
     integral number of UTF-8 characters.  Trailing octets which do not
     form a valid UTF-8 encoded character are discarded.

(4)  the (optional) display separator character; if present, this part
     is a single character which is produced for display after each
     application of this octet-specification; however, this character is
     not produced for display if it would be immediately followed by the
     display of the repeat terminator character for this octet-
     specification.  This character can be any character other than a
     decimal digit and a `*'.

(5)  the (optional) repeat terminator character, which can be present
     only if the display separator character is present and this octet-
     specification begins with a repeat indicator; if present, this part
     is a single character which is produced after all the zero or more
     repeated applications (as given by the repeat count) of this
     octet-specification.  This character can be any character other
     than a decimal digit and a `*'.
 */

public class TextualConvention {
    
    private final String m_name;
    private final String m_displayHint;
    private final static Pattern VALID_DISPLAY_HINT_PAT = Pattern.compile("((\\*?)(\\d+)([xdoat])([^0-9*])?([^0-9*])?)+");

    public class TextualConventionException extends Exception {
        public TextualConventionException() {
            super();
        }
        
        public TextualConventionException(String message) {
            super(message);
        }
    }
    
    public TextualConvention(String name, String displayHint) {
        m_name = name;
        m_displayHint = displayHint;
    }

    public String format(SnmpValue input) {
        StringBuilder outputBldr = new StringBuilder();
        try {
            validateDisplayHint();
        } catch (TextualConventionException tce) {
            String punt = new String(input.getBytes());
            LogUtils.warnf(getClass(), tce, "Problem with display hint \"%s\". Returning raw bytes as string: %s", m_displayHint, punt);
            return punt;
        }
        
        return outputBldr.toString();
    }
    
    public void validateDisplayHint() throws TextualConventionException {
        Matcher mat = VALID_DISPLAY_HINT_PAT.matcher(m_displayHint);
        if (!mat.matches()) {
            throw new TextualConventionException("Display hint does not pass regex \"" + VALID_DISPLAY_HINT_PAT.toString() + "\"");
        }
        mat.reset();
        while (mat.find()) {
            String repeatIndicator = mat.group(2);      // optional
            String octetLength = mat.group(3);          // mandatory
            String displayFormat = mat.group(4);        // mandatory
            String displaySeparator = mat.group(5);     // optional
            String repeatTerminator = mat.group(6);     // optional, with displaySeparator and repeatIndicator as co-prerequisites
            
            if (repeatTerminator != null) {
                if (displaySeparator == null || repeatIndicator == null) {
                    throw new TextualConventionException("Repeat terminator (" + repeatTerminator + ") appears illegally without a display separator and a repeat indicator. See https://tools.ietf.org/rfc/rfc2579");
                }
            }
        }
    }
    
    private String doFormat(SnmpValue input) {
        StringBuilder bldr = new StringBuilder();
        return bldr.toString();
    }
}
