package io.roger.dat159.blockchain.application;

import javax.naming.InsufficientResourcesException;

import io.roger.dat159.blockchain.*;

public class DemoApplication {

	public static void main(String[] args) throws Exception {

		/*
		 * In this assignment, we are going to look at how to represent and record
		 * monetary transactions. We will use Bitcoin as the basis for the assignment,
		 * but there will be a lot of simplifications!
		 */

		/*
		 * 1. First, you should create the one and only FullNode. The full node will
		 * create it's internal Wallet, create the (centralized) Blockchain and UtxoMap,
		 * mine the genesis Block and add it to the blockchain (and update the UtxoMap).
		 */
		FullNode fullNode = new FullNode("Satoshi");

		/*
		 * 2. Next, you should create two additional wallets and reference the three
		 * wallets from three variables.
		 */
		Wallet wallet1 = fullNode.getWallet();

		Wallet wallet2 = new Wallet("Nakamoto", fullNode);
		Wallet wallet3 = new Wallet("Vitalik", fullNode);

		/*
		 * 3. Next, you should create a Transaction to transfer some money from the
		 * miner's (full node's) wallet address to one of the other wallet addresses.
		 * The full node should receive this transaction, validate the transaction, mine
		 * a new block and append it to the blockchain.
		 */
		Transaction tx = wallet1.createTransaction(1000, wallet2.getAddress());
		fullNode.mineAndAppendBlockContaining(tx);

		/*
		 * 4. Repeat the above (transfer some money from one wallet address to another
		 * and record this in the blockchain ledger).
		 */
		Transaction tx2 = wallet2.createTransaction(500, wallet3.getAddress());
		fullNode.mineAndAppendBlockContaining(tx2);

		/*
		 * 5. Repeat the above (transfer some money from one wallet address to another
		 * and record this in the blockchain ledger).
		 */
		Transaction tx3;
		try {
			System.out.println("Transaction with insufficient funds");

			tx3 = wallet3.createTransaction(10000, wallet1.getAddress());

			fullNode.mineAndAppendBlockContaining(tx3);
		} catch (InsufficientResourcesException e) {
			System.out.println("\n----------Insufficient funds.----------\n\n");
		}

		/*
		 * 6. Now, it is time to look at the finished result. Print out: - An overview
		 * of the full node - An overview of each of the three wallets - An overview of
		 * each of the four blocks in the blockchain
		 */
		fullNode.printOverview();

		wallet1.printOverview();
		wallet2.printOverview();
		wallet3.printOverview();

		fullNode.getBlockchain().getBlocks().forEach(block -> block.printOverview());
	}
}
