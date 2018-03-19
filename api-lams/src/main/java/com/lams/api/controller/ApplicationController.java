package com.lams.api.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lams.api.service.ApplicationsService;
import com.lams.api.service.LenderApplicationMappingService;
import com.lams.model.bo.ApplicationRequestBO;
import com.lams.model.bo.ApplicationsBO;
import com.lams.model.bo.LamsResponse;
import com.lams.model.bo.master.ApplicationTypeMstrBO;
import com.lams.model.utils.CommonUtils;
import com.lams.model.utils.Enums;


@RestController
@RequestMapping(value="/application")
public class ApplicationController {
	
	@Autowired
	private ApplicationsService applicationsService;
	
	@Autowired
	private LenderApplicationMappingService applicationMappingService;

	public static final Logger logger = Logger.getLogger(ApplicationController.class);
	
	@RequestMapping(value="/getAll",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LamsResponse> getAll (HttpServletRequest httpServletRequest){
		logger.info("Enter in application list");
		Long userId = (Long) httpServletRequest.getAttribute(CommonUtils.USER_ID);
		Long userType = (Long) httpServletRequest.getAttribute(CommonUtils.USER_TYPE);
		try {
			if (Enums.UserType.BORROWER.getId() == userType) {
				List<ApplicationsBO> applicationsBO = applicationsService.getAll(userId);
				return new ResponseEntity<LamsResponse>(
						new LamsResponse(HttpStatus.OK.value(), "Successfully get data", applicationsBO),
						HttpStatus.OK);
			} else if (Enums.UserType.LENDER.getId() == userType) {
				List<ApplicationTypeMstrBO> list = applicationMappingService
						.getApplicationTypeByUserIdAndIsActive(userId, true);
				return new ResponseEntity<LamsResponse>(
						new LamsResponse(HttpStatus.OK.value(), "Successfully get data", list), HttpStatus.OK);
			} else {
				return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.OK.value(), "Invalid User"),
						HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Throw Exception while get application list ---------------->");
			e.printStackTrace();
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/get/{id}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LamsResponse> getAll (@PathVariable("id") Long id){
		logger.info("Enter in application by id");
		try {
			ApplicationsBO applicationsBO = applicationsService.get(id);
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.OK.value(), "Successfully get data", applicationsBO),
					HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Throw Exception while get application by id ---------------->");
			e.printStackTrace();
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/save",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LamsResponse> save (@RequestBody ApplicationRequestBO applicationsBO,HttpServletRequest httpServletRequest){
		logger.info("Enter in save application");
		Long userId = (Long) httpServletRequest.getAttribute(CommonUtils.USER_ID);
		if(CommonUtils.isObjectNullOrEmpty(applicationsBO.getApplicationTypeId())) {
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.BAD_REQUEST.value(), "Application Type Null Or Empty"),
					HttpStatus.OK);
		}
		if(CommonUtils.isObjectNullOrEmpty(applicationsBO.getData())) {
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.BAD_REQUEST.value(), "Data value Null Or Empty"),
					HttpStatus.OK);
		}
		applicationsBO.setUserId(userId);
		try {
			Long id = applicationsService.save(applicationsBO);
			if(!CommonUtils.isObjectNullOrEmpty(id)) {
				return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.OK.value(), "Successfully save data", id),
						HttpStatus.OK);	
			} else {
				return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Request"),
						HttpStatus.OK);
			}
		} catch (Exception e) {
			logger.info("Throw Exception while save application by id ---------------->");
			e.printStackTrace();
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value="/getLoanDetails/{id}/{appTypeId}",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LamsResponse> save (@PathVariable("id") Long id,@PathVariable("appTypeId") Long appTypeId,HttpServletRequest httpServletRequest){
		logger.info("Enter in get application details");
		if(CommonUtils.isObjectNullOrEmpty(appTypeId)) {
			logger.info("Application Type Null Or Empty");
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.BAD_REQUEST.value(), "Application Type Null Or Empty"),
					HttpStatus.OK);
		}
		if(CommonUtils.isObjectNullOrEmpty(id)) {
			logger.info("Id Null Or Empty");
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.BAD_REQUEST.value(), "Id Null Or Empty"),
					HttpStatus.OK);
		}
		Long userId = (Long) httpServletRequest.getAttribute(CommonUtils.USER_ID);
		try {
			LamsResponse lamsResponse = applicationsService.getLoanApplicationDetails(id, appTypeId,userId);
			lamsResponse.setStatus(HttpStatus.OK.value());
			logger.info("Successfully get details");
			return new ResponseEntity<LamsResponse>(lamsResponse,HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Throw Exception while save application by id ---------------->");
			e.printStackTrace();
			return new ResponseEntity<LamsResponse>(new LamsResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong"),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
