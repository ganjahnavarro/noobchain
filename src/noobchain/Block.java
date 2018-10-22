package noobchain;

import java.util.Date;

public class Block {

	private String hash;
	private String previousHash;
	private String data; // our data will be a simple message.
	private long timeStamp; // as number of milliseconds since 1/1/1970.
	private int nonce;

	// Block Constructor.
	public Block(String data, String previousHash) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); // Making sure we do this after we set the other values.
	}

	public String calculateHash() {
		return StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + data);
	}

	public void mineBlock(int difficulty) {
		long miningStart = System.currentTimeMillis();

		String target = new String(new char[difficulty]).replace('\0', '0'); // Create a string with difficulty * "0"
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}

		long duration = System.currentTimeMillis() - miningStart;
		System.out.println("Block mined. Duration: " + duration + "ms. Hash: " + hash);
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

}
