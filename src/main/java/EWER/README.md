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
| 1200 | Submitter SIgnature |
| 2 | Validator Sgnatures Size | Y x 1200 | Validator SIgnature |
| 2 | Transaction Size | X | Transaction |
| 2 | Transaction Size | X | Transaction |
| 2 | Transaction Size | X | Transaction |
| etc... | etc... | etc... | etc... |

