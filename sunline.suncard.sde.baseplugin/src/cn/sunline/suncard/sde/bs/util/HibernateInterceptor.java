package cn.sunline.suncard.sde.bs.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import cn.sunline.suncard.sde.bs.common.Constants;
import cn.sunline.suncard.sde.bs.system.Context;


public class HibernateInterceptor extends EmptyInterceptor {

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		for ( int i=0; i < propertyNames.length; i++ ) {
        	if ( "modiDate".equals( propertyNames[i] ) ) {
            	state[i] = new Timestamp(new Date().getTime());
            }
            if ( "modiUser".equals( propertyNames[i] ) ) {
            	state[i] = Context.getSessionMap().get(Constants.CURRENT_USER_ID);
            }
            if ( "version".equals( propertyNames[i] ) ) {
            	if(state[i] == null) {
            		state[i] = 1;
            	} else {
            		state[i] = (Integer)state[i]+1;
            	}
            }
        }
        return true;

	}

}
