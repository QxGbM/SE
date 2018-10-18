package main;

import static org.junit.Assert.*;

import org.junit.Test;

public class SecurityToolsTest {

	@Test
	public void testMapAndReverseMap() {
		SecurityTools x = new SecurityTools();
		String test = "11,123,555";
		assertEquals(test,x.reverseMap(x.map(test)));
		
	
	}

	
	@Test
	public void testElgamalEncryptionAndDecryption() {
		SecurityTools x = new SecurityTools();
		long a = 1000;
		assertEquals(a,x.elgamalDecryption(x.elgamalEncryption(a)));
	}

	

	@Test
	public void testNoiseAndSilencer() {
		String test = "test";
		assertEquals(test,SecurityTools.silencer(3, SecurityTools.noise(3, test)));
	}

	

	@Test
	public void testEncryptAndDecrypt() {
		String test = "message!";
		long nn = 60293329013L;
		long ee = 11;
		long dd = 1370291771L;
		
		assertEquals(test,SecurityTools.decrypt(SecurityTools.encrypt(test, ee, nn), dd, nn));
	}

	

	@Test
	public void testIsPrime() {
		assertTrue(SecurityTools.isPrime(113, 5));
	}

	@Test
	public void testGcd() {
		assertEquals(SecurityTools.gcd(99, 66),33);
	}

	@Test
	public void testLcm() {
		assertEquals(SecurityTools.lcm(3, 5),15);
	}

	@Test
	public void testFindCoprime() {
		assertEquals(SecurityTools.findCoprime(10),3);
	}

	@Test
	public void testModularInverse() {
		assertEquals(SecurityTools.modularInverse(2, 5),3);
	}

	

	@Test
	public void testSignAndVerify() {
		long[] signs = SecurityTools.sign("test");
		assertTrue(SecurityTools.verify("test", signs[0], signs[1], signs[2]));
	}

	

}
