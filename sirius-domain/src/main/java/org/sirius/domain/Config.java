package org.sirius.domain;

import java.io.UnsupportedEncodingException;

import org.sirius.common.Utils;

public class Config {
	
	private String namespace;
	
	private String name;
	
	private String content;
	
	private boolean dirty;
	
	private String md5;

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
	
	
	public void makeMd5(){
        try {
			this.md5 = Utils.md5(content);
		} catch (UnsupportedEncodingException e) {
		}
	}

	public String getMd5() {
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

}
