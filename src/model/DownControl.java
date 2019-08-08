package model;

public class DownControl {
	private int len;
	private String fileId;
	private String from;
	private boolean endArr;
	private int numArr;
	
	public DownControl(String fileId,String from,int len){
		this.fileId = fileId;
		this.from = from;
		this.len = len;
	}
	
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public void setEndArr(boolean endArr) {
		this.endArr = endArr;
	}
	public int getNumArr() {
		return numArr;
	}
	public void setNumArr(int numArr) {
		this.numArr = numArr;
	}
	public double getProcess(){
		return numArr * 1.0 / len * 1.0;
	}
	
	public void proContinue(){
		this.numArr++;
	}
	
	public boolean canStart(){
		return numArr == len && endArr;
	}
}
