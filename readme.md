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

The external client uses the
[rsa-accumulator-kotlin](https://github.com/dlt-interoperability/rsa-accumulator-kotlin)
library to verify membership proofs provided by the Fabric agent. This
repository needs to be cloned, built, and published to a local Maven repository.
Follow instructions in the repo to do this.
**Change line 30 in the `build.gradle` to point to your local Maven repository directory.**

Ensure that the Fabric network is running. The recommended network is the
[test-network](https://github.com/hyperledger/fabric-samples/tree/master/test-network)
in the fabric-samples repository. It is also recommended to use images for
Fabric v2.2. After cloning or pulling the latest version of the fabric-samples
repository, run the following from the test-network directory:

```
./network.sh up createChannel -c mychannel -ca
./network.sh deployCC -ccn basic -ccl javascript
```

Ensure that the Fabric commitment agent is running. To get the agent up and
running, clone [the
repo](https://github.com/dlt-interoperability/commitment-agent) and run:

```
./gradlew run
```

## Building and Running

The external client is a command line application. Build the binary with:

```
./gradlew installDist
```

The only available command is `get-proof <key>`. This can be used as follows:

```
./build/install/external-client/bin/external-client get-proof abc
```

## Coding principles

This codebase uses functional programming principles as much as possible. A
functional library for Kotlin, called [Arrow](https://arrow-kt.io/docs/core/) is
used, primarily for error handling with the `Either` type.

Conventions

- Use immutable state.
- Catch exceptions as close as possible to their source and convert to [Arrow's
  `Either`
  type](https://arrow-kt.io/docs/apidocs/arrow-core-data/arrow.core/-either/).
- Implement functions as expressions. Flows that produce errors can be composed
  using `map`, `flatMap` and `fold`. Avoid statements with side effects in functions.
- Use recursion over loops (when tail recursion is possible to avoid stack overflow).

An example of how to catch exceptions and convert to and Either type is shown in
[this gist](https://gist.github.com/airvin/79f1fb2a3821a9e5d227db3ee9561f42).

An example of folding over an Either Error to reduce to a single type is
demonstrated in [this
gist](https://gist.github.com/airvin/eabc99a9552a0573afd2dd9a13e75948).

## TODO

- Import Web3J to query the Ethereum smart contract
- Config file for the build.gradle
- Use Gson (or Moshi) to make KV out of the JSON stringified state that's
  returned in the proof.
