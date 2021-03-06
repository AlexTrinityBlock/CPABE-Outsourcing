package cn.edu.pku.ss.crypto.abe.apiV2;

import java.math.BigInteger;
import java.util.ArrayList;

import cn.edu.pku.ss.crypto.abe.PairingPoint;
import cn.edu.pku.ss.crypto.abe.PubParameter;
import cn.edu.pku.ss.crypto.abe.ReturnParameter;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeAPairing;

public class OutsourceDevice {
	//public Element Zr;
	public Element P;
	public Element Q;
	public Element GT;
	public BigInteger Order;
	public TypeACurveGenerator pg;
	public PairingParameters curveParams;
	public Pairing pairing;
	public int PairingPointNumber;
	
	private ArrayList<ReturnParameter> RParameter;
	private ArrayList<PairingPoint> PairPoint;
	
	public OutsourceDevice() {
		
	}
	
	public OutsourceDevice(PubParameter PubPar) {
		this.P = PubPar.P;
		this.Q = PubPar.Q;
		this.GT = PubPar.GT;
		this.Order = PubPar.Order;
		this.pairing = PubPar.pairing;
		this.PairingPointNumber = 0;
		this.PairPoint = new ArrayList<PairingPoint>();
		this.RParameter = new ArrayList<ReturnParameter>();
	}
	
	public void GetPubParameter(PubParameter PubPar) {
		this.P = PubPar.P;
		this.Q = PubPar.Q;
		this.GT = PubPar.GT;
		this.Order = PubPar.Order;
		this.pairing = PubPar.pairing;
		
		this.PairingPointNumber = 0;
		this.PairPoint = new ArrayList<PairingPoint>();
		this.RParameter = new ArrayList<ReturnParameter>();
	}
	
	public void PrintParameter() {
		System.out.print("Order = ");
		System.out.println(Order);
		System.out.print("G1 = ");
		System.out.println(P);
		System.out.print("G2 = ");
		System.out.println(Q);
	}
	
	public void PrintPairingPoint() {
		for(int i = 0 ; i < PairingPointNumber ; i++) {
			System.out.println(Integer.toString(i+1) + " = ");
			System.out.print("LP = ");
			System.out.println(PairPoint.get(i).LP);
			System.out.print("RP = ");
			System.out.println(PairPoint.get(i).RP);
		}
	}
	public void PrintReturnParameter() {
		for (int i = 0 ; i < this.PairPoint.size() ; i++) {
			System.out.println(i+1);
			System.out.print("Left Point : ");
			System.out.println(RParameter.get(i).G1);
			System.out.print("Right Point : ");
			System.out.println(RParameter.get(i).G2);
			System.out.print("Result: ");
			System.out.println(RParameter.get(i).Result);
		}
	}
	
	//Get Points that need to be calculated
	public void GetPairingPoint(ArrayList<PairingPoint> PP) {
		PairingPointNumber = PP.size();
		for(int i = 0 ; i < PairingPointNumber ; i++) {
			this.PairPoint.add(PP.get(i));
		}
	}
	
	//Get Points that need to be calculated
	public void GetPairingPoint(PairingPoint PP) {
		PairingPointNumber = 1;
		for(int i = 0 ; i < PairingPointNumber ; i++) {
			this.PairPoint.add(PP);
		}
	}
	
	public ArrayList<ReturnParameter> CalculatePairing() {
		for (int i = 0 ; i < this.PairPoint.size() ; i++) {
			ReturnParameter RP = new ReturnParameter();
			RP.G1 = this.PairPoint.get(i).LP;
			RP.G2 = this.PairPoint.get(i).RP;
			RP.Result = this.pairing.pairing(PairPoint.get(i).LP, PairPoint.get(i).RP);
			
			RParameter.add(RP);	
		}
		return RParameter;
	}
}
