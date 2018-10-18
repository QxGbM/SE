package server;
import java.util.*;
import java.math.*;

public final class SecurityTools{
  private long d;
  public long e;
  public long n;
  private long[] keyz;//n,e,d
 static Character[][] map = new Character[12][10];
  
  private  static long elgamal = 1461984521467L;
  private static long a = 14619841L;
  private  static long aa = 404736305373L;
  
  public static int test = 0b10;
  
   public SecurityTools(){
     
    this.keyz = generateKey();
    this.n = keyz[0];
    this.e = keyz[1];
    this.d = keyz[2];
    System.out.println("n:"+this.n + " e:"+this.e+" d:"+this.d);

   }
   
   private SecurityTools(String placeHolder){
     int charIndex=  40;
     for(int i = 0; i <8;i++){
       
       for(int j = 0; j < 10; j++){
         if(i == 5 && j == 2){
           map[5][2] = '#';
           continue;
         }
         map[i][j] = (char) charIndex;
         
         charIndex++;
         
       }
       
     } 
     
     charIndex = 180;
     for(int i = 8; i <12;i++){
       
       for(int j = 0; j < 10; j++){
         map[i][j] = (char) charIndex;
        
         charIndex++;
       }
       
     }
   }
  
   protected static String map(String m){
      new SecurityTools("palcde holdre");
     StringBuilder peon = new StringBuilder();
     Character st = new Character(' ');
     Character nd = new Character(' ');
     int length = m.length();
     for(int i = 0;i< length - 1;i+=2){
       st = m.charAt(i);
       nd = m.charAt(i+1);
       if((int) st == (int) ','){
         peon.append(map[11][(int) (Math.random() * 10)]);
         peon.append(map[10][Integer.parseInt(nd.toString())]);
       }
       else if((int) nd == (int) ','){
         peon.append(map[10][Integer.parseInt(st.toString())]);
         peon.append(map[11][(int) (Math.random() * 10)]);
       }
       else{
         if(st == '\\' || nd == '\\')
           System.out.println("position: " + st + " " + nd);
         peon.append(map[Integer.parseInt(st.toString())][Integer.parseInt(nd.toString())]);
       }       
     }
     
     if(Math.floorMod(length,2) != 0){
       if((int) m.charAt(length - 1) == (int) ',')
         peon.append(map[11][Integer.parseInt(nd.toString())]);
       else
         peon.append(map[10][Integer.parseInt(new Character(m.charAt(length - 1)).toString())]);
     }
     
     return peon.toString();
   }
   
   
   protected static String reverseMap(String c){
     new SecurityTools("palcde holdre");
     StringBuilder peon = new StringBuilder();
     int length = c.length();
     int index = 0;
     Character b = new Character(' ');
     boolean end = false;
     while(index < length){
       b = c.charAt(index);
       end = false;
       for(int i = 0; i< 10 && end == false;i++){
         for(int j = 0; j < 10 && end == false;j++){
           if((int) map[i][j] == (int) b){
             peon.append(new Integer(i).toString());
             peon.append(new Integer(j).toString());
             end = true;
           }
         }
       }
       
       for(int i = 0; i < 10; i++){
         
         if( (int) map[10][i] == (int) b){
          
           peon.append(new Integer(i).toString());
           break;
         }
       }
       
       for(int i = 0; i < 10;i++){
         if((int) map[11][i] == (int) b){
           peon.append(",");
           break;
         }
       }
       index = index + 1;
     }
     
     return peon.toString();
   }
   
  protected static long[] elgamalEncryption(long shift){
    long k = (long) (Math.random() * Math.pow(2,21));
    long c1 = SecurityTools.repeatedSquaring(2,elgamal,k);
    long c2 = SecurityTools.repeatedSquaring(SecurityTools.repeatedSquaring(aa,elgamal,k) * shift,elgamal,1);
    long[] key = {c1,c2};
    return key;
  }
  
  protected static long elgamalDecryption(long[] keys){
    long x = SecurityTools.modularInverse(SecurityTools.repeatedSquaring(keys[0],elgamal,a),elgamal);
    BigInteger xi = BigInteger.valueOf(x);
    BigInteger c2 = BigInteger.valueOf(keys[1]);
    BigInteger r = xi.multiply(c2);
    return r.modPow(BigInteger.valueOf(1),BigInteger.valueOf(elgamal)).longValue();
  }
  
