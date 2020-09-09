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
  using `map` and `flatMap`. Avoid statements with side effects.
- Use recursion over loops (when tail recursion is possible to avoid stack overflow).

An example of how to catch exceptions and convert to and Either type is shown in
[this gist](https://gist.github.com/airvin/79f1fb2a3821a9e5d227db3ee9561f42).

## TODO

- User CLI to make queries
- Import Web3J to query the Ethereum smart contract
- Make a config file
- Import the RSA accumulator library and use it for proof verification
