package noobchain.tx;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import noobchain.NoobChain;
import noobchain.util.StringUtil;

public class Transaction {

	private String id; // this is also the hash of the transaction.
	private PublicKey sender; // senders address/public key.
	private PublicKey recipient; // Recipients address/public key.
	private float value;
	private byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

	private List<TransactionInput> inputs = new ArrayList<TransactionInput>();
	private List<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // a rough count of how many transactions have been generated.

	// Constructor:
	public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}

	// Signs all the data we don't wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	// Verifies the data we signed hasn't been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	// Calculates the transaction hash (which will be used as its id)
	private String calulateHash() {
		sequence++; // increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(recipient)
				+ Float.toString(value) + sequence);
	}

	// Returns true if new transaction could be created
	public boolean processTransaction() {
		if (verifiySignature() == false) {
			System.out.println("Transaction signature failed to verify.");
			return false;
		}

		// Gather transaction inputs (make sure they are unspent)
		for (TransactionInput i : inputs) {
			i.setUTXO(NoobChain.getUTXOs().get(i.getTransactionOutputId()));
		}

		// Check if transaction is valid
		if (getInputsValue() < NoobChain.getMinimumTransaction()) {
			System.out.println("Transaction inputs is too small: " + getInputsValue());
			return false;
		}

		// Generate transaction outputs
		// Get value of inputs then the left over change
		float leftOver = getInputsValue() - value;
		id = calulateHash();

		// Send value to recipient
		outputs.add(new TransactionOutput(this.recipient, value, id));

		// Send the left over 'change' back to sender
		outputs.add(new TransactionOutput(this.sender, leftOver, id));

		// Add outputs to unspent transactions list
		for (TransactionOutput o : outputs) {
			NoobChain.getUTXOs().put(o.getId(), o);
		}

		// Remove transaction inputs from UTXO lists as spent
		for (TransactionInput i : inputs) {
			if (i.getUTXO() == null) {
				continue;
			}
			NoobChain.getUTXOs().remove(i.getUTXO().getId());
		}

		return true;
	}

	// Returns sum of inputs
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput input : inputs) {
			if (input.getUTXO() == null) {
				continue;
			}
			total += input.getUTXO().getValue();
		}
		return total;
	}

	// Returns sum of outputs
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput output : outputs) {
			total += output.getValue();
		}
		return total;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TransactionInput> getInputs() {
		return inputs;
	}

	public List<TransactionOutput> getOutputs() {
		return outputs;
	}

	public PublicKey getRecipient() {
		return recipient;
	}

	public PublicKey getSender() {
		return sender;
	}

	public float getValue() {
		return value;
	}

}
