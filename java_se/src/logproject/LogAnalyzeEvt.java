package logproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class LogAnalyzeEvt {

	private MainView mv;
	private ArrayList<LogVO> lvoList;

	private Map<String, Integer> serviceMap = new HashMap<>();
	private Map<String, Integer> browserMap = new HashMap<>();
	private Map<String, Integer> keyMap = new HashMap<>();
	private Map<String, Integer> timeMap = new HashMap<>();

	private int books500Cnt;
	private int startLine, endLine;

	private StringBuilder[] sb;
	

	public LogAnalyzeEvt(MainView mv, ArrayList<LogVO> lvoList) throws IOException {
		this.mv = mv;
		this.lvoList = lvoList;

		processLogs();
		printResults();

	}// 생성자 end

	// 로그 파일 처리
	public void processLogs() {
		
		//setLogTable Method 호출 위한 객체 생성
		MainViewEvt mve = new MainViewEvt(mv, null);
		
		//inputLine에 입력된 값으로 시작과 끝 값을 설정. 만약 입력된 값이 없다면 default 값(처음과 끝)으로 각각 설정
		startLine = mv.getInputLineFrom().getText().isBlank() ? 1 : Integer.parseInt(mv.getInputLineFrom().getText());
		endLine = mv.getInputLineTo().getText().isBlank() ? lvoList.size() : Integer.parseInt(mv.getInputLineTo().getText());
		
		//inputLine에 입력된 값이 파일 배열의 크기를 넘어서면 default 값(처음과 끝)으로 각각 설정
		startLine = Math.max(startLine, 1);
		endLine = Math.min(endLine, lvoList.size());
		
		mv.getInputLineFrom().setText(String.valueOf(startLine));
		mv.getInputLineTo().setText(String.valueOf(endLine));
		
		//범위 값만 출력하기 위해 일단 필드 초기화
		mv.getDtm().setRowCount(0);
		
		for (int i = startLine - 1; i < endLine; i++) {
			LogVO log = lvoList.get(i);
			
			//mve의 로그 테이블 채우는 method 호출
			mve.setLogTable(i, log.getLogMsg());
			
			//key가 존재하면 기존 값에 +1, 없으면 1로 초기화 (getOrDefault보다 더 줄일 수 있다니 신세계다)
			serviceMap.merge(log.getService(), 1, Integer::sum);
			browserMap.merge(log.getBrowserName(), 1, Integer::sum);
			keyMap.merge(log.getKeyName(), 1, Integer::sum);
			timeMap.merge(log.getTime(), 1, Integer::sum);
		
			//만약 log의 요청값이 books이면 service가 500이면 카운트 증가
			if (log.isBooks() && "500".equals(log.getService())) {
				books500Cnt++;
			}
		}

	}// processLogs

	// 결과 출력
	public void printResults() {

		//레포트 파일 생성 클래스에 넘기기 위한 StringBuilder 배열 초기화
		sb = new StringBuilder[6];
		for(int i=0; i<6; i++) {
			sb[i] = new StringBuilder();
		}
		
		// 1. 최다 사용 키의 이름과 횟수
		int countMostUsedKey = Collections.max(keyMap.values());
		String mostUsedKey = "";
		// keySet을 for 문으로 돌면서 최대횟수인 value 를 가지는 key 값 찾기
		for (String key : keyMap.keySet()) {
			if (keyMap.get(key).equals(countMostUsedKey)) {
				mostUsedKey = key;
				break;
			}
		}
		
		sb[0].append(mostUsedKey + " / " + countMostUsedKey + "회");
		mv.getAnswerLbl()[0].setText(sb[0].toString());

		// 2. 브라우저별 접속
		for (var e : browserMap.entrySet()) {
			sb[1].append(e.getKey()).append(" - ").append(e.getValue()).append("[")
					.append(String.format("%.2f", (double) e.getValue() / (endLine - startLine + 1) * 100))
					.append("%] / ");
		}
		
		mv.getAnswerLbl()[1].setText(sb[1].toString());

		// 3. 서비스를 성공적으로 수행한(200) 횟수,실패(404) 횟수
		int serviceSucceed = serviceMap.getOrDefault("200", 0);
		int serviceFailed = serviceMap.getOrDefault("404", 0);

		sb[2].append("200 - " + serviceSucceed + "회 / 404 - " + serviceFailed + "회");
		mv.getAnswerLbl()[2].setText(sb[2].toString());

		// 4. 요청이 가장 많은 시간 [10시]
		int numTime = Collections.max(timeMap.values());
		String mostUsedTime = "";
		// keySet을 for 문으로 돌면서 최대횟수인 value 를 가지는 key 값 찾기
		for (String key : timeMap.keySet()) {
			if (timeMap.get(key).equals(numTime)) {
				mostUsedTime = key + "시";
			}
		}

		sb[3].append(mostUsedTime);
		mv.getAnswerLbl()[3].setText(sb[3].toString());

		// 5. 비정상적인 요청(403)이 발생한 횟수,비율구하기
		int cnt403 = serviceMap.getOrDefault("403", 0);

		sb[4].append(cnt403 + "[" + String.format("%.2f", (double) cnt403 / (endLine - startLine + 1) * 100) + "%]");
		mv.getAnswerLbl()[4].setText(sb[4].toString());

		// 6. books 에 대한 요청 URL중 에러(500)가 발생한 횟수, 비율 구하기 ( 전체 레코드를 비율의 대상으로 구하세요 )
		sb[5].append(books500Cnt + "[" + String.format("%.2f", (double) books500Cnt / (endLine - startLine + 1) * 100) + "%]");
		mv.getAnswerLbl()[5].setText(sb[5].toString());
	
	} // end printResults()

	public StringBuilder[] getSb() {
		return sb;
	}

}// end class
