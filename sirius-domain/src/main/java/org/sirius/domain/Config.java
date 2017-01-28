package org.sirius.domain;

import java.io.UnsupportedEncodingException;

import org.sirius.common.Utils;

public class Config {
	
	private String namespace;
	
	private  String name;
	
	private  String content;
	
	private boolean dirty;
	
	private String md5;
	
	private boolean forceTransaction;
	
	public Config() {
		super();
	}

	public Config(String namespace, String name, String content) {
		this.namespace = namespace;
		this.name = name;
		this.content = content;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	
	public boolean isForceTransaction() {
		return forceTransaction;
	}

	public void setForceTransaction(boolean forceTransaction) {
		this.forceTransaction = forceTransaction;
	}

	public void makeMd5(){
        try {
			this.md5 = Utils.md5(content);
		} catch (UnsupportedEncodingException e) {
		}
	}

	public String getMd5() {
		if(md5 == null){
			makeMd5();
		}
		return md5;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
