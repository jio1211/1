package com.peisia.c.mysqltest9;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProcBoard {
	public static final int PER_PAGE = 3;
	
	Connection con = null;
	Statement st = null;
	ResultSet result = null;
	
	Scanner sc = new Scanner(System.in);
	
	void run() {
		Display.showTitle();
		dbInit();
		
		int startIndex = 0;
		int currentPage = 1;
		
		loop:
		while(true) {
			dbPostCount();
			Display.showMainMenu();
			System.out.println("명령입력:");
			String cmd = sc.next();
			switch(cmd) {
			case "1":
				startIndex = (currentPage -1)*PER_PAGE;
				
				System.out.println("==========================================");
				System.out.println("================= 글리스트 ==================");
				System.out.println("==========================================");
				try {
					String sql = "select * from board limit "+startIndex+","+PER_PAGE;
					System.out.println("전송한sql문:"+sql);
					result = st.executeQuery(sql);
					
					while(result.next()) {
						String no = result.getString("b_no");
						String title = result.getString("b_title");
						String id = result.getString("b_id");
						String datetime = result.getString("b_datetime");
						System.out.println(no+""+title+""+id+""+datetime);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case "2":
				System.out.println("읽을 글 번호를 입력해주세요:");
				String readNo = sc.next();

				try {
					result = st.executeQuery("select * from board where b_no="+readNo);
					result.next();
					String title = result.getString("b_title");
					String content = result.getString("b_next");
					System.out.println("글제목:"+title);
					System.out.println("글내용:"+content);
				}catch(Exception e) {
					e.printStackTrace();
				}
				break;
			case "3":
				sc.nextLine();
				System.out.println("제목을 입력해주세요:");
				String title = sc.nextLine();
				System.out.println("글내용을 입력해주세요:");
				String content = sc.nextLine();
				System.out.println("작성자id를 입력해주세요:");
				String id = sc.next();
				try {
					st.executeUpdate("insert into board(b_title,b_id,b_datetime,b_text,b_hit)"
							+"values('+title+','+id+',now(),'+content+',0)");
					System.out.println("글등록 완료");
				}catch(SQLException e) {
					e.printStackTrace();
				}
				break;
			case "4":
				System.out.println("삭제할 글번호를 입력해주세요:");
				String delNo = sc.next();
				dbExecuteUpdate("delete from board where b_no="+delNo);
				break;
			case "5":
				System.out.println("수정할 글번호를 입력해주세요:");
				String editNo = sc.next();
				
				System.out.println("제목을 입력해주세요:");
				sc.nextLine();
				String edTitle = sc.nextLine();
				System.out.println("작성자id를 입력해주세요:");
				String edId = sc.next();
				System.out.println("글내용을 입력해주세요:");
				sc.nextLine();
				String edContent = sc.nextLine();
				
			    dbExecuteUpdate("update board set b_title='+edTitle+', b_id='+edId+', b_datetime=now(), b_text='+edContent' where b_no="+editNo);
				break;
			case "0":
				break;
			case "e":
				System.out.println("프로그램종료");
				break loop;
			}
		}
	}
	private void dbInit() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/my_cat", "root", "root");
			st = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void dbExecuteUpdate(String query) {
		try {
			int resultCount = st.executeUpdate(query);
			System.out.println("처리된 행 수:"+resultCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void dbPostCount() {	
		try {
			result = st.executeQuery("select count(*) from board");
			result.next();
			String count = result.getString("count(*)");
			System.out.println("글 수:"+count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
