import io.javalin.Javalin
import org.zeromq.*

fun main() {
    val app = Javalin.create().start(8080)
    app.post("/emergency") { ctx ->
        val contentType = ctx.contentType().orEmpty()
        val payload = ctx.body()
        ZContext().use { context ->
            val proxy: ZMQ.Socket = context.createSocket(SocketType.REQ)
            proxy.connect( "tcp://*:5556")
            proxy.send("[[<HTTP>,<$contentType>]]$payload")
            val reply = proxy.recvStr()
            proxy.close()
            println(reply)
        }
    }
}