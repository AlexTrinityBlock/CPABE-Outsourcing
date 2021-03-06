package cn.edu.pku.ss.crypto.abe.apiV2;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;

import cn.edu.pku.ss.crypto.abe.CPABEImpl;
import cn.edu.pku.ss.crypto.abe.CPABEImplWithoutSerialize;
import cn.edu.pku.ss.crypto.abe.Ciphertext;
import cn.edu.pku.ss.crypto.abe.GenerateParameter;
import cn.edu.pku.ss.crypto.abe.PairingManager;
import cn.edu.pku.ss.crypto.abe.PairingPoint;
import cn.edu.pku.ss.crypto.abe.Parser;
import cn.edu.pku.ss.crypto.abe.Policy;
import cn.edu.pku.ss.crypto.abe.PubParameter;
import cn.edu.pku.ss.crypto.abe.PublicKey;
import cn.edu.pku.ss.crypto.abe.ReturnParameter;
import cn.edu.pku.ss.crypto.abe.SecretKey;
import cn.edu.pku.ss.crypto.abe.serialize.SerializeUtils;
import cn.edu.pku.ss.crypto.aes.AES;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Client {
	private PublicKey PK;
	private SecretKey SK;
	private String[] attrs;
	
	private BigInteger [] r;
	private BigInteger [] b;
	private BigInteger [] rp;
	private BigInteger [] a;
	private int UBase;
	private int CalculateNum;
	private int PairingNumber;
	private int Extension;
	private BigInteger Aa;
	private BigInteger Ab;
	private BigInteger Aab;
	private ArrayList<BigInteger> AB;
	private ArrayList<BigInteger> KAB;
	
	private Element [] aP;
	private Element [] bQ;
	private Element [] InverseE;
	private Map <String,PairingPoint> CalTable;
	private Map <String,ReturnParameter> ResultTable;
	private Map <String,String> CheckingTable;
	private Element A;
	private Element B;
	private Element PA;
	private Element PB;
	private ArrayList<Element> EAB;
	private ArrayList<PairingPoint> PairPoint;
	private ArrayList<ReturnParameter> Result;
	private ArrayList<Element> eAiBis;
	
	public int OutsourcingNumber;
	public long PrecomputationTotal;
	public long OutsourceTotal;
	public long VerifyTotal;
	public long OriginalTotal;
	
	public Element P;
	public Element Q;
	public Element GT;
	public BigInteger Order;
	public static Pairing pairing = PairingManager.defaultPairing;
	
	public Client(){
		P = pairing.getG1().newRandomElement().getImmutable();
        Q = pairing.getG2().newRandomElement().getImmutable();
        PA = pairing.getG1().newRandomElement();
        PB = pairing.getG2().newRandomElement();
        A = pairing.getG1().newRandomElement().getImmutable();
        B = pairing.getG2().newRandomElement().getImmutable();
        GT = pairing.getGT().newRandomElement().getImmutable();
        Aa = new BigInteger("1");
        Ab = new BigInteger("1");
        Aab = new BigInteger("1");
        OutsourcingNumber = 0;
        PrecomputationTotal = 0;
        OutsourceTotal = 0;
        VerifyTotal = 0;
        OriginalTotal = 0;
        Extension = 1;
        
        Order = P.getField().getOrder();
        PairPoint = new ArrayList<PairingPoint>();
        Result = new ArrayList<ReturnParameter>();
        EAB = new ArrayList<Element>();
        AB = new ArrayList<BigInteger>();
        KAB = new ArrayList<BigInteger>();
        eAiBis = new ArrayList<Element>();
	}
	public Client(String[] attrs){
		this.attrs = attrs;
		
		P = pairing.getG1().newRandomElement().getImmutable();
        Q = pairing.getG2().newRandomElement().getImmutable();
        PA = pairing.getG1().newRandomElement();
        PB = pairing.getG2().newRandomElement();
        A = pairing.getG1().newRandomElement().getImmutable();
        B = pairing.getG2().newRandomElement().getImmutable();
        GT = pairing.getGT().newRandomElement().getImmutable();
        Aa = new BigInteger("1");
        Ab = new BigInteger("1");
        Aab = new BigInteger("1");
        OutsourcingNumber = 0;
        PrecomputationTotal = 0;
        OutsourceTotal = 0;
        VerifyTotal = 0;
        OriginalTotal = 0;
        Extension = 1;
        
        Order = P.getField().getOrder();
        PairPoint = new ArrayList<PairingPoint>();
        Result = new ArrayList<ReturnParameter>();
        EAB = new ArrayList<Element>();
        AB = new ArrayList<BigInteger>();
        KAB = new ArrayList<BigInteger>();
        eAiBis = new ArrayList<Element>();
	}
	
	public String[] getAttrs(){
		return attrs;
	}
	
	public void setAttrs(String[] attrs){
		this.attrs = attrs;
	}
	
	public PublicKey getPK() {
		return PK;
	}

	public void setPK(String PKJSONString) {
		JSONObject json = JSON.parseObject(PKJSONString);
		byte[] b = json.getBytes("PK");
		this.PK = SerializeUtils.constructFromByteArray(PublicKey.class, b);	
		this.setPP();
	}
	public void setSecret(Element A,Element B) {
		this.A = A;
		this.B = B;
	}

	public SecretKey getSK() {
		return SK;
	}
	
	public PubParameter getPP() {
		PubParameter PubPar = new PubParameter();
		PubPar.P = this.P;
		PubPar.Q = this.Q;
		PubPar.GT = this.GT;
		PubPar.Order = this.Order;
		PubPar.pairing = this.pairing;
		
		return PubPar;
	}
	
	public void setPP() {
		this.P = this.PK.g;
		this.Q = this.PK.gp;
		this.GT = this.PK.g_hat_alpha;
		this.Order = this.PK.g.getField().getOrder();
	}
	
	public void setSK(String SKJSONString) {
		JSONObject json = JSON.parseObject(SKJSONString);
		byte[] b = json.getBytes("SK");
		this.SK = SerializeUtils.constructFromByteArray(SecretKey.class, b);
	}

	public void enc(File in, String policy, String outputFileName){
		Parser parser = new Parser();
		Policy p = parser.parse(policy);
		CPABEImplWithoutSerialize.enc(in, p, this.PK, outputFileName);
	}
	
	public void dec(File in){
		
		String ciphertextFileName = null; 
		DataInputStream dis = null;
		try {
			ciphertextFileName = in.getCanonicalPath();
			dis = new DataInputStream(new FileInputStream(in));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Ciphertext ciphertext = SerializeUtils._unserialize(Ciphertext.class, dis);
		
		String output = null;
		if(ciphertextFileName.endsWith(".cpabe")){
			int end = ciphertextFileName.indexOf(".cpabe");
			output = ciphertextFileName.substring(0, end);
		}
		else{
			output = ciphertextFileName + ".out";
		}
		File outputFile = CPABEImpl.createNewFile(output);
		OutputStream os = null;
		try {
			os =  new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Element m = CPABEImpl.dec(ciphertext, SK, PK);
		AES.crypto(Cipher.DECRYPT_MODE, dis, os, m);
		
	}
	public void dec_with_outsourcing(File in) {
		String ciphertextFileName = null; 
		DataInputStream dis = null;
		try {
			ciphertextFileName = in.getCanonicalPath();
			dis = new DataInputStream(new FileInputStream(in));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Ciphertext ciphertext = SerializeUtils._unserialize(Ciphertext.class, dis);
		
		String output = null;
		if(ciphertextFileName.endsWith(".cpabe")){
			int end = ciphertextFileName.indexOf(".cpabe");
			output = ciphertextFileName.substring(0, end);
		}
		else{
			output = ciphertextFileName + ".out";
		}
		File outputFile = CPABEImpl.createNewFile(output);
		OutputStream os = null;
		try {
			os =  new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		Element m = CPABEImpl.dec_with_outsourcing(ciphertext, SK, PK, this);
		AES.crypto(Cipher.DECRYPT_MODE, dis, os, m);
	}
	
	public void serializePK(File f){
		SerializeUtils.serialize(this.PK, f);
	}
	
	public void serializeSK(File f){
		SerializeUtils.serialize(this.SK, f);
	}
	
	//Initialization
	//All Parameters like alpha/beta are denoted in Calculate Table
	//All Elements that after pairing are denoted in the Result Table
	//We should calculate the inverse key to decrypt the pairing for each participants.
	public void PreComputation(int UBase) {
		boolean error = true;
		while(error) {
			try {
				this.UBase = UBase;
				r = GenerateR(UBase);
				rp = GenerateR(UBase);
				a = GenerateConstant(rp, UBase, Order);
				b = GenerateConstant(r, UBase, Order);
				aP = CalculatePara(a, P);
				bQ = CalculatePara(b, Q);
				PA = P.pow(Aa).getImmutable();
				PB = Q.pow(Ab).getImmutable();
				InverseE = CalculateInverseE(a, b, P, Q, aP, bQ, Order, pairing);
				CalTable = new TreeMap<String,PairingPoint>(); 
				ResultTable = new TreeMap<String, ReturnParameter>();
				CheckingTable = new TreeMap<String,String>();
				//batch A,B->PA,PB
				CreatCalculateTable(A, B, aP, bQ);
				//CalculateKAB();
				error = false;
			}catch (Exception e) {
			 //TODO: handle exception
				//System.out.println(e);
				error = true;
			}
		}
		//System.out.println("Precomputation is completed!");
	}
	//Send Public Parameter to each outsource Device
	public PubParameter SendPubParameter() {
		PubParameter PubPar = new PubParameter();
		PubPar.P = this.P;
		PubPar.Q = this.Q;
		PubPar.GT = this.GT;
		PubPar.Order = this.Order;
		PubPar.pairing = this.pairing;
		
		return PubPar;
	}
	public BigInteger GenerateBigRandom() {
	    BigInteger BigRandom = pairing.getZr().newRandomElement().toBigInteger();
		return BigRandom;
	}
	public void CalculateEAB() {
		Element eab = pairing.pairing(PA, PB);
		System.out.print("original e(A,B) = ");
		System.out.println(eab.toBigInteger());
	}
	public void CalculateAB(BigInteger ea, BigInteger eb) {
		this.Aa = this.Aa.multiply(ea).mod(Order);
		this.Ab = this.Ab.multiply(eb).mod(Order);
		BigInteger ab = ea.multiply(eb).mod(Order);
		
		this.Aab = this.Aab.multiply(ab).mod(Order);
		this.AB.add(ab);
	}
	public Element GetPairing() {
		Element Pairing = EAB.get(0);
		return Pairing;
	}
	public Element GetA() {
		return this.A;
	}
	public Element GetB() {
		return this.B;
	}
	//Send the points pair that need to be calculated to Outsource Device
	public ArrayList<PairingPoint> SendPairingPoint(int OutsourceDeviceNumber) {
		CalculateNum = (PairingNumber / OutsourceDeviceNumber)*Extension + 1 ;
		ArrayList<PairingPoint> PP = new ArrayList<PairingPoint>();
		int index = 0;
		for(int i = 0 ; i < CalculateNum ; i++) {
			if(PairPoint.size() > 0) {
				index = (int)(Math.random()*PairPoint.size());
				PP.add(PairPoint.get(index));
				PairPoint.remove(index);
			}else {
				break;
			}
		}
		
		return PP;
	}
	//Get the result that calculated by the outsource Device
	public void GetPairingResult(ArrayList<ReturnParameter> RP) {
		for(int i = 0 ; i < RP.size() ; i++ ) {
			this.Result.add(RP.get(i));
		}
	}
	//Update the Calculate Table to Result Table that store the pairing value
	public void UpdateTable() {
		if(CheckResult()) {
			for(String key : CalTable.keySet()) {
					
				PairingPoint PP = new PairingPoint();
				PP = CalTable.get(key);
				ReturnParameter RP = new ReturnParameter();
				RP.G1 = PP.LP;
				RP.G2 = PP.RP;
				RP.Result = pairing.getZr().newZeroElement();
				boolean Find = false;
				
				for(int i = 0 ; i < Result.size() ; i++) {
					if(Result.get(i).G1.equals(RP.G1) && Result.get(i).G2.equals(RP.G2)) {
						RP.Result = Result.get(i).Result;
						Find = true;
						ResultTable.put(key,RP);
						break;
					}
				}
				if(Find == false){
					System.out.println("Not Found the PairingPoint on Dictionary!");
				}
			}
		}
	}
	//Check whether all the parameters like alpha/beta meet the rules that we set or not
	public boolean AuthenticationParameterCheck() {
		boolean Checking = true;
		
		for(String key : ResultTable.keySet()) {		
			if(key == "theta" || CheckingTable.get(key) != "unknown") {
				continue;
			}
			BigInteger CheckingR [] = new BigInteger[r.length];
			String temp[] = key.split(" ");
			int index = Integer.valueOf((temp[1]));
			index = index + 10;
			int group = (index-3)/20;
			String Key = Integer.toString(index);
			String CmpKey = temp[0] + " " + Key;
			int value = 0;
			
			if(key.contains("alpha")) {
				for(int i = 0 ; i < r.length ; i++) {
					CheckingR[i] = r[i];
				}
			}else if(key.contains("beta")) {
				for(int i = 0 ; i < rp.length ; i++) {
					CheckingR[i] = rp[i];
				}
			}
			Element Authentication1 = pairing.getGT().newElement(ResultTable.get(key).Result.getImmutable());
			Element Authentication2 = pairing.getGT().newElement(ResultTable.get(CmpKey).Result.getImmutable());
			index = index % 2;
			if(index == 0) {
				value = 1;
			}else {
				value = 0;
			}
			//alpha and beta check
			if(CheckingR[4* group + 2 + value].compareTo(CheckingR[4 * group + 0 + value]) == 1 || CheckingR[4* group + 2 + value].equals(CheckingR[4 * group + 0 + value])) {
				float Denominator = CheckingR[4* group + 2 + value].intValue();
				float Numerator = CheckingR[4* group + 0 + value].intValue();
				float fraction = Denominator/Numerator;
				
				if(fraction > 0) {
					BigInteger k = CheckingR[4* group + 2 + value].multiply(CheckingR[4* group + 0 + value].modInverse(Order)).mod(Order);
					Element left = pairing.getGT().newElement(Authentication1);
					Element right = pairing.getGT().newElement(Authentication2.pow(k));
					
					if(!left.isEqual(right)) {
						CheckingTable.put(key, "fault");
						CheckingTable.put(CmpKey, "fault");
						Checking = false;
					}else {
						CheckingTable.put(key, "success");
						CheckingTable.put(CmpKey, "success");
					}
				}else if(fraction < 0) {
					BigInteger MinusOne = new BigInteger("-1");
					BigInteger k = CheckingR[4* group + 2 + value].multiply(MinusOne).mod(Order).multiply(CheckingR[4 * group + 0 + value].modInverse(Order));
					Element left = pairing.getGT().newElement(Authentication1);
					Element right = pairing.getGT().newElement(Authentication2.pow(k));
					if(!left.add(right).toBigInteger().equals(BigInteger.ONE)) {
						CheckingTable.put(key, "fault");
						CheckingTable.put(CmpKey, "fault");
						Checking = false;
					}else {
						CheckingTable.put(key, "success");
						CheckingTable.put(CmpKey, "success");
					}
				}
			}else {
				float Denominator = CheckingR[4* group + 0 + value].intValue();
				float Numerator = CheckingR[4* group + 2 + value].intValue();
				float fraction = Denominator/Numerator;
				
				if(fraction > 0) {
					BigInteger k = CheckingR[4* group + 0 + value].multiply(CheckingR[4* group + 2 + value].modInverse(Order)).mod(Order);
					Element left = pairing.getGT().newElement(Authentication1).pow(k);
					Element right = pairing.getGT().newElement(Authentication2);
					if(!left.isEqual(right)) {
						CheckingTable.put(key, "fault");
						CheckingTable.put(CmpKey, "fault");
						Checking = false;
					}else {
						CheckingTable.put(key, "success");
						CheckingTable.put(CmpKey, "success");
					}
					
				}else if(fraction < 0) {
					BigInteger MinusOne = new BigInteger("-1");
					BigInteger k = CheckingR[4* group + 0 + value].multiply(MinusOne).mod(Order).multiply(CheckingR[4 * group + 2 + value].modInverse(Order));	
					Element left = pairing.getGT().newElement(Authentication1.pow(k));
					Element right = pairing.getGT().newElement(Authentication2);
					
					if(!left.add(right).toBigInteger().equals(BigInteger.ONE)) {
						CheckingTable.put(key, "fault");
						CheckingTable.put(CmpKey, "fault");
						Checking = false;
					}else {
						CheckingTable.put(key, "success");
						CheckingTable.put(CmpKey, "success");
					}
				}
			}
		}
		//System.out.print("Checking on each parameters is ");
		//System.out.println(Checking);
		return Checking;
	}
	public void MergeResult(){
		this.OutsourcingNumber = this.OutsourcingNumber + 1;
		if(!CheckCheckingTable()) {
			System.out.println("Parameters are not be calculated!");
		}
		
		for(int i = 0 ; i < UBase ; i++) {
			Element result = pairing.getGT().newElement();
			Element original = pairing.getGT().newElement();
			Element ABQ = pairing.getGT().newElement();
			Element APB = pairing.getGT().newElement();
			
			String alpha11 = "alpha " + Integer.toString(2*i+1) + "1";
			String alpha12 = "alpha " + Integer.toString(2*i+1) + "2";
			String alpha21 = "alpha " + Integer.toString(2*i+2) + "1";
			String alpha22 = "alpha " + Integer.toString(2*i+2) + "2";
			
			String beta11 = "beta " + Integer.toString(2*i+1) + "1";
			String beta12 = "beta " + Integer.toString(2*i+1) + "2";
			String beta21 = "beta " + Integer.toString(2*i+2) + "1";
			String beta22 = "beta " + Integer.toString(2*i+2) + "2";
				
			APB = ResultTable.get(beta11).Result.duplicate();
			APB = APB.add(ResultTable.get(beta12).Result).duplicate();
			APB = APB.add(ResultTable.get(beta21).Result).duplicate();
			APB = APB.add(ResultTable.get(beta22).Result).duplicate();
			APB = APB.add(InverseE[4*i+1]).duplicate();
			APB = APB.add(InverseE[4*i+2]).duplicate();
			
			ABQ = ResultTable.get(alpha11).Result.duplicate();
			ABQ = ABQ.add(ResultTable.get(alpha12).Result).duplicate();
			ABQ = ABQ.add(ResultTable.get(alpha21).Result).duplicate();
			ABQ = ABQ.add(ResultTable.get(alpha22).Result).duplicate();
			ABQ = ABQ.add(InverseE[4*i+3]).duplicate();
			ABQ = ABQ.add(InverseE[4*i+4]).duplicate();
		
			result = InverseE[0].add(ResultTable.get("theta").Result.duplicate().add(ABQ).add(APB));
			EAB.add(result);
		}
		if(CheckEAB()) {
			System.out.println("e(A,B) results are not all the same");
		}//else {
			//CalculateAiBi();
			//System.out.println("e(A,B) results are all the same");
		//}
	}
	public void PointsExtension(int a) {
		ArrayList<PairingPoint> Exp = new ArrayList<PairingPoint>(PairPoint);
		Extension = a;
		for(int i = 1 ; i < a ; i++) {
			for(int j = 0 ; j < Exp.size() ; j++) {
				PairPoint.add(Exp.get(j));
			}
		}
		PairingNumber = PairPoint.size();
	}
	public Element Outsourcing(int UBase, int OutsourceDeviceNumber,Element A, Element B) {
		ArrayList<OutsourceDevice> OutDevice = new ArrayList<OutsourceDevice>();
		ArrayList<PairingPoint> PP = new ArrayList<PairingPoint>();
		
		long PrecomputaionTime,OutsourceTime,VerifyTime,EndTime = 0;
		
		
		//System.out.println("Step1 : Precomputation");
		PrecomputaionTime = System.currentTimeMillis();
		this.setSecret(A, B);
		this.PreComputation(UBase);
		
		//System.out.println("Step2 : Outsource");
		//Outsource
		OutsourceTime = System.currentTimeMillis();
		for(int j = 0 ; j < OutsourceDeviceNumber ; j++) {
			OutsourceDevice out = new OutsourceDevice();
			out.GetPubParameter((this.SendPubParameter()));
			out.GetPairingPoint(this.SendPairingPoint(OutsourceDeviceNumber));
			OutDevice.add(out);
		}
		
		// OutsourceDevice calculate the result
		for(int j = 0 ; j < OutsourceDeviceNumber ; j++) {
			this.GetPairingResult(OutDevice.get(j).CalculatePairing());
		}
		
		//Verify the Result
		//System.out.println("Step3 : Verify");
		VerifyTime = System.currentTimeMillis();
		this.UpdateTable();
		this.AuthenticationParameterCheck();
		
		//IOTDevice Merge all the result and calculate e(A,B) and separate it into n parts.
		//System.out.println("Step4 : Compare that results");
		this.MergeResult();
		EndTime = System.currentTimeMillis();
		
		PrecomputaionTime = (OutsourceTime-PrecomputaionTime);
		OutsourceTime = (VerifyTime - OutsourceTime);
		VerifyTime = (EndTime - VerifyTime);
		
		this.PrecomputationTotal = this.PrecomputationTotal + PrecomputaionTime;
		this.OutsourceTotal = this.OutsourceTotal + OutsourceTime;
		this.VerifyTotal = this.VerifyTotal + VerifyTime;
		
		Element Result = this.GetPairing();
		
		//System.out.println(Result);
		//System.out.println(pairing.pairing(A, B));
		EAB = new ArrayList<Element>();
		return Result;
	}
	private void CalculateAiBi() {
		for(int i = 0 ; i < this.KAB.size() ; i++) {
			Element AiBi = this.EAB.get(0).pow(this.KAB.get(i).mod(Order));
			this.eAiBis.add(AiBi);
		}
	}
	//Check whether some parameters are not be calculated or not
	private boolean CheckCheckingTable() {
		boolean full = true;
		for(String key : CheckingTable.keySet()) {
			if(CheckingTable.get(key) == "unknown") {
				full = false;
				break;
			}
		}
		return full;
	}
	//Check whether the pairing results on the same points are the same or not
	private boolean CheckResult() {
		boolean same = true;
		for(int i = 0 ; i < Result.size() ; i++) {
			for(int j = i+1 ; j < Result.size() ; j++) {
				if(Result.get(i).G1.equals(Result.get(j).G1) && Result.get(i).G2.equals(Result.get(j).G2)) {
					if(!Result.get(i).Result.equals(Result.get(j).Result)) {
						same = false;
						System.out.println("There are different result on the two points");
						System.out.println(Result.get(i).G1);
						System.out.print(" and ");
						System.out.println(Result.get(i).G2);
					}
				}
			}
		}
		//System.out.println("The results on the same two points are all the same");
		return same;
	}
	//Check whether each e(A,B) is the same or not
	private boolean CheckEAB() {
		boolean different = false;
		Element result = pairing.getGT().newElement(EAB.get(0));
		for(int i = 0 ; i < EAB.size() ; i++) {
			if(!result.equals(EAB.get(i))) {
				different = true;
				break;
			}
		}
		return different;
	}
	private void CalculateKAB() {
		for(int i = 0 ; i < AB.size() ; i++) {
			BigInteger Kab = this.Aab.multiply(AB.get(i).modInverse(Order)).modInverse(Order);
			this.KAB.add(Kab);
		}
	}
  	private void CreatCalculateTable(Element A,Element B,Element aP[],Element bQ[]) {
		Element x_point = A.duplicate().add(aP[0]);
		Element y_point = B.duplicate().add(bQ[0]);
		String key = "theta";
		PairingPoint PP = new PairingPoint();
		PP.LP = x_point;
		PP.RP = y_point;
		CalTable.put(key, PP);
		PairPoint.add(PP);
		
		Element A2PBase = A.duplicate().add(aP[1]);
		Element A3PBase = A.duplicate().add(aP[2]);
		Element B2QBase = B.duplicate().add(bQ[1]);
		Element B3QBase = B.duplicate().add(bQ[2]);
		
		for(int i = 0 ; i < UBase ; i++) {
			key = "alpha " + Integer.toString(2*i+1) + "1";
			PP = new PairingPoint();
			x_point = A2PBase;
			y_point = bQ[(4*(i+1))-1];
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "alpha " + Integer.toString(2*i+1) + "2";
			PP = new PairingPoint();
			x_point = A3PBase;
			y_point = bQ[(4*(i+1))];
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "beta " + Integer.toString(2*i+1) + "1";
			PP = new PairingPoint();
			x_point = aP[(4*(i+1))-1];
			y_point = B2QBase;
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "beta " + Integer.toString(2*i+1) + "2";
			PP = new PairingPoint();
			x_point = aP[(4*(i+1))];
			y_point = B3QBase;
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "alpha " + Integer.toString(2*i+2) + "1";
			PP = new PairingPoint();
			x_point = A2PBase;
			y_point = bQ[(4*(i+1)) + 1];
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "alpha " + Integer.toString(2*i+2) + "2";
			PP = new PairingPoint();
			x_point = A3PBase;
			y_point = bQ[(4*(i+1)) + 2];
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "beta " + Integer.toString(2*i+2) + "1";
			PP = new PairingPoint();
			x_point = aP[(4*(i+1)) + 1];
			y_point = B2QBase;
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
			
			key = "beta " + Integer.toString(2*i+2) + "2";
			PP = new PairingPoint();
			x_point = aP[(4*(i+1)) + 2];
			y_point = B3QBase;
			PP.LP = x_point;
			PP.RP = y_point;
			CalTable.put(key, PP);
			PairPoint.add(PP);
			CheckingTable.put(key, "unknown");
		}
		PairingNumber = PairPoint.size();
	}
	private BigInteger [] GenerateR(int UBase){
		GenerateParameter GP = new GenerateParameter();
		String [] RnumText = {"1","2","4","8","16","32","64"};
		BigInteger[] r = new BigInteger[4 * UBase];
		
		for(int i=0 ; i < r.length ; i++) {
			int index = (int)(Math.random()*6);
			r[i] = new BigInteger(RnumText[index]);
		}
		return r;
	}
	private BigInteger [] GenerateConstant(BigInteger r[],int UBase, BigInteger Order) {
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
	private boolean TestConstant(BigInteger r[],BigInteger b[],BigInteger Order) {
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
	private boolean TestInverseE(Element InverseE[],BigInteger a[],BigInteger b[],Element P,Element Q,Element aP[],Element bQ[],BigInteger Order,Pairing pairing) {
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
	private Element [] CalculatePara(BigInteger c[],Element E) {
		int UBase = c.length/4;
		Element e = E.getImmutable();
		Element [] Result = new Element[4*UBase + 3];
		for(int i = 0 ; i < 4*UBase + 3 ; i++) {
			Result[i] = e.pow(c[i]);
		}
		return Result;
	}
	private Element [] CalculateInverseE(BigInteger a[],BigInteger b[],Element P,Element Q,Element aP[],Element bQ[],BigInteger Order,Pairing pairing) {
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
	public void PrintOutsourcingTime() {
		System.out.print("This Client has outsource ");
		System.out.print(this.OutsourcingNumber);
		System.out.println(" pairings.");
		
		System.out.print("The precomputation phase has took ");
		System.out.print(this.PrecomputationTotal);
		System.out.println(" ms.");
		
		System.out.print("The outsourcing phase has took ");
		System.out.print(this.OutsourceTotal);
		System.out.println(" ms.");
		
		System.out.print("The verification phase has took ");
		System.out.print(this.VerifyTotal);
		System.out.println(" ms.");
	}
	
	public void PrintPairingPoint() {
		for(int i = 0 ; i < PairPoint.size() ; i++) {
			System.out.println(Integer.toString(i+1) + " = ");
			System.out.println(PairPoint.get(i).LP);
			System.out.println(PairPoint.get(i).RP);
		}
	}
	public void PrintParameter() {
		System.out.print("Order = ");
		System.out.println(Order);
		System.out.print("G1 = ");
		System.out.println(P);
		System.out.print("G2 = ");
		System.out.println(Q);
		
		PrintConstant(a,"a");
		PrintConstant(b,"b");
		PrintR(r, "r");
		PrintR(rp, "rp");
		PrintInverseE(InverseE);		
	}
	public void PrintCalculateTable() {
		for(String key : CalTable.keySet()) {
			System.out.println(key + ":");
			System.out.print("Left Point = ");
			System.out.println(CalTable.get(key).LP.toString());
			System.out.print("Right Point = ");
			System.out.println(CalTable.get(key).RP.toString());
		}
	}	
	public void PrintResultTable() {
		for(String key : ResultTable.keySet()) {
			System.out.print(key + "?G = ");
			System.out.println(ResultTable.get(key).Result);
		}
	}
	public void PrintResult() {
		for(int i = 0 ; i < Result.size() ; i++) {
			System.out.println("Result " + Integer.toString(i+1));
			System.out.print("G1 = ");
			System.out.println(Result.get(i).G1);
			System.out.print("G2 = ");
			System.out.println(Result.get(i).G2);
			System.out.print("PairingResult = ");
			System.out.println(Result.get(i).Result);
		}
	}
	public void PrintCheckingTable() {
		for(String key : CheckingTable.keySet()) {
			System.out.print(key + "?G = ");
			System.out.println(CheckingTable.get(key));
		}
	}
	public void PrintEAB() {
		System.out.print("outsource e(A,B) = ");
		System.out.println(EAB.get(0).toBigInteger());
	}
	public void PrintAB() {
		for(int i = 0 ; i < AB.size() ; i++) {
			System.out.print("Participant");
			System.out.print(i+1);
			System.out.print(":");
			System.out.println(AB.get(i));
		}
	}
	public void PrintKAB() {
		for(int i = 0 ; i < KAB.size() ; i++) {
			System.out.print("K * Aab :");
			System.out.println(this.Aab.multiply(KAB.get(i)).mod(Order));
			
			//System.out.print("Key of Participant");
			//System.out.print(i+1);
			//System.out.print(":");
			//System.out.println(KAB.get(i));
			
		}
	}
	public void PrintAiBi() {
		for(int i = 0 ; i < eAiBis.size() ; i++) {
			System.out.print("Result of Participant");
			System.out.print(i+1);
			System.out.print(":");
			System.out.println(eAiBis.get(i));
		}
	}
	private void PrintR(BigInteger r[],String name) {
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
	private void PrintConstant(BigInteger c[],String name) {
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
	private void PrintInverseE(Element E[]) {
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
}
