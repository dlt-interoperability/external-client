package external.client

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async
import proof.ProofOuterClass.Commitment
import proof.ProofOuterClass.Request
import java.io.FileInputStream
import java.lang.Error
import java.util.*

fun main(args: Array<String>) {
    val properties = Properties()
    FileInputStream("${System.getProperty("user.dir")}/src/main/resources/config.properties")
            .use { properties.load(it) }
    val host = properties["GRPC_HOST"] as String
    val port = (properties["GRPC_PORT"] as String).toInt()
    createGrpcConnection(host, port).map { grpcClient ->
        val commitment = Commitment.newBuilder()
                .setAccumulator("dummy accumulator")
                .setBlockHeight(1)
                .build()
        val request = Request.newBuilder()
                .setKey("dummyKey")
                .setCommitment(commitment)
                .build()
        runBlocking {
            async { grpcClient.RequestState(request) }.await()
        }
    }
}

fun createGrpcConnection(host: String, port: Int): Either<Error, GrpcClient> = try {
    Right(GrpcClient(
            ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .executor(Dispatchers.Default.asExecutor())
                    .build()))
} catch (e: Exception) {
    println("GrpcError: Error creating gRPC connection: ${e.stackTrace}\n")
    Left(Error("Error: ${e.message}"))
}

