package EWER;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class EWEREncodingBenchmark {

    private static long previousBlockNumber;
    private static byte[] previousBlockHash;
    private static short blockchainVersion;
    private static long timeStamp;
    private static byte[] submitterSignature;
    private static byte[][] validatorSignature;
    private static byte[][] transactions;

    //Encoding with ByteBuffer
    private static byte[] encode1(
            long previousBlockNumber,
            byte[] previousBlockHash,
            short blockchainVersion,
            long timeStamp,
            byte[] submitterSignature,
            byte[][] validatorSignature,
            byte[][] transactions) {
//        int blockSize = 8 //Previous Block Number
//              + 32 //Previous Block Hash
//              + 2 //Blockchain Version
//              + 8 //Timestamp
//              + 1200 //Submitter Signature
//              + 2 //Validator Signature Count Identifier
//              + 4 //Transactions Count Identifier
//         ; // = 1256

        int blockSize = 1256 + (validatorSignature.length * 1200) + (transactions.length * 2);

        for(byte[] transaction : transactions) {
            blockSize += transaction.length; //Transaction Identifier
        }

        ByteBuffer buffer = ByteBuffer.allocate(blockSize);

        //Previous Block Number
        buffer.putLong(previousBlockNumber);

        //Previous Block Hash
        buffer.put(previousBlockHash);

        //Blockchain Version
        buffer.putShort(blockchainVersion);

        //Timestamp
        buffer.putLong(timeStamp);

        //Submitter Signature
        buffer.put(submitterSignature);

        //Validator Signature Count Identifier
        buffer.putShort((short) validatorSignature.length);

        //Transaction Count Identifier
        buffer.putInt(transactions.length);

        //Validator Signature
        for(byte[] validatorSignatureBlock : validatorSignature) {
            buffer.put(validatorSignatureBlock);
        }

        //Transaction Identifier and transactions
        for(byte[] transaction : transactions) {
            buffer.putShort((short) transaction.length);
            buffer.put(transaction);
        }

        return buffer.array();
    }

    //Encoding with System.arraycopy
    private static byte[] encode2(
            long previousBlockNumber,
            byte[] previousBlockHash,
            short blockchainVersion,
            long timeStamp,
            byte[] submitterSignature,
            byte[][] validatorSignature,
            byte[][] transactions) {
//        int blockSize = 8 //Previous Block Number
//              + 32 //Previous Block Hash
//              + 2 //Blockchain Version
//              + 8 //Timestamp
//              + 1200 //Submitter Signature
//              + 2 //Validator Signature Count Identifier
//              + 4 //Transactions Count Identifier
//         ; // = 1256

        int blockSize = 1256 + (validatorSignature.length * 1200) + (transactions.length * 2);

        for(byte[] transaction : transactions) {
            blockSize += transaction.length; //Transaction Identifier
        }

        byte[] block = new byte[blockSize];

        //Previous Block Number
        System.arraycopy(longToBytes(previousBlockNumber), 0, block, 0, 8);

        //Previous Block Hash
        System.arraycopy(previousBlockHash, 0, block, 8, 32);

        //Blockchain Version
        System.arraycopy(shortToBytes(blockchainVersion), 0, block, 40, 2);

        //Timestamp
        System.arraycopy(longToBytes(timeStamp), 0, block, 42, 8);

        //Submitter Signature
        System.arraycopy(submitterSignature, 0, block, 50, 1200);

        //Validator Signature Count Identifier
        System.arraycopy(shortToBytes((short) validatorSignature.length), 0, block, 1250, 2);

        //Transaction Count Identifier
        System.arraycopy(longToBytes(transactions.length), 0, block, 1252, 4);

        //Validator Signature
        int offset = 1256;

        for(byte[] validatorSignatureBlock : validatorSignature) {
            System.arraycopy(validatorSignatureBlock, 0, block, offset, 1200);
            offset += 1200;
        }

        //Transaction Identifier and transactions
        for(byte[] transaction : transactions) {
            System.arraycopy(shortToBytes((short) transaction.length), 0, block, offset, 2);
            System.arraycopy(transaction, 0, block, offset + 2, transaction.length);
            offset += 2 + transaction.length;
        }

        return block;
    }

    //Function to generate x random byte
    public static byte[] generateRandomBytes(int x) {
        byte[] bytes = new byte[x];
        for(int i = 0; i < x; i++) {
            bytes[i] = (byte) (Math.random() * 256);
        }
        return bytes;
    }

    //Function to convert Long to byte arrays
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    //Function to convert Long to byte arrays
    public static byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(x);
        return buffer.array();
    }

    public static List<byte[]> convertToList(byte[][] array) {
        List<byte[]> list = new ArrayList<>();
        for (byte[] bytes : array) {
            list.add(bytes);
        }
        return list;
    }

    public static byte[] generateBlock(int validatorSignatureCount, int transcationCount) {
        previousBlockNumber = 1;
        previousBlockHash = generateRandomBytes(32);
        blockchainVersion = 1;
        timeStamp = Instant.now().getEpochSecond();
        submitterSignature = generateRandomBytes(1200);

        validatorSignature = new byte[validatorSignatureCount][];
        for(int i = 0; i < validatorSignatureCount; i++) {
            validatorSignature[i] = generateRandomBytes(1200);
        }

        transactions = new byte[transcationCount][];
        for(int i = 0; i < transcationCount; i++) {
            transactions[i] = generateRandomBytes(98);
        }

        return encode2(previousBlockNumber, previousBlockHash, blockchainVersion, timeStamp, submitterSignature, validatorSignature, transactions);
    }

    @Setup
    public void setup() {
        previousBlockNumber = 1;
        previousBlockHash = generateRandomBytes(32);
        blockchainVersion = 1;
        timeStamp = Instant.now().getEpochSecond();
        submitterSignature = generateRandomBytes(1200);

        int validatorCount = 400;
        validatorSignature = new byte[validatorCount][];
        for(int i = 0; i < validatorCount; i++) {
            validatorSignature[i] = generateRandomBytes(1200);
        }

        int txnCount = 500000;
        transactions = new byte[txnCount][];
        for(int i = 0; i < txnCount; i++) {
            transactions[i] = generateRandomBytes(98);
        }
    }

    //Winner
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void benchmarkEncode1() {
        byte[] block = encode1(previousBlockNumber, previousBlockHash, blockchainVersion, timeStamp, submitterSignature, validatorSignature, transactions);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void benchmarkEncode1_2() {
        byte[] block = encode1(previousBlockNumber, previousBlockHash, blockchainVersion, timeStamp, submitterSignature, validatorSignature, transactions);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(EWEREncodingBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
