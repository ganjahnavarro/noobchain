package noobchain.tx;

import java.security.PublicKey;

import noobchain.util.StringUtil;

/**
 * 
 * This class will show the final amount sent to each party from the
 * transaction. These, when referenced as inputs in new transactions, act as
 * proof that you have coins to send.
 *
 */
public class TransactionOutput {

	private String id;

	// New owner of the coins
	private PublicKey recipient;

	// Amount of coins they own
	private float value;

	// ID of the transaction this output was created in
	private String parentTransactionId;

	public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil
				.applySha256(StringUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
	}

	// Check if coin belongs to you
	public boolean isRecipientSelf(PublicKey publicKey) {
		return (publicKey == recipient);
	}

	public String getId() {
		return id;
	}

	public PublicKey getRecipient() {
		return recipient;
	}

	public float getValue() {
		return value;
	}

	public String getParentTransactionId() {
		return parentTransactionId;
	}

}
