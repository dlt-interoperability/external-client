package external.client

import arrow.core.flatMap
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import proof.ProofOuterClass
import java.io.File
import java.io.FileOutputStream
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
	    val queryTimes = listOf(1,2,3,4,5,6,7,8,9,10).map {
            val startTime = System.currentTimeMillis()
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
	        val queryTime = System.currentTimeMillis() - startTime
	        println("Query $it took $queryTime ms")
	        queryTime
	    }
        FileOutputStream(File(config["RESULTS_FILE"] as String), true).bufferedWriter().use { writer ->
            writer.append("\nNext set of blocks")
            writer.append("\nQuery times are: $queryTimes")
            writer.append("\nAverage query time over 10 queries was ${queryTimes.average()} ms\n")
        }
	    println("Query times are: $queryTimes")
	    println("Average query time over 10 queries was ${queryTimes.average()} ms")
    }
}
