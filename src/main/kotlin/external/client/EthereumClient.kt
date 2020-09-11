package external.client

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import proof.ProofOuterClass

fun getLatestAccumulator(blockHeight: Int): Either<Error, ProofOuterClass.Commitment> = try {
    // TODO: Fetch actual commitment from Ethereum
    // This is currently creating a dummy commitment
    Right(ProofOuterClass.Commitment.newBuilder()
            .setAccumulator("229106625344179343892429585266069669503721218095865999800301083878039438783685867064176351695488480907498995117550818657595606793094027192072413425109096666547329218839185710826895343981359386836625266179285875075202593439371515552545515582951468299040843524929468091380272232006710357364667820260831798730944798194360565292706593025864194358750790219046535069328293287530614394694172318593075768056712278596312598912555687435090191197023856126361719832375349046901656846533017448936396684152395163758277484840656145219273498159176197746780747896224016710253183987346398407901831462462178691615244978100754282029800951576243514382644363506805900117338767997997640554524890253529652457400171308515836838032117285569477480344883630286155198395804688059256401640605504354630774974750513867183916687304679145479260841120189749098507352928111894230924757873089008446537533729143739385713070686890514831899039300642068919603284819")
            .setBlockHeight(blockHeight)
            .build())
} catch (e: Exception) {
    println("Ethereum Error: Error fetching commitment from Ethereum: ${e.stackTrace}\n")
    Left(Error("Ethereum Error: Error fetching commitment from Ethereum: ${e.message}"))
}