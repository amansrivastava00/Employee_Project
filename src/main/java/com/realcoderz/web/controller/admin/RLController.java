package com.realcoderz.web.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.realcoderz.business.bean.ComplianceBean;
import com.realcoderz.business.bean.DepartmentBean;
import com.realcoderz.business.bean.EmployeeBean;
import com.realcoderz.business.bean.StatusReportBean;
import com.realcoderz.exceptions.RLNotSubmitted;
import com.realcoderz.service.admin.AdminComplianceService;
import com.realcoderz.service.admin.AdminDepartmentService;
import com.realcoderz.service.admin.AdminEmployeeService;


/**
* <h1>RLController</h1>
* 
* <p>This Controller class is use to handle request of create regulation, add compliance and close compliance</p>
* 
* @author  Ismaeel Siddiqui
* @version 1.0
* @since   2021-05-28
*/
@Controller
@ControllerAdvice
public class RLController {

	private static final Logger LOGGER = Logger.getLogger(RLController.class);

	@Autowired
	private AdminEmployeeService adminEmployeeService;
	
	
	@Autowired
	private AdminComplianceService adminComplianceService;
	
	@Autowired
	private AdminDepartmentService adminDepartmentService;
	
	/**
	   * This method is used to create the regulation
	   * @param ComplianceBean, model
	   */
	@RequestMapping(value = "/createRL", method = RequestMethod.POST)
	public void createRL(@ModelAttribute("compliance") ComplianceBean ComplianceBean, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LOGGER.info("starts createRL method");

		HttpSession session = request.getSession();
		ComplianceBean.setCreateDate(new Date());
		
		
		Map<Integer, DepartmentBean> allDepartments = (Map<Integer, DepartmentBean>)session.getAttribute("allDepartments");
		
		ComplianceBean.setDepartmentName(allDepartments.get(ComplianceBean.getDepartmentId()).getDepartmentName());
		
		Integer saved = adminComplianceService.saveCompliance(ComplianceBean);
		
		ModelAndView modelAndView = new ModelAndView();
		
		if(saved > 0)
		{
			modelAndView.addObject("message", "Compliance added");
		}
		else
		{
			 modelAndView.addObject("message", "Compliance not added");
		}
		
		LOGGER.info("Ends createRL method");
		response.sendRedirect(request.getContextPath()+"/admin.html");
		
	}
	
	/**
	   * This method is used add compliance
	   * @param No parameter
	   */
	@RequestMapping(value = "/addCompliancePage", method = RequestMethod.GET)
	public ModelAndView showAddCompliance(HttpServletRequest request)
	{
		LOGGER.info("starts showAddCompliance method");
		HttpSession session = request.getSession();
		
		Map<Integer, DepartmentBean> allDepartment = adminDepartmentService.getAllDepartment();
		session.setAttribute("allDepartments", allDepartment);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("addCompliance");
		modelAndView.addObject("departmentmap", allDepartment);
		modelAndView.addObject("compliance", new ComplianceBean());
		
		 
		LOGGER.info("Ends showAddCompliance method");
		return modelAndView;
	}
	
	/**
	   * This method is used to close the compliance
	   * @param departmentId, complianceId
	   */
	@RequestMapping(value = "/closeCompliance", method = RequestMethod.GET)
	public ModelAndView closeCompliance(@RequestParam("departmentId") int departmentId, @RequestParam("compId") int coplianceId, HttpServletRequest request)
	{ 
		LOGGER.info("starts closeCompliance method");
		HttpSession session = request.getSession(false);
		ModelAndView modelAndView= new ModelAndView();
		if(session == null)
		{
			modelAndView.setViewName("forward:/index.jsp");
			
			return modelAndView;
		}
		
		session.removeAttribute("message"); 
		
		ComplianceBean complianceBean = new ComplianceBean();
		
		complianceBean.setDepartmentId(departmentId);
		complianceBean.setComplianceId(coplianceId);
		System.out.println("close compliance Handler called");
		try {
			Integer closeCompliance = adminComplianceService.closeCompliance(complianceBean);
			
			if(closeCompliance > 0)
			{
				session.setAttribute("message", "Compliance has been closed");
			}
			else
			{
				session.setAttribute("message", "Compliance has not been closed");
			}
			
			 
		} catch (RLNotSubmitted e) {
			session.setAttribute("message", "Compliance can not be closed as response have not been submitted yet");
			LOGGER.info("Exception in closeCompliance method " + e);
		}
		
		
		LOGGER.info("Ends closeCompliance method");
		
		modelAndView.setViewName("forward:/admin.html");
		
		return modelAndView;
	}
	
	
	
	@ExceptionHandler(value=Exception.class)
	public ModelAndView getErrorPage()
	{
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName("errorPage");
		
		return modelAndView;
	}
	
	@RequestMapping(value="/statusReport", method=RequestMethod.GET)
	public ModelAndView getStatusReport(@RequestParam("complianceId") int complianceId, HttpServletRequest request)
	{
		ModelAndView modelAndView = new ModelAndView();
		
		HttpSession session = request.getSession(false);
		
		if(session == null)
		{
			modelAndView.setViewName("redirect:/");
			
			return modelAndView;
		}
		
		List<StatusReportBean> statusReports = adminComplianceService.getStatusReport(complianceId);
		
		if(statusReports == null)
		{
			modelAndView.setViewName("forward:/admin.html");
			
			session.setAttribute("message", "Not Comments found for Compliance ID: " + complianceId);
			
			return modelAndView;
			
		}
		
		
		
		modelAndView.setViewName("statusReports");
		modelAndView.addObject("statusReports", statusReports);
		modelAndView.addObject("department", new DepartmentBean());
		
		return modelAndView;
	}
	
	@RequestMapping(value="/adminProfile")
	public ModelAndView getAdminProfile(HttpServletRequest request)
	{
		LOGGER.info("starts adminDashboard method");
		HttpSession session = request.getSession(false);
		System.out.println("Redirected to admin.html---------------->");
		ModelAndView modelAndView = new ModelAndView();
		if (session == null) {
			System.out.println("Session is " + session);
			
			modelAndView.setViewName("redirect:/index.jsp");
			
			return modelAndView;
		} 
 
		Map<Integer, DepartmentBean> allDepartment = adminDepartmentService.getAllDepartment();
		session.setAttribute("allDepartments", allDepartment);

		System.out.println("After session check ");
		
		modelAndView.setViewName("adminProfile");

		List<EmployeeBean> allEmployees = adminEmployeeService.getAllEmployees();
		/* List<DepartmentBean> departments = departmentService.getAllDepartment(); */
		List<ComplianceBean> allCompliance = adminComplianceService.getAllCompliance();

		session.setAttribute("allDepartments", allDepartment);

		modelAndView.addObject("compliance", new ComplianceBean());
		modelAndView.addObject("allCompliance", allCompliance);
		/* modelAndView.addObject("departments", departments); */
		modelAndView.addObject("employee", new EmployeeBean());
		modelAndView.addObject("allEmployees", allEmployees);
		modelAndView.addObject("department", new DepartmentBean());

		LOGGER.info("Ends adminDashboard method");
		return modelAndView;
	}
}
