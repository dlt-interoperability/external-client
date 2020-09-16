package external.client

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import commitment.CommitmentOuterClass
import org.starcoin.rsa.RSAAccumulator
import org.starcoin.rsa.stringToHashBigInteger
import proof.ProofOuterClass
import java.math.BigInteger

fun verifyProofResponse(
        proofResponse: ProofOuterClass.StateProofResponse,
        ethCommitment: CommitmentOuterClass.Commitment
): Either<Error, Boolean> {
    println("Verifying proof returned from Fabric agent.")
    return if (proofResponse.hasProof()) {
        // Check that the accumulator returned from Fabric is the same accumulator
        // that was retrieved from the bulletin board
        if (proofResponse.proof.a.contains(ethCommitment.accumulator)) {
            // Create the hash key from the state returned from the Fabric agent
            val key = stringToHashBigInteger(proofResponse.proof.state)
            val isValid = RSAAccumulator.verifyMembership(
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
            println("Verification Error: accumulator returned from Fabric does not match bulletin board.\n")
            Left(Error("Verification Error: accumulator returned from Fabric does not match bulletin board."))
        }
    } else {
        println("Fabric Agent Error: ${proofResponse.error}")
        Left(Error("Fabric Agent Error: ${proofResponse.error}"))
    }
}