package test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import error.JlkError;
import scanner.Scanner;
import scanner.Token;


public class TestJsLike {

	@Test
	public void testErrorReport() {
		JlkError.error(2, "Unexpected character.");
	}
	
	@Test
	public void testScanning() {
		String text1 = "//this is a comment";
		Scanner scan = new Scanner(text1);
		List<Token> tokens = scan.scanTokens();
		
		//EOF token
		Assert.assertEquals(1, tokens.size());
		
		String text2 = "(( )){} // grouping stuff";
		Scanner scan1 = new Scanner(text2);
		List<Token> tokens1 = scan1.scanTokens();
		Assert.assertEquals(7, tokens1.size());
		
		String text3 = "!*+-/=<> <= == // operators";
		Scanner scan2 = new Scanner(text3);
		List<Token> tokens2 = scan2.scanTokens();
		Assert.assertEquals(11, tokens2.size());
	}
	
	@Test
	public void testScanDigitAndString() {
		//digit
		String digit = "222.333";
		Scanner scandigit = new Scanner(digit);
		Token token = scandigit.scanTokens().get(0);
		Assert.assertEquals(222.333, token.getLiteral());
		//string
		String str = "\"hello world\"";
		Scanner scanstr = new Scanner(str);
		Token strToken = scanstr.scanTokens().get(0);
		Assert.assertEquals("hello world", strToken.getLiteral());
	}
	
	
}