  protected static String noise(int n, String m){
    StringBuilder peon = new StringBuilder(m);
    int i = 0;
    
    while(i<peon.length()){
      
      if(Math.floorMod(i,n) == 1){
        for(int j = 0;j<n;j++){
          peon.insert(i,(int) (Math.random() * 10));
          i++;
        }
      }
      i++;
    }
    return peon.toString();
  }
  
  protected static String silencer(long n, String m){
    StringBuilder peon = new StringBuilder(m);
    int i = 0;
    while(i<peon.length()){
      if(Math.floorMod(i,n) == 1){
        for(int j = 0; j< n;j++){
          peon.deleteCharAt(i);
        }
      }
      i++;
    }
    return peon.toString();
  }
  
  
  /*public long[] generateKey(){
    long[] key = {0,0,0};
    long p = generatePrime();
    
    this.keyz = this.generateKey();
    this.n = keyz[0];
    this.e = keyz[1];
    this.d = keyz[2];
    return key;
  }*/
  
  public static long[] generateKey(){
    long[] key = {0,0,0};
    long p = generatePrime();
   
    long q= nextPrime(p);
    
    long n = p*q;
    long nn = lcm(p-1,q-1);
     
    long e = findCoprime(nn);
    
    long d = modularInverse(e,nn);
    key[0] = n;
    key[1] = e;
    key[2] = d;
    return key;
  }
 
  public String encrypt(String m){

    return SecurityTools.encrypt(m,this.e,this.n);
  }
  
  public static String encrypt(String m, long e, long n){
    int length = m.length();
    StringBuilder x = new StringBuilder();
    int noise = (int) (Math.random()*9)+1;
    long[] noises = SecurityTools.elgamalEncryption(noise);
    x.append(new Long(noises[0]).toString() + "," + new Long(noises[1]).toString() + ",");
    String noiseCreator = "";
    for(int i = 0;i<length;i++){
      noiseCreator = new String(new Long(repeatedSquaring(((long) m.charAt(i)),  n,e)).toString());
      x.append(SecurityTools.noise(noise,noiseCreator));
      x.append(",");
    }
    x.deleteCharAt(x.length()-1);
    return SecurityTools.map(x.toString());
  }
 
  public String decrypt(String m){
    return SecurityTools.decrypt(m,this.d,this.n);
  }
  
  public static String decrypt(String c, long d, long n){
    String cc = SecurityTools.reverseMap(c);
    String[] x = cc.split(",");
    StringBuilder m = new StringBuilder();
    long[] silencer = {Long.parseLong(x[0]),Long.parseLong(x[1])};
    long noiseLength =  SecurityTools.elgamalDecryption(silencer);

    int length = x.length;
    for(int i = 2;i<length;i++){       
      x[i] = silencer(noiseLength,x[i]);
      m.append((char) repeatedSquaring( Long.parseLong(x[i]) , n , d));
    }
    return m.toString();
  }
  
   public static boolean isPrime(long n, int iteration)
    {
        /** base case **/
        if (n == 0 || n == 1)
            return false;
        /** base case - 2 is prime **/
        if (n == 2)
            return true;
        /** an even number other than 2 is composite **/
        if (n % 2 == 0)
            return false;
 
        long s = n - 1;
        while (s % 2 == 0)
            s /= 2;
 
        Random rand = new Random();
        for (int i = 0; i < iteration; i++)
        {
            long r = Math.abs(rand.nextLong());            
            long a = r % (n - 1) + 1, temp = s;
            long mod = modPow(a, temp, n);
            while (temp != n - 1 && mod != 1 && mod != n - 1)
            {
                mod = mulMod(mod, mod, n);
                temp *= 2;
            }
            if (mod != n - 1 && temp % 2 == 0)
                return false;
        }
        return true;        
    }
  
