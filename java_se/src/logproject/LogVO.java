package logproject;

public class LogVO {

	private String service;
	private String browserName;
	private String keyName;
	private String time;
	private boolean isBooks = false;
	
	public LogVO(String line) {
		//받아들인 라인을 ']' 기준으로 쪼개 배열에 저장
		String[] lineArr = line.split("]");
		
		service = lineArr[0].substring(1);
		
		int startIdx = lineArr[1].indexOf("key=") + 4;
		int endIdx = lineArr[1].indexOf("&");
		keyName = lineArr[1].substring(startIdx, endIdx);
		
		browserName = lineArr[2].substring(1);
		
		startIdx = lineArr[3].indexOf(" ") + 1;
		endIdx = lineArr[3].indexOf(":");
		time = lineArr[3].substring(startIdx, endIdx);
		
		startIdx = lineArr[1].indexOf("find/") + 5;
		endIdx = lineArr[1].indexOf("?");
		if(lineArr[1].substring(startIdx, endIdx).equals("books")) {
			isBooks = true;
		}
		
	}

	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public boolean isBooks() {
		return isBooks;
	}

	public void setBooks(boolean isBooks) {
		this.isBooks = isBooks;
	}
	
	
	
}
