package external.client

import io.grpc.ManagedChannel
import proof.AgentGrpcKt
import proof.ProofOuterClass.Request
import java.io.Closeable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import proof.ProofOuterClass
import java.util.concurrent.TimeUnit

class GrpcClient(private val channel: ManagedChannel) : Closeable {
    private val stub = AgentGrpcKt.AgentCoroutineStub(channel)

    suspend fun RequestState(request: Request): ProofOuterClass.ProofResponse = coroutineScope {
        println("Requesting state and proof for $request")
        val response = async { stub.requestState(request) }.await()
        println("Received ProofResponse from agent: $response\n")
        return@coroutineScope response
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}