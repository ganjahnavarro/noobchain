package noobchain;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

public class NoobChain {

	public static List<Block> blockchain = new ArrayList<Block>();

	public static void main(String[] args) {
		// add our blocks to the blockchain list
		blockchain.add(new Block("Hi im the first block", "0"));
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).getHash()));
		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).getHash()));

		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
		System.out.println("Valid: " + isChainValid());
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;

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
		}
		return true;
	}

}
