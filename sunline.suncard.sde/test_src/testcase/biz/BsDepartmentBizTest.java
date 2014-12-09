package testcase.biz;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import cn.sunline.suncard.sde.bs.biz.BsDepartmentBiz;
import cn.sunline.suncard.sde.bs.entity.BsDepartment;
import cn.sunline.suncard.sde.bs.entity.BsDepartmentId;

public class BsDepartmentBizTest extends TestCase {

	@Test
	public void testQueryAll() {
		
		BsDepartmentBiz biz = new BsDepartmentBiz();
		List<BsDepartment> list = biz.getAll();
		System.out.println("TestCase: " + list.size());		
	}
	
	@Test
	public void testSave(){
		
		BsDepartment bsDepart = new BsDepartment();
		bsDepart.setId(new BsDepartmentId(Long.valueOf(999999),"5"));
		bsDepart.setDepartmentName("资源管理部");
		bsDepart.setModiUser("yangxs");
		Date dt = new Date();		
		bsDepart.setModiDate(new Timestamp(dt.getTime()));
		bsDepart.setVersion(1);
		BsDepartmentBiz biz = new BsDepartmentBiz();
		biz.insert(bsDepart);
	}
	
	@Test
	public void testUpdate(){
		
		BsDepartment bsDepart = new BsDepartment();
		bsDepart.setId(new BsDepartmentId(Long.valueOf(999999),"2"));
		bsDepart.setDepartmentName("上海研发中心");
		bsDepart.setModiUser("yangxs");
		Date dt = new Date();		
		bsDepart.setModiDate(new Timestamp(dt.getTime()));
		bsDepart.setVersion(1);
		BsDepartmentBiz biz = new BsDepartmentBiz();
		biz.update(bsDepart);
	}
	
	@Test
	public void testDelete(){
		
		BsDepartmentId bsDepartmentId = new BsDepartmentId(Long.valueOf(999999),"2");		
		BsDepartmentBiz biz = new BsDepartmentBiz();
		biz.delete(bsDepartmentId);
	}
	
	public void testFindByPk(){
		
		BsDepartmentId idObj = new BsDepartmentId(Long.valueOf(999999),"2");	
		BsDepartmentBiz biz = new BsDepartmentBiz();
		biz.findByPk(idObj);
	}
}
