package external.client

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import proof.ProofOuterClass
import java.util.*

class GetProofCommand(): CliktCommand(help = "Makes a request to the Fabric agent for a proof of state." +
        "Requires the state key.") {
    val config by requireObject<Map<String, Any>>()
    val key: String by argument()
    val ledgerContractAddress: String by argument()
    val orgName: String by argument()
    override fun run() {
        println("Getting latest accumulator from Ethereum.")

        val config = Properties()
        this::class.java.getResourceAsStream("/${orgName}config.properties")
                .use { config.load(it) }
        val ethereumClient = EthereumClient(orgName)
        ethereumClient.getLatestAccumulator(ledgerContractAddress).map { commitment ->
            val request = ProofOuterClass.StateProofRequest.newBuilder()
                    .setCommitment(commitment)
                    .setKey(key)
                    .build()

            println("Getting the proof of state from Fabric Agent.")
            val host = (config["GRPC_HOST"] as String?) ?: "localhost"
            val port = (config["GRPC_PORT"] as String?)?.toInt() ?: 9099
            createGrpcConnection(host, port).map { grpcClient ->
                runBlocking {
                    val proofResponse = async { grpcClient.requestStateProof(request) }.await()
                    grpcClient.close()
                    proofResponse
                }
            }.flatMap { proofResponse ->
                verifyProofResponse(proofResponse, commitment)
            }
        }
    }
}
