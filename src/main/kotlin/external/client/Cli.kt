package external.client

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import proof.ProofOuterClass

class GetProofCommand: CliktCommand(help = "Makes a request to the Fabric agent for a proof of state." +
        "Requires the state key.") {
    val config by requireObject<Map<String, String>>()
    val key: String by argument()
    override fun run() {
        println("Getting latest accumulator from Ethereum.")
        val commitment = ProofOuterClass.Commitment.newBuilder()
                .setAccumulator("dummy accumulator")
                .setBlockHeight(1)
                .build()
        val request = ProofOuterClass.Request.newBuilder()
                .setKey("dummyKey")
                .setCommitment(commitment)
                .build()

        println("Getting the proof of state from Fabric Agent.")
        val host = config["GRPC_HOST"] ?: "localhost"
        val port = config["GRPC_PORT"]?.toInt() ?: 9099
        createGrpcConnection(host, port).map { grpcClient ->
            runBlocking {
                async { grpcClient.RequestState(request) }.await()
            }
        }
    }
}

