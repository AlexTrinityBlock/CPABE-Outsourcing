package cn.edu.pku.ss.crypto.abe;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeAPairing;

public class PairingManager {
	private static final String TYPE_A = "pairing/params/curves/a.properties";
	/*
	static int rBits = 256;
	static int qBits = 256;
	static TypeACurveGenerator pg = new TypeACurveGenerator(rBits,qBits);
	static PairingParameters curveParams = pg.generate();
	public static final Pairing defaultPairing = new TypeAPairing(curveParams);
	*/
	public static final Pairing defaultPairing = PairingFactory.getPairing(TYPE_A);

	public Pairing getPairing(String parametersPath){
		return PairingFactory.getPairing(parametersPath);
	}
}
