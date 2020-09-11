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

### Publish the RSA Accumulator library to MavenLocal

The external client uses the
[rsa-accumulator-kotlin](https://github.com/dlt-interoperability/rsa-accumulator-kotlin)
library to verify membership proofs provided by the Fabric agent. This
repository needs to be cloned, built, and published to a local Maven repository.
Follow instructions in the repo to do this.
**Change line 30 in the `build.gradle` to point to your local Maven repository directory.**

### Start the Fabric network

The recommended network to use is [Fabric
network](https://github.com/dlt-interoperability/fabric-network). Start the
network and deploy and invoke the chaincode with:

```
make start
make deploy-cc
make invoke-cc
```

The `invoke-cc` make target starts a Fabric node.js application that submits
`CreateAsset` transactions every 10 seconds. This can be cancelled with
`ctrl-c`. The `make invoke-cc` can be used repeatedly without needing to
restart the network.

### Start the Fabric Agent

The [Fabric
commitment agent](https://github.com/dlt-interoperability/commitment-agent) is
used to maintain an accumulator for the ledger state. It returns state and proof
of membership to the state on request. Clone the repo, update the `build.gradle`
to point to the local Maven repo, and start the agent with:

```
./gradlew run
```

## Building and Running

The external client is a command line application. Build the binary with:

```
./gradlew installDist
```

The only available command is `get-proof <key> <block-height>`. This can be used as follows:

```
./build/install/external-client/bin/external-client get-proof key1 7
```

The requirement for the block height is a temporary workaround while the
external client is creating a dummy Ethereum commitment. When the external
client makes the actual request to Ethereum it will use the block height
corresponding to the accumulator in the latest commitment.

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
