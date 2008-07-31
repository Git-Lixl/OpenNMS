package org.opennms.web.rest;

import javax.ws.rs.core.MultivaluedMap;

import org.hibernate.criterion.Restrictions;
import org.opennms.netmgt.model.OnmsCriteria;
import org.opennms.netmgt.model.OnmsEvent;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class OnmsRestService {

	protected enum ComparisonOperation { EQ, NE, ILIKE, LIKE, GT, LT, GE, LE, ISNULL, NOTNULL}

	public OnmsRestService() {
		super();
	}

	protected void setLimitOffset(MultivaluedMap<java.lang.String, java.lang.String> params, OnmsCriteria criteria) {
		setLimitOffset(params, criteria, 10);  //Default limit is 10
	}
	
	protected void setLimitOffset(MultivaluedMap<java.lang.String, java.lang.String> params, OnmsCriteria criteria, int defaultLimit) {
		int limit=defaultLimit;
		if(params.containsKey("limit")) {
			limit=Integer.parseInt(params.getFirst("limit"));
			params.remove("limit");
		}
		criteria.setMaxResults(limit);
	
		if(params.containsKey("offset")) {
			criteria.setFirstResult(Integer.parseInt(params.getFirst("offset")));
			params.remove("offset");
		}
	}

	protected void addFiltersToCriteria(MultivaluedMap<java.lang.String, java.lang.String> params, OnmsCriteria criteria) {
		//By default, just do equals comparision
		ComparisonOperation op=ComparisonOperation.EQ;
		
		if(params.containsKey("comparator")) {
			String comparatorLabel=params.getFirst("comparator");
			params.remove("comparator");
	
			if(comparatorLabel.equals("equals")) {
				op=ComparisonOperation.EQ;
			}else if (comparatorLabel.equals("ilike")) {
				op=ComparisonOperation.ILIKE;
			}else if (comparatorLabel.equals("like")) {
				op=ComparisonOperation.LIKE;
			}else if (comparatorLabel.equals("gt")) {
				op=ComparisonOperation.GT;
			}else if (comparatorLabel.equals("lt")) {
				op=ComparisonOperation.LT;
			}else if (comparatorLabel.equals("ge")) {
				op=ComparisonOperation.GE;
			}else if (comparatorLabel.equals("le")) {
				op=ComparisonOperation.LE;
			}else if (comparatorLabel.equals("isull")) {
				op=ComparisonOperation.ISNULL;
			}else if (comparatorLabel.equals("notnull")) {
				op=ComparisonOperation.NOTNULL;
			}else if (comparatorLabel.equals("ne")) {
				op=ComparisonOperation.NE;
			}
		}
		BeanWrapper wrapper = new BeanWrapperImpl(OnmsEvent.class);
		wrapper.registerCustomEditor(java.util.Date.class, new ISO8601DateEditor());
		for(String key: params.keySet()) {
			String stringValue=params.getFirst(key);
			Object thisValue=wrapper.convertIfNecessary(stringValue, wrapper.getPropertyType(key));
			switch(op) {
	   		case EQ:
	    		criteria.add(Restrictions.eq(key, thisValue));
				break;
	  		case NE:
	    		criteria.add(Restrictions.ne(key,thisValue));
				break;
	   		case ILIKE:
	    		criteria.add(Restrictions.ilike(key, thisValue));
				break;
	   		case LIKE:
	    		criteria.add(Restrictions.like(key, thisValue));
				break;
	   		case GT:
	    		criteria.add(Restrictions.gt(key, thisValue));
				break;
	   		case LT:
	    		criteria.add(Restrictions.lt(key, thisValue));
				break;
	   		case GE:
	    		criteria.add(Restrictions.ge(key, thisValue));
				break;
	   		case LE:
	    		criteria.add(Restrictions.le(key, thisValue));
				break;
	   		case ISNULL:
	    		criteria.add(Restrictions.isNull(key));
				break;
	   		case NOTNULL:
	    		criteria.add(Restrictions.isNotNull(key));
				break;
			}
		}
	}

}