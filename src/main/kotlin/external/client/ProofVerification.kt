package external.client

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import org.starcoin.rsa.RSAAccumulator
import org.starcoin.rsa.stringToHashBigInteger
import proof.ProofOuterClass
import java.math.BigInteger

fun verifyProofResponse(
        proofResponse: ProofOuterClass.StateProofResponse,
        accumulator: BigInteger
): Either<Error, Boolean> {
    println("Verifying proof returned from Fabric agent.")
    return if (proofResponse.hasProof()) {
        // Create the hash key from the state returned from the Fabric agent
        val key = stringToHashBigInteger(proofResponse.proof.state)
        val isValid = RSAAccumulator.verifyMembership(
                // TODO: This should be the accumulator retrieved from Ethereum
                a = BigInteger(proofResponse.proof.a),
                x = key,
                nonce = BigInteger(proofResponse.proof.nonce),
                proof = BigInteger(proofResponse.proof.proof),
                n = BigInteger(proofResponse.proof.n)
        )
        if (isValid) {
            println("Proof verification passed.")
            Right(true)
        } else {
            println("Verification Error: Proof not valid")
            Left(Error("Verification Error: Proof not valid"))
        }
    } else {
        println("Fabric Agent Error: ${proofResponse.error}")
        Left(Error("Fabric Agent Error: ${proofResponse.error}"))
    }
}