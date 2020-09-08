package external.client

import io.grpc.ManagedChannel
import proof.AgentGrpcKt
import proof.ProofOuterClass.Request
import proof.ProofOuterClass.Proof
import java.io.Closeable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

class GrpcClient(private val channel: ManagedChannel) : Closeable {
    private val stub = AgentGrpcKt.AgentCoroutineStub(channel)

    suspend fun RequestState(request: Request): Proof = coroutineScope {
        println("Requesting state and proof for $request")
        val response = async { stub.requestState(request) }.await()
        println("Received RequestState response from agent: $response\n")
        return@coroutineScope response
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}