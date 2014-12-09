
package cn.sunline.suncard.sde.bs.db;
/**
 * 文件名：DefaultDaoImpl.java
 * 版权：Copyright 2012-2020 Sunline Tech. Co. Ltd. All Rights Reserved. 
 * 描述：[描述]
 * 修改人：tpf
 * 修改内容：新增
 * 修改时间：2012-2-20
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Dao抽象实现类
 * @author    tpf
 * @version   1.0  2012-2-20
 * @see       [相关类/方法]
 * @since     [产品/模块版本] 
 */

@SuppressWarnings("unchecked")
public abstract class DefaultDaoImpl<T> extends HibernateDaoSupport implements DefaultDao<T> {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDaoImpl.class);
	
	private String entityName;
	
	private final static String isEmpty = "isEmpty";
	private final static String isNotEmpty = "isNotEmpty";
	private final static String isNotNull = "isNotNull";
	private final static String isNull = "isNull";
	private final static String eq = "eq";
	private final static String ge = "ge";
	private final static String gt = "gt";
	private final static String le = "le";
	private final static String like = "like";
	private final static String lt = "lt";
	private final static String orderPrefix = "_orderby_";//排序打头的字符串
	private final static String idPrefix = "_id_"; //主键打头
	
	//主键打头的字段串,页面传来参数以 <input type="hidden" name="clause['_orderby__id_xx']" value="A"> A升序 D降序,xx为主键对应字段
	
	//这样写
	//<input type="hidden" name="clause._orderby__id_systemId" id="systemId_order" value="A"/> 排序
    //<input type="hidden" name="clause._id_systemId" id="systemId_q" value="like"/> 主键
    //<input type="hidden" name="clause.functionName" id="functionName_q" value="like"/> 非主键
	
	private Class<T> clazz;
	
	public DefaultDaoImpl(){
		Type type = this.getClass().getGenericSuperclass();
		if (! (type instanceof ParameterizedType))
			throw new RuntimeException();
		ParameterizedType parameterizedType = (ParameterizedType)type;
		clazz = (Class<T>)parameterizedType.getActualTypeArguments()[0];
		this.entityName = clazz.getSimpleName();
	}
	
	@Override
	public void save(T t) {
		logger.debug("saving entity instance");
		getHibernateTemplate().save(t);
		logger.debug("save successful");
	}

	@Override
	public void delete(T t) {
		getHibernateTemplate().delete(t);
	}
	
	@Override
	public void deleteAll(List<T> list){
		getHibernateTemplate().deleteAll(list);
	}

	@Override
	public void saveOrUpdate(T t) {
		getHibernateTemplate().saveOrUpdate(t);
	}

	@Override
	public void update(T t) {
		getHibernateTemplate().update(t);
	}

	@Override
	public List<T> findByTemplate(T t, Map<String,String> clause) {
		
		return formatCriteria(t,clause).list();
	}
	
	private Criteria formatCriteria(T t, Map<String,String> clause) {
		Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(clazz);
		Example example = Example.create(t);
		HashMap<String,Object> id = new HashMap<String,Object>();
		HashMap<String, Object> ufields = new HashMap<String, Object>();
		//example.excludeNone();
		try {
			try{
				Method method_id = clazz.getMethod("getId", new Class[]{});
				Object _id = method_id.invoke(t, new Object[]{});
				if(_id != null){
					Class idClass = _id.getClass();
					Field[] fields = idClass.getDeclaredFields();
					for(Field field : fields){
						String _fieldName = field.getName();
						String _methodName = "get"+_fieldName.substring(0, 1).toUpperCase()+_fieldName.substring(1, _fieldName.length());
						Method method_id_get = idClass.getMethod(_methodName, new Class[]{});
						Object _fieldValue = method_id_get.invoke(_id, new Object[]{});
						if(_fieldValue == null || "".equalsIgnoreCase(_fieldValue.toString()))continue;
						id.put("id."+_fieldName, _fieldValue);
					}
				}
			}catch (NoSuchMethodException e) {
				logger.info(t.getClass().toString()+"是单主键表对应的实体");
			}
			
			Field[] usualFields= clazz.getDeclaredFields();
			for(Field usualField : usualFields){
				String _fieldName = usualField.getName();
				if("id".equals(_fieldName)){
					continue;
				}
				String _methodName = "get"+_fieldName.substring(0,1).toUpperCase()+_fieldName.substring(1, _fieldName.length());
				Method method_get = clazz.getMethod(_methodName, new Class[]{});
				Object _fieldValue = method_get.invoke(t, new Object[]{});
				if(_fieldValue == null || "".equalsIgnoreCase(_fieldValue.toString()))continue;
				ufields.put(_fieldName, _fieldValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		} 
		
		
		if(clause != null &&clause.size()>0){
			for(Map.Entry<String, String> fieldClause : clause.entrySet()){
				String fieldName = fieldClause.getKey();
				
				//排序
				if(fieldName.startsWith(orderPrefix)){
					fieldName = fieldName.replace(orderPrefix, "");
					if(fieldName.startsWith(idPrefix)){
						fieldName = fieldName.replaceFirst("_", "").replace("_", ".");
					}
					if("A".equalsIgnoreCase(fieldClause.getValue())){
						criteria.addOrder(Order.asc(fieldName));
					}else if("D".equalsIgnoreCase(fieldClause.getValue())){
						criteria.addOrder(Order.desc(fieldName));
					}
					continue;
				}
				
				Object fieldValue = null;
				if(!fieldName.startsWith("_id_")){
					//example.excludeProperty(fieldName);
					ufields.remove(fieldName);
					try {
						Method method_get = clazz.getMethod("get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1, fieldName.length()), new Class[]{});
						fieldValue = method_get.invoke(t, new Object[]{});
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException();
					} 
				}
				else{
					fieldName = fieldName.replaceFirst("_", "").replace("_", ".");
					String[] fieldStrings = fieldName.split("\\.");
					id.remove(fieldName);
					try {
						Method method_id = clazz.getMethod("getId", new Class[]{});
						Object _id_ = method_id.invoke(t, new Object[]{});
						Class idClass = _id_.getClass();
						String methodString = "get"+fieldStrings[1].substring(0, 1).toUpperCase()+fieldStrings[1].substring(1, fieldStrings[1].length());
						Method method_get = idClass.getMethod(methodString, new Class[]{});
						fieldValue = method_get.invoke(_id_, new Object[]{});
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException();
					}
				}
				
				String op = fieldClause.getValue();
				
				if(isEmpty.equalsIgnoreCase(op)){
					criteria.add(Restrictions.isEmpty(fieldName));
				}else if(isNotEmpty.equalsIgnoreCase(op)){
					criteria.add(Restrictions.isNotEmpty(fieldName));
				}else if(isNotNull.equalsIgnoreCase(op)){
					criteria.add(Restrictions.isNotNull(fieldName));
				}else if(isNull.equalsIgnoreCase(op)){
					criteria.add(Restrictions.isNull(fieldName));
				}else if(ge.equalsIgnoreCase(op)){
					criteria.add(Restrictions.ge(fieldName,fieldValue));
				}else if(gt.equalsIgnoreCase(op)){
					criteria.add(Restrictions.gt(fieldName,fieldValue));
				}else if(le.equalsIgnoreCase(op)){
					criteria.add(Restrictions.le(fieldName,fieldValue));
				}else if(like.equalsIgnoreCase(op)){
					criteria.add(Restrictions.like(fieldName, fieldValue.toString(), MatchMode.ANYWHERE));
				}else if(lt.equalsIgnoreCase(op)){
					criteria.add(Restrictions.lt(fieldName,fieldValue));
				}else{
					criteria.add(Restrictions.eq(fieldName,fieldValue));
				}
			}
		} 
		//criteria.add(example);
		
		for(Map.Entry<String, Object> entry : id.entrySet()){
			criteria.add(Restrictions.eq(entry.getKey(),entry.getValue()));
		}
		for(Map.Entry<String, Object> entry : ufields.entrySet()){
			criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
		}
		return criteria;
		
	}

	@Override
	public List<T> findByTemplateWithSize(T t, Map<String,String> clause, int size) {
		Criteria criteria = formatCriteria(t,clause);
		
		if(size>0)criteria.setFetchSize(size);
		return criteria.list();
	}

	@Override
	public List<T> findByTemplateWithOrders(T t, Map<String,String> clause, String[] ... orders) {
		Criteria criteria = formatCriteria(t,clause);
		if(orders != null){
			for(int i=0;i<orders.length;i++){
				String[] order = orders[i];
				if("A".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.asc(order[0]));
				else if("D".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.desc(order[0]));
			}
		}
		
		return criteria.list();
	}

	@Override
	public List<T> findByTemplateWithSizeAndOrders(T t, Map<String,String> clause, int size, String[] ... orders) {
		Criteria criteria = formatCriteria(t,clause);
		if(orders != null){
			for(int i=0;i<orders.length;i++){
				String[] order = orders[i];
				if("A".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.asc(order[0]));
				else if("D".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.desc(order[0]));
			}
		}
		
		if(size>0)criteria.setFetchSize(size);
		
		return criteria.list();
	}

	@Override
	public List<T> findByTemplateWithPage(T t, Map<String,String> clause, int pageNo, int pageSize) {
		Criteria criteria = formatCriteria(t,clause);
		
		int firstResult = pageSize*(pageNo-1);
		
		criteria.setFirstResult(firstResult>0?firstResult:0);
		criteria.setMaxResults(pageSize);
		return criteria.list();
	}

	@Override
	public List<T> findByTemplateWithPageAndOrders(T t, Map<String,String> clause, int pageNo,
			int pageSize, String[] ... orders) {
		Criteria criteria = formatCriteria(t,clause);
		
		if(orders != null){
			for(int i=0;i<orders.length;i++){
				String[] order = orders[i];
				if("A".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.asc(order[0]));
				else if("D".equalsIgnoreCase(order[1]))
					criteria.addOrder(Order.desc(order[0]));
			}
		}
		
		int firstResult = pageSize*(pageNo-1);
		
		criteria.setFirstResult(firstResult>0?firstResult:0);
		criteria.setMaxResults(pageSize);
		return criteria.list();
	}

	@Override
	public long queryCount(T t, Map<String, String> clause) {
		Criteria criteria = formatCriteria(t,clause);
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.rowCount());
		criteria.setProjection(proList);
		return ((Number)criteria.uniqueResult()).longValue();
	}

	@Override
	public List queryWithFunc(T t, Map<String, String> clause, String[] ... fieldNames) {
		Criteria criteria = formatCriteria(t,clause);
		ProjectionList proList = Projections.projectionList();
		for(String[] fieldName : fieldNames){
			if("sum".equalsIgnoreCase(fieldName[1])){
				proList.add(Projections.sum(fieldName[0]));
			}else if("avg".equalsIgnoreCase(fieldName[1])){
				proList.add(Projections.avg(fieldName[0]));
			}else if("max".equalsIgnoreCase(fieldName[1])){
				proList.add(Projections.max(fieldName[0]));
			}else if("min".equalsIgnoreCase(fieldName[1])){
				proList.add(Projections.min(fieldName[0]));
			}
		}
		criteria.setProjection(proList);
		return criteria.list();
	}

	@Override
	public List queryMax(T t, Map<String, String> clause, String... fieldNames) {
		String method = "max";
		String[][] _fieldNames = new String[fieldNames.length][2];
		for(int i=0;i<fieldNames.length;i++){
			_fieldNames[i][0]=method;
			_fieldNames[i][1]=fieldNames[i];
		}
		return queryWithFunc(t,clause,_fieldNames);
	}

	@Override
	public List queryMin(T t, Map<String, String> clause, String... fieldNames) {
		String method = "min";
		String[][] _fieldNames = new String[fieldNames.length][2];
		for(int i=0;i<fieldNames.length;i++){
			_fieldNames[i][0]=method;
			_fieldNames[i][1]=fieldNames[i];
		}
		return queryWithFunc(t,clause,_fieldNames);
	}

	@Override
	public List querySum(T t, Map<String, String> clause, String... fieldNames) {
		String method = "sum";
		String[][] _fieldNames = new String[fieldNames.length][2];
		for(int i=0;i<fieldNames.length;i++){
			_fieldNames[i][0]=method;
			_fieldNames[i][1]=fieldNames[i];
		}
		return queryWithFunc(t,clause,_fieldNames);
	}

	@Override
	public List queryAvg(T t, Map<String, String> clause, String... fieldNames) {
		String method = "avg";
		String[][] _fieldNames = new String[fieldNames.length][2];
		for(int i=0;i<fieldNames.length;i++){
			_fieldNames[i][0]=method;
			_fieldNames[i][1]=fieldNames[i];
		}
		return queryWithFunc(t,clause,_fieldNames);
	}


	@Override
	public List<T> findByTemplate(T t) {
		return findByTemplate(t,null);
	}

	@Override
	public List<T> findByTemplateWithSize(T t, int size) {
		return findByTemplateWithSize(t,null,size);
	}

	@Override
	public List<T> findByTemplateWithOrders(T t, String[]... orders) {
		return findByTemplateWithOrders(t,null,orders);
	}

	@Override
	public List<T> findByTemplateWithSizeAndOrders(T t, int size,
			String[]... orders) {
		return findByTemplateWithSizeAndOrders(t,null,size,orders);
	}

	@Override
	public List<T> findByTemplateWithPage(T t, int pageNo, int pageSize) {
		return findByTemplateWithPage(t,null,pageNo,pageSize);
	}

	@Override
	public List<T> findByTemplateWithPageAndOrders(T t, int pageNo,
			int pageSize, String[]... orders) {
		return findByTemplateWithPageAndOrders(t,null,pageNo,pageSize,orders);
	}

	@Override
	public long queryCount(T t) {
		return queryCount(t,null);
	}

	@Override
	public List queryWithFunc(T t, String[]... fieldNames) {
		return queryWithFunc(t,null,fieldNames);
	}

	@Override
	public List queryMax(T t, String... fieldNames) {
		return queryMax(t,null,fieldNames);
	}

	@Override
	public List queryMin(T t, String... fieldNames) {
		return queryMin(t,null,fieldNames);
	}

	@Override
	public List querySum(T t, String... fieldNames) {
		return querySum(t,null,fieldNames);
	}

	@Override
	public List queryAvg(T t, String... fieldNames) {
		return queryAvg(t,null,fieldNames);
	}

	@Override
	public List queryBySql(String sql) {
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(sql);
		return query.list();
	}
	
	@Override
	public List queryByHql(String hql) {
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery(hql);
		return query.list();
	}

	@Override
	public int excuteBySql(String sql) {
		Query query = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(sql);
		return query.executeUpdate();
	}
}

