package noobchain.tx;

/**
 * 
 * This class will be used to reference TransactionOutputs that have not yet
 * been spent. The transactionOutputId will be used to find the relevant
 * TransactionOutput, allowing miners to check your ownership.
 *
 */
public class TransactionInput {

	// Contains the Unspent transaction output
	private TransactionOutput UTXO;
	private String transactionOutputId;

	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public TransactionOutput getUTXO() {
		return UTXO;
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

}
