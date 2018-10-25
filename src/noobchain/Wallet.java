package noobchain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import noobchain.tx.Transaction;
import noobchain.tx.TransactionInput;
import noobchain.tx.TransactionOutput;

public class Wallet {

	private PrivateKey privateKey;
	private PublicKey publicKey;

	// UTXOs owned by this wallet
	private Map<String, TransactionOutput> UTXOs = new HashMap<>();

	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	// Returns balance and stores the UTXOs owned by this wallet in UTXOs variable
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : NoobChain.getUTXOs().entrySet()) {
			TransactionOutput UTXO = item.getValue();

			// If coins belong to me, add it to our list of unspent transactions
			if (UTXO.isRecipientSelf(publicKey)) {
				UTXOs.put(UTXO.getId(), UTXO);
				total += UTXO.getValue();
			}
		}
		return total;
	}

	// Generates and returns a new transaction from this wallet
	public Transaction sendFunds(PublicKey recipient, float value) {
		 // Gather balance and check funds
		if (getBalance() < value) {
			System.out.println("Not enough funds to send transaction. Transaction Discarded.");
			return null;
		}

		List<TransactionInput> inputs = new ArrayList<>();

		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.getValue();
			inputs.add(new TransactionInput(UTXO.getId()));

			if (total > value) {
				break;
			}
		}

		Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
		newTransaction.generateSignature(privateKey);

		for (TransactionInput input : inputs) {
			UTXOs.remove(input.getTransactionOutputId());
		}
		return newTransaction;
	}

}
