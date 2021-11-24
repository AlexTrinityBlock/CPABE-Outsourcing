package cn.edu.pku.ss.crypto.abe;

import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeAPairing;

public class PubParameter {
	public Element Zr;
	public Element P;
	public Element Q;
	public Element GT;
	public BigInteger Order;
	public Pairing pairing;
}
