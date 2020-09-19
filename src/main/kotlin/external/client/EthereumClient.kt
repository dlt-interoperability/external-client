package external.client

import arrow.core.*
import commitment.CommitmentOuterClass
import external.client.contracts.generated.LedgerState
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import java.io.FileInputStream
import java.math.BigInteger
import java.util.*

class EthereumClient(val orgName: String) {
    // This defaults to http://localhost:8545/
    // TODO add this to config
    val web3j = Web3j.build(HttpService())
    val gasProvider = StaticGasProvider(BigInteger.valueOf(20000000000), BigInteger.valueOf(6721975))
    val config = Properties()
    var credentials: Credentials

    init {
        this::class.java.getResourceAsStream("/${orgName}config.properties")
                .use { config.load(it) }
        // By default his is the private key of the last account created by the ganache-cli deterministic network
        val privateKey = (config["ETHEREUM_PRIVATE_KEY"] as String)
        credentials = Credentials.create(privateKey)
    }

    fun getLatestAccumulator(
            ledgerContractAddress: String
    ): Either<Error, CommitmentOuterClass.Commitment> = try {
        val lsInstance = LedgerState.load(
                ledgerContractAddress,
                web3j,
                credentials,
                gasProvider)
        val (commitment, _, blockHeight) = lsInstance.getCommitment().sendAsync().get()
        Right(CommitmentOuterClass.Commitment.newBuilder()
                .setAccumulator(commitment.toString(Charsets.UTF_8))
                .setBlockHeight(blockHeight.toInt())
                .build())
    } catch (e: Exception) {
        println("Ethereum Error: Error fetching commitment from Ethereum: ${e.message}\n")
        Left(Error("Ethereum Error: Error fetching commitment from Ethereum: ${e.message}"))
    }
}