package org.sirius.core.cluster;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.sirius.domain.Config;
import org.sirius.exception.ClusterException;
import org.sirius.exception.ErrorCode;

import com.alibaba.fastjson.JSONObject;

/**
 * 集群中的一个节点
 * 
 * @author liwei
 *
 */
public class SiriusNode {

	HttpClient client;

	/**
	 * 节点地址
	 */
	private String host;

	/**
	 * 节点端口
	 */
	private int port;

	public SiriusNode(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		buildHttpClient();
	}

	private void buildHttpClient() {
		client = new DefaultHttpClient();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * 向节点推送配置
	 * @param config
	 * @throws IOException
	 */
	public void pushConfig(Config config) throws IOException {
		HttpPost post = new HttpPost(MessageFormat.format("http://{0}:{1}/sirius-server/async", host, String.valueOf(port)));
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("type", "store"));
		pairs.add(new BasicNameValuePair("namespace", config.getNamespace()));
		pairs.add(new BasicNameValuePair("name", config.getName()));
		pairs.add(new BasicNameValuePair("content", config.getContent()));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
		post.setEntity(entity);
		try{
			HttpResponse resp = client.execute(post);
			if (resp.getStatusLine().getStatusCode() != 200) {
				throw new ClusterException(ErrorCode.PUSH_CONFIG_ERROR);
			}
		}finally{
			post.releaseConnection();
		}
		
	}

	/**
	 * 节点是否存活
	 * @return
	 * @throws IOException
	 */
	public boolean isAlive() throws IOException {
		HttpPost post = new HttpPost(MessageFormat.format("http://{0}:{1}/sirius-server/async?type=alive", host, String.valueOf(port)));
		try{
			HttpResponse resp = client.execute(post);
			return resp.getStatusLine().getStatusCode() == 200;
		}finally{
			post.releaseConnection();
		}
		
	}
	
	
	/**
	 * 从节点获取配置
	 * @param namespace
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public Config getConfig(String namespace,String name) throws IOException{
		HttpPost post = new HttpPost(MessageFormat.format("http://{0}:{1}/sirius-server/async", host, String.valueOf(port)));
		List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("type", "query"));
		pairs.add(new BasicNameValuePair("namespace", namespace));
		pairs.add(new BasicNameValuePair("name", name));
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs);
		post.setEntity(entity);
		try{
			HttpResponse resp = client.execute(post);
			
			if(resp.getStatusLine().getStatusCode() == 200){
				String content = EntityUtils.toString(resp.getEntity());
				Config config = JSONObject.parseObject(content, Config.class);
				return config;
			}
		}finally{
			post.releaseConnection();
		}
		
		return null;
	}
	
	
	public static void main(String[] args) throws IOException {
		SiriusNode node = new SiriusNode("localhost", 8080);
		System.out.println(node.isAlive());
		System.out.println(JSONObject.toJSONString(node.getConfig("aa", "bb")));
	}

}
