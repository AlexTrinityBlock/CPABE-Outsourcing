package cn.edu.pku.ss.crypto.abe;

import java.math.BigInteger;
import java.util.Random;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeAPairing;
public class GenerateParameter {
	public BigInteger GenerateBigRandom(BigInteger min, BigInteger max) {
		BigInteger maxLimit = max ;
		BigInteger minLimit = min ;
		BigInteger bigInteger = maxLimit.subtract(minLimit);
	    Random randNum = new Random();
	    int len = maxLimit.bitLength();
	    BigInteger BigRandom = new BigInteger(len,randNum);
	    if (BigRandom.compareTo(minLimit) < 0)
	        BigRandom = BigRandom.add(minLimit);
	    if (BigRandom.compareTo(bigInteger) >= 0)
	        BigRandom = BigRandom.mod(bigInteger).add(minLimit);
		return BigRandom;
	}
	
	public BigInteger [] GenerateR(int UBase){
		GenerateParameter GP = new GenerateParameter();
		String [] RnumText = {"-4","-2","-1","1","2","4"};
		BigInteger[] r = new BigInteger[4 * UBase];
		
		for(int i=0 ; i < r.length ; i++) {
			int index = (int)(Math.random()*6);
			r[i] = new BigInteger(RnumText[index]);
		}
		return r;
	}
	
	public BigInteger [] GenerateConstant(BigInteger r[],int UBase, BigInteger Order) {
		GenerateParameter GP = new GenerateParameter();
		BigInteger [] b = new BigInteger[4 * UBase + 3];
		BigInteger Min = BigInteger.ONE.add(BigInteger.ONE);
		BigInteger Max = Order.subtract(BigInteger.ONE);
		BigInteger Value = BigInteger.ONE;
		BigInteger MinusOne = new BigInteger("-1");

		for (int i = 1 ; i < 3 ; i++) {
			b[i] = GP.GenerateBigRandom(Min, Max);
		}
			
		b[3] = GP.GenerateBigRandom(Min, Max);
		b[4] = GP.GenerateBigRandom(Min, Max);
		b[5] = r[0].multiply(b[3]).multiply((r[2].modInverse(Order))).mod(Order);
		b[6] = r[1].multiply(b[4]).multiply((r[3].modInverse(Order))).mod(Order);
		Value = (b[3].add(b[4]).add(b[5]).add(b[6]).multiply(MinusOne)).mod(Order);
					
		for (int i = 1 ; i < UBase ; i++) {

			BigInteger Multi1 = r[(4*i) + 0].multiply(r[(4*i) + 2].modInverse(Order)).add(BigInteger.ONE).mod(Order);
			BigInteger Multi2 = r[(4*i) + 1].multiply(r[(4*i) + 3].modInverse(Order)).add(BigInteger.ONE).mod(Order);
			BigInteger Result = BigInteger.ZERO;
					
			b[(4*i) + 3] = GP.GenerateBigRandom(Min, Max);
			b[(4*i) + 4] = Value.multiply(MinusOne).subtract(Multi1.multiply(b[(4*i) + 3])).multiply(Multi2.modInverse(Order)).mod(Order);
			b[(4*i) + 5] = Multi1.subtract(BigInteger.ONE).multiply(b[(4*i) + 3]).mod(Order);
			b[(4*i) + 6] = Multi2.subtract(BigInteger.ONE).multiply(b[(4*i) + 4]).mod(Order);
					
			Result = (b[(4*i) + 3].add(b[(4*i) + 4]).add(b[(4*i) + 5]).add(b[(4*i) + 6]).multiply(MinusOne)).mod(Order);
		}
		
		b[0] = (b[3].add(b[4]).add(b[5]).add(b[6])).multiply(MinusOne).mod(Order);
		return b;
	}
	
	public void PrintR(BigInteger r[],String name) {
		int UBase = r.length / 4;
		for (int i = 0 ; i < UBase ; i++) {
			System.out.print("Group");
			System.out.println(i + 1);
			for (int j = 0 ; j < 4 ; j++) {
				System.out.print(name);
				System.out.print(4*i + j + 4);
				System.out.print(" = ");
				System.out.print(r[4*i + j]);
				System.out.print("   ");
			}
			System.out.println("");
		}
	}
	
