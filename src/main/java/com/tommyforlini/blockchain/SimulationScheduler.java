package com.tommyforlini.blockchain;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class SimulationScheduler {

    public static List<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 5;

    private final ObjectMapper mapper = new ObjectMapper();

    @Scheduled(cron = "0 */2 * * * MON-FRI")
    public void run() throws Exception {

        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        System.out.println("Mining GENESIS block... ");
        addBlock(new Block("The GENESIS block", "0"));

        Instant start = Instant.now();
        Instant blockStart;
        Instant blockEnd;
        Duration blockDuration;
        for (int i = 1; i <= 10; ++i) {
            System.out.println("Trying to Mine block " + i + "... ");
            blockStart = Instant.now();
            addBlock(new Block("Block-" + i + " block", blockchain.get(blockchain.size() - 1).getHash()));
            blockEnd = Instant.now();
            blockDuration = Duration.between(blockStart, blockEnd);
            System.out.println("Block-" + i + " mining duration " + blockDuration.getSeconds() + "sec");
        }
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("\n\nTOTAL MINING Duration " + duration.getSeconds() + "sec");

        System.out.println("\n\n\nIs Blockchain Valid ? " + isChainValid());

        String blockchainJson = mapper.writeValueAsString(blockchain);
        System.out.println("\nThe BlockChain: ");
        System.out.println(blockchainJson);
    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = Block.getDificultyString(difficulty);
        
        //loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            //compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            //compare previous hash and registered previous hash
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }
            //compare registered hash and registered next hash
            if (!currentBlock.getHash().equals(previousBlock.getNextHash())) {
                System.out.println("Next Hashes not equal");
                return false;
            }
            //check if hash is solved
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("Block not mined");
                return false;
            }

        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        //allow bi-directional knowledge of hashes
        if (blockchain.size() > 1) {
            blockchain.get(blockchain.size() - 2).setNextHash(newBlock.getHash());
        }
    }

}