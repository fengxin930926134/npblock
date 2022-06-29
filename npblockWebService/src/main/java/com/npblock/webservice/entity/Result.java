package com.npblock.webservice.entity;

import com.alibaba.fastjson.JSON;
import com.npblock.webservice.util.ConstUtils;
import lombok.Data;

import java.util.List;

/**
 * 数据返回实体
 * @author fengxin
 */
@Data
public class Result {

	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 *  响应信息
	 */
	private String msg;
	/**
	 * 数据对象
	 */
	private Object result;

	/**
	 * 返回状态码, 响应信息
	 * @param code 状态码
	 * @param msg 响应信息
	 */
	public Result(Integer code, String msg){
		this.code = code;
		this.msg = msg;
	}

	/**
	 * 请求成功, 返回数据对象
	 * @param result 数据对象
	 */
	public Result(Object result){
		this.msg = "success";
		this.code = ConstUtils.REST_OK;
		this.result = result;
	}

	/**
	 * 请求成功, 返回数据对象
	 * @param result 数据对象
	 */
	public Result(List<?> result){
		this.msg = "success";
		this.code = ConstUtils.REST_OK;
		this.result = JSON.toJSONString(result);
	}

	/**
	 * 请求成功
	 */
	public Result(){
		this.msg = "success";
		this.code = ConstUtils.REST_OK;
	}
}