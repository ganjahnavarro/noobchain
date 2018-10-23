package noobchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.gson.GsonBuilder;

import noobchain.tx.Transaction;
import noobchain.util.StringUtil;

public class NoobChain {

	private static List<Block> blockchain = new ArrayList<Block>();
	private static int difficulty = 5;

	private static Wallet walletA;
	private static Wallet walletB;

	public static void main(String[] args) {
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new BouncyCastleProvider());
		
		// Create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		
		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.getPrivateKey()));
		System.out.println(StringUtil.getStringFromKey(walletA.getPublicKey()));
		
		// Create a test transaction from WalletA to walletB
		Transaction transaction = new Transaction(walletA.getPublicKey(), walletB.getPublicKey(), 5, null);
		transaction.generateSignature(walletA.getPrivateKey());

		// Verify the signature works and verify it from the public key
		System.out.println("Signature verified: " + transaction.verifiySignature());
	}

	public static void basicDemo() {
		// add our blocks to the blockchain list
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1..");
		blockchain.get(0).mineBlock(difficulty);

		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).getHash()));
		System.out.println("Trying to Mine block 2..");
		blockchain.get(1).mineBlock(difficulty);

		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).getHash()));
		System.out.println("Trying to Mine block 3..");
		blockchain.get(2).mineBlock(difficulty);

		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
		System.out.println("Valid: " + isChainValid());
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			// compare registered hash and calculated hash:
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}

			// compare previous hash and registered previous hash
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("Previous Hashes not equal");
				return false;
			}

			// check if hash is solved
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}

}
