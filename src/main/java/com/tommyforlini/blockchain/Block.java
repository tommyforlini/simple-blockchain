package com.tommyforlini.blockchain;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.google.common.hash.Hashing;

public class Block {

	private String hash;
	private String previousHash;
	private String nextHash;
	private String transaction;
	private long timeStamp;
	private int proofOfWork;

	public Block(String transaction, String previousHash) {
		this.transaction = transaction;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();

		//Must be the last step!
		this.hash = calculateHash();
	}

	public String calculateHash() {
		String sha256hex = Hashing.sha256()
				.hashString(previousHash + 
				Long.toString(timeStamp) + 
				Integer.toString(proofOfWork) + 
				transaction, StandardCharsets.UTF_8)
				.toString();
		return sha256hex;
	}

	public void mineBlock(int difficulty) {
		//Increases pow until hash target is reached.
		String target = getDificultyString(difficulty);
		while (!hash.substring(0, difficulty).equals(target)) {
			proofOfWork++;
			hash = calculateHash();
		}
		System.out.println("Block Mined > " + hash);
	}

	public static String getDificultyString(int difficulty) {
		//Create a string with difficulty * "0"
		return new String(new char[difficulty]).replace('\0', '0');
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public String getNextHash() {
		return nextHash;
	}

	public void setNextHash(String nextHash) {
		this.nextHash = nextHash;
	}

	public String getTransaction() {
		return transaction;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public int getProofOfWork() {
		return proofOfWork;
	}

}
