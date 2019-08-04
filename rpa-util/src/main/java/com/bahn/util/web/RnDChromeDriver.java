package com.bahn.util.web;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class RnDChromeDriver extends ChromeDriver {
	
	private static int TIMEOUTMILLI = 100;

	public RnDChromeDriver() {
		this.manage().timeouts().implicitlyWait(TIMEOUTMILLI, TimeUnit.MILLISECONDS);
	}

	public RnDChromeDriver(ChromeDriverService service) {
		super(service);
	}

	public RnDChromeDriver(ChromeOptions options) {
		super(options);
	}

	public RnDChromeDriver(ChromeDriverService service, ChromeOptions options) {
		super(service, options);
	}
	

	
	public WebElement waitUntilFind(By by, int milliWait) throws NoSuchElementException{
		WebElement element = null;
		int times = milliWait / TIMEOUTMILLI;
		for (int i=0;i<=times;i++){
			try {
				element = this.findElement(by);
			} catch (NoSuchElementException e){
				
			}
		}
		
		if (element == null){
			this.findElement(by);
		}
		return element;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
