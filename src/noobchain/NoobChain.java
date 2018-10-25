package noobchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import noobchain.tx.Transaction;
import noobchain.tx.TransactionInput;
import noobchain.tx.TransactionOutput;

public class NoobChain {

	private static List<Block> blockchain = new ArrayList<>();
	private static Map<String, TransactionOutput> UTXOs = new HashMap<>();

	private static int difficulty = 5;
	private static float minimumTransaction = 0.1f;
	private static Wallet walletA;
	private static Wallet walletB;
	private static Transaction genesisTransaction;

	public static void main(String[] args) {
		// Setup Bouncy castle as a Security Provider
		Security.addProvider(new BouncyCastleProvider());

		// Create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		// Create genesis (first) transaction, which sends 100 NoobCoin to walletA:
		genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		genesisTransaction.generateSignature(coinbase.getPrivateKey()); // manually sign the genesis transaction
		// Manually set genesis transaction values
		TransactionOutput genesisTxOutput = new TransactionOutput(genesisTransaction.getRecipient(),
				genesisTransaction.getValue(), genesisTransaction.getId());
		genesisTransaction.setId("0");
		genesisTransaction.getOutputs().add(genesisTxOutput);

		// Store our first transaction in the UTXOs list
		UTXOs.put(genesisTxOutput.getId(), genesisTxOutput);

		System.out.println("Creating and mining genesis block.. \n");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);

		// Testing
		Block block1 = new Block(genesis.getHash());
		System.out.println("Wallet A's balance is: " + walletA.getBalance() + "\n");
		System.out.println("Wallet A is attempting to send funds (40) to Wallet B.. \n");
		block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
		addBlock(block1);
		System.out.println("Wallet A's balance is: " + walletA.getBalance());
		System.out.println("Wallet B's balance is: " + walletB.getBalance() + "\n");

		Block block2 = new Block(block1.getHash());
		System.out.println("Wallet A attempting to send more funds (1000) than it has.. \n");
		block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
		addBlock(block2);
		System.out.println("Wallet A's balance is: " + walletA.getBalance());
		System.out.println("Wallet B's balance is: " + walletB.getBalance() + "\n");

		Block block3 = new Block(block2.getHash());
		System.out.println("Wallet B is attempting to send funds (20) to Wallet A.. \n");
		block3.addTransaction(walletB.sendFunds(walletA.getPublicKey(), 20));
		System.out.println("Wallet A's balance is: " + walletA.getBalance());
		System.out.println("Wallet B's balance is: " + walletB.getBalance() + "\n");

		isChainValid();
	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// Temporary working list of unspent transactions at a given block state.
		Map<String, TransactionOutput> tempUTXOs = new HashMap<>();
		tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

		// Loop through blockchain to check hashes
		for (int i = 1; i < blockchain.size(); i++) {

			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);

			// Compare registered hash and calculated hash:
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				System.out.println("Current hashes are not equal.");
				return false;
			}

			// Compare previous hash and registered previous hash
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("Previous hashes are not equal.");
				return false;
			}

			// Check if hash is solved
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined.");
				return false;
			}

			// Loop thru blockchain's transactions:
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.getTransactions().size(); t++) {
				Transaction currentTransaction = currentBlock.getTransactions().get(t);

				if (!currentTransaction.verifiySignature()) {
					System.out.println("Signature on Transaction(" + t + ") is invalid.");
					return false;
				}

				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("Inputs are not equal to outputs on Transaction (" + t + ").");
					return false;
				}

				for (TransactionInput input : currentTransaction.getInputs()) {
					tempOutput = tempUTXOs.get(input.getTransactionOutputId());

					if (tempOutput == null) {
						System.out.println("Referenced input on Transaction (" + t + ") is missing.");
						return false;
					}

					if (input.getUTXO().getValue() != tempOutput.getValue()) {
						System.out.println("Referenced input Transaction (" + t + ") value is invalid.");
						return false;
					}

					tempUTXOs.remove(input.getTransactionOutputId());
				}

				for (TransactionOutput output : currentTransaction.getOutputs()) {
					tempUTXOs.put(output.getId(), output);
				}

				if (currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
					System.out.println("Transaction (" + t + ") output recipient is not who it should be.");
					return false;
				}
				if (currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
					System.out.println("Transaction (" + t + ") output changed is not for sender. \n");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid.");
		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	public static float getMinimumTransaction() {
		return minimumTransaction;
	}

	public static Map<String, TransactionOutput> getUTXOs() {
		return UTXOs;
	}
	
}
