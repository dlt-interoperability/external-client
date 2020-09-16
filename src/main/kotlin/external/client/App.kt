package external.client

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findOrSetObject
import com.github.ajalt.clikt.core.subcommands
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.io.FileInputStream
import java.lang.Error
import java.util.*

class App: CliktCommand() {
    val config by findOrSetObject { mutableMapOf<String, Any>() }
    override fun run() {
        val properties = Properties()
        FileInputStream("${System.getProperty("user.dir")}/src/main/resources/config.properties")
                .use { properties.load(it) }
        config["GRPC_HOST"] = properties["GRPC_HOST"] as String
        config["GRPC_PORT"] = properties["GRPC_PORT"] as String
        config["ETHEREUM_CLIENT"] = EthereumClient()
    }
}

fun main(args: Array<String>) = App()
        .subcommands(GetProofCommand())
        .main(args)

fun createGrpcConnection(host: String, port: Int): Either<Error, GrpcClient> = try {
    Right(GrpcClient(
            ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .executor(Dispatchers.Default.asExecutor())
                    .build()))
} catch (e: Exception) {
    println("GrpcError: Error creating gRPC connection: ${e.message}\n")
    Left(Error("Error: ${e.message}"))
}
