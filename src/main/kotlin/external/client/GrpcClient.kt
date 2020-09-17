package external.client

import io.grpc.ManagedChannel
import proof.ProofOuterClass.StateProofRequest
import proof.ProofOuterClass.StateProofResponse
import java.io.Closeable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import proof.StateProofServiceGrpcKt
import java.util.concurrent.TimeUnit

class GrpcClient(private val channel: ManagedChannel) : Closeable {
    private val stub = StateProofServiceGrpcKt.StateProofServiceCoroutineStub(channel)

    suspend fun requestStateProof(request: StateProofRequest): StateProofResponse = coroutineScope {
        println("Requesting state and proof for $request")
        val response = async { stub.requestStateProof(request) }.await()
        println("Received ProofResponse from agent: $response\n")
        return@coroutineScope response
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}