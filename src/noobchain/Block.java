package noobchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import noobchain.tx.Transaction;
import noobchain.util.StringUtil;

public class Block {

	private String hash;
	private String previousHash;

	private String merkleRoot;
	private List<Transaction> transactions = new ArrayList<Transaction>();

	private long timeStamp; // as number of milliseconds since 1/1/1970.
	private int nonce;

	// Block Constructor.
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); // Making sure we do this after we set the other values.
	}

	public String calculateHash() {
		return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		long miningStart = System.currentTimeMillis();

		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * "0"
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}

		long duration = System.currentTimeMillis() - miningStart;
		System.out.println("Block mined. Duration: " + duration + "ms. Hash: " + hash);
	}

	// Add transaction to this block, returns true if transaction is successfully
	// added
	public boolean addTransaction(Transaction transaction) {
		// Process transaction and check if valid, unless block is genesis (first) block
		// then ignore
		if (transaction == null) {
			return false;
		}

		if (previousHash != "0") {
			if (transaction.processTransaction() != true) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transaction successfully added to block.");
		return true;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

}
