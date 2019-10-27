package io.roger.dat159.blockchain;

import io.roger.dat159.blockchain.util.*;

/**
 * Contains both the full Blockchain and the full UtxoMap. Also contains the
 * wallet for the mining rewards + fees.
 *
 * Services: - Receive a transaction, validate the transaction, create and mine
 * a new block, validate and append the mined block to the blockchain + update
 * the UxtoMap. - Provide a limited UtxoMap that matches one particular address
 * to wallets.
 */
public class FullNode {

	private Blockchain blockchain;
	private UtxoMap utxoMap;
	private Wallet wallet;

	/**
	 * Initializes a node containing a blockchain, a UTXO-map and a wallet for block
	 * rewards. Then initializes the blockchain by mining and adding a genesis
	 * block.
	 */
	public FullNode(String walletId) {
		this.wallet = new Wallet(walletId, this);
		this.blockchain = new Blockchain();
		this.utxoMap = new UtxoMap();

		mineAndAddGenesisBlock();
	}

	/**
	 * Does what it says.
	 */
	public void mineAndAddGenesisBlock() {

		// 1. Create the coinbase transaction
		CoinbaseTx coinbaseTx = new CoinbaseTx(0, DateTimeUtil.getTimestamp() + " - Transaction", wallet.getAddress());

		if (coinbaseTx.isValid()) {

			// 2. Add the coinbase transaction to a new block and mine the block
			Block genesisBlock = new Block("0", coinbaseTx, null);
			genesisBlock.mine();

			// 3. Validate the block. If valid:
			if (genesisBlock.isValidAsGenesisBlock()) {

				// 4. Add the block to the blockchain
				blockchain.setGenesisBlock(genesisBlock);

				// 5. Update the utxo set
				utxoMap.addOutput(new Input(coinbaseTx.getTxId(), 0), coinbaseTx.getOutput());
			} else {
				System.out.println("Block invalid as genesis block");
			}

		} else {
			System.out.println("TX is invalid");
		}
	}

	/**
	 * Does what it says.
	 */
	public void mineAndAppendBlockContaining(Transaction tx) {

		// 1. Create the coinbase transaction
		CoinbaseTx coinbaseTx = new CoinbaseTx(blockchain.getHeight(),
				DateTimeUtil.getTimestamp() + " by " + wallet.getId(), wallet.getAddress());

		if (coinbaseTx.isValid()) {

			// 2. Add the two transactions to a new block and mine the block
			Block newBlock = new Block(blockchain.getLastBlockHash(), coinbaseTx, tx);

			// 3. Validate the block. If valid:
			newBlock.mine();

			if (newBlock.isValid()) {

				// 4. Add the block to the blockchain
				blockchain.appendBlock(newBlock);

				// 5. Update the utxo set with the new coinbaseTx
				utxoMap.addOutput(new Input(coinbaseTx.getTxId(), 0), coinbaseTx.getOutput());

				// 6. Update the utxo set with the new tx
				for (Input i : tx.getInputs()) {
					utxoMap.removeOutput(i);
				}

				for (int i = 0; i < tx.getOutputs().size(); i++) {
					utxoMap.addOutput(new Input(tx.getTxId(), i), tx.getOutputs().get(i));
				}

			} else {
				System.out.println("Block is invalid as a new block");
			}
		} else {
			System.out.println("TX is invalid");
		}
	}

	public Blockchain getBlockchain() {
		return blockchain;
	}

	public UtxoMap getUtxoMap() {
		return utxoMap;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void printOverview() {
		System.out.println();
		System.out.println("Full node overview");
		System.out.println("------------------");
		System.out.println("   Associated wallet");
		wallet.printOverviewIndented();
		System.out.println("   Associated blockchain");
		blockchain.printOverview();
	}
}
