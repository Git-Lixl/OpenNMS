/*
 * Created on Sep 8, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.core.utils;

import java.util.List;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface ExecutorStrategy {
	/**This method executes the command using a Process. The method will decide if 
	 an input stream needs to be used.
	 @param commandLine the command to execute as a command line call
	 @param arguments a list of Argument objects that need to be passed to the command line call
	 @return int, the return code of the command
	 */
	public abstract int execute(String commandLine, List arguments);
}