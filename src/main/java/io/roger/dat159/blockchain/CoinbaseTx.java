package io.roger.dat159.blockchain;

import io.roger.dat159.blockchain.util.*;

/**
 *
 */
public class CoinbaseTx {

	/*
	 * The block number wherein this coinbase tx is located. This is to ensure
	 * unique txIds, see BIP34
	 */
	private int blockHeight;

	/*
	 * The message for this coinbase tx.
	 */
	private String message;

	/*
	 * The output for this coinbase tx.
	 */
	private Output output;

	/* --------------------------------------------------------------------- */

	public CoinbaseTx(int blockHeight, String message, String walletAddress) {
		this.blockHeight = blockHeight;
		this.message = message;
		this.output = new Output(1000, walletAddress);
	}

	public boolean isValid() {
		if (blockHeight < 0 || message == null || output == null) {
			return false;
		}

		return true;
	}

	public String getMessage() {
		return message;
	}

	public Output getOutput() {
		return output;
	}

	/**
	 * The block hash as a hexadecimal String.
	 */
	public String getTxId() {
		return EncodingUtil.bytesToHex(HashUtil.sha256(blockHeight + message));
	}

	@Override
	public String toString() {
		return getTxId() + "\n\tmessage    : " + message + "\n\toutput(0)  : " + output;
	}
}
