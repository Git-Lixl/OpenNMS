/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.opennms.netmgt.config;

import java.util.*;

/**
 * @author perfmon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BeanInfo {
	private String mbeanName;
	private String objectName;
	private String[] attributes;
	private ArrayList operations;
	
	public BeanInfo() {
		operations = new ArrayList();
	}
	
	public void setAttributes(String[] attr){
		attributes = attr;
	}
	public String[] getAttributeNames() {
		return attributes;
	}
	
	public void addOperations(Object attr){
		operations.add(attr);
	}
	public ArrayList getOperations() {
		return operations;
	}
	

	/**
	 * @return Returns the mbeanName.
	 */
	public String getMbeanName() {
		return mbeanName;
	}
	/**
	 * @param mbeanName The mbeanName to set.
	 */
	public void setMbeanName(String mbeanName) {
		this.mbeanName = mbeanName;
	}
	/**
	 * @return Returns the objectName.
	 */
	public String getObjectName() {
		return objectName;
	}
	/**
	 * @param objectName The objectName to set.
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
}
