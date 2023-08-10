 package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.vivatelecoms.greenzone.services.impl.PostClientCrbtServiceImpl;
import com.vivatelecoms.greenzone.services.impl.PostClientKaafiyaServiceImpl;


@CrossOrigin
@RestController
public class KaafiyaBalanceQuery {

	private static final Logger logger = LogManager.getLogger(KaafiyaBalanceQuery.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/getKaafiyaUserBalance",method = RequestMethod.GET)
	@ResponseBody
	public String getKaafiyaUserBalanceController(@RequestParam("aparty") String aparty, @RequestParam("bparty") String bparty,@RequestParam("langId") String langId,@RequestParam("serviceId") String serviceId, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("getKaafiyaUserBalanceController|aparty="+aparty+"|bparty="+bparty+"|serviceId="+serviceId+"|langId="+langId);
		String responseString = new String();
		
		String balanceQueryUrl=env.getProperty("KAAFIYA_BALANCE_QUERY_URL");
		String kaafiyaAccountDetails=env.getProperty("KAAFIYA_ACCOUNT_TYPE");
		String mainAccount=env.getProperty("MAIN_ACCOUNT_TYPE");
		String minBalanceCheck = env.getProperty("MIN_LOW_BALANCE");
		logger.info("getUserBalanceController|BALANCE_QUERY_URL="+balanceQueryUrl);
		String jsonBodyData="{"+"\"smsNumber\":null," + "\"msisdn\":\"" +aparty+ "\",\"type\":null "+ "}";
		logger.info("jsonBodyData="+jsonBodyData);
		PostClientKaafiyaServiceImpl postClientKaafiyaService = new PostClientKaafiyaServiceImpl();
		String postClientRes = postClientKaafiyaService.sendPostClientKaafiyaReq(balanceQueryUrl, jsonBodyData, "kaafiya-balance-query",kaafiyaAccountDetails,mainAccount,minBalanceCheck);
		logger.info("sufficientBalance="+postClientRes+"|aparty="+aparty);
		responseString = responseString.concat("BALANCE_QUERY_RES.sufficientBalance=\'"+postClientRes+"\';");
		return responseString;
		
	}
	
	
}
