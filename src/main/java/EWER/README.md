# README: EWER Encoding and Decoding

## Overview

This document provides an overview of the encoding and decoding mechanisms used in the EWER (Easy Write Easy Read) algorythm. EWER is designed to handle blocks encoding and decoding over the PWR Chain.

Blocks on the PWR Chain follow a predefined structure, which makes it simple and straight forward to encode and decode them.

## Block Structure

1. Previous Block Number: An 8 byte integer representing the sequence number of the previous block.
2. Previous Block Hash: A 32-byte array representing the hash of the previous block.
3. Blockchain Version: A 2 byte integer specifying the version of the blockchain protocol.
4. Timestamp: An 8 byte integer representing the block creation timestamp.
5. Submitter Signature: A 1200-byte array for the digital signature of the block submitter.
6. Validator Signatures: A list of validator signatures, each 1200 bytes.
7. Transactions: An list of transactions with varying sizes.

## Block Encoding

The block encoding process begins with the addition of the fixed-size components. Each component, starting from the Previous Block Number to the Submitter Signature, is sequentially placed into the byte array.
The order of addition is crucial and follows the predefined block structure. This ensures consistency and readability during the decoding process.

Prior to appending the validator signatures, a 2-byte integer is integrated into the block. The value of this integer represents the quantity of signatures to be added. This integer plays a pivotal role in the decoding process, as it conveniently indicates the number of forthcoming 1200-byte signatures.

When incorporating transactions, each transaction is preceded by a 2-byte integer. Similar to the validator's signature, this integer denotes the size of the transaction and is crucial in the decoding process. It conveniently signals the number of bytes that must be read to accurately extract the transaction.

| Size | Name | Size | Name |
| --- | --- | --- | --- |
| 8 | Previous Block Number | 
| 32 | Previous Block Hash | 
| 2 | Blockchain Version |
| 8 | Timestamp |
| 1200 | Submitter Signature |
| 2 | Validator Sgnatures Size | Y x 1200 | Validator SIgnature |
| 2 | Transaction Size | X | Transaction |
| 2 | Transaction Size | X | Transaction |
| 2 | Transaction Size | X | Transaction |
| etc... | etc... | etc... | etc... |

## Encoding Benchmark

The following benchmark was ran on one of our team members PC, and used only 1 thread.
The benchmark encodes a 50 MB blocks holding 400 Validator Signatures and 500k transactions.

Mode  Cnt   Score         Error     Units
avgt   5    15689.136  ±  2739.039  us/op

On average, EWER Encoding was capable of encoding a 50MB block holding 500k transactions in 15.6 Milliseconds.
 
##Encoding Time Complexity

1. **Fixed-Size Components Encoding**: The encoding of fixed-size components (Previous Block Number, Previous Block Hash, Blockchain Version, Timestamp, Submitter Signature) is a constant-time operation, \( O(1) \), as each component's size is constant and independent of the size of the block or the number of transactions and signatures.

2. **Validator Signatures Encoding**: This step involves iterating over a list of validator signatures and appending each to the block. If there are \( V \) validator signatures, and each signature is a fixed size, this operation is \( O(V) \).

3. **Transactions Encoding**: Similar to validator signatures, each transaction is preceded by a 2-byte integer indicating its size. If there are \( T \) transactions, the complexity of this step is \( O(T) \), assuming the process of appending each transaction is constant time.

Overall, the encoding time complexity would be \( O(V + T) \), dominated by the number of validator signatures and transactions.
## Block Decoding

The process of block decoding reverses the steps taken during encoding to retrieve the original data from the encoded byte array. Here's how it works:

1. **Read Fixed-Size Components**: Start by reading the fixed-size components from the beginning of the encoded block. These components are known to have a constant size, making them straightforward to extract. The sequence is as follows:
   - Previous Block Number (8 bytes): Read and interpret the first 8 bytes as the Previous Block Number.
   - Previous Block Hash (32 bytes): Follow this by reading the next 32 bytes, which represent the hash of the previous block.
   - Blockchain Version (2 bytes): Extract the subsequent 2 bytes for the blockchain protocol version.
   - Timestamp (8 bytes): Read the next 8 bytes as the timestamp of the block creation.
   - Submitter Signature (1200 bytes): Then, extract the 1200-byte array that forms the digital signature of the block submitter.

2. **Validator Signatures Decoding**: After the fixed-size components, decode the validator signatures.
   - First, read the next 2 bytes, which indicate the number of validator signatures included in the block.
   - Knowing the count, sequentially extract each 1200-byte validator signature as indicated.

3. **Transactions Decoding**: Finally, decode the transactions.
   - Each transaction is preceded by a 2-byte integer specifying its size. Read these 2 bytes to understand the length of the subsequent transaction.
   - Extract the transaction data of the indicated length.
   - Continue this process for all transactions until the end of the block.

4. **Reassembling the Block Information**: As each component is decoded, reassemble the information in a structured format. This may involve converting byte data into meaningful representations (like converting timestamps into human-readable dates or hashes into hexadecimal strings) for further processing or analysis.

By meticulously following these steps, the EWER algorithm efficiently decodes each block on the PWR Chain, ensuring the integrity and accuracy of the data for blockchain operations.

##Decoding Benchmark

The following benchmark was ran on one of our team members PC, and used only 1 thread.
The benchmark decodes a 50 MB blocks holding 400 Validator Signatures and 500k transactions.

Mode  Cnt   Score      Error   Units
avgt   5    54.696  ±  1.818   us/op

On Average, EWER Decoding was capable of decoding a 50MB blocks holding 500k transactions in 0.054 Milliseconds.
In other words, EWER Decoding is capable of decoding 18,518 such big blocks in 1 second.

##Decoding Time Complexity


1. **Fixed-Size Components Decoding**: This is a constant-time operation \( O(1) \) since each component has a fixed size.

2. **Validator Signatures Decoding**: This involves reading and processing \( V \) validator signatures, each of a fixed size. Therefore, this step has a complexity of \( O(V) \).

3. **Transactions Decoding**: Here, each of the \( T \) transactions is decoded by first reading its size and then extracting the transaction data. Assuming the size read and data extraction operations are constant time for each transaction, this step also has a complexity of \( O(T) \).

Thus, the decoding time complexity is also \( O(V + T) \), dependent on the number of validator signatures and transactions.
