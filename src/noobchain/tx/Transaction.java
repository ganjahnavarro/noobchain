package noobchain.tx;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import noobchain.util.StringUtil;

public class Transaction {

	private String id; // this is also the hash of the transaction.
	private PublicKey sender; // senders address/public key.
	private PublicKey reciepient; // Recipients address/public key.
	private float value;
	private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

	private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // a rough count of how many transactions have been generated.

	// Constructor:
	public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	// Signs all the data we don't wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// Verifies the data we signed hasn't been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; // increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value) + sequence);
	}

}
