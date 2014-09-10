package jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Logger {
	public static void main(String[] args) throws IOException {
		/*
			// 指定のウィンドウサイズに変更
			int width = 1000;
			int height = 500;
			driver.manage().window().setSize(new Dimension(width, height));
		 */
		//Ingress移したい範囲をLinkからURLを貼り付け。
		final String url = "https://www.ingress.com/intel?ll=35.713213,139.785979&z=146";
		final String logAllPath = "C:\\Ingress\\Log_Kitasenju\\newlog.csv";

		// ブラウザ(Firefox)を起動
		WebDriver driver = new FirefoxDriver();
		driver.manage().window().maximize();// 最大化
		driver.get(url);//遷移

		//ログインボタンを押す
		driver.findElement(By.className("button_link")).click();


		//メールフォームに打ち込む
		driver.findElement(By.id("Email")).sendKeys("e-mail");
		//パスワードを押す
		driver.findElement(By.id("Passwd")).sendKeys("password");

		//ログインボタンを押す
		driver.findElement(By.id("signIn")).click();

		while(true){
				/* Ingress画面操作 */
				new Logger().sleep(1000*5);//5sec wait
				//allクリック
				driver.findElement(By.id("pl_tab_all")).click();

				new Logger().sleep(1000*10);//10sec wait

				String allTabSource = driver.getPageSource();
				String convertSource = findElementsHtml(allTabSource,"UTF-8","plexts","id");

				//これを1つの単位元とし、解析する。
				//System.out.println(findElementsHtml(allTabSource,"UTF-8","plext","class"));
				//allTabSourceにHTMLの情報が入っているので、ここからDocument要素子ノードを解析する。
				String[] allDataTmp = splitByTag(convertSource);
				System.out.println(allDataTmp.length);
				List <String> allData = new ArrayList<>();
				for(int i=1;i < allDataTmp.length;i++){
					allData.add(allDataTmp[i]);
				}
				Data[] data = new Data[allData.size()];
				System.out.println(data.length);
				System.out.println(allData.get(0));

				/* メインData入力 */
				for(int i=0;i<allData.size();i++){
					Document doc = Jsoup.parse(nameShaper(allData.get(i)),"UTF-8");
					Elements date = doc.getElementsByClass("pl_timestamp_date");
					Elements rPlayer = doc.getElementsByClass("RESISTANCE_pl_nudge_player");
					Elements ePlayer = doc.getElementsByClass("ENLIGHTENED_pl_nudge_player");
					Elements portalInfo = doc.getElementsByClass("pl_portal_name");
					Elements info = doc.getElementsByClass("pl_content_pl_broad");
					data[i] = new Data();
					if(rPlayer.text().equals("")){
						data[i].setId(ePlayer.text());
						data[i].setForce("ENLIGHTENED");
					}else if(ePlayer.text().equals("")){
						data[i].setId(rPlayer.text());
						data[i].setForce("RESISTANCE");
					}
					data[i].setDate(date.text());
					data[i].setPortal(portalInfo.outerHtml());
					data[i].setInfo(info.text());
					data[i].dataInit();
				}
				for(int i=0;i < data.length;i++){
					if(data[i].getPortalName()==""){
						data[i].setPortalName("dummy");
						data[i].setPortalPlng("dummy");
						data[i].setPortalPlat("dummy");
					}
				}
				/* 過去データ読み込み */
				String past = readData(logAllPath);


				/* CSV出力 */
				// FileOutputStreamオブジェクト生成（出力ファイルの指定）
				FileOutputStream fall = new FileOutputStream(logAllPath);
				// OutputStreamWriterオブジェクト生成（文字コードの指定）
				OutputStreamWriter oall = new OutputStreamWriter(fall, "Shift_JIS");

				oall.write(new Data().outputCSVInit());
				oall.write(past);//過去データ書き込み
				for(int i=0 ; i < data.length;i++){
					oall.write(data[i].toString());
				}
				// ストリームの解放
				oall.close();
				fall.close();
				//
				new Logger().sleep(1000* 60);//60sec wait
				driver.get(url);//更新処理
				new Logger().sleep(1000* 15);//15sec wait
		}
//		driver.quit();
	}
	/* HashCodeという言葉を見て、配列に分割する。(あまり良くないと思う。)開始タグと終了タグをみて、分割するような方法を考えねば*/
    private static String[] splitByTag(String t){
    	String passcode = "nbpstrx1eidks";
    	t = t.replaceAll("<div class=\"plext\">", passcode + "<div class=\"plext\">");
        String[] strAry = t.split(passcode);
		return strAry;
    }
    private static String nameShaper(String t){
    	t = t.replaceAll("pl_content pl_broad","pl_contsent_pl_broad");
    	t = t.replaceAll("RESISTANCE pl_nudge_player","RESISTANCE_pl_nudge_player");
    	t = t.replaceAll("ENLIGHTENED pl_nudge_player","ENLIGHTENED_pl_nudge_player");
		return t;
    }

	//要素を検索する。
	private static String findElementsHtml(String source,String encodings,String target,String property){
			Document document = Jsoup.parse(source, encodings);
			if(property.equals("class")){
				Elements element = document.getElementsByClass(target);
				return element.outerHtml();
			}else if(property.equals("id")){
				Element element = document.getElementById(target);
				return element.outerHtml();
			}
			return null;
	}
	//要素を検索する。
	private static String findElementsText(String source,String encodings,String target,String property){
			Document document = Jsoup.parse(source, encodings);
			if(property.equals("class")){
				Elements element = document.getElementsByClass(target);
				return element.text();
			}else if(property.equals("id")){
				Element element = document.getElementById(target);
				return element.text();
			}
			return null;
	}
	public synchronized void sleep(long msec){	//指定ミリ秒実行を止めるメソッド
		try
		{
			wait(msec);
		}catch(InterruptedException e){}
	}
	//データを読み込みStringで返す。
	private static String readData(String path) throws IOException{
		StringBuilder sb = null;
		try {
		      File csv = new File(path); // CSVデータファイル

		      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv),"Shift_JIS"));
		      sb = new StringBuilder();

		      // 最終行まで読み込む
		      String line = "";
		      while ((line = br.readLine()) != null) {

		        // 1行をデータの要素に分割
		        StringTokenizer st = new StringTokenizer(line, ",");

		        while (st.hasMoreTokens()) {
		          // 1行の各要素をタブ区切りで表示
		        	sb.append(st.nextToken(",")).append(",");
		        }
		        sb.append("\n");
		      }
		      br.close();
		    } catch (FileNotFoundException e) {
		    	File file = new File(path);
		    	file.createNewFile();
		    	return "";
		    } catch (IOException e) {
		      // BufferedReaderオブジェクトのクローズ時の例外捕捉
		      e.printStackTrace();
		    }
		return sb.toString();
	}
	//textFormatter:
	private static String textFormatter(String target){
	       return target.replaceAll("\n" , ",\n");
	}
}