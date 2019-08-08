package model;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private short cmd = -1;// 内容
	private String messageId;// 消息id
	private String[] meg;// 消息内容
	private String from;// 发送者
	private String to;// 接受者
	private String data;// 时间

	private byte[] bt;//流传输
	private boolean flag;
	private String fileId;//发送者id + filed + .type

	public Message() {
		this((short) -1, "", null, "", "", "");
	}

	public Message(short cmd, String messageId, String fileId, byte[] bt) {
		this.bt = bt;
		this.messageId = messageId;
		this.cmd = cmd;
		this.fileId = fileId;
	}

	public Message(short cmd, String from, boolean flag) {
		this.cmd = cmd;
		this.from = from;
		this.flag = flag;
	}

	public boolean getFlag() {
		return flag;
	}

	public byte[] getBt() {
		return this.bt;
	}

	public Message(short cmd, String messageId, String[] meg, String from, String to, String data) {
		this.cmd = cmd;
		this.messageId = messageId;
		this.meg = meg;
		this.from = from;
		this.to = to;
		this.data = data;
	}
	
	public Message(short cmd, String messageId, String[] meg, String from, String to, String data, byte[] bt) {
		this.cmd = cmd;
		this.messageId = messageId;
		this.meg = meg;
		this.from = from;
		this.to = to;
		this.data = data;
		this.bt = bt;
	}
	
	public void setBt(byte[] bt){
		this.bt = bt;
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(Short cmd) {
		this.cmd = cmd;
	}

	public String getFileId() {
		return fileId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String[] getMeg() {
		return meg;
	}

	public void setMeg(String[] meg) {
		this.meg = meg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public ChildMessage getCM() {
		return new ChildMessage(this.messageId, meg[0]);
	}

	@Override
	public String toString() {
		return "Message [cmd=" + cmd + ", messageId=" + messageId + ", meg=" + Arrays.toString(meg) + ", from=" + from
				+ ", to=" + to + ", data=" + data + "]";
	}
}
