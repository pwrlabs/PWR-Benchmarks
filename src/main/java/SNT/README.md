# README: SNT Encoding and Decoding

## Overview

This document provides an overview of the encoding and decoding mechanisms used in the SNT (Simple Native Transactions) algorythm. SNT is designed to handle different types of PWR transactions, such as transfers, joining as avalidator node, delegating, VM data transacctions and many others.

## Encoding and Decoding Transactions

### General Structure

Transactions in the SNT system are byte arrays with a specific structure that varies depending on the transaction type. Each transaction has an identifier that determines its type.

### **Types of Transactions**

1. **Transfer Transaction:** Used for transferring PWR coins between accounts.
2. **Join Transaction:** Used for a user to join as avalidator node.
3. **Claim Spot:** Used by validator to claim active spot
4. etc....

### **Encoding Process**

The encoding of transactions involves converting transaction details into a byte array format. This is typically handled before the transaction is sent over the network.
All transactions start with an identifier byte which lets us know which type of transaction it is.
Following this identifier byte the other details of the transaction are placed in a predefined manner.

#### Transfer Transaction Encoding
- **Identifier:** 1 byte
- **Nonce:** 4 bytes
- **Amount:** 8 bytes
- **Recipient:** 20 bytes

#### Join Transaction Encoding
- **Identifier:** 1 byte
- **Nonce:** 4 bytes
- **IP Address:** Variable length

### **Decoding Process**

Decoding is the process of interpreting a byte array back into a transaction's details. This is crucial for processing and validating transactions.
By reading the first byte (identifier byte) we can immedietly know which type of transaction this is.
Once we know the type of the transactions, we can immedietly extract all the variables from it due to its predefined structure which we know.

#### Transfer Transaction Decoding
Extract and convert each part of the transaction from the byte array:
   - Nonce
   - Amount
   - Recipient

#### Join Transaction Decoding
Extract and convert components from the byte array:
   - Nonce
   - IP Address

### **Decoding Time Complexity**

Extracting the identifier, nonce, amount, recipient, and other info that might be in the transactions involves a series of bitwise operations and array copying. Bitwise operations are O(1), and array copying can be considered O(n), where n is the length of the part being copied. However, since the length of each part is predefined and fixed and does not scale with input size, these operations can be treated as O(1).

Given these considerations, the overall time complexity of the SNT decoding process can be approximated as O(1), meaning it should execute in constant time regardless of the transaction size
