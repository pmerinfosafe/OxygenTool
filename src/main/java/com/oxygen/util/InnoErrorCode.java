package com.oxygen.util;

import java.util.HashMap;
import java.util.Map;


/**
 * @program: qxygenTool-通用
 * @description: 定义业务中的常见的异常代码，错误码长度为5位
 * @author: pmer_infoSafe
 * @create: 2019-11-14 14:46
 **/
public class InnoErrorCode {
	
	public static Map<String,String> errors = new HashMap<String,String>();
	
	/***********华丽分割线，业务错误代码开始（以10打头）************************************/
	/**
	 * 10001：网络异常
	 */
	public static final String NET_IS_FAIL = "10001";

	

	/**********华丽分割线，业务错误代码结束（以10打头）*********************************/
	

	static{
		//业务错误代码
		errors.put(NET_IS_FAIL, "网络异常,请稍候重试");

	}
}
