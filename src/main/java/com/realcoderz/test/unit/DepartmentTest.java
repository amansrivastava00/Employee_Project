package com.realcoderz.test.unit;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.realcoderz.business.bean.ComplianceBean;
import com.realcoderz.business.bean.EmployeeBean;
import com.realcoderz.business.bean.LoginMasterBean;
import com.realcoderz.service.department.DepartmentComplianceService;
import com.realcoderz.service.department.DepartmentEmployeeService;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:com/realcoderz/resources/cst-root-ctx.xml")
@Rollback(true)
@Transactional
public class DepartmentTest {

	@Autowired
	private DepartmentComplianceService departmentComplianceService;
	@Autowired
	private DepartmentEmployeeService departmentEmployeeService;

	@Before
	public void testMethodBeforeInterceptor() {
		System.out.println("Starting a new Test Method..");
	}

	@After
	public void testMethodAfterInterceptor() {
		System.out.println("End the Test Method..");
	}

	@Test
	public void testgetAllCompliance() throws Exception {

		List<ComplianceBean> complianceList = departmentComplianceService.getAllCompliance(139);

		Assert.assertTrue(complianceList != null);
	}

	@Test
	public void testgetEmployee() throws Exception {

	EmployeeBean employeeBean = departmentEmployeeService.getEmployee(98);
	Assert.assertTrue(employeeBean != null);
	}
	
	@Test
	public void testgetAllEmployees() throws Exception {

		List<EmployeeBean> employeeBeanList = departmentEmployeeService.getAllEmployees(138);
		Assert.assertTrue(employeeBeanList != null);
	}

}
