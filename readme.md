# External Client

The external client is used to simulate an entity that wishes to get state from
a Fabric network, along with proof that that state is part of the immutable
ledger. This proof comes in the form of a membership witness of the state in an
RSA accumulator. The RSA accumulator is a commitment to the entire state of the
Fabric ledger. It is updated continuously as blocks are added to the ledger and
is published periodically to a public bulletin board, in this case, Ethereum. To
verify the proof, the external client queries both the smart contract on the
Ethereum mainnet for the latest accumulator, and the [Fabric commitment
agent](https://github.com/dlt-interoperability/commitment-agent) for the state
and proof of state in the accumulator.

## Prerequisites

Ensure that the Fabric commitment agent is running. To get the agent up and
running, clone [the
repo](https://github.com/dlt-interoperability/commitment-agent) and run:

```
./gradlew run
```

## Running

```
./gradlew run
```

## TODO

- User CLI to make queries
- Import Web3J to query the Ethereum smart contract
- Make a config file
- Import the RSA accumulator library and use it for proof verification