   private static long modPow(long a, long b, long c)
    {
        long res = 1;
        for (int i = 0; i < b; i++)
        {
            res *= a;
            res %= c; 
        }
        return res % c;
    }
    /** Function to calculate (a * b) % c **/
    private static long mulMod(long a, long b, long mod) 
    {
        return BigInteger.valueOf(a).multiply(BigInteger.valueOf(b)).mod(BigInteger.valueOf(mod)).longValue();
    }


  

  

  

  


 
 
 public static long generatePrime(){
   long prime = 10;
   while(!isPrime(prime,3)){

     prime = (long) (Math.random() * Math.pow(2,20));
     

   }
   return prime;
 }
 
 protected static long nextPrime(long prime){
   long pc = prime + 1;
   long upperBound = prime + prime;
   for(long i = prime + 1; i < upperBound; i++){

     if(isPrime(pc++,3))
       return pc - 1;


   }
   return (long) -1;
 }
 

 
 
 static long gcd(long a, long b) {
    BigInteger b1 = BigInteger.valueOf(a);
    BigInteger b2 = BigInteger.valueOf(b);
    BigInteger gcd = b1.gcd(b2);
    return gcd.longValue();
}
 
 static long lcm(long a, long b)
 {
   // the lcm is simply (a * b) divided by the gcf of the two
   
   return (a * b) / gcd(a, b);
 }
 
 static long findCoprime(long candidate){
   long cop = 2;
   while(gcd(cop,candidate) !=1){
     
   cop++;}
   return cop;
 }
 
 static long modularInverse(long in, long mod){
   BigInteger input = BigInteger.valueOf(in);
   long inverse = input.modInverse(BigInteger.valueOf(mod)).longValue();
   return inverse;
 }
 
 public static long repeatedSquaring(long num, long mod, long pow){
   BigInteger remainder = BigInteger.valueOf(num);
   
   return remainder.modPow(BigInteger.valueOf(pow),BigInteger.valueOf(mod)).longValue();
 }
 
 public static long getAPrimeFactor(long n){
   return getAPrimeFactor(n,(long) 3);
 }
 
 public static long getAPrimeFactor(long n, long lowerBound) {
   long last = 0;
   if (n<=1 || lowerBound >= n) {

     throw new IllegalArgumentException("n must be bigger than 1 or the lower bound must be smaller than n"); } 
   for(long i=lowerBound; i<=n; i++){ 
     if(n%i==0 && isPrime(i,5)){
       
       last = i; 
       return i;}
   }
   if (last != 0) {
     return getAPrimeFactor(n/last); }
   else
   { return 1; }
 }
 
 protected static long getValue(String m){
   byte[] bytes = m.getBytes();
   long sum = 0;
   for(int i = 0;i < bytes.length;i++){
     sum += bytes[i];
   }
   return sum;
 }
 
 
 public static long[] sign(String m){
   long p = (long) 5617986553L;
   long q = (long) 26459;
   long g = (long) 185765;
   long x = (long) (Math.random() * q);

   long y = SecurityTools.repeatedSquaring(g,p,x);
   long k = (long) (Math.random() * q);

   long r = SecurityTools.repeatedSquaring(SecurityTools.getValue(m) * SecurityTools.repeatedSquaring(g,p,k),p,1);
   long s = SecurityTools.repeatedSquaring( SecurityTools.repeatedSquaring(x*r,q,1) + k,q,1);

   long[] keys = {r,s,y};
   return keys;
 }
 
 public static boolean verify(String m,long r, long s, long y){
   long p = (long) 5617986553L;
  // long q = (long) 26459;
   long g = 185765;
   long gInverse = SecurityTools.modularInverse(g,p);

   long finalG = SecurityTools.repeatedSquaring(gInverse,p,s);

   long finalY = SecurityTools.repeatedSquaring(y,p,r);
   BigInteger result =BigInteger.valueOf(r).multiply( BigInteger.valueOf(finalG).multiply(BigInteger.valueOf(finalY)));
   return  SecurityTools.getValue(m) == result.modPow(BigInteger.valueOf(1),BigInteger.valueOf(p)).longValue();
 }
 
 

 
 
 public static void main(String abs[]){
	 //testing for error
	 String test = encrypt("Login admin0 password", 11,60293329013L);
	 System.out.println(test);
	 System.out.println(decrypt(test,1370291771L,60293329013L));
   
 }
 
}