	public void PrintConstant(BigInteger c[],String name) {
		int UBase = c.length / 4;
		System.out.println("Base");
		System.out.print(name);
		System.out.print("1 = ");
		System.out.println(c[0]);
		System.out.print(name);
		System.out.print("2 = ");
		System.out.println(c[1]);
		System.out.print(name);
		System.out.print("3 = ");
		System.out.println(c[2]);
		
		for (int i = 0 ; i < UBase ; i++) {
			System.out.print("Group");
			System.out.println(i + 1);
			for (int j = 0 ; j < 4 ; j++) {
				System.out.print(name);
				System.out.print(4*i + j + 4);
				System.out.print(" = ");
				System.out.println(c[4*i + j + 3]);
			}
		}
	}
	public void PrintInverseE(Element E[]) {
		int UBase = E.length/4;
		System.out.println("Base");
		System.out.print("e1 = ");
		System.out.println(E[0].toBigInteger());
		for(int i = 0 ; i < UBase ; i++) {
			System.out.print("Group");
			System.out.println(i + 1);
			for (int j = 0 ; j < 4 ; j++) {
				System.out.print("e");
				System.out.print(4*i + j + 2);
				System.out.print(" = ");
				System.out.println(E[4*i + j + 1].toBigInteger());
			}
		}
	}
	
	public boolean TestConstant(BigInteger r[],BigInteger b[],BigInteger Order) {
		boolean Check = true;
		BigInteger MinusOne = new BigInteger("-1");
		BigInteger left;
		BigInteger right;
		
		int UBase = r.length/4;
		for (int i = 0 ; i < UBase ; i++) {
			//Check whether b4r4 = b6r6 or not
			left = b[4*i + 3].multiply(r[4*i + 0]).mod(Order);
			right = b[4*i + 5].multiply(r[4*i + 2]).mod(Order);
			if(!left.equals(right)) {
				System.out.print("left = ");
				System.out.println(left);
				System.out.print("right = ");
				System.out.println(right);	
				Check = false;
			}
			//Check whether b4 + b5 + b6 + b7 = -b1
			if(!(b[0].equals((b[(4*i) + 3].add(b[(4*i) + 4]).add(b[(4*i) + 5]).add(b[(4*i) + 6]).multiply(MinusOne)).mod(Order)))) {
				Check = false;
			}
		}
		return Check;
	}
	
	public boolean TestInverseE(Element InverseE[],BigInteger a[],BigInteger b[],Element P,Element Q,Element aP[],Element bQ[],BigInteger Order,TypeAPairing pairing) {
		boolean Checking = true;
		int UBase = a.length/4;
		
		Element left = aP[0].getImmutable();
		Element right = bQ[0].getImmutable();
		Element EBase = pairing.pairing(left, right).getImmutable();
		Element E;
		
		if(!EBase.add(InverseE[0]).toBigInteger().equals(BigInteger.ONE)) Checking = false;
		
		for(int i = 0 ; i < UBase ; i++) {
			left = P.getImmutable();
			right = bQ[1].getImmutable();
			E = pairing.pairing(left.pow(a[4*i + 3].add(a[4*i + 5])), right);
			if(!E.add(InverseE[4*i + 1]).toBigInteger().equals(BigInteger.ONE)) Checking = false;
			
			left = P.getImmutable();
			right = bQ[2].getImmutable();
			E = pairing.pairing(left.pow(a[4*i + 4].add(a[4*i + 6])), right);
			if(!E.add(InverseE[4*i + 2]).toBigInteger().equals(BigInteger.ONE)) Checking = false;
			
			left = aP[1].getImmutable();
			right = Q.getImmutable();
			E = pairing.pairing(left.pow(b[4*i + 3].add(b[4*i + 5])), right);
			if(!E.add(InverseE[4*i + 3]).toBigInteger().equals(BigInteger.ONE)) Checking = false;
			
			left = aP[2].getImmutable();
			right = Q.getImmutable();
			E = pairing.pairing(left.pow(b[4*i + 4].add(b[4*i + 6])), right);
			if(!E.add(InverseE[4*i + 4]).toBigInteger().equals(BigInteger.ONE)) Checking = false;
			
		}
		return Checking;
	}
	
