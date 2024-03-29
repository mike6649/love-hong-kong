package com.bahn;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.openqa.selenium.WebDriverException;

import com.bahn.bean.PhotoBackup;
import com.bahn.excel.RnDCSVReader;
import com.bahn.util.task.TaskKiller;
import com.bahn.util.web.RnDChromeDriver;

public class LoveHongKongConnector {

	public static void main(String[] args) {
		try {
			new TaskKiller ("chromedriver.exe");
			
			URL f = LoveHongKongConnector.class.getResource("chromedriver.exe");
			if (f == null){
				System.setProperty("webdriver.chrome.driver",
						LoveHongKongConnector.class.getClassLoader().getResource("chromedriver.exe").getFile());
			} else {
				System.setProperty("webdriver.chrome.driver", f.getFile());
			}

			RnDChromeDriver driver = new RnDChromeDriver();
			RnDCSVReader reader = new RnDCSVReader(new File("C:\\Users\\michael\\Downloads\\backup-pics.csv"));
			List<PhotoBackup> list = reader.readAsList();
			
			for (PhotoBackup bak : list) {
				if (bak.urlList.isEmpty()){
					System.err.println("[WARNING] " + bak.time + " contains no URLs to navigate");
					continue;
				}
				
				for (String url : bak.urlList){
					if (!url.startsWith("http://") && !url.startsWith("https://")){
						url = "http://" + url;
					}
					try {
						driver.get(url);
					} catch (WebDriverException e){
						System.out.println("[ERROR] " + url + " is an invalid URL");
						continue;
					}
					System.out.println();
//					continue here...
//					WebElement el = driver.waitUntilFind(By.xpath("//img"),1000);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
