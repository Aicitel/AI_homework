package com.aihomework.TuLingAPI.bean;

import java.util.List;

/***
 * tuling123 AnswerBean
 * 
 * @author low
 *
 */
public class AnswerBean {

	String statusCode;
	String code;
	String message;
	String text;
	String url;
	List<ListBean> list;

	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<ListBean> getList() {
		return list;
	}
	public void setList(List<ListBean> list) {
		this.list = list;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String toString(){
		return "message : " + message + " >> code : " + code + " >> text : " + text + " >> url : " + url + " >> list : " + list ;
	}
}
