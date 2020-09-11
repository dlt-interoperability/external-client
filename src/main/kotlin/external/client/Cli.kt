package external.client

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import proof.ProofOuterClass
import java.math.BigInteger


class GetProofCommand: CliktCommand(help = "Makes a request to the Fabric agent for a proof of state." +
        "Requires the state key.") {
    val config by requireObject<Map<String, String>>()
    val key: String by argument()
    // TODO: remove blockHeight as this should come from Ethereum
    // This is a temporary workaround while we are creating a dummy commitment
    val blockHeight: Int by argument().int()
    override fun run() {
        println("Getting latest accumulator from Ethereum.")
        getLatestAccumulator(blockHeight).map { commitment ->
            // Construct the request with the accumulator from Ethereum
            // and the user-provided key
            val request = ProofOuterClass.Request.newBuilder()
                    .setCommitment(commitment)
                    .setKey(key)
                    .build()

            println("Getting the proof of state from Fabric Agent.")
            val host = config["GRPC_HOST"] ?: "localhost"
            val port = config["GRPC_PORT"]?.toInt() ?: 9099
            createGrpcConnection(host, port).map { grpcClient ->
                runBlocking {
                    async { grpcClient.RequestState(request) }.await()
                }
            }.flatMap { proofResponse ->
                verifyProofResponse(proofResponse, BigInteger(commitment.accumulator))
            }
        }
    }
}

