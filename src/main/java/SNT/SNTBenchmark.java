package SNT;

import org.bouncycastle.util.encoders.Hex;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class SNTBenchmark {
    private static byte[] txn;
    public static void decodeTransferTxn(byte[] txn, byte[] sender) {
        if (txn.length != 98) {
            throw new IllegalArgumentException("Invalid txn length");
        }

        /*
         * Identifier - 1
         * Nonce - 4
         * amount - 8
         * recipient - 20
         * signature - 65
         * */

        int nonce = (txn[1] << 24) | ((txn[2] & 0xFF) << 16) | ((txn[3] & 0xFF) << 8) | (txn[4] & 0xFF);
        long amount = ((long)txn[5] << 56) | ((long)(txn[6] & 0xFF) << 48) | ((long)(txn[7] & 0xFF) << 40) | ((long)(txn[8] & 0xFF) << 32)| ((long)(txn[9] & 0xFF) << 24)
                | ((txn[10] & 0xFF) << 16) | ((txn[11] & 0xFF) << 8) | (txn[12] & 0xFF);
        byte[] recipientByte = new byte[20];
        byte[] signature = new byte[65];

        System.arraycopy(txn, 13, recipientByte, 0, 20);
        System.arraycopy(txn, 33, signature, 0, 65);
    }
    public static void decodeJoinTxn(byte[] txn, byte[] sender) {
        if(txn.length < 78 || txn.length > 86) {
            throw new IllegalArgumentException("Invalid txn length");
        }

        /*
         * Identifier - 1
         * Nonce - 4
         * ip - X
         * signature - 65
         * */

        int nonce = (txn[1] << 24) | ((txn[2] & 0xFF) << 16) | ((txn[3] & 0xFF) << 8) | (txn[4] & 0xFF);
        byte[] ipByteArray = new byte[txn.length - 70];
        byte[] signature = new byte[65];
        System.arraycopy(txn, 5, ipByteArray, 0, txn.length - 70);
        System.arraycopy(txn, txn.length - 65, signature, 0, 65);
    }
    public static void decodeTxn(byte[] txn) {
        short identifier = txn[0];

        if(identifier == 0) {
            decodeTransferTxn(txn, null);
        } else if (identifier == 1) {
            decodeJoinTxn(txn, null);
        } else {
            //...
        }
    }

    @Setup
    public void setup() {
        txn = Hex.decode("0000000000000000003b9aca00ee7a6b518898dd5f4c209fa180ad5fe739a64f036792087a455674be0fa84a30ef18c51422ce9423d301633b19cde9aa8a0c79cf6aa9d2a2e80ca6cdad59774a885255a180f068faf98daac6bbd1f490131219bb1b");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void benchmark() {
        decodeTxn(txn);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(SNTBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
