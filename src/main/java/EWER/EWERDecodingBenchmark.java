package EWER;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class EWERDecodingBenchmark {

    byte[] block;

    //Decoding with ByteBuffer
    private static void decode1(byte[] blocks) {
        ByteBuffer buffer = ByteBuffer.wrap(blocks);

        //Previous Block Number
        long previousBlockNumber = buffer.getLong();

        //Previous Block Hash
        byte[] previousBlockHash = new byte[32];
        buffer.get(previousBlockHash);

        //Blockchain Version
        short blockchainVersion = buffer.getShort();

        //Timestamp
        long timeStamp = buffer.getLong();

        //Submitter Signature
        byte[] submitterSignature = new byte[1200];
        buffer.get(submitterSignature);

        //Validator Signature Count Identifier
        short validatorSignatureCount = buffer.getShort();

        //Transactions Count Identifier
        int transactionsCount = buffer.getInt();

        //Validator Signature
        byte[][] validatorSignature = new byte[validatorSignatureCount][1200];
        for (int i = 0; i < validatorSignatureCount; i++) {
            //validatorSignature[i] = new byte[1200];
            buffer.get(validatorSignature[i]);
        }

        //Transactions
        byte[][] transactions = new byte[transactionsCount][];
        for (int i = 0; i < transactionsCount; i++) {
            short transactionIdentifier = buffer.getShort();
            transactions[i] = new byte[transactionIdentifier];
            buffer.get(transactions[i]);
        }
    }

    //Decoding with byte array
    private static void decode2(byte[] block) {
        long previousBlockNumber = ((long) block[0] << 56) |
                ((long) (block[1] & 0xFF) << 48) |
                ((long) (block[2] & 0xFF) << 40) |
                ((long) (block[3] & 0xFF) << 32) |
                ((long) (block[4] & 0xFF) << 24) |
                ((block[5] & 0xFF) << 16) |
                ((block[6] & 0xFF) << 8) |
                (block[7] & 0xFF);

        byte[] previousBlockHash = new byte[32];
        System.arraycopy(block, 8, previousBlockHash, 0, 32);

        short blockchainVersion = (short) ((block[40] << 8) | (block[41] & 0xFF));

        long timeStamp = ((long) block[42] << 56) |
                ((long) (block[43] & 0xFF) << 48) |
                ((long) (block[44] & 0xFF) << 40) |
                ((long) (block[45] & 0xFF) << 32) |
                ((long) (block[46] & 0xFF) << 24) |
                ((block[47] & 0xFF) << 16) |
                ((block[48] & 0xFF) << 8) |
                (block[49] & 0xFF);

        byte[] submitterSignature = new byte[1200];
        System.arraycopy(block, 50, submitterSignature, 0, 1200);

        short validatorSignatureCount = (short) ((block[1250] << 8) | (block[1251] & 0xFF));

        int transactionsCount = ((block[1252] & 0xFF) << 24) |
                ((block[1253] & 0xFF) << 16) |
                ((block[1254] & 0xFF) << 8) |
                (block[1255] & 0xFF);

        byte[][] validatorSignature = new byte[validatorSignatureCount][];
        int offset = 1256;
        for (int i = 0; i < validatorSignatureCount; i++) {
            validatorSignature[i] = new byte[1200];
            System.arraycopy(block, offset, validatorSignature[i], 0, 1200);
            offset += 1200;
        }

        byte[][] transactions = new byte[transactionsCount][];
        for(int i = 0; i < transactionsCount; i++) {
            byte[] transactionIdentifierByteArray = new byte[2];
            System.arraycopy(block, offset, transactionIdentifierByteArray, 0, 2);
            short transactionIdentifier = byteArrayToShort(transactionIdentifierByteArray);

            transactions[i] = new byte[transactionIdentifier];
            System.arraycopy(block, offset + 2, transactions[i], 0, transactionIdentifier);

            offset += 2 + transactionIdentifier;
        }
    }

    //Function - Byte array to short
    public static short byteArrayToShort(byte[] b) {
        return (short) ((b[0] << 8) | (b[1] & 0xFF));
    }

    @Setup
    public void setup() {
        block = EWEREncodingBenchmark.generateBlock(400, 10000);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void benchmarkDecode1() {
        decode1(block);
    }

    //Winner
    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void benchmarkDecode2() {
        decode2(block);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(EWERDecodingBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }


}
