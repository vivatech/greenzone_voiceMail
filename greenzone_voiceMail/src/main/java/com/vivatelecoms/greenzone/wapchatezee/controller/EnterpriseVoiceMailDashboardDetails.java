package com.vivatelecoms.greenzone.wapchatezee.controller;

import java.text.SimpleDateFormat;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostRemove;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vivatelecoms.greenzone.utils.ChatUtils;
import com.vivatelecoms.greenzone.wapchatezee.model.ResponseDTO;
import com.vivatelecoms.greenzone.wapchatezee.model.ResponseHeader;
import com.vivatelecoms.greenzone.wapchatezee.model.VoiceMailDashboardDetails;
import com.vivatelecoms.greenzone.wapchatezee.model.VoiceMailDetails;



@CrossOrigin
@RestController
public class EnterpriseVoiceMailDashboardDetails {

	private static final Logger logger = LogManager.getLogger(EnterpriseVoiceMailDashboardDetails.class);
	@Autowired
	Environment env;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	@RequestMapping(value = "/enterpriseVoiceMailDashboardDetails",method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getVoiceMailDashboardDetails(@RequestParam("vMsisdn") String vMsisdn, HttpServletRequest req,
			HttpServletResponse res) {
		
		logger.info("checkVoiceMailDetails|vMsisdn="+vMsisdn);
		String totalTodayQuery ;
		String yesterdayTotalQuery;
		String lastSevenDaysTotalQuery;
		String lastThirdyDaysTotalQuery;
		
		totalTodayQuery= ChatUtils.getQuery(env.getProperty("SQL46_SELECT_VOICEMAIL_TODAY"), vMsisdn);
		yesterdayTotalQuery= ChatUtils.getQuery(env.getProperty("SQL47_SELECT_VOICEMAIL_YESTERDAY"), vMsisdn);
		lastSevenDaysTotalQuery=ChatUtils.getQuery(env.getProperty("SQL48_SELECT_VOICEMAIL_WEEKLY"), vMsisdn);
		lastThirdyDaysTotalQuery=ChatUtils.getQuery(env.getProperty("SQL49_SELECT_VOICEMAIL_MONTHLY"), vMsisdn);
		logger.trace("final totalTodayQuery="+totalTodayQuery);
		logger.trace("final yesterdayTotalQuery="+yesterdayTotalQuery);
		logger.trace("final lastSevenDaysTotalQuery="+lastSevenDaysTotalQuery);
		List<VoiceMailDashboardDetails> voiceMailDashboardDetails = new ArrayList<VoiceMailDashboardDetails>();
		ResponseDTO<List<VoiceMailDashboardDetails>> response = new ResponseDTO<>();
		ResponseHeader addHeader = new ResponseHeader();
		
		try {
			VoiceMailDashboardDetails voiceMailDashboardDetail = new VoiceMailDashboardDetails();
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(totalTodayQuery);
			if(queryForList.isEmpty()) 
			{
				/**No record found. Error handling here*/
				
				voiceMailDashboardDetail.setTodayTraffic("0");
				
			}else {
				
				for(Map<String, Object> row : queryForList)
				{
					
					if(row.get("count(1)")!=null)
						voiceMailDashboardDetail.setTodayTraffic(row.get("count(1)").toString());
					
				}
			}
			 queryForList = jdbcTemplate.queryForList(yesterdayTotalQuery);
			 if(queryForList.isEmpty()) 
				{
					/**No record found. Error handling here*/
					
					voiceMailDashboardDetail.setYesterdayTraffic("0");
					
				}else {
					
					for(Map<String, Object> row : queryForList)
					{
						
						if(row.get("count(1)")!=null)
							voiceMailDashboardDetail.setYesterdayTraffic(row.get("count(1)").toString());
						
					}
				}
			 queryForList = jdbcTemplate.queryForList(lastSevenDaysTotalQuery);
			 if(queryForList.isEmpty()) 
				{
							
					voiceMailDashboardDetail.setlastSevenDaysTraffic("0");
					
				}else {
					
					for(Map<String, Object> row : queryForList)
					{
						
						if(row.get("count(1)")!=null)
							voiceMailDashboardDetail.setlastSevenDaysTraffic(row.get("count(1)").toString());
						
					}
				}
			 queryForList = jdbcTemplate.queryForList(lastThirdyDaysTotalQuery);
			 if(queryForList.isEmpty()) 
				{
							
					voiceMailDashboardDetail.setlastThirtyDaysTraffic("0");
					
				}else {
					
					for(Map<String, Object> row : queryForList)
					{
						
						if(row.get("count(1)")!=null)
							voiceMailDashboardDetail.setlastThirtyDaysTraffic(row.get("count(1)").toString());
						
					}
				}
			 voiceMailDashboardDetails.add(voiceMailDashboardDetail);
				
		}catch(Exception e) {
			addHeader.setCode(2);
			addHeader.setMessage("DB Error");
			e.printStackTrace();
			logger.error("Exception="+e);			
		}
		response.setHeader(addHeader);
		response.setBody(voiceMailDashboardDetails);
		logger.info("Response="+response.getHeader()+"|body="+response.getBody());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
	
	