	public Element [] CalculatePara(BigInteger c[],Element E) {
		int UBase = c.length/4;
		Element e = E.getImmutable();
		Element [] Result = new Element[4*UBase + 3];
		for(int i = 0 ; i < 4*UBase + 3 ; i++) {
			Result[i] = e.pow(c[i]);
		}
		return Result;
	}
	
	public Element [] CalculateInverseE(BigInteger a[],BigInteger b[],Element P,Element Q,Element aP[],Element bQ[],BigInteger Order,TypeAPairing pairing) {
		int UBase = a.length/4;
		Element InverseE[] = new Element[4*UBase + 1]; 
		Element left = aP[0].getImmutable().invert();
		Element right = bQ[0].getImmutable();
		Element EBase = pairing.pairing(left, right).getImmutable();
		Element E;
		InverseE[0] = EBase;
		
		for(int i = 0 ; i < UBase ; i++) {
			left = P.getImmutable();
			right = bQ[1].getImmutable().invert();
			E = pairing.pairing(left.pow(a[4*i + 3].add(a[4*i + 5])), right);
			InverseE[4*i + 1] = E;
			
			left = P.getImmutable();
			right = bQ[2].getImmutable().invert();
			E = pairing.pairing(left.pow(a[4*i + 4].add(a[4*i + 6])), right);
			InverseE[4*i + 2] = E;
			
			left = aP[1].getImmutable().invert();
			right = Q.getImmutable();
			E = pairing.pairing(left.pow(b[4*i + 3].add(b[4*i + 5])), right);
			InverseE[4*i + 3] = E;
			
			left = aP[2].getImmutable().invert();
			right = Q.getImmutable();
			E = pairing.pairing(left.pow(b[4*i + 4].add(b[4*i + 6])), right);
			InverseE[4*i + 4] = E;
		}
		return InverseE;
	}

	public static void main(String[] args) {
		int rBits = 160;
		int qBits = 256;
        TypeACurveGenerator pg = new TypeACurveGenerator(rBits,qBits);  
        PairingParameters curveParams = pg.generate();
        TypeAPairing pairing = new TypeAPairing(curveParams);    
        
        Element Zr = pairing.getZr().newRandomElement().getImmutable();
        Element P = pairing.getG1().newRandomElement().getImmutable();
        Element Q = pairing.getG2().newRandomElement().getImmutable();
        Element GT = pairing.getGT().newRandomElement().getImmutable();
        
        BigInteger Order = P.getField().getOrder(); 
		int UBase = 2;
		
	    GenerateParameter GP = new GenerateParameter();
	    BigInteger [] r = GP.GenerateR(UBase);
	    BigInteger [] b = GP.GenerateConstant(r, UBase, Order);
	    BigInteger [] rp = GP.GenerateR(UBase);
	    BigInteger [] a = GP.GenerateConstant(rp, UBase, Order);
	    
	    Element [] InverseE = new Element[4*UBase + 1];    
	    Element [] aP = GP.CalculatePara(a, P);
	    Element [] bQ = GP.CalculatePara(b, Q);
	    
	    InverseE = GP.CalculateInverseE(a, b, P, Q, aP, bQ, Order, pairing);
	       
	    GP.PrintConstant(a,"a");
	    GP.PrintConstant(b,"b");
	    GP.PrintR(rp,"rp");
	    GP.PrintR(r,"r");
	    GP.PrintInverseE(InverseE);
	    
	    System.out.print("Checking on InverseE is : ");
	    System.out.println(GP.TestInverseE(InverseE, a, b, P, Q, aP, bQ, Order, pairing));
	    System.out.print("Checking on relation of b and r is : ");
	    System.out.println(GP.TestConstant(r, b, Order));
	    System.out.print("Checking on relation of a and r' is : ");
	    System.out.println(GP.TestConstant(rp, a, Order));
	 }
}
